package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

import java.util.ArrayList;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ChildEntityComponent;
import ecs.components.DamageComponent;
import ecs.components.JointComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.SteeringComponent;
import verberg.com.shmup.ShmupGame;
import MessageManagement.INTENT;

/**
 * Created by Orion on 11/26/2015.
 */

//We can move this to the entity manager when it gets created

public class RemovalSystem implements SubSystem{

    public void processMessage(INTENT intent, Object... list) {
        if(list[0].getClass() == Entity.class) {
            Entity e = (Entity)list[0];

            switch (intent) {
                case REMOVE:
                    remove(e);
                    break;
                case DIED:
                    if(list.length >= 2)
                        System.out.println(e.getName() + " was killed by " + (list[1] == null ? "Probably the level" : ((Entity)list[1]).getName()));
                    madeDead(e);
                    break;
            }
        }
    }

    public void remove(Entity e) {
        e.removeAllComponents();
        /*if (e.recursiveHas(PhysicalComponent.class)) {
            Body b2dBody = (e.get(PhysicalComponent.class)).getBody();
            if(b2dBody != null)
                b2dBody.getWorld().destroyBody(b2dBody);
        }
        if (e.recursiveHas(JointComponent.class)) {
            Joint j = (e.get(JointComponent.class)).joint;
            if(j != null)
                ShmupGame.getWorld().destroyJoint(j);
        }
        e.removeAllComponents();*/
    }

    public void madeDead(Entity e) {
        if(e.has(DamageComponent.class)){
            e.removeComponent(DamageComponent.class);
        }


        if(e.has(PhysicalComponent.class)) {
            if (e.get(PhysicalComponent.class).isRoot) {
                //Destroy Joints
                if (e.has(ChildEntityComponent.class)) {
                    ArrayList<Entity> toRm  = new ArrayList<Entity>();
                    for (Entity child : e.get(ChildEntityComponent.class).childList) {
                        if (child.has(JointComponent.class)) {

                            Joint j = (child.get(JointComponent.class)).joint;
                            if (j != null) {
                                ShmupGame.getWorld().destroyJoint(j);
                                child.get(JointComponent.class).joint = null;
                            }
                            e.removeComponent(JointComponent.class);
                            toRm.add(child);
                        }
                    }
                    for(Entity rm : toRm){
                       // e.get(ChildEntityComponent.class).childList.remove(rm);
                    }

                }
                e.recursiveRemove(SteeringComponent.class);
            } else {
                //Tire

                if (e.has(ParentEntityComponent.class)) {
                    Entity parent = (e.get(ParentEntityComponent.class)).parent;

                    if (parent.has(JointComponent.class)) {
                        Joint j = (parent.get(JointComponent.class)).joint;
                        if (j != null) {
                            ShmupGame.getWorld().destroyJoint(j);
                            (parent.get(JointComponent.class)).joint = null;
                        }
                        e.removeComponent(JointComponent.class);
                    }

                    if (e.has(SteeringComponent.class)) {
                        e.removeComponent(SteeringComponent.class);
                    }

                }
            }
        }
    }
}