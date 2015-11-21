package components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import verberg.com.shmup.Game;

/**
 *
 * Created by Orion on 11/20/2015.
 */
public class Bullet {

    static long lastbullet = 0;

    //move to bullet class? weapon compoenent?
    public static void createAndFire(Body sourceBody, Vector2 position, Vector2 linearVelocity){
        if(lastbullet + 200 > System.currentTimeMillis()){
            return;
        }
        lastbullet = System.currentTimeMillis();
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //add 90 since car definition is 90 deg off 0
        float direction = sourceBody.getAngle() + (float)Math.PI/2;
        float addx = (float)(Math.cos(direction)) * 8.5f;
        float addy = (float)(Math.sin(direction)) * 8.5f;

        Body bulletbody = Game.world.createBody(bodyDef);
        bulletbody.setTransform(position.add(addx,addy), direction);
        CircleShape circle = new CircleShape();
        circle.setRadius(1f);
        FixtureDef fixture = new FixtureDef();
        fixture.shape = circle;
        fixture.density = 1f;
        fixture.friction = 0.1f;

        bulletbody.createFixture(fixture);
        bulletbody.applyLinearImpulse(((float) Math.cos(direction)) * 1500f, ((float) Math.sin(direction)) * 1500f, bulletbody.getWorldCenter().x, bulletbody.getWorldCenter().y, true);
        circle.dispose();
    }
}
