package ecs;

import java.util.HashMap;
import java.util.UUID;

import ecs.components.ChildEntityComponent;
import ecs.components.ParentEntityComponent;
import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/22/2015.
 * TODO: Entity Manager
 */
public class Entity {
    //Tried a Hashmap of <Class, ? Extends component> but it wouldn't match a steeringcomponent.class to steeringcomponent.class. No clue why.
    HashMap<Class, ? extends Component> components = new HashMap<Class,Component>();
    String name;
    UUID uuid;


    public Entity(){
        this.uuid = UUID.randomUUID();
        this.name = uuid.toString();
        //Add to entity list in game
        Game.addEntity(this);
    }

    public Entity(String name){
        this.uuid = UUID.randomUUID();
        this.name = name;
        //Add to entity list in game
        Game.addEntity(this);
    }

    public <T extends Component> Entity(String name, T ... components){
        for(T com : components){
            ((HashMap<Class, T>)this.components).put(com.getClass(), com);
        }
        this.uuid = UUID.randomUUID();
        this.name = name;
        //Add to entity list in game
        Game.addEntity(this);
    }


    public <T extends Component> Entity(T ... components){
        for(T com : components){
            ((HashMap<Class, T>)this.components).put(com.getClass(), com);
        }
        this.uuid = UUID.randomUUID();
        this.name = uuid.toString();
        //Add to entity list in game
        Game.addEntity(this);
    }


    public boolean recursiveHas(Class<? extends Component> c){
        if(components.containsKey(c)){
            return true;
        }
        if(components.containsKey(ParentEntityComponent.class)){
            ParentEntityComponent pec = (ParentEntityComponent)components.get(ParentEntityComponent.class);
            return pec.parent.recursiveHas(c);
        }
        return false;
    }

    public <T extends Component> T  recursiveGet(Class<? extends Component> c){
        if(components.containsKey(c)){
            return (T)this.components.get(c);
        }
        if(components.containsKey(ParentEntityComponent.class)){
            ParentEntityComponent pec = (ParentEntityComponent)components.get(ParentEntityComponent.class);
            return (T)pec.parent.recursiveGet(c);
        }
        return null;
    }

    public boolean has(Class<? extends Component> c){
        return this.components.containsKey(c);
    }

    public <T extends Component> T get(Class c){
        return (T)this.components.get(c);
    }

    /**
     * Will overwrite a component of the same class
     * @param component: The component to add to the entity
     */
    public <T extends Component> void addComponent(T component){
        ((HashMap<Class,T>)components).put(component.getClass(), component);
    }

    public void removeComponent(Class c){
        if(has(c))
            this.components.remove(c);
    }

    public void recursiveRemove(Class c){
        if(has(c))
            removeComponent(c);
        if(has(ChildEntityComponent.class)){
            ChildEntityComponent cec = get(ChildEntityComponent.class);
            for(Entity e : cec.childList){
                e.recursiveRemove(c);
            }
        }
    }
}
