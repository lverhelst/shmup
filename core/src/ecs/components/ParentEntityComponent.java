package ecs.components;

import java.util.UUID;

import ecs.Component;
import ecs.Entity;

/**
 * Created by Orion on 11/22/2015.
 * Used to connect the Tire entities to the Car entity
 * Used to show a car entity
 */
public class ParentEntityComponent extends Component
{
    //TODO: change to entity ID
    public Entity parent;
    public UUID owner;

    public ParentEntityComponent(Entity parent, UUID owner){
        this.parent = parent;
        this.owner = owner;
    }

    @Override
    public void dispose(){
        //do not dispose of parent entity
        //since the disposal iterates down the tree, we would have
        //reached this from the parent already
        //trying to dispose or remove the parent entity's components
        //causes an infinite loop > stack overflow

        //we also use parent entity for which bullets are owned by what
        //disposing that woulc be bad
    }

}
