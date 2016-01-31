package ecs.subsystems;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

import Factories.Factory;
import MessageManagement.INTENT;
import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ChildEntityComponent;
import ecs.components.ControlledComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

import Level.Point;

/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnSystem implements SubSystem{
    static Factory factory;
    static ArrayList<Point> spawnPoints = new ArrayList<Point>();
    static int newSpawn = 0; //TODO: replace with something better
    static int spawnsAdded = 0;

    public SpawnSystem(String gameMode){
        factory = new Factory(gameMode);
    }


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
                        newSpawn = (++newSpawn) % spawnPoints.size();
                        //load into entity, since the passed in entity gets destroyed
                        Entity toTrans = factory.applyLifeTimeWarranty(e);
                        transformEntity(toTrans, spawnPoints.get(newSpawn));
                        factory.addComponentsForGameMode(toTrans);
                    }
                }
            }else{
                if(e.get(PhysicalComponent.class).isRoot){
                    System.out.println("Repositioning car");
                    newSpawn = (++newSpawn) % spawnPoints.size();
                    transformEntity(e, spawnPoints.get(newSpawn));
                    factory.addComponentsForGameMode(e);
                }
            }


        }else if(e.has(PhysicalComponent.class)){
            newSpawn = (++newSpawn) % spawnPoints.size();
            transformEntity(e, spawnPoints.get(newSpawn));
            if(e.get(PhysicalComponent.class).isRoot){
                factory.addComponentsForGameMode(e);
            }

        }
    }

    /***
     * Recursively move entity
     * @param toTransform The entity (or entity heirarchy) to move
     * @param toPoint Target point to move to
     */
    private void transformEntity(Entity toTransform, Point toPoint){

        if(toTransform.has(PhysicalComponent.class)){
            System.out.println(toTransform.get(PhysicalComponent.class).getBody().getJointList().size + " " + toTransform.getName() + " " + toPoint.position.toString());
            toTransform.get(PhysicalComponent.class).getBody().setTransform(toPoint.position, toTransform.get(PhysicalComponent.class).getAngleRadians());
        }
        if(toTransform.has(ChildEntityComponent.class)){
            for(Entity uid : toTransform.get(ChildEntityComponent.class).childList){
                transformEntity(uid, toPoint);
            }
        }
    }


    public void addSpawnPoint(Point spawn) {
        spawnPoints.add(spawn);
    }

    public Vector2 getSpawnPoint(){
        return spawnPoints.get(new Random().nextInt(spawnPoints.size())).position;
    }
}
