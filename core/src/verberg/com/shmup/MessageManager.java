package verberg.com.shmup;

import java.util.ArrayList;

import ecs.Entity;
import ecs.components.ParentEntityComponent;

/**
 * Created by Orion on 11/24/2015.
 */
public class MessageManager {

    static ArrayList<Message> messages = new ArrayList<Message>();

    public static  void addMessage(Message m){
        messages.add(m);
    }

    public static  void clearMessages(){
        messages.clear();
    }

    public static Message[] getMessages(){
        return messages.toArray(new Message[messages.size()]);
    }

    public static boolean hasMessage(Entity e, INTENT i){
        for(Message m : messages){
            if(m.entity.equals(e) && m.intent == i)
                return true;
        }
        return false;
    }


}
