package ecs.subsystems;

import com.badlogic.gdx.InputAdapter;

import java.util.ArrayList;

import Input.MyInputAdapter;
import ecs.Entity;
import ecs.components.PlayerControlledComponent;

/**
 * Created by Orion on 11/24/2015.
 * I think this is too many layers of abstraction here
 */
public class InputSystem {

    public void update(ArrayList<Entity> entities){
        for(Entity e : entities){
            if(e.has(PlayerControlledComponent.class)){
                MyInputAdapter.getIntentsForPlayerControlledEntity(e);
            }
        }
    }
}
