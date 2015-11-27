package verberg.com.shmup;

import ecs.Entity;
import ecs.subsystems.WeaponSystem;

/**
 * Created by emery on 2015-11-25.
 */
public class WeaponMessage implements Message {
    private static WeaponSystem ws = new WeaponSystem();
    public Entity entity;

    public WeaponMessage(Entity e) {
        this.entity = e;
    }

    public void submitMessage() {
        ws.update(entity);
    }
}
