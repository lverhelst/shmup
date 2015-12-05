package verberg.com.shmup;

import java.util.ArrayList;
import java.util.HashMap;

import ecs.SubSystem;

/**
 * Created by Orion on 11/24/2015.
 */
public class MessageManager {
    //TODO create a pool for messages? (lower the number of new operator)
    private HashMap<Class, SubSystem> systems;
    private ArrayList<Message> messages;

    public MessageManager() {
        systems = new HashMap<Class, SubSystem>();
        messages = new ArrayList<Message>();
    }

    public void addSystem(Class systemType, SubSystem system) {
        if(!systems.containsKey(systemType))
            systems.put(systemType, system);
    }

    public void addMessage(Class system, Parameter ... list){
        messages.add(new Message(system, list));
    }

    public void clearMessages(){
        messages.clear();
    }

    public void update() {
        for(Message msg: messages) {
            SubSystem system = systems.get(msg.getSystem());
            system.processMessage(msg.getParameters());
        }
        clearMessages();
    }
}