package cz.uhk.teeter;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Level {

    private List<Obstacle> obstacles;

    private List<Hole> holes;

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

    public static class Loader {

        public Level loadFromAssets(Context context, String assetFileName) throws IOException{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
            Gson gson = new Gson();
            Level level = gson.fromJson(bufferedReader, Level.class);
            return level;
        }

        public void saveToJson(Level level){
            Gson gson = new Gson();
            String json = gson.toJson(level);
            System.out.println(json); //TODO odtud posílat na server
        }
    }
}
