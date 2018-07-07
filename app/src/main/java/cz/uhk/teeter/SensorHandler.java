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

import java.util.ArrayList;

import static android.content.Context.SENSOR_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

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
    private float SLOWDOWN_INDEX = 0.50f; //hodnota 0,5 zajistí pomalejší nelineární rozjezd a také pomalejší útlum <3
    public static final float REFLECTION = 0.99f;
    private final float NOISE = 0.35f;

    private Sphere sphere;
    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private boolean sphereLocked = true;

    @Override
    public void onSensorChanged(SensorEvent event) {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        if (sphereLocked) {
            lastMillis = System.currentTimeMillis();
            return;
        }

        long nowMillis = System.currentTimeMillis();

        float[] clearValues = new float[]{clear(event.values[0]), clear(event.values[1]), clear(event.values[2])};

        gravity[0] = alpha * gravity[0] + (1 - alpha) * clearValues[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * clearValues[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * clearValues[2];

        //třeba se lineární akcelerace bude hodit
        linearAcceleration[0] = clearValues[0] - gravity[0];
        linearAcceleration[1] = clearValues[1] - gravity[1];
        linearAcceleration[2] = clearValues[2] - gravity[2];

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        //normalizace gravitace
        float sum = Math.abs(gravity[0]) + Math.abs(gravity[1]) + Math.abs(gravity[2]);
        gravity[0] = (gravity[0] / sum * GRAVITY);
        gravity[1] = (gravity[1] / sum * GRAVITY);
        gravity[2] = (gravity[2] / sum * GRAVITY);

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        float deltaTime = (float) (nowMillis - lastMillis) / 1000f;

        //              zpomalování třením

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


        float velX = (sphere.getVelocityX() * nowFriction) + (xValue);
        float velY = (sphere.getVelocityY() * nowFriction) + (yValue);

        sphere.setVelocityX((alpha * sphere.getVelocityX()) + (1 - alpha) * velX);
        sphere.setVelocityY((alpha * sphere.getVelocityY()) + (1 - alpha) * velY);

        //TODO - detekci hodu s telefonem - aby to nerozhodilo kuličku

        lastMillis = nowMillis;

        Sphere.Point2D spherePosition = sphere.getPositionPoint();
        if (spherePosition.x >= pixelsToMeters(sphere.radius) && spherePosition.x <= (width)) {
            spherePosition.x -= (sphere.getVelocityX() * deltaTime);
        }
        if (spherePosition.y >= pixelsToMeters(sphere.radius) && spherePosition.y <= (height)) {
            spherePosition.y += (sphere.getVelocityY() * deltaTime);
        }
        //border světa - detekce kolize
        if (spherePosition.x < pixelsToMeters(sphere.radius)) {
            spherePosition.x = pixelsToMeters(sphere.radius);
            sphere.setVelocityX(sphere.getVelocityX() * (-REFLECTION));
        } else if (spherePosition.x > (width)) {
            spherePosition.x = (width);
            sphere.setVelocityX(sphere.getVelocityX() * (-REFLECTION));
        }
        if (spherePosition.y < pixelsToMeters(sphere.radius)) {
            spherePosition.y = pixelsToMeters(sphere.radius);
            sphere.setVelocityY(sphere.getVelocityY() * (-REFLECTION));
        } else if (spherePosition.y > (height)) {
            spherePosition.y = (height);
            sphere.setVelocityY(sphere.getVelocityY() * (-REFLECTION));
        }

        for (Obstacle obstacle : obstacles) {
            obstacle.handleCollision(sphere);
        }
        //System.out.println(String.format("%s %s", /*sphere.velocity[0]*/" - ", sphere.velocity[1]));

        sphere.setPositionPoint(spherePosition);

        sphere.setPositionInMeters(new Sphere.Point2D(metersToPixels(spherePosition.x), metersToPixels(spherePosition.y)));

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void init(Context context, SurfaceView surfaceView) {
        Display display = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        orientation = display.getRotation();
        density = context.getResources().getDisplayMetrics().densityDpi;

        if (sphere == null) {
            sphere = new Sphere();
            sphere.radius = 20;
            width = surfaceView.getWidth() - sphere.radius;
            height = surfaceView.getHeight() - sphere.radius;
            width = pixelsToMeters((int) width);
            height = pixelsToMeters((int) height);
            sphere.setPositionPoint(new Sphere.Point2D(width / 2, height / 2));
            sphere.setPositionInMeters(new Sphere.Point2D(metersToPixels(width / 2), metersToPixels(height / 2)));

        }
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void finish(Context context) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public Sphere.Point2D getPosition() {
        return sphere.getPositionInMeters();
    }

    private float clear(float f) {
        return (f < NOISE && f > -NOISE) ? 0 : f;
    }

    public static float pixelsToMeters(int pixelsCount) {
        return ((float) pixelsCount / (float) density / 39f); //39 - to jest převod z palců na metr .. protože density je v PPI - pixels per inch
    }

    public static int metersToPixels(float metersCount) {
        return (int) (metersCount * 39f * (float) density);
    }

    public void lockSphere() {
        sphereLocked = !sphereLocked;
    }

    public boolean isSphereLocked() {
        return sphereLocked;
    }
}


