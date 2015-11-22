package systems;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Random;

import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/17/2015.
 *
 */
public class WorldSystem {
    //NOTE: Not sure if this was your intended use... but can be moved elsewhere if needed

    //move to definitions class
    final int PPM = 5;
    public World world;

    public void create(World world) {
        this.world = world;
        Random rand = new Random();

        loadWorld(Gdx.files.internal("defaultLevel"));
    }

    public void loadWorld(FileHandle level) {
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(level);
        JsonValue map = json.get("level");
        JsonValue blocks = map.get("blocks");

        for(JsonValue block : blocks){
            float[] pos = block.get("location").asFloatArray();
            float[] size = block.get("size").asFloatArray();

            createBox(pos[0], pos[1], size[0], size[1], 1, BodyDef.BodyType.StaticBody);
        }
    }

    public Body createBox(float x, float y, float w, float h, float friction, BodyDef.BodyType type) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        //this is a fix because you car is stupid and wheels spawn outside the level :p
        x -= 64f;
        y -= 64f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);
        body.createFixture(box, friction);
        box.dispose();

        return body;
    }
}
