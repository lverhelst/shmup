package Level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

import verberg.com.shmup.*;

/**
 * Created by emery on 2015-12-01.
 */
public class Node {
    private Vector2 position;
    private boolean navigation;
    private boolean spawn;
    private boolean powerup;

    public void create(JsonValue node) {
        float[] pos = node.get("location").asFloatArray();
        String[] types = node.get("type").asStringArray();

        for(String type : types) {
            this.powerup |= type.equals("powerup");
            this.spawn |= type.equals("spawn");
            this.navigation |= type.equals("nav");
        }

        position = new Vector2(pos[0],pos[1]);
    }

    public void create(float x, float y, boolean navigation, boolean spawn, boolean powerup) {
        position = new Vector2(x,y);

        this.navigation = navigation;
        this.spawn = spawn;
        this.powerup = powerup;
    }

    public boolean isNavigation() { return navigation; }
    public boolean isSpawn() { return spawn; }
    public boolean isPowerup() { return powerup; }

    public Vector2 getPosition() { return position; }

    public void setPosition(Vector2 position) { this.position = position; }
    public void setPosition(float x, float y) { this.position.set(x, y); }

    public void setNavigation(boolean navigation) { this.navigation = navigation; }
    public void setSpawn(boolean spawn) { this.spawn = spawn; }
    public void setPowerup(boolean powerup) { this.navigation = powerup; }
}