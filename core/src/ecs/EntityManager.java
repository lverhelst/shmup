package ecs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import ecs.components.ChildEntityComponent;
import ecs.components.ParentEntityComponent;

/**
 * Created by Orion on 12/13/2015.
 */
public class EntityManager {

    HashMap<UUID, String> entityNames;
    ArrayList<UUID> entityList;
    HashMap<UUID, Entity> entitiesMap;
    HashMap<Class, HashMap<UUID, ? extends Component>> componentStores;
    private static EntityManager instance;


    protected EntityManager(){
        entityList = new ArrayList<UUID>();
        entityNames = new HashMap<UUID, String>();
        entitiesMap = new HashMap<UUID, Entity>();
        componentStores = new HashMap<Class, HashMap<UUID, ? extends Component>>();
    }

    public static EntityManager getInstance(){
        if(instance == null){
            instance = new EntityManager();
        }
        return instance;
    }

    public void addEntity(Entity e){
        System.out.println(e.getName() + " " + e.uuid);
        entitiesMap.put(e.uuid, e);
    }

    public Entity getEntity(UUID uid){
        if(entitiesMap.containsKey(uid))
            return entitiesMap.get(uid);
        throw new IllegalArgumentException("No entites with ID" + uid.toString() + " exist");
    }

    public ArrayList<Entity> entityList(){
        //lets just waste all the memory here
        return new ArrayList<Entity>(entitiesMap.values());
    }



    public <T extends Component> boolean hasComponent(UUID entity, Class<T> componentClass){

        HashMap<UUID, ? extends Component> store = componentStores.get(componentClass);

        if(store == null)
            return false;
        else
            return store.containsKey(entity);
    }

    public boolean hasComponents(UUID entity, Class ... components){
        HashMap<UUID, ? extends Component> store;
        int i = 0;
        for(Class c : components){
            store = componentStores.get(c);
            if(store.containsKey(entity)){
                i++;
            }
        }
        return components.length == i;
    }



    public <T extends Component> T getComponent(UUID entity, Class<T> componentClass){
        HashMap<UUID, ? extends Component> store = componentStores.get(componentClass);
        if(store == null)
            throw new IllegalArgumentException("No components of type" + componentClass.getName() + " exist");
        T result = (T)store.get(entity);
        if(result == null){
            throw new IllegalArgumentException("No components of type" + componentClass.getName() + " exist for entity " + entity.toString());
        }
        return result;
    }

    public <T extends Component> void addComponent(UUID entity, T component){
        HashMap<UUID, ? extends Component> store = componentStores.get(component.getClass());
        if(store == null) {
            store = new HashMap<UUID, T>();
            componentStores.put(component.getClass(), store);
        }
            ;//removing a component that doesn't exist. Not a big deal
        ((HashMap<UUID,T>)store).put(entity, component);
    }



    public <T extends Component> void removeComponent(UUID entity, Class<T> componentClass){
        HashMap<UUID, ? extends Component> store = componentStores.get(componentClass);
        if(store == null)
            ;//removing a component that doesn't exist. Not a big deal
        store.remove(entity);
    }

    public <T extends Component> ArrayList<T> getAllComponentsOfType(Class<T> componentClass){
        HashMap<UUID, ? extends Component> store = componentStores.get(componentClass);
        if(store == null)
            return new ArrayList<T>();
        return (ArrayList<T>)store.values();
    }

    public <T extends Component> ArrayList<UUID> getEntitiesWithComponent(Class<T> componentClass){
        HashMap<UUID, ? extends Component> store = componentStores.get(componentClass);
        if(store == null)
            return new ArrayList<UUID>();
        return new ArrayList<UUID>(store.keySet());
    }

    /***
     * WARNING: bad order of complexity is bad
     * @param componentClass
     * @return
     */
    public ArrayList<UUID> getEntitiesWithComponents(Class ... componentClass){
        HashMap<UUID, Integer> entities = new HashMap<UUID, Integer>();


        for (Class c : componentClass) {
            for(UUID key : componentStores.get(c).keySet()){
                Integer val = 1;
                if(entities.containsKey(key)){
                    val = entities.get(key);
                    val += 1;
                }
                entities.put(key,val);
            }
        }

        ArrayList<UUID> arrEntities = new ArrayList<UUID>();
        for(UUID key : entities.keySet()){
            if(entities.get(key) == componentClass.length){
                arrEntities.add(key);
            }
        }
        return arrEntities;
    }

    public void disposeEntity(UUID entity){

        //be sure to start with child entities
        //this makes sure we destroy joints before bodies
        if(hasComponent(entity, ChildEntityComponent.class)){
           for(Entity e : getComponent(entity, ChildEntityComponent.class).childList){
               disposeEntity(e.uuid);
           }
        }

        for(HashMap<UUID, ? extends Component> components : componentStores.values()){
            Component  c = components.get(entity);
            if(c != null) {
                c.dispose();
                components.remove(entity);
            }
        }

        entityList.remove(entity);
        entityNames.remove(entity);
        entitiesMap.remove(entity);
    }


    public UUID createEntity(){
        UUID uid = UUID.randomUUID();
        entityList.add(uid);
        entityNames.put(uid, uid.toString());
        return uid;
    }

    public UUID createEntity(String name){
        UUID uid = UUID.randomUUID();
        entityList.add(uid);
        entityNames.put(uid,name);
        return uid;
    }

    public UUID createEntity(UUID uuid, String name){
        entityList.add(uuid);
        entityNames.put(uuid,name);
        return uuid;
    }

    public <T extends Component> UUID createEntity(T ... components){
        UUID uid = UUID.randomUUID();
        entityList.add(uid);
        entityNames.put(uid,uid.toString());
        for(Component comp : components){
            addComponent(uid, comp);
        }
        return uid;
    }

    public <T extends Component> UUID createEntity(String name, T ... components){
        UUID uid = UUID.randomUUID();
        entityList.add(uid);
        entityNames.put(uid,name);
        for(Component comp : components){
            addComponent(uid, comp);
        }
        return uid;
    }

    public void setName(UUID entity, String name){
        entityNames.put(entity, name);
    }

    public String getName(UUID entity){
        if(entityNames.containsKey(entity))
            return entityNames.get(entity);
        return "Unnamed entity/Entity may not exist";
    }

    public ArrayList<UUID> getEntitiesList(){
        return entityList;
    }




}
