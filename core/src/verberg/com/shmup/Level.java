package verberg.com.shmup;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

/**
 * Created by Orion on 11/17/2015.
 */
public class Level {
    //move to definitions class
    final int PPM = 5;

    private HashMap<String, Body> bodies;
    public World world;

    public void create(World world) {
        this.world = world;

        loadLevel(Gdx.files.internal("defaultLevel"));
    }

    public void loadLevel(FileHandle level) {
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(level);
        JsonValue map = json.get("level");
        JsonValue blocks = map.get("blocks");
        Body body;

        for(JsonValue block : blocks){
            //String name = block.get("name").asString();
            float[] pos = block.get("location").asFloatArray();
            float[] size = block.get("size").asFloatArray();
            float friction = block.get("friction").asFloat();
            float density = block.get("density").asFloat();
            BodyDef.BodyType type = block.get("dynamic").asBoolean() ? BodyType.DynamicBody : BodyType.StaticBody;

            body = createBox(pos[0], pos[1], size[0], size[1], friction, density, type);
            //bodies.put(name, body);
        }

        Body circle = createCircle(160, 155, 5, 1, 1, BodyType.StaticBody);
        Body blade = createBox(165, 154.5f, 150, 2, 1, 1, BodyType.DynamicBody);

        RevoluteJointDef joint = new RevoluteJointDef();
        joint.bodyA = circle;
        joint.bodyB = blade;
        joint.localAnchorB.set(0,0);
        joint.enableMotor = true;
        joint.maxMotorTorque = 100000.0f;
        joint.motorSpeed = 2f;
        world.createJoint(joint);

    }

    public Body createBox(float x, float y, float w, float h, float friction, float density, BodyType type) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = box;
        fixture.density = density;
        fixture.friction = friction;

        body.createFixture(fixture);
        box.dispose();

        return body;
    }

    public Body createCircle(float x, float y, float r, float friction, float density, BodyType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(new Vector2(x, y));

        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = circle;
        fixture.density = density;
        fixture.friction = friction;

        body.createFixture(fixture);
        circle.dispose();

        return body;
    }
}