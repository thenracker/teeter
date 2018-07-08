package cz.uhk.teeter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.WindowManager;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.WINDOW_SERVICE;
import static cz.uhk.teeter.UnitsHelper.pixelsToMeters;

public class SensorHandler implements SensorEventListener {


    private int orientation;
    private final static float alpha = 0.8f;
    private float[] gravity = new float[]{0f, 0f, 0f};
    private float[] linearAcceleration = new float[]{0f, 0f, 0f};

    private long lastMillis;
    private float width, height;
    private static int density;

    private final float GRAVITY = 9.8f;
    private float FRICTION = 0.95f;
    public static final float REFLECTION = 0.99f;
    private final float NOISE = 0.35f;

    private Ball ball;
    private Level level;
    private boolean ballLocked = true;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        if (ballLocked) {
            lastMillis = System.currentTimeMillis();
            return;
        }

        long nowMillis = System.currentTimeMillis();

        float[] clearValues = new float[]{clear(event.values[0]), clear(event.values[1]), clear(event.values[2])};

        //low pass filter
        //chceme jen urcitou cast novych hodnot a zachovavat stare
        gravity[0] = alpha * gravity[0] + (1 - alpha) * clearValues[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * clearValues[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * clearValues[2];

        //třeba se lineární akcelerace bude hodit
//        linearAcceleration[0] = clearValues[0] - gravity[0];
//        linearAcceleration[1] = clearValues[1] - gravity[1];
//        linearAcceleration[2] = clearValues[2] - gravity[2];

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        //normalizace gravitace
        float sum = Math.abs(gravity[0]) + Math.abs(gravity[1]) + Math.abs(gravity[2]);
        gravity[0] = (gravity[0] / sum * GRAVITY);
        gravity[1] = (gravity[1] / sum * GRAVITY);
        gravity[2] = (gravity[2] / sum * GRAVITY);

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        float deltaTime = (float) (nowMillis - lastMillis) / 1000f;

        // v = a*t
        float newVelocityX = (gravity[0] * deltaTime);// * SLOWDOWN_INDEX;
        float newVelocityY = (gravity[1] * deltaTime);// * SLOWDOWN_INDEX;

        float nowFriction;
        //při nulové pozici chceme vyšší tření
        if (gravity[0] > -NOISE && gravity[0] < NOISE && gravity[1] > -NOISE && gravity[1] < NOISE) {
            nowFriction = 0.97f;
        } else {
            nowFriction = FRICTION;
        }

        // změna vektorů dle natočení displeje
        float xValue, yValue;
        if (orientation == Surface.ROTATION_0) {
            xValue = newVelocityX;
            yValue = newVelocityY;
        } else if (orientation == Surface.ROTATION_90) {
            xValue = -newVelocityY;
            yValue = newVelocityX;
        } else if (orientation == Surface.ROTATION_180) {
            xValue = -newVelocityX;
            yValue = -newVelocityY;
        } else {
            xValue = newVelocityY;
            yValue = -newVelocityX;
        }

        // v = v0 + a*t
        float velX = (ball.getVelocityX() * nowFriction) + (xValue);
        float velY = (ball.getVelocityY() * nowFriction) + (yValue);

        //opet chceme jen cast novych hodnot, aby zmeny smeru pohybu byly postupne
        ball.setVelocityX((alpha * ball.getVelocityX()) + (1 - alpha) * velX);
        ball.setVelocityY((alpha * ball.getVelocityY()) + (1 - alpha) * velY);

        lastMillis = nowMillis;

        Ball.Point2D ballPosition = ball.getPositionPoint();
        if (ballPosition.x >= pixelsToMeters(ball.radius, density) && ballPosition.x <= (width)) {
            ballPosition.x -= (ball.getVelocityX() * deltaTime);
        }
        if (ballPosition.y >= pixelsToMeters(ball.radius, density) && ballPosition.y <= (height)) {
            ballPosition.y += (ball.getVelocityY() * deltaTime);
        }
        //border světa - detekce kolize
        //leva
        if (ballPosition.x < pixelsToMeters(ball.radius, density)) {
            ballPosition.x = pixelsToMeters(ball.radius, density);
            ball.setVelocityX(ball.getVelocityX() * (-REFLECTION));
        }//prava
        else if (ballPosition.x > (width)) {
            ballPosition.x = (width);
            ball.setVelocityX(ball.getVelocityX() * (-REFLECTION));
        }//horni
        if (ballPosition.y < pixelsToMeters(ball.radius, density)) {
            ballPosition.y = pixelsToMeters(ball.radius, density);
            ball.setVelocityY(ball.getVelocityY() * (-REFLECTION));
        }//dolni
        else if (ballPosition.y > (height)) {
            ballPosition.y = (height);
            ball.setVelocityY(ball.getVelocityY() * (-REFLECTION));
        }

        ball.setPositionPoint(ballPosition);

        for (Obstacle obstacle : level.getObstacles()) {
            obstacle.handleCollision(ball, density);
        }

        ball.updatePositionInPixels(density);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ignore
    }

    public void init(Context context, SurfaceView surfaceView, Level level, float ballRadius) {
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        orientation = display.getRotation();
        density = context.getResources().getDisplayMetrics().densityDpi;

        if (ball == null) {
            ball = new Ball();
            ball.radius = (int) ballRadius;
            width = surfaceView.getWidth() - ball.radius;
            height = surfaceView.getHeight() - ball.radius;
            width = pixelsToMeters((int) width, density);
            height = pixelsToMeters((int) height, density);
            if (level.hasStartingPosition()) {
                ball.setPositionInPixels(level.getStartingPosition());
                ball.setPositionPoint(new Ball.Point2D(pixelsToMeters((int) level.getStartingPosition().x, density), pixelsToMeters((int) level.getStartingPosition().y, density)));
            } else {
                ball.setPositionPoint(new Ball.Point2D(width / 2, height / 2));
                ball.updatePositionInPixels(density);
            }
        }

        this.level = level;
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void finish(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public Ball.Point2D getPosition() {
        return ball.getPositionInPixels();
    }

    private float clear(float f) {
        return (f < NOISE && f > -NOISE) ? 0 : f;
    }

    public void lockBall() {
        ballLocked = !ballLocked;
    }

    public boolean isBallLocked() {
        return ballLocked;
    }

    public void resetBall() {
        if (level.hasStartingPosition()) {
            ball.setPositionInPixels(level.getStartingPosition());
            ball.setPositionPoint(new Ball.Point2D(pixelsToMeters((int) level.getStartingPosition().x, density), pixelsToMeters((int) level.getStartingPosition().y, density)));
            ball.setVelocityX(0);
            ball.setVelocityY(0);
        }
    }
}


