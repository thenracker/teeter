package cz.uhk.teeter;

import java.util.Random;

public class Obstacle {

    private int x, y, width, height;
    private CanvasActivity canvasActivity;

    public Obstacle(float width, float height, CanvasActivity canvasActivity) {
        this.width = 50;
        this.height = 50; //pixelů zatím

        this.x = new Random().nextInt((int) width - 20) + 20;
        this.y = new Random().nextInt((int) height - 20) + 20;

        this.canvasActivity = canvasActivity;
    }


    public void handleCollision(Sphere sphere) {

//        float[] position = new float[]{mainActivity.metersToPixels(sphere.getPositionPoint().x), mainActivity.metersToPixels(sphere.getPositionPoint().y)};
//
//        // kolize pravá strana
//        if (position[0] >= (x - sphere.circleWidth)
//                && position[1] > y - sphere.circleHeight
//                && position[1] < y + height) {
//            position[0] = x - sphere.circleWidth;
//            sphere.velocity[0] *= -REFLECTION;
//        }
//
//        // kolize levá strana
//        if (position[0] <= (x + sphere.circleWidth)
//                && position[1] > y - sphere.circleHeight
//                && position[1] < y + height) {
//            position[0] = x + sphere.circleWidth;
//            sphere.velocity[0] *= -REFLECTION;
//        }

        //TODO zbytek stran
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
