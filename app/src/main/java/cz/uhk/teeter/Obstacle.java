package cz.uhk.teeter;

import static cz.uhk.teeter.SensorHandler.REFLECTION;

public class Obstacle {

    private float x, y, width, height;

    public Obstacle(float x, float y, float width, float height) {
        this.width = width;
        this.height = height;

        this.x = x;
        this.y = y;
    }


    public void handleCollision(Sphere sphere) {

        float[] position = new float[]{sphere.getPositionInMeters().x, sphere.getPositionInMeters().y};

        // kolize pravá strana
        if (position[0] >= (x - sphere.radius)
                && position[1] > y - sphere.radius
                && position[1] < y + height) {
            position[0] = x - sphere.radius;
            sphere.setVelocityX(sphere.getVelocityX() * -REFLECTION);
        }

        // kolize levá strana
        if (position[0] <= (x + sphere.radius)
                && position[1] > y - sphere.radius
                && position[1] < y + height) {
            position[0] = x + sphere.radius;
            sphere.setVelocityX(sphere.getVelocityX() * -REFLECTION);
        }

        //TODO zbytek stran
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
