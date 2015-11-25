package gameObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;


/**
 * Created by Orion on 11/17/2015.

    Body Component makes a physics box...maybe this is more of a physics Component.

 */
public class BodyComponent {
    private Body body;

    //move to definitions class
    final int PPM = 5;

    public BodyComponent(World world){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(160/PPM, 120/PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.linearVelocity.set(0f, 0f);



        //personally I don't like adding a body to the world here, it should be done where all the other additions to the world are done
        body = world.createBody(bodyDef);
        body.setFixedRotation(false);


        PolygonShape pbox = new PolygonShape();
        pbox.setAsBox(5/PPM, 10/PPM);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = pbox;
        fixDef.friction = 0.4f;
        //set collision bits here
        body.createFixture(fixDef);
    }

    public void setUserData(Object userData){
        body.setUserData(userData);
    }


    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
