package ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import ecs.Component;
import verberg.com.shmup.ShmupGame;


/**
 * Created by Orion on 11/22/2015.
 * A box2d body
 */
public class PhysicalComponent extends Component {

    public int maxContacts = 0, numberOfContact = 0;
    public boolean isRoot; //Says if this node is the root of an entity tree
    Body box2dBody;

    public PhysicalComponent(Body body){
        isRoot = false;
        this.box2dBody = body;
    }

    public Body getBody(){
        return box2dBody;
    }

    public Vector2 getPosition() {
        return  box2dBody.getPosition();
    }


    public float getSpeed(){
        float velX = box2dBody.getLinearVelocity().x;
        float velY = box2dBody.getLinearVelocity().y;
        return  (float)Math.sqrt(velX * velX + velY * velY);
    }

    float angle;
    public float getAngleDegrees(){
        angle = (float)Math.toDegrees(box2dBody.getAngle());
        while(angle<=0){
            angle += 360;
        }
        while(angle>360){
            angle -= 360;
        }
        return angle;
    }

    public float getAngleRadians(){
        return box2dBody.getAngle();
    }

    Vector2 facing = new Vector2();
    public Vector2 facingVector(float distance){
        float adjustX = (float) (Math.cos( getAngleRadians() + Math.PI / 2) * distance) + box2dBody.getPosition().x;
        float adjustY = (float) (Math.sin( getAngleRadians() + Math.PI / 2) * distance) + box2dBody.getPosition().y;
        return facing.set(adjustX, adjustY);
    }



    public void dispose(){
        if(box2dBody != null) {
            ShmupGame.getWorld().destroyBody(box2dBody);
            box2dBody = null;
        }
    }

}
