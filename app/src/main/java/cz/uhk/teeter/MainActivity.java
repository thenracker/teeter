package cz.uhk.teeter;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private final static float alpha = 0.8f;
    private float[] gravity = new float[]{0f, 0f, 0f};
    private float[] linearAcceleration = new float[]{0f, 0f, 0f};

    private int sensor = Sensor.TYPE_ACCELEROMETER; //TYPE_GRAVITY;// - by se hodila no

    private float[] velocity = new float[]{0f, 0f}; //pro x (levá, pravá) a y(horní dolní) // obrázek os zde - https://developer.android.com/reference/android/hardware/SensorEvent

    private long lastMillis;
    private View textView;
    private float[] position;
    private boolean init;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        textView = findViewById(R.id.textView);

        textView.post(new Runnable() {
            @Override
            public void run() {
                position = new float[]{textView.getX(), textView.getY()};
                init = true;
            }
        });
    }

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

        linearAcceleration[0] = clearValues[0] - gravity[0];
        linearAcceleration[1] = clearValues[1] - gravity[1];
        linearAcceleration[2] = clearValues[2] - gravity[2];

        //System.out.println(String.format("%s %s %s : %s %s %s", gravity[0], gravity[1], gravity[2], linearAcceleration[0], linearAcceleration[1], linearAcceleration[2]));

        float time = (float) (nowMillis - lastMillis) / 1000f;

        //             zpomalování třením
        velocity[0] = (velocity[0] * 0.95f) + (gravity[0] * time);
        velocity[1] = (velocity[1] * 0.95f) + (gravity[1] * time);

        

        lastMillis = nowMillis;

        //System.out.println(String.format("%s %s", velocity[0], velocity[1]));

        if (init) {
            position[0] -= (velocity[0] * time * 10000); //todo přepočet z metru na displej
            position[1] += (velocity[1] * time * 10000);
            textView.setX((int) position[0]);
            textView.setY((int) position[1]);

            if (position[0] < 0 || position[0] > 600) velocity[0] *= -1f;
            if (position[1] < 0 || position[1] > 1200) velocity[1] *= -1f;
            System.out.println(String.format("%s %s", position[0], position[1]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    private float clear(float f) {
        return (f < 0.5f && f > -0.5f) ? 0 : f;
    }

    //TODO sepsat funkci pro zpomalování kuličky třením
}
