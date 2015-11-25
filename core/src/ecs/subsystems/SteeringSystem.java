package ecs.subsystems;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import java.util.ArrayList;

import Input.MyInputAdapter;
import ecs.Entity;
import ecs.SubSystem;
import ecs.components.JointComponent;
import ecs.components.PhysicalComponent;
import ecs.components.PlayerControlledComponent;
import ecs.components.SteeringComponent;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.MessageManager;

/**
 * Created by Orion on 11/23/2015.
 * Used to steer tires
 */
public class SteeringSystem extends SubSystem {

    public void update(ArrayList<Entity> entities){

        //Iterate entities
        for(Entity entity : entities){
            if(entity.has(SteeringComponent.class)){
                SteeringComponent sc = entity.get(SteeringComponent.class);
                //This should be separated into a input system and should make intent messages
                //So IS = Input SubSystem
                //   SS = Steering SubSystem
                Body body = ((PhysicalComponent)entity.get(PhysicalComponent.class)).getBody();
                //update friction before steering
                updateFriction(body,sc);

                //IS -- denotes that this should be in the input system
                //Should check isControlledComponent
                if(entity.has(PlayerControlledComponent.class)){
                    //IS
                    if(MessageManager.hasMessage(entity, INTENT.ACCELERATE)){ //Should be (check messages for entity ID, CMD.UP

                        //SS {
                        //current speed
                        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
                        //strangle enough currentForwardNormal gets cleared somehow
                        //so we save current forward normal into a new vector
                        Vector2 wtf = new Vector2(currentForwardNormal.x,currentForwardNormal.y);
                        float currentSpeed = getForwardVelocity(body).dot(wtf);
                        //apply the forces!
                        float force;
                        if(sc.maxForwardSpeed > currentSpeed) {
                            force = sc.maxDriveForce;
                            body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
                        }
                        //}
                    }
                    if(MessageManager.hasMessage(entity, INTENT.DECELERATE)){ //Should be (check messages for entity ID, CMD.UP
                        //SS {
                        //current speed
                        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1));
                        //strangle enough currentForwardNormal gets cleared somehow
                        //so we save current forward normal into a new vector
                        Vector2 wtf = new Vector2(currentForwardNormal.x,currentForwardNormal.y);
                        float currentSpeed = getForwardVelocity(body).dot(wtf);
                        //apply the forces!
                        float force;
                        if(sc.maxBackwardsSpeed < currentSpeed) {
                            force = -sc.maxDriveForce;
                            body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
                        }
                        //}
                    }
                    boolean didTurn = false;
                    if(MessageManager.hasMessage(entity, INTENT.LEFTTURN)){ //Should be (check messages for entity ID, CMD.UP
                        //SS {
                        sc.setSteeringDirection(SteeringComponent.DIRECTION.LEFT);
                        //}
                        didTurn |= true;
                    }
                    if(MessageManager.hasMessage(entity, INTENT.RIGHTTURN)){ //Should be (check messages for entity ID, CMD.UP

                        //SS {
                        sc.setSteeringDirection(SteeringComponent.DIRECTION.RIGHT);
                        //}
                        didTurn |= true;
                    }
                    if(!didTurn){ //Should be (check messages for entity ID, CMD.UP

                        //SS {
                        sc.setSteeringDirection(SteeringComponent.DIRECTION.STRAIGHT);
                        //}
                    }


                    //check if entity has joint
                    if(entity.recursiveHas(JointComponent.class)){
                        JointComponent jc = entity.recursiveGet(JointComponent.class);
                        jc.joint.setLimits((float)Math.toRadians(sc.steeringDirectionAngle()),(float)Math.toRadians(sc.steeringDirectionAngle()));
                    }
                }
            }
        }
    }


    private Vector2 getLateralVelocity(Body body){
        Vector2 currentRightNormal = body.getWorldVector(new Vector2(1, 0));
        return currentRightNormal.scl(currentRightNormal.dot(body.getLinearVelocity()));
    }

    private Vector2 getForwardVelocity(Body body){
        Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
        return currentForwardNormal.scl(currentForwardNormal.dot(body.getLinearVelocity()));
    }


    public void updateFriction(Body body, SteeringComponent sc){
        //kill sideways movement
        Vector2 impulse = getLateralVelocity(body).scl(-1 * body.getMass());
        //allow skidding
        if(impulse.len() > sc.maxLateralImpulse)
            impulse = impulse.scl(sc.maxLateralImpulse / impulse.len());
        //apply
        body.applyLinearImpulse(impulse, body.getWorldCenter(), true);

        //dampen angular velocity
        body.applyAngularImpulse(0.1f * body.getInertia() * body.getAngularVelocity(), true);

        //drag
        Vector2 currentForwardNormal = getForwardVelocity(body);
        float speed = (float)Math.sqrt(currentForwardNormal.x * currentForwardNormal.x + currentForwardNormal.y * currentForwardNormal.y);
        body.applyForceToCenter(currentForwardNormal.scl(-0.02f * speed),true);
    }
}
