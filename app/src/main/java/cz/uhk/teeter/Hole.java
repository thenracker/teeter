package cz.uhk.teeter;

public class Hole {

    private Ball.Point2D positionInMeters;

    public Hole(int x, int y) {
        //Random random = new Random();
        //this.positionInMeters = new Ball.Point2D(random.nextInt(displayWidth), random.nextInt(displayHeight));
        this.positionInMeters = new Ball.Point2D(x, y);
    }

    public Ball.Point2D getPositionInMeters() {
        return positionInMeters;
    }

    public void setPositionInMeters(Ball.Point2D positionInMeters) {
        this.positionInMeters = positionInMeters;
    }
}
