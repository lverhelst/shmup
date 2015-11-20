package systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.Gdx;

import java.util.Random;

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

        for(int i = 0; i < 100; i++) {
            createBox(i * 3, (i%2 == 0 ? 0 : 280), 10/PPM, 10/PPM);
            createBox((i%2 == 0 ? 0 : 280), i * 3, 10/PPM, 10/PPM);

        }
    }

    public void createGround(float x, float y, float w, float h) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);
        body.createFixture(box, 0.0f);
        box.dispose();
    }

    public Body createBox(float x, float y, float w, float h) {
       // y = Gdx.graphics.getHeight()- y;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));

        Body body = world.createBody(bodyDef);
        PolygonShape box = new PolygonShape();
        FixtureDef fixture = new FixtureDef();
        fixture.shape = box;
        fixture.density = 1.0f;
        fixture.friction = 0.1f;

        box.setAsBox(w, h);
        body.createFixture(fixture);
        box.dispose();

        return body;
    }
}
