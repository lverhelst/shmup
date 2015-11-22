package systems;

import com.badlogic.gdx.physics.box2d.Body;

import components.Bullet;
import components.ShmupActor;
import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/21/2015.
 */

public class MunitionsFactory {

    static Bullet nextOffTheConveyorBelt;

    public static Bullet createBullet(ShmupActor owner){
        Game.addActor(nextOffTheConveyorBelt = new Bullet(owner));
        return nextOffTheConveyorBelt;
    }
}
