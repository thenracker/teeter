package cz.uhk.teeter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class CanvasActivity extends AppCompatActivity {

    private final static int FPS = 120;
    private Runnable runnable;
    private Handler handler;
    private SurfaceView surfaceView;
    Paint paint;
    SensorHandler sensorHandler;
    private boolean init;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_canvas);

        surfaceView = findViewById(R.id.surfaceView);
        Toast.makeText(this, getResources().getString(R.string.tap_to_play), Toast.LENGTH_SHORT).show();


        handler = new Handler();
        sensorHandler = new SensorHandler();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (init) {
                    draw();
                }

                handler.postDelayed(runnable, 1000 / FPS);
            }
        };

    }

    @Override
    protected void onResume() {
        super.onResume();
        surfaceView.post(new Runnable() {
            @Override
            public void run() {
                sensorHandler.init(CanvasActivity.this, surfaceView);
                init = true;

                surfaceView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sensorHandler.lockSphere();
                    }
                });
            }
        });

        handler.postDelayed(runnable, 1000 / FPS);
    }

    @Override
    protected void onPause() {
        //todo poreskat vypinani displeje, trosku se nam ta kulicka posouva :D
        if (!sensorHandler.isSphereLocked()) {
            sensorHandler.lockSphere();
        }
        super.onPause();
        sensorHandler.finish(this);
        handler.removeCallbacks(runnable);
        init = false;
    }

    private void draw() {
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        Sphere.Point2D position = sensorHandler.getPosition();
        if (position != null && canvas != null) {
            canvas.drawColor(Color.WHITE);
            canvas.drawCircle(position.x, position.y, 20, paint);
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }
}
