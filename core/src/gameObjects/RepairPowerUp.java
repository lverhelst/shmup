package gameObjects;


import com.badlogic.gdx.physics.box2d.Joint;

import java.util.ArrayList;

import Factories.CarFactory;
import ecs.Entity;
import ecs.components.ChildEntityComponent;
import ecs.components.HealthComponent;
import ecs.components.JointComponent;

/**
 * Created by Orion on 12/1/2015.
 */
public class RepairPowerUp extends PowerUp {

    ArrayList<Entity> jointsToRepair;

    public RepairPowerUp(){
        super(ChildEntityComponent.class);
        jointsToRepair = new ArrayList<Entity>();
    }

    @Override
    public void update() {
        for(Entity s : jointsToRepair){
            CarFactory.insuranceClaimForJoint(s,pickedUpEntity);
        }
        jointsToRepair.clear();
        super.update();
    }

    @Override
    public void applyToEntity(Entity e) {
        this.pickedUpEntity = e;
        this.timePickedUp = System.currentTimeMillis();
        this.timeSpawned = System.currentTimeMillis();
        this.destroyBodyOnUpdate = true;

        if(e.has(HealthComponent.class)){
            if(e.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD)
                return;
        }

        //this applies a bit of what we know about who gets powerups
        //A) powerups only collide with car bodies
        //B) car bodies have a child component that has a list of joint entites
        //C) if a joint entity has a null joint, rebuild that wheel
        if(e.has(forComponent)){
            for(Entity jointEntity : e.get(ChildEntityComponent.class).childList){
                //currently unnecessary safety check
                if(jointEntity.has(JointComponent.class))
                {
                    if(jointEntity.get(JointComponent.class).joint == null){
                        jointsToRepair.add(jointEntity);
                    }
                }
            }
        }
    }
}
