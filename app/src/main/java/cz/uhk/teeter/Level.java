package cz.uhk.teeter;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Level {

    private int width, height;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private List<Obstacle> obstacles = new ArrayList<>();

    private List<Hole> holes = new ArrayList<>();

    private Sphere.Point2D startingPosition;

    private Sphere.Point2D endPosition;


    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<Obstacle> obstacles) {
        this.obstacles = obstacles;
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public void setHoles(List<Hole> holes) {
        this.holes = holes;
    }

    public Sphere.Point2D getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Sphere.Point2D startingPosition) {
        this.startingPosition = startingPosition;
    }

    public Sphere.Point2D getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(Sphere.Point2D endPosition) {
        this.endPosition = endPosition;
    }

    public boolean hasStartingPosition() {
        return startingPosition != null;
    }

    public boolean hasEndPosition() {
        return endPosition != null;
    }

    public static class Loader {

        public static Level loadFromAssets(Context context, String assetFileName, int width, int height) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
            Gson gson = new Gson();
            Level level = gson.fromJson(bufferedReader, Level.class);
            for (Hole hole : level.getHoles()) {
                Sphere.Point2D position = hole.getPositionInMeters();
                position.x = position.x * (float) width / (float) level.width;
                position.y = position.y * (float) height / (float) level.height;
            }
            for (Obstacle obstacle : level.getObstacles()) {
                obstacle.setX((int)(obstacle.getX() * (float) width / (float) level.width));
                obstacle.setY((int)(obstacle.getY() * (float) width / (float) level.width));
                obstacle.setX2((int)(obstacle.getX2() * (float) width / (float) level.width));
                obstacle.setY2((int)(obstacle.getY2() * (float) width / (float) level.width));
            }
            {
                Sphere.Point2D position = level.getStartingPosition();
                position.x = position.x * (float) width / (float) level.width;
                position.y = position.y * (float) height / (float) level.height;
            }
            {
                Sphere.Point2D position = level.getEndPosition();
                position.x = position.x * (float) width / (float) level.width;
                position.y = position.y * (float) height / (float) level.height;
            }

            level.width = width;
            level.height = height;
            return level;
        }

        public static String saveToJson(Level level) {
            Gson gson = new Gson();
            String json = gson.toJson(level);
            System.out.println(json); //TODO odtud posílat na server
            return json;
        }
    }
}

