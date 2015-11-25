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


}
