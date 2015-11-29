package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Fixture;

import Factories.CarFactory;
import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;

import AI.AI;
/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnSystem {
    CarFactory carFactory = new CarFactory();

    public void update(Entity entity){
        if(entity.has(HealthComponent.class)){
            HealthComponent hc = ((HealthComponent)entity.get(HealthComponent.class));
            if(hc.getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                //hc.cur_health = hc.max_health;

                if(entity.recursiveHas(PhysicalComponent.class)){
                    PhysicalComponent pc = (PhysicalComponent) entity.get(PhysicalComponent.class);
                    //We know only car bodies are root
                    //Car bodies are the point we want for the respawn message, cause we can traverse it's tree at that point
                    //At some point we may want a type component to determine if an entity is part of a car/bullet/someother type/boat
                    if(pc.isRoot){
                        //Rebuild the car from the definition
                        carFactory.applyLifeTimeWarranty(entity);
                    }
                }
            }
        }
    }

}
