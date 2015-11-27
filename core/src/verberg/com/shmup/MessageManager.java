package verberg.com.shmup;

import java.util.ArrayList;

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

    public static void update() {
        for(Message msg: messages) {
            msg.submitMessage();

        }

        clearMessages();
    }
}