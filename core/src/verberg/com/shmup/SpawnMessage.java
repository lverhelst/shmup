package verberg.com.shmup;

import ecs.Entity;
import ecs.subsystems.SpawnSystem;

/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnMessage implements Message {
    static SpawnSystem ss = new SpawnSystem();
    Entity e;

    public SpawnMessage(Entity e){
        this.e = e;
    }


    @Override
    public void submitMessage() {
        ss.update(e);
    }
}
