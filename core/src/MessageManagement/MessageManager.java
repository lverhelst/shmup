package MessageManagement;

import java.util.ArrayList;
import java.util.HashMap;

import ecs.SubSystem;

/**
 * Created by Orion on 11/24/2015.
 */
public class MessageManager {

    //TODO create a pool for messages? (lower the number of new operator)
    private HashMap<INTENT, ArrayList<SubSystem>> intentList;
    private ArrayList<Message> messages;
    private static MessageManager instance;


    protected MessageManager() {
        intentList = new HashMap<INTENT, ArrayList<SubSystem>>();
        messages = new ArrayList<Message>();
    }

    public static synchronized MessageManager getInstance(){
        if(instance == null){
            instance = new MessageManager();
        }
        return instance;
    }

    public void registerSystem(INTENT intent, SubSystem system) {
        if(!intentList.containsKey(intent)) {
            intentList.put(intent, new ArrayList<SubSystem>());
        }

        if(!intentList.get(intent).contains(system)) {
            intentList.get(intent).add(system);
        }
    }

    public void addMessage(INTENT intent, Object ... list){
        messages.add(new Message(intent, list));
    }

    public void clearMessages(){
        messages.clear();
    }

    public void update() {
        for(Message msg: messages) {
            for(SubSystem system: intentList.get(msg.getIntent())) {
                system.processMessage(msg.getIntent(), msg.getParameters());
            }
        }
        clearMessages();
    }
}