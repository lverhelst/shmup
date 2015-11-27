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
                hc.cur_health = hc.max_health;

                //re-add all the other components (render)?
                //Set any physical components to be a not sensor
                //spawn in some location if
                //TODO:
                if(entity.recursiveHas(PhysicalComponent.class)){
                    for(Fixture f : ((PhysicalComponent) entity.get(PhysicalComponent.class)).getBody().getFixtureList()) {
                        f.setSensor(false);
                    }
                }
            }
        }
    }

}
