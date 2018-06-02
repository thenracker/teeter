package cz.uhk.teeter;

import static cz.uhk.teeter.MainActivity.REFLECTION;

public class Obstacle {

    private int x, y, width, height;

    private void handleCollision(MainActivity.Sphere sphere){

        // kolize pravá strana
        if (sphere.position[0] >= (x - sphere.circleWidth)
                && sphere.position[1] > y - sphere.circleHeight
                && sphere.position[1] < y + height){
            sphere.position[0] = x - sphere.circleWidth;
            sphere.velocity[0] *= -REFLECTION;
        }

        //TODO zbytek stran
    }
}
