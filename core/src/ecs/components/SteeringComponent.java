package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 11/22/2015.
 * Used for Tires in the car
 */
public class SteeringComponent extends Component {
    private int steering_angle;

    public enum DIRECTION {
        LEFT(35),
        STRAIGHT(0),
        RIGHT(-35);

        private final int angle;

        DIRECTION(int angle) {
            this.angle = angle;
        }

        public int getAngle() {
            return angle;
        }
    }

    public boolean canTurn;
    public int maxForwardSpeed = 0;
    public int maxBackwardsSpeed = 0;
    public int maxDriveForce= 0;
    public float maxLateralImpulse = 0;


    private DIRECTION steeringDirection;

    public SteeringComponent() {
        steeringDirection = DIRECTION.STRAIGHT;
        canTurn = false;
    }

    public void setSteeringDirection(DIRECTION dir){
        if(canTurn){
            steeringDirection = dir;
        }
    }

    public int getSteering_angle() {
        return steering_angle;
    }

    public void setSteering_angle(int steering_angle) {
        if(canTurn) {
            if (steering_angle > DIRECTION.LEFT.angle) {
                this.steering_angle = DIRECTION.LEFT.angle;
            } else if (steering_angle < DIRECTION.RIGHT.angle) {
                this.steering_angle = DIRECTION.RIGHT.angle;
            } else {
                this.steering_angle = steering_angle;
            }
        }
    }

    public int steeringDirectionAngle(){
        return steeringDirection.angle;
    }


}
