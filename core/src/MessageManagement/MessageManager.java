package MessageManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;

import ecs.SubSystem;

/**
 * Created by Orion on 11/24/2015.
 */
public class MessageManager {

    //TODO create a pool for messages? (lower the number of new operator)
    private HashMap<INTENT, ArrayList<SubSystem>> intentList;
    private ArrayList<Message> messages;
    private ArrayList<Message> duringIteration;
    private static MessageManager instance;
    private ListIterator<Message> iter;
    private boolean iterating;


    protected MessageManager() {
        intentList = new HashMap<INTENT, ArrayList<SubSystem>>();
        messages = new ArrayList<Message>();
        duringIteration = new ArrayList<Message>();
        iterating = false;
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
        if(!iterating)
            messages.add(new Message(intent, list));
        else{
            duringIteration.add(new Message(intent, list));
        }

    }

    public void clearMessages(){
        messages.clear();
    }

    public void update() {
        iterating = true;
        for(Message msg : messages){
            for(SubSystem system: intentList.get(msg.getIntent())) {
                system.processMessage(msg.getIntent(), msg.getParameters());
            }
        }
        iterating = false;
        messages.clear();
        if(duringIteration.size() > 0){
            messages.addAll(duringIteration);
        }
        duringIteration.clear();
    }

    public void clear(){
        intentList = new HashMap<INTENT, ArrayList<SubSystem>>();
        messages = new ArrayList<Message>();
        duringIteration = new ArrayList<Message>();
        iterating = false;
    }
}