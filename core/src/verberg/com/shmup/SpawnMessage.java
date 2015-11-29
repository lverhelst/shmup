package verberg.com.shmup;

import com.badlogic.gdx.math.Rectangle;

import ecs.Entity;
import ecs.subsystems.SpawnSystem;

/**
 * Created by Orion on 11/26/2015.
 */
public class SpawnMessage implements Message {
    static SpawnSystem ss = new SpawnSystem();
    Rectangle spawn;
    Entity e;

    public SpawnMessage(Entity e){
        this.e = e;
    }
    public SpawnMessage(Rectangle spawn) { this.spawn = spawn; }


    @Override
    public void submitMessage() {
        //TODO: Make this less hacky
        if( e != null) {
            ss.update(e);
        } else {
            ss.addSpawnPoint(spawn);
        }
    }
}