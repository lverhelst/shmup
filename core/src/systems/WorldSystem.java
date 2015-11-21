package systems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.Gdx;

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

        /*
        for(int i = 0; i < 100; i++) {
            createBox(i * 3, (i%2 == 0 ? 0 : 280), 10/PPM, 10/PPM);
            createBox((i%2 == 0 ? 0 : 280), i * 3, 10/PPM, 10/PPM);
        }
        */

        //bottom left
        createGround(0, 0, 50f, 2f);
        createGround(0, 2, 2f, 46f);
        createGround(0, 48f, 15, 2f);
        createGround(35, 48f, 15, 2f);
        createGround(50f, 0, 2f, 15);
        createGround(50f, 35, 2f, 15);

        //bottom outer wall
        createGround(52, 6.5f, 96, 2);
        createGround(148, 6.5f, 2, 8.5f);
        createGround(150, 13, 18, 2);
        createGround(168, 6.5f, 2, 8.5f);
        createGround(170, 6.5f, 96, 2);

        //bottom inner wall
        createGround(52, 41.5f, 96, 2);
        createGround(148, 35, 2, 8.5f);
        createGround(168, 35, 2, 8.5f);
        createGround(170, 41.5f, 96, 2);

        //bottom right
        createGround(266, 0, 50f, 2f);
        createGround(266, 48f, 15, 2f);
        createGround(301, 48f, 15, 2f);
        createGround(316, 0, 2f, 50);
        createGround(266, 2, 2f, 13);
        createGround(266, 35, 2f, 13);

        //left outer wall
        createGround(6.5f, 50, 2, 96);
        createGround(6.5f, 146, 8.5f, 2);
        createGround(13, 148, 2, 18);
        createGround(6.5f, 166, 8.5f, 2);
        createGround(6.5f, 168, 2, 96);

        //left inner wall
        createGround(41.5f, 50, 2, 96);
        createGround(35, 146, 8.5f, 2);
        createGround(35, 166, 8.5f, 2);
        createGround(41.5f, 168, 2, 96);

        //right inner wall
        createGround(272.5f, 50, 2, 96);
        createGround(272.5f, 146, 8.5f, 2);
        createGround(272.5f, 166, 8.5f, 2);
        createGround(272.5f, 168, 2, 96);

        //right outer wall
        createGround(307.5f, 50, 2, 96);
        createGround(301, 148, 2, 18);
        createGround(301, 146, 8.5f, 2);
        createGround(301, 166, 8.5f, 2);
        createGround(307.5f, 168, 2, 96);

        //top left
        createGround(0, 312, 50f, 2f);
        createGround(0, 264, 15, 2f);
        createGround(35, 264, 15, 2f);
        createGround(0, 266, 2f, 46f);
        createGround(50f, 264, 2f, 15);
        createGround(50f, 299, 2f, 15);

        //top right
        createGround(266, 312, 50f, 2f);
        createGround(266, 264, 15, 2f);
        createGround(301, 264, 15, 2f);
        createGround(316, 264, 2f, 50);
        createGround(266, 266, 2f, 13);
        createGround(266, 299, 2f, 13);

        //top inner wall
        createGround(52, 270.5f, 96, 2);
        createGround(148, 270.5f, 2, 8.5f);
        createGround(168, 270.5f, 2, 8.5f);
        createGround(170, 270.5f, 96, 2);

        //top outer wall
        createGround(52, 305.5f, 96, 2);
        createGround(148, 299, 2, 8.5f);
        createGround(150, 299, 18, 2);
        createGround(168, 299, 2, 8.5f);
        createGround(170, 305.5f, 96, 2);

    }

    public void createGround(float x, float y, float w, float h) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        //this is a fix because you car is stupid and wheels spawn outside the level :p
        x -= 64f;
        y -= 64f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);
        body.createFixture(box, 1f);
        box.dispose();
    }


    public Body createBox(float x, float y, float w, float h) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

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
