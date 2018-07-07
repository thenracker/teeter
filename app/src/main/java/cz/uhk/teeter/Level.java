package cz.uhk.teeter;

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
}
