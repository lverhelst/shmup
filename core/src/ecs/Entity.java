package ecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import ecs.components.ChildEntityComponent;
import ecs.components.ParentEntityComponent;
import verberg.com.shmup.ShmupGame;

/**
 * Created by Orion on 11/22/2015.
 * TODO: Entity Manager
 */
public class Entity {
    String name;
    UUID uuid;


    public Entity(){
        this.uuid = UUID.randomUUID();
        this.name = uuid.toString();
        //Add to entity list in game
        EntityManager.getInstance().createEntity(uuid,name);
        EntityManager.getInstance().addEntity(this);
    }

    public Entity(String name){
        this.uuid = UUID.randomUUID();
        this.name = name;
        //Add to entity list in game
        EntityManager.getInstance().createEntity(uuid,name);
        EntityManager.getInstance().addEntity(this);
    }

    public <T extends Component> Entity(String name, T ... components){

        this.uuid = UUID.randomUUID();
        this.name = name;
        //Add to entity list in game
        EntityManager.getInstance().createEntity(uuid, name);
        for(Component comp : components) {
            EntityManager.getInstance().addComponent(uuid, comp);
        }
        EntityManager.getInstance().addEntity(this);
    }


    public <T extends Component> Entity(T ... components){
        this.uuid = UUID.randomUUID();
        this.name = uuid.toString();
        //Add to entity list in game
        EntityManager.getInstance().createEntity(uuid, name);
        for(Component comp : components) {
            EntityManager.getInstance().addComponent(uuid, comp);
        }
        EntityManager.getInstance().addEntity(this);
    }


    public boolean recursiveHas(Class<? extends Component> c){
        if(has(c)){
            return true;
        }
        if(has(ParentEntityComponent.class)){
            ParentEntityComponent pec = EntityManager.getInstance().getComponent(uuid, ParentEntityComponent.class);
            return pec.parent.recursiveHas(c);
        }
        return false;
    }

    public <T extends Component> T  recursiveGet(Class<? extends Component> c){
        if(has(c)){
            return (T)get(c);
        }
        if(has(ParentEntityComponent.class)){
            ParentEntityComponent pec = get(ParentEntityComponent.class);
            return (T)pec.parent.recursiveGet(c);
        }
        return null;
    }

    public boolean has(Class<? extends Component> c){

        return EntityManager.getInstance().hasComponent(uuid,c);
    }

    public <T extends Component> T get(Class<T> c)
    {
        T result = EntityManager.getInstance().getComponent(uuid, c);

        return result;
    }

    /**
     * Will overwrite a component of the same class
     * @param component: The component to add to the entity
     */
    public <T extends Component> void addComponent(T component){
        EntityManager.getInstance().addComponent(uuid, component);
    }

    public void removeComponent(Class c){
        if(has(c)) {
            EntityManager.getInstance().removeComponent(uuid, c);
        }
    }


    public void removeAllComponents(){
        /*ArrayList<Component> removeThis = new ArrayList<Component>();
        for(Component c : components.values()){
            c.dispose();
            removeThis.add(c);
        }
        for(Component rm : removeThis){
            components.remove(rm.getClass());
        }*/
        EntityManager.getInstance().disposeEntity(uuid);

    }


    public void recursiveRemove(Class c){
        if(has(ChildEntityComponent.class)){
            ChildEntityComponent cec = get(ChildEntityComponent.class);
            for(Entity e : cec.childList){
                e.recursiveRemove(c);
            }
        }
        if(has(c))
            removeComponent(c);
    }

    public String getName() {
        return name;
    }
    public UUID getUuid() {return uuid; }
}
