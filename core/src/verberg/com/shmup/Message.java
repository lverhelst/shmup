package verberg.com.shmup;

import ecs.Entity;

/**
 * Created by Orion on 11/24/2015.
 */
public class Message {
    public Entity entity;
    public INTENT intent;

    public Message(Entity e, INTENT i){
        this.entity = e;
        this.intent = i;
    }
}
