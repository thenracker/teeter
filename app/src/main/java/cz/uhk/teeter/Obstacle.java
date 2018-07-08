package cz.uhk.teeter;

import static cz.uhk.teeter.CanvasActivity.OBS_RADIUS;
import static cz.uhk.teeter.SensorHandler.REFLECTION;
import static cz.uhk.teeter.UnitsHelper.pixelsToMeters;

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


    public void handleCollision(Ball ball, int density) {

        Ball.Point2D ballPosition = ball.getPositionInPixels();

        // kolize leva strana
        if (ball.getVelocityX() < 0 && //leti smerem doprava
                ballPosition.x > x - ball.radius
                && ballPosition.x < x + ball.radius
                && ballPosition.y > y - ball.radius
                && ballPosition.y < y2 + ball.radius) {
            ball.getPositionPoint().x = pixelsToMeters(x - ball.radius, density);
            ball.setVelocityX(ball.getVelocityX() * (-REFLECTION));
        } //kolize prava strana
        else if (ball.getVelocityX() > 0//leti smerem doleva
                && ballPosition.x < x2 + ball.radius
                && ballPosition.x > x2 - ball.radius
                && ballPosition.y > y - ball.radius
                && ballPosition.y < y2 + ball.radius) {
            ball.getPositionPoint().x = pixelsToMeters(x2 + ball.radius, density);
            ball.setVelocityX(ball.getVelocityX() * (-REFLECTION));
        } //kolize horni strana
        else if (ball.getVelocityY() > 0 //leti dolu
                && ballPosition.x > x - ball.radius
                && ballPosition.x < x2 + ball.radius
                && ballPosition.y > y - ball.radius
                && ballPosition.y < y + ball.radius) {
            ball.getPositionPoint().y = pixelsToMeters(y - ball.radius, density);
            ball.setVelocityY(ball.getVelocityY() * (-REFLECTION));
        } // kolize spodni strana
        else if (ball.getVelocityY() < 0 //leti nahoru
                && ballPosition.x > x - ball.radius
                && ballPosition.x < x2 + ball.radius
                && ballPosition.y < y2 + ball.radius
                && ballPosition.y > y2 - ball.radius) {
            ball.getPositionPoint().y = pixelsToMeters(y2 + ball.radius, density);
            ball.setVelocityY(ball.getVelocityY() * (-REFLECTION));
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

    public int getX2() {
        return x2;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {
        return y2;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }
}
