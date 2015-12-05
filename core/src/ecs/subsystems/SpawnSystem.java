package ecs.subsystems;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

import Factories.CarFactory;
import ecs.Entity;
import ecs.SubSystem;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

import AI.AI;
import verberg.com.shmup.Node;
import verberg.com.shmup.Parameter;

/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnSystem implements SubSystem{
    static CarFactory carFactory = new CarFactory();
    static ArrayList<Node> spawnPoints = new ArrayList<Node>();
    static int newSpawn = 0; //TODO: replace with something better

    public void processMessage(Parameter ... list) {
        if(list[0].getType() == Entity.class) {
            Entity e = (Entity)list[0].getValue();
            spawn(e);
        } else if(list[0].getType() == Node.class) {
            Node spawn = (Node)list[0].getValue();
            addSpawnPoint(spawn);
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
        }
    }

    public void addSpawnPoint(Node spawn) {
        spawnPoints.add(spawn);
    }
}