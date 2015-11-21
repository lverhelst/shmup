package components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import verberg.com.shmup.Game;

/**
 * https://github.com/lverhelst/shmup.git
 * Created by Orion on 11/18/2015.
 */
public class Tire {

    float maxForwardSpeed = 300;
    float maxBackwardSpeed = -150;
    float maxDriveForce = 25;
    float maxLateralImpulse = 2;
    Vector2 location;
    boolean canTurn;
    String name;
    Body body;

    public Tire(){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = Game.getWorld().createBody(bdef);


        PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(0.5f, 1.25f);
        Fixture fixture = body.createFixture(pShape, 1f);
       // fixture.setSensor(true);
    }

    public Tire(World world){
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.setAsBox(0.5f, 1.25f);
        Fixture fixture = body.createFixture(pShape, 1f);

    }


    public void setCharacteristics(String name, boolean canTurn, Vector2 location, float maxForwardSpeed, float maxBackwardSpeed, float maxDriveForce, float maxLateralImpulse){
        this.name = name;
        this.canTurn = canTurn;
        this.location = location;
        this.maxForwardSpeed = maxForwardSpeed;
        this.maxBackwardSpeed = maxBackwardSpeed;
        this.maxDriveForce = maxDriveForce;
        this.maxLateralImpulse = maxLateralImpulse;
    }

    public void setCharacteristics(float maxForwardSpeed, float maxBackwardSpeed, float maxDriveForce, float maxLateralImpulse){
        this.maxForwardSpeed = maxForwardSpeed;
        this.maxBackwardSpeed = maxBackwardSpeed;
        this.maxDriveForce = maxDriveForce;
        this.maxLateralImpulse = maxLateralImpulse;
    }


    private Vector2 getLateralVelocity(){
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1, 0));
        return currentRightNormal.scl(currentRightNormal.dot(body.getLinearVelocity()));
    }

    private Vector2 getForwardVelocity(){
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        return currentForwardNormal.scl(currentForwardNormal.dot(body.getLinearVelocity()));
    }

    public void updateFriction(){
        //kill sideways movement
        Vector2 impulse = getLateralVelocity().scl(-1 * body.getMass());
        //allow skidding
        if(impulse.len() > maxLateralImpulse)
            impulse = impulse.scl(maxLateralImpulse / impulse.len());
        //apply
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        //dampen angular velocity
        body.applyAngularImpulse(0.1f * body.getInertia() * body.getAngularVelocity(), true);

        //drag
        Vector2 currentForwardNormal = getForwardVelocity();
        float speed = (float)Math.sqrt(currentForwardNormal.x * currentForwardNormal.x + currentForwardNormal.y * currentForwardNormal.y);
        body.applyForceToCenter(currentForwardNormal.scl(-0.02f * speed),true);


    }

    public void updateDrive(){
        float desiredSpeed = 0;
        if(MyInputAdapter.getKeysdown()[Input.Keys.UP]) {
            desiredSpeed = maxForwardSpeed;
        }
        else if(MyInputAdapter.getKeysdown()[Input.Keys.DOWN]) {
            desiredSpeed = maxBackwardSpeed;
        }else return;

        //current speed
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        //strangle enough currentForwardNormal gets cleared somehow
        //so we save current forward normal into a new vector
        Vector2 wtf = new Vector2(currentForwardNormal.x,currentForwardNormal.y);
        float currentSpeed = getForwardVelocity().dot(wtf);
        //apply the forces!
        float force = 0;
        if(desiredSpeed > currentSpeed)
            force = maxDriveForce;
        else if (desiredSpeed < currentSpeed)
            force = -maxDriveForce;
        else return;

        body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
    }

    public void accelerate(){
        //current speed
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        //strangle enough currentForwardNormal gets cleared somehow
        //so we save current forward normal into a new vector
        Vector2 wtf = new Vector2(currentForwardNormal.x,currentForwardNormal.y);
        float currentSpeed = getForwardVelocity().dot(wtf);
        //apply the forces!
        float force;
        if(maxForwardSpeed > currentSpeed)
            force = maxDriveForce;
        else return;
        body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
    }

    public void deccelerate(){
        //current speed
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
        //strangle enough currentForwardNormal gets cleared somehow
        //so we save current forward normal into a new vector
        Vector2 wtf = new Vector2(currentForwardNormal.x,currentForwardNormal.y);
        float currentSpeed = getForwardVelocity().dot(wtf);
        //apply the forces!
        float force;
        if(maxBackwardSpeed < currentSpeed)
            force = -maxDriveForce;
        else return;
        body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
    }


    public Body getBody(){
        return body;
    }
}
