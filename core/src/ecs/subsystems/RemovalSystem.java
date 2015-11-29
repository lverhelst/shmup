package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ChildEntityComponent;
import ecs.components.JointComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.SteeringComponent;
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
                Game.getWorld().destroyJoint(j);
            }
            Game.removeEntity(e);
        }
        else if (i == INTENT.DIED){
            if(e.has(PhysicalComponent.class)){
                PhysicalComponent pc = e.get(PhysicalComponent.class);
                if(pc.isRoot){
                    //Destroy Joints
                    //should replace with recursive remove

                   if(e.has(ChildEntityComponent.class)){
                        ChildEntityComponent cec = e.get(ChildEntityComponent.class);
                        for(Entity child : cec.childList){
                            if(child.has(JointComponent.class)){

                                Joint j = ((JointComponent)child.get(JointComponent.class)).joint;
                                if(j != null) {
                                    Game.getWorld().destroyJoint(j);
                                    ((JointComponent)child.get(JointComponent.class)).joint = null;
                                }
                                e.removeComponent(JointComponent.class);
                            }
                        }
                    }
                    e.recursiveRemove(SteeringComponent.class);
                }else{
                    //Tire

                    if(e.has(ParentEntityComponent.class)){
                         Entity parent = ((ParentEntityComponent)e.get(ParentEntityComponent.class)).parent;

                         if(parent.has(JointComponent.class)){
                             Joint j = ((JointComponent)parent.get(JointComponent.class)).joint;
                             if(j != null) {
                                 Game.getWorld().destroyJoint(j);
                                 ((JointComponent)parent.get(JointComponent.class)).joint = null;
                             }

                             e.removeComponent(JointComponent.class);
                         }

                         if(e.has(SteeringComponent.class)){
                             e.removeComponent(SteeringComponent.class);
                         }

                     }
                }
                for(Fixture f : pc.getBody().getFixtureList()) {
                    f.setSensor(true);
                }
            }
            //remove joints?
            //respawn would have to recreate the components then..
        }
    }

}
