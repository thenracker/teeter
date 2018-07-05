package cz.uhk.teeter;

import java.util.Random;

public class Hole {

    private Sphere.Point2D positionInMeters;

    public Hole(int displayWidth, int displayHeight) {
        Random random = new Random();
        this.positionInMeters = new Sphere.Point2D(random.nextInt(displayWidth), random.nextInt(displayHeight));
    }

    public Sphere.Point2D getPositionInMeters() {
        return positionInMeters;
    }

    public void setPositionInMeters(Sphere.Point2D positionInMeters) {
        this.positionInMeters = positionInMeters;
    }
}
