package ecs.subsystems;

import java.util.ArrayList;
import java.util.List;

import ecs.Entity;
import ecs.components.ControlledComponent;

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