package cz.uhk.teeter;

public class Sphere {
    public int radius;
    private float velocityX = 0f;
    private float velocityY = 0f;
    private Point2D positionPoint;
    private Point2D positionInPixels;

//    public float[] velocity = new float[]{0f, 0f}; //pro x (levá, pravá) a y(horní dolní) // obrázek os zde - https://developer.android.com/reference/android/hardware/SensorEvent


    public float getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(float velocityX) {
        this.velocityX = velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(float velocityY) {
        this.velocityY = velocityY;
    }

    public Point2D getPositionPoint() {
        return positionPoint;
    }

    public void setPositionPoint(Point2D positionPoint) {
        this.positionPoint = positionPoint;
    }

    public Point2D getPositionInPixels() {
        return positionInPixels;
    }

    public void setPositionInPixels(Point2D positionInPixels) {
        this.positionInPixels = positionInPixels;
    }

    public void updatePositionInPixels() {
        positionInPixels = new Point2D(SensorHandler.metersToPixels(positionPoint.x), SensorHandler.metersToPixels(positionPoint.y));
    }

    public static class Point2D {
        public float x, y;

        public Point2D(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
