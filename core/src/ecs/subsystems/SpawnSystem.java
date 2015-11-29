package ecs.subsystems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

import Factories.CarFactory;
import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

import AI.AI;
/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnSystem {
    static CarFactory carFactory = new CarFactory();
    static ArrayList<Rectangle> spawnPoints = new ArrayList<Rectangle>();
    static int newSpawn = 0; //TODO: replace with something better

    public void update(Entity entity){
        if(entity.has(HealthComponent.class)){
            if(entity.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                //hc.cur_health = hc.max_health;
                if(entity.recursiveHas(PhysicalComponent.class)){
                    //We know only car bodies are root
                    //Car bodies are the point we want for the respawn message, cause we can traverse it's tree at that point
                    //At some point we may want a type component to determine if an entity is part of a car/bullet/someother type/boat
                    if(entity.get(PhysicalComponent.class).isRoot){
                        //Rebuild the car from the definition

                        newSpawn = (newSpawn + 1) % spawnPoints.size();
                        carFactory.applyLifeTimeWarranty(entity, spawnPoints.get(newSpawn));
                    }
                }
            }
        }
    }

    public void addSpawnPoint(Rectangle spawn) {
        spawnPoints.add(spawn);
    }
}