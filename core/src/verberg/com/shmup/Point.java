package verberg.com.shmup;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by emery on 2015-12-01.
 */
public class Point {
    public Vector2 position;
    public String type;
    public String subType;

    public void create(String type, String subType, float x, float y) {
        this.type = type;
        this.subType = subType;

        position = new Vector2(x,y);
    }
}