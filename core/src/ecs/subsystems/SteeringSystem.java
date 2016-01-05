package ecs.subsystems;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;


import ecs.Entity;
import ecs.SubSystem;
import ecs.components.HealthComponent;
import ecs.components.JointComponent;
import ecs.components.PhysicalComponent;
import ecs.components.ControlledComponent;
import ecs.components.SteeringComponent;
import MessageManagement.INTENT;

/**
 * Created by Orion on 11/23/2015.
 * Used to steer tires
 */
public class SteeringSystem implements SubSystem {
    private Vector2 currentForwardNormal, wtf;
    private float force, currentSpeed;
    private boolean didTurn;
    private int boost_multiplier;
    private int steering_angle_deg;

    public void processMessage(INTENT intent, Object ... list) {
        if(list[0].getClass() == Entity.class) {
            Entity e = (Entity)list[0];
            if(intent == INTENT.LEFTTURN || intent == INTENT.RIGHTTURN || intent == INTENT.STRAIGHT){
                steering_angle_deg = (Integer)list[1];
            }

            updateSteering(e,intent);
        }
    }

    public void updateSteering(Entity entity, INTENT intent){

        if(entity.has(HealthComponent.class)){
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                return;
            }
        }

        if(entity.has(SteeringComponent.class)) {
            SteeringComponent sc = entity.get(SteeringComponent.class);
            Body body = (entity.get(PhysicalComponent.class)).getBody();
            //update friction before steering
            updateFriction(body, sc);

            //IS -- denotes that this should be in the input system
            //Should check isControlledComponent
            if (entity.has(ControlledComponent.class)) {

                didTurn = false;
                boost_multiplier = 1;
                switch (intent) {
                    case BOOST:
                        //if(!sc.canTurn) {
                            currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
                            //strangly enough currentForwardNormal gets cleared somehow
                            //so we save current forward normal into a new vector
                            wtf = new Vector2(currentForwardNormal.x, currentForwardNormal.y);
                            boost_multiplier = (sc.canTurn ? 1: 3);
                            force = sc.maxDriveForce * boost_multiplier;
                            body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
                       // }
                    case ACCELERATE:
                        currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
                        //strangly enough currentForwardNormal gets cleared somehow
                        //so we save current forward normal into a new vector
                        wtf = new Vector2(currentForwardNormal.x, currentForwardNormal.y);
                        currentSpeed = getForwardVelocity(body).dot(wtf);
                        //apply the forces!
                        if (sc.maxForwardSpeed > currentSpeed) {
                            force = sc.maxDriveForce * boost_multiplier;
                            body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
                        }
                        break;
                    case DECELERATE:
                        currentForwardNormal = body.getWorldVector(new Vector2(0, 1));
                        //strangle enough currentForwardNormal gets cleared somehow
                        //so we save current forward normal into a new vector
                        wtf = new Vector2(currentForwardNormal.x, currentForwardNormal.y);
                        currentSpeed = getForwardVelocity(body).dot(wtf);

                        //apply the forces!
                        if (sc.maxBackwardsSpeed < currentSpeed) {
                            force = -sc.maxDriveForce;
                            body.applyForce(wtf.scl(force), body.getWorldCenter(), true);
                        }
                        break;
                    case LEFTTURN:
                        //sc.setSteeringDirection(SteeringComponent.DIRECTION.LEFT);
                    case RIGHTTURN:
                       // sc.setSteeringDirection(SteeringComponent.DIRECTION.RIGHT);
                    case STRAIGHT:
                        sc.setSteering_angle(steering_angle_deg);
                        //sc.setSteeringDirection(SteeringComponent.DIRECTION.STRAIGHT);
                        break;
                }

                //check if entity has joint
                if (entity.recursiveHas(JointComponent.class)) {
                    JointComponent jc = entity.recursiveGet(JointComponent.class);
                    jc.joint.setLimits((float) Math.toRadians(sc.getSteering_angle()), (float) Math.toRadians(sc.getSteering_angle()));
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
        body.applyAngularImpulse(0.001f * body.getInertia() * body.getAngularVelocity(), true);

        //drag
        Vector2 currentForwardNormal = getForwardVelocity(body);
        float speed = (float)Math.sqrt(currentForwardNormal.x * currentForwardNormal.x + currentForwardNormal.y * currentForwardNormal.y);
        body.applyForceToCenter(currentForwardNormal.scl(-0.002f * speed),true);
    }
}
