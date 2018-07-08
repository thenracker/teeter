package cz.uhk.teeter;

import static cz.uhk.teeter.CanvasActivity.OBS_RADIUS;
import static cz.uhk.teeter.SensorHandler.REFLECTION;

public class Obstacle {

    private int x, y, x2, y2;

    public Obstacle(int x, int y, int x2, int y2) {
        if (x > x2) {
            int pom = x;
            x = x2;
            x2 = pom;
        }
        if (y > y2) {
            int pom = y;
            y = y2;
            y2 = pom;
        }

        int xDiff = x2 - x;
        int yDiff = y2 - y;
        if (yDiff > xDiff) {
            if (xDiff > OBS_RADIUS) {
                x += (xDiff - OBS_RADIUS) / 2;
                x2 -= (xDiff - OBS_RADIUS) / 2;
            } else {
                x -= (OBS_RADIUS - xDiff) / 2;
                x2 += (OBS_RADIUS - xDiff) / 2;
            }
        } else {
            if (yDiff > OBS_RADIUS) {
                y += (yDiff - OBS_RADIUS) / 2;
                y2 -= (yDiff - OBS_RADIUS) / 2;
            } else {
                y -= (OBS_RADIUS - yDiff) / 2;
                y2 += (OBS_RADIUS - yDiff) / 2;
            }
        }

        this.x = x;
        this.y = y;

        this.x2 = x2;
        this.y2 = y2;

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
