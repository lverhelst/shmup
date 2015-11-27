package ecs.components;

import java.util.ArrayList;

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
    public ArrayList<Entity> childList;

    public ChildEntityComponent(){
        this.childList = new ArrayList<Entity>();
    }


    public ChildEntityComponent(Entity e){
        this.childList = new ArrayList<Entity>();
        childList.add(e);
    }


}
