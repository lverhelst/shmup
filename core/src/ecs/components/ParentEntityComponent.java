package ecs.components;

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

    public ParentEntityComponent(Entity parent){
        this.parent = parent;
    }

    @Override
    public void dispose(){
        //do not dispose of parent entity
        //since the disposal iterates down the tree, we would have
        //reached this from the parent already
        //trying to dispose or remove the parent entity's components
        //causes an infinite loop > stack overflow
    }

}
