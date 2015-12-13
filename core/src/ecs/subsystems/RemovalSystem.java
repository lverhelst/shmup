package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Joint;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ChildEntityComponent;
import ecs.components.JointComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.SteeringComponent;
import verberg.com.shmup.Game;
import MessageManagement.INTENT;

/**
 * Created by Orion on 11/26/2015.
 */

//We can move this to the entity manager when it gets created

public class RemovalSystem implements SubSystem{

    public void processMessage(Object... list) {
        if(list[0].getClass() == Entity.class && list[1].getClass() == INTENT.class) {
            Entity e = (Entity)list[0];
            INTENT i = (INTENT)list[1];

            switch (i) {
                case REMOVE:
                    remove(e);
                    break;
                case DIED:
                    madeDead(e);
                    break;
            }
        }
    }

    public void remove(Entity e) {
        if (e.recursiveHas(PhysicalComponent.class)) {
            Body b2dBody = (e.get(PhysicalComponent.class)).getBody();
            if(b2dBody != null)
                b2dBody.getWorld().destroyBody(b2dBody);
        }
        if (e.recursiveHas(JointComponent.class)) {
            Joint j = (e.get(JointComponent.class)).joint;
            if(j != null)
                Game.getWorld().destroyJoint(j);
        }
        Game.removeEntity(e);
    }

    public void madeDead(Entity e) {
        if(e.has(PhysicalComponent.class)) {
            if (e.get(PhysicalComponent.class).isRoot) {
                //Destroy Joints
                if (e.has(ChildEntityComponent.class)) {
                    for (Entity child : e.get(ChildEntityComponent.class).childList) {
                        if (child.has(JointComponent.class)) {

                            Joint j = (child.get(JointComponent.class)).joint;
                            if (j != null) {
                                Game.getWorld().destroyJoint(j);
                                child.get(JointComponent.class).joint = null;
                            }
                            e.removeComponent(JointComponent.class);
                        }
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
                            Game.getWorld().destroyJoint(j);
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