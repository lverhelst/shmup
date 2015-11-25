package ecs.components;

import ecs.Component;
import ecs.Entity;

/**
 * Created by Orion on 11/23/2015.
 * Used to set a child entity in a component
 *
 */
public class ChildEntityComponent extends Component {
    //TODO: replace with entity ID
    //TODO: make into a list of entity ids instead of a single entity
    Entity e;

    public ChildEntityComponent(Entity e){
        this. e = e;
    }
}
