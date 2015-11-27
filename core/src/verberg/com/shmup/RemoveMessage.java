package verberg.com.shmup;

import ecs.Entity;
import ecs.subsystems.RemovalSystem;

/**
 * Created by Orion on 11/26/2015.
 */
public class RemoveMessage implements Message {
    Entity e;
    INTENT i;
    static RemovalSystem rs = new RemovalSystem();

    public RemoveMessage(Entity e, INTENT intent){
        this.e = e;
        this.i = intent;
    }

    @Override
    public void submitMessage() {
        rs.update(e,i);
    }
}
