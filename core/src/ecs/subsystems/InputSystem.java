package ecs.subsystems;

import java.util.ArrayList;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ControlledComponent;
import verberg.com.shmup.Parameter;

/**
 * Created by Orion on 11/24/2015.
 * I think this is too many layers of abstraction here
 */
public class InputSystem {

    public void update(ArrayList<Entity> entities){
        for(Entity e : entities){

            if(e.has(ControlledComponent.class)){
                e.get(ControlledComponent.class).ig.generateIntents(e);
            }
        }
    }
}