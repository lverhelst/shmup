package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.JointComponent;
import ecs.components.PhysicalComponent;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;

/**
 * Created by Orion on 11/26/2015.
 */

//We can move this to the entity manager when it gets created

public class RemovalSystem extends SubSystem{

    public void update(Entity e, INTENT i){
        if(i == INTENT.REMOVE) {
            if (e.recursiveHas(PhysicalComponent.class)) {
                Body b2dBody = ((PhysicalComponent) e.get(PhysicalComponent.class)).getBody();
                b2dBody.getWorld().destroyBody(b2dBody);
            }
            if (e.recursiveHas(JointComponent.class)) {
                Joint j = ((JointComponent) e.recursiveGet(JointComponent.class)).joint;
                Game.world.destroyJoint(j);
            }
            Game.removeEntity(e);
        }
        else if (i == INTENT.DIED){
            if(e.recursiveHas(PhysicalComponent.class)){
                for(Fixture f : ((PhysicalComponent) e.get(PhysicalComponent.class)).getBody().getFixtureList()) {
                    f.setSensor(true);
                }
            }
            //remove joints?
            //respawn would have to recreate the components then..
        }
    }

}
