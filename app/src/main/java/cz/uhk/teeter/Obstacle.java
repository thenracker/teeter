package cz.uhk.teeter;

import static cz.uhk.teeter.SensorHandler.REFLECTION;

public class Obstacle {

    private int x, y, x2, y2;

    public Obstacle(int x, int y, int width, int height) {
        this.x2 = width;
        this.y2 = height;

        this.x = x;
        this.y = y;
    }


    public void handleCollision(Sphere sphere) {

        Sphere.Point2D spherePosition = sphere.getPositionInPixels();

        // kolize leva strana
        if (sphere.getVelocityX() < 0 && //leti smerem doprava
                spherePosition.x > x - sphere.radius
                && spherePosition.x < x + sphere.radius
                && spherePosition.y > y - sphere.radius
                && spherePosition.y < y2 + sphere.radius) {
            sphere.getPositionPoint().x = SensorHandler.pixelsToMeters(x - sphere.radius);
            sphere.setVelocityX(sphere.getVelocityX() * (-REFLECTION));
        } //kolize prava strana
        else if (sphere.getVelocityX() > 0//leti smerem doleva
                && spherePosition.x < x2 + sphere.radius
                && spherePosition.x > x2 - sphere.radius
                && spherePosition.y > y - sphere.radius
                && spherePosition.y < y2 + sphere.radius) {
            sphere.getPositionPoint().x = SensorHandler.pixelsToMeters(x2 + sphere.radius);
            sphere.setVelocityX(sphere.getVelocityX() * (-REFLECTION));
        } //kolize horni strana
        else if (sphere.getVelocityY() > 0 //leti dolu
                && spherePosition.x > x - sphere.radius
                && spherePosition.x < x2 + sphere.radius
                && spherePosition.y > y - sphere.radius
                && spherePosition.y < y + sphere.radius) {
            sphere.getPositionPoint().y = SensorHandler.pixelsToMeters(y - sphere.radius);
            sphere.setVelocityY(sphere.getVelocityY() * (-REFLECTION));
        } // kolize spodni strana
        else if (sphere.getVelocityY() < 0 //leti nahoru
                && spherePosition.x > x - sphere.radius
                && spherePosition.x < x2 + sphere.radius
                && spherePosition.y < y2 + sphere.radius
                && spherePosition.y > y2 - sphere.radius) {
            sphere.getPositionPoint().y = SensorHandler.pixelsToMeters(y2 + sphere.radius);
            sphere.setVelocityY(sphere.getVelocityY() * (-REFLECTION));
        }
    }

    public float getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return x2;
    }

    public void setWidth(int width) {
        this.x2 = width;
    }

    public int getHeight() {
        return y2;
    }

    public void setHeight(int height) {
        this.y2 = height;
    }
}
