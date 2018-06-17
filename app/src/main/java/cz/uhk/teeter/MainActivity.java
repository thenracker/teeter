package cz.uhk.teeter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int orientation;
    private ArrayList<Obstacle> obstacles;

    public static class Sphere {
        public int circleWidth, circleHeight;
        public float[] position;
        public float[] velocity = new float[]{0f, 0f}; //pro x (levá, pravá) a y(horní dolní) // obrázek os zde - https://developer.android.com/reference/android/hardware/SensorEvent
    }

    private SensorManager sensorManager;
    private final static float alpha = 0.8f;
    private float[] gravity = new float[]{0f, 0f, 0f};
    private float[] linearAcceleration = new float[]{0f, 0f, 0f};

    private int sensor = Sensor.TYPE_ACCELEROMETER; //TYPE_GRAVITY;// - by se hodila no


    private long lastMillis;
    private View circle;
    private float width, height;
    private int density;
    private boolean init;

    private final float GRAVITY = 9.8f;
    private float FRICTION = 0.95f;
    private float SLOWDOWN_INDEX = 0.50f; //hodnota 0,5 zajistí pomalejší nelineární rozjezd a také pomalejší útlum <3
    public static final float REFLECTION = 0.99f;
    private final float NOISE = 0.35f;

    private Sphere sphere = new Sphere();

    //private SurfaceView surfaceView;

    /**
     * TODO co je potřeba udělat:
     * - obstacle dodělat na kolize se stranami - myslet na to, že v rozích bychom měli počítat spíše s poloměrem kuličky
     * - různé tvar překážek?
     * - ukládání, načítání překážek
     * - ukládání herního postupu
     * - dynamické prostředí: scrollview ve kterém bude dlouhý level - dle pozice kuličky vhodně skrolovat layoutem
     * -- na mravenčím displeji si totiž moc nezahrajeme :(
     * - dodělat dírky do kterých se dá spadnout
     * - dodělat nakloněné roviny - gradient stínový kde bude gravitace násobena indexem - ez
     * - něco tě napadá KUBO ještě?
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        circle = findViewById(R.id.circle);

        obstacles = new ArrayList<>();

        circle.post(new Runnable() {
            @Override
            public void run() {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                density = metrics.densityDpi;
                sphere.circleWidth = circle.getWidth();
                sphere.circleHeight = circle.getHeight();
                width = ((FrameLayout) circle.getParent()).getWidth() - sphere.circleWidth;
                height = ((FrameLayout) circle.getParent()).getHeight() - sphere.circleHeight;
                //surfaceView.setLayoutParams(new FrameLayout.LayoutParams((int) width + sphere.circleWidth, (int) height + sphere.circleHeight)); //Surface view se stáhnul na polovic z nějakého důvodu -_-
                width = pixelsToMeters((int) width);
                height = pixelsToMeters((int) height);
                circle.setX(width / 2);
                circle.setY(height / 2);
                sphere.position = new float[]{circle.getX(), circle.getY()};
                init = true;
            }
        });

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        orientation = display.getRotation();

        /*surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                addObstacles();
                draw();
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });*/

    }

    /*private void addObstacles() {
        for (int i = 0; i < 20; i++) {
            Obstacle obstacle = new Obstacle(surfaceView.getWidth(), surfaceView.getHeight(), this);
            obstacles.add(obstacle);
        }
    }

    private void draw() {
        Canvas canvas = surfaceView.getHolder().lockCanvas();

        canvas.drawColor(Color.WHITE);
        for (Obstacle obstacle : obstacles) {
            canvas.drawRect((obstacle.getX()), (obstacle.getY()), obstacle.getX() + obstacle.getWidth(), obstacle.getY() + obstacle.getHeight(), new Paint(Color.GRAY));
        }

        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        lastMillis = System.currentTimeMillis();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensor), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(sensor));
        super.onPause();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        long nowMillis = System.currentTimeMillis();

        float[] clearValues = new float[]{clear(event.values[0]), clear(event.values[1]), clear(event.values[2])};

        gravity[0] = alpha * gravity[0] + (1 - alpha) * clearValues[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * clearValues[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * clearValues[2];

        //třeba se lineární akcelerace bude hodit
        linearAcceleration[0] = clearValues[0] - gravity[0];
        linearAcceleration[1] = clearValues[1] - gravity[1];
        linearAcceleration[2] = clearValues[2] - gravity[2];

        //normalizace gravitace
        float sum = Math.abs(gravity[0]) + Math.abs(gravity[1]) + Math.abs(gravity[2]);
        gravity[0] = (gravity[0] / sum * GRAVITY);
        gravity[1] = (gravity[1] / sum * GRAVITY);
        gravity[2] = (gravity[2] / sum * GRAVITY);

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        float deltaTime = (float) (nowMillis - lastMillis) / 1000f;

        //              zpomalování třením
        float newVelocityX = (gravity[0] * deltaTime) * SLOWDOWN_INDEX;
        float newVelocityY = (gravity[1] * deltaTime) * SLOWDOWN_INDEX;

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
        sphere.velocity[0] = (sphere.velocity[0] * nowFriction) + (xValue);
        sphere.velocity[1] = (sphere.velocity[1] * nowFriction) + (yValue);

        //TODO - detekci hodu s telefonem - aby to nerozhodilo kuličku

        lastMillis = nowMillis;

        if (init) {
            if (sphere.position[0] >= 0 && sphere.position[0] <= (width)) {
                sphere.position[0] -= (sphere.velocity[0] * deltaTime);
            }
            if (sphere.position[1] >= 0 && sphere.position[1] <= (height)) {
                sphere.position[1] += (sphere.velocity[1] * deltaTime);
            }

            circle.setX(metersToPixels(sphere.position[0]));
            circle.setY(metersToPixels(sphere.position[1]));

            //border světa - detekce kolize
            if (sphere.position[0] < 0) {
                sphere.position[0] = 0;
                sphere.velocity[0] *= -REFLECTION;
            } else if (sphere.position[0] > (width)) {
                sphere.position[0] = (width);
                sphere.velocity[0] *= -REFLECTION;
            }
            if (sphere.position[1] < 0) {
                sphere.position[1] = 0;
                sphere.velocity[1] *= -REFLECTION;
            } else if (sphere.position[1] > (height)) {
                sphere.position[1] = (height);
                sphere.velocity[1] *= -REFLECTION;
            }

            for (Obstacle obstacle : obstacles) {
                obstacle.handleCollision(sphere);
            }
            //System.out.println(String.format("%s %s", /*sphere.velocity[0]*/" - ", sphere.velocity[1]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private float clear(float f) {
        return (f < NOISE && f > -NOISE) ? 0 : f;
    }

    public float pixelsToMeters(int pixelsCount) {
        return ((float) pixelsCount / (float) density / 39f);
    }

    public int metersToPixels(float metersCount) {
        return (int) (metersCount * 39f * (float) density);
    }

}

