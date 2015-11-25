package Factories;

import gameObjects.Bullet;
import gameObjects.ShmupActor;
import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/21/2015.
 * We create a munitions factory so that you don't have to remember to add your bullet
 * to the actor list every spot you create one.
 */

public class MunitionsFactory {

    static Bullet nextOffTheConveyorBelt;

    public static Bullet createBullet(ShmupActor owner){
        Game.addActor(nextOffTheConveyorBelt = new Bullet(owner));
        return nextOffTheConveyorBelt;
    }
}
