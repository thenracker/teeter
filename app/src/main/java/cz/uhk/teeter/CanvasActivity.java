package cz.uhk.teeter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CanvasActivity extends AppCompatActivity {

    private final static int FPS = 120;
    private final static float CIRCLE_RADIUS = 20;

    private Runnable runnable;
    private Handler handler;
    private SurfaceView surfaceView;

    Paint paintCircle;
    private Paint paintHoles;
    SensorHandler sensorHandler;
    private boolean init;

    private List<Hole> holes;
    private List<Obstacle> obstacles;

    private float xDown = 0f;
    private float yDown = 0f;

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
        paintCircle = new Paint();
        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(Color.GRAY);

        paintHoles = new Paint();
        paintHoles.setStyle(Paint.Style.FILL);
        paintHoles.setColor(Color.BLACK);

        runnable = new Runnable() {
            @Override
            public void run() {
                if (init) {
                    draw();
                    detectFails();
                }

                handler.postDelayed(runnable, 1000 / FPS);
            }
        };

        createLists();
    }

    private void createLists() {
        holes = new ArrayList<>();
        obstacles = new ArrayList<>();
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;
        /*for (int i = 0; i < 20; i++) {
            //holes.add(new Hole(widthPixels, heightPixels));
        }*/
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

                surfaceView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                            if (xDown == 0){
                                xDown = motionEvent.getX();
                                yDown = motionEvent.getY();
                            }
                            return true;
                        }

                        if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                            float x = motionEvent.getX();
                            float y = motionEvent.getY();

                            if (Math.abs(x - xDown) < 20 && Math.abs(y - yDown) < 20){ //třeba 20 pixelů tolerance
                                holes.add(new Hole((int)x, (int)y));
                            } else {
                                obstacles.add(new Obstacle(xDown, yDown, x, y));
                            }
                            xDown = 0f;
                            yDown = 0f;

                            return true;
                        }
                        return false;
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
        sensorHandler.finish(this);
        handler.removeCallbacks(runnable);
        init = false;
        super.onPause();
    }

    private void draw() {
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        Sphere.Point2D position = sensorHandler.getPosition();
        if (position != null && canvas != null) {
            canvas.drawColor(Color.WHITE);
            canvas.drawCircle(position.x, position.y, CIRCLE_RADIUS, paintCircle);
            for (Hole hole : holes) {
                canvas.drawCircle(hole.getPositionInMeters().x, hole.getPositionInMeters().y, CIRCLE_RADIUS, paintHoles);
            }
            for (Obstacle obs : obstacles) {
                canvas.drawRect(obs.getX(), obs.getY(), obs.getWidth(), obs.getHeight(), paintHoles);
            }
        }
        surfaceView.getHolder().unlockCanvasAndPost(canvas);
    }

    private void detectFails() {
        Sphere.Point2D position = sensorHandler.getPosition();
        if (position != null) {
            for (Hole hole : holes) {
                //c2 = a2 + b2 - pokud je c kratší než radius kuličky, pak díra
                //TODO -
                if (Math.sqrt((Math.pow(hole.getPositionInMeters().x - position.x, 2) + Math.pow(hole.getPositionInMeters().y - position.y, 2))) < CIRCLE_RADIUS){
                    Toast.makeText(this, "PROHRÁL JSI", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
