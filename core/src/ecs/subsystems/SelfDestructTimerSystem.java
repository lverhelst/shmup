package ecs.subsystems;

import java.util.ArrayList;
import java.util.UUID;

import MessageManagement.INTENT;
import ecs.Entity;
import ecs.EntityManager;
import ecs.SubSystem;
import ecs.components.SelfDestructTimerComponent;

/**
 * Created by Orion on 1/22/2016.
 */
public class SelfDestructTimerSystem implements SubSystem{


    public void update(ArrayList<UUID> entities) {
        for (UUID ent : entities) {
            Entity e = EntityManager.getInstance().getEntity(ent);
            if(e.has(SelfDestructTimerComponent.class)){
                if(e.get(SelfDestructTimerComponent.class).isDead()){
                    e.removeAllComponents();
                }
            }
        }
    }

    @Override
    public void processMessage(INTENT intent, Object... parameters) {
        //do nothing
    }
}
