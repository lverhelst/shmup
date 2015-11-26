package verberg.com.shmup;

import ecs.Entity;
import ecs.subsystems.SteeringSystem;

/**
 * Created by emery on 2015-11-25.
 */
public class SteeringMessage implements Message {
    private static SteeringSystem ss = new SteeringSystem();
    public Entity entity;
    public INTENT intent;

    public SteeringMessage(Entity e, INTENT i) {
        this.entity = e;
        this.intent = i;
    }

    public void submitMessage() {
        ss.update(entity, intent);
    }
}