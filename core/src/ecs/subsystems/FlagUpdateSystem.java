package ecs.subsystems;

import java.util.ArrayList;
import java.util.UUID;

import MessageManagement.INTENT;
import ecs.Entity;
import ecs.EntityManager;
import ecs.SubSystem;
import ecs.components.FlagComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

/**
 * Created by Orion on 1/7/2016.
 */
public class FlagUpdateSystem implements SubSystem {

    @Override
    public void processMessage(INTENT intent, Object... parameters) {
        //TODO make messagable
    }

    public void update(ArrayList<UUID> entities){
        for(UUID e_uuid : entities){
            Entity e = EntityManager.getInstance().getEntity(e_uuid);
            if(e.has(FlagComponent.class)){
                FlagComponent fc = e.get(FlagComponent.class);
                if(fc.getHeldBy() != null){
                    Entity flagHolder = EntityManager.getInstance().getEntity(fc.getHeldBy());
                    if(flagHolder.has(HealthComponent.class) && flagHolder.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                        fc.setHeldBy(null);
                    }else if(e.has(PhysicalComponent.class) && flagHolder.has(PhysicalComponent.class)){
                        //move flag to holder position
                        e.get(PhysicalComponent.class).getBody().setTransform(flagHolder.get(PhysicalComponent.class).getBody().getPosition()
                                                                                ,(float)( flagHolder.get(PhysicalComponent.class).getBody().getAngle() + Math.PI)) ;
                    }
                }
                //kill velocity
                if(e.has(PhysicalComponent.class)){
                    e.get(PhysicalComponent.class).getBody().setLinearVelocity(0f,0f);
                    e.get(PhysicalComponent.class).getBody().setAngularVelocity(0f);
                }

            }
        }

    }

}
