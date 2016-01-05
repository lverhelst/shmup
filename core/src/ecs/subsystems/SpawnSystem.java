package ecs.subsystems;

import java.util.ArrayList;

import Factories.CarFactory;
import MessageManagement.INTENT;
import ecs.Entity;
import ecs.SubSystem;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

import Level.Point;

/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnSystem implements SubSystem{
    static CarFactory carFactory = new CarFactory();
    static ArrayList<Point> spawnPoints = new ArrayList<Point>();
    static int newSpawn = 0; //TODO: replace with something better

    public void processMessage(INTENT intent, Object ... list) {
        switch (intent) {
            case SPAWN:
                Entity e = (Entity)list[0];
                spawn(e);
                break;
            case ADDSPAWN:
                Point spawn = (Point)list[0];
                addSpawnPoint(spawn);
                break;
        }
    }

    public void spawn(Entity e) {
        if(e.has(HealthComponent.class)){
            if(e.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                //hc.cur_health = hc.max_health;
                if(e.recursiveHas(PhysicalComponent.class)){
                    //We know only car bodies are root
                    //Car bodies are the point we want for the respawn message, cause we can traverse it's tree at that point
                    //At some point we may want a type component to determine if an entity is part of a car/bullet/someother type/boat
                    if(e.get(PhysicalComponent.class).isRoot){
                        //Rebuild the car from the definition

                        newSpawn = (newSpawn + 1) % spawnPoints.size();
                        carFactory.applyLifeTimeWarranty(e, spawnPoints.get(newSpawn));
                    }
                }
            }
        }else if(e.has(PhysicalComponent.class)){
            newSpawn = (newSpawn + 1) % spawnPoints.size();
            System.out.println("Spawning " + e.getName() + " at " + spawnPoints.get(newSpawn).position);
            e.get(PhysicalComponent.class).getBody().setTransform(spawnPoints.get(newSpawn).position, newSpawn);
        }
    }

    public void addSpawnPoint(Point spawn) {
        spawnPoints.add(spawn);
    }
}
