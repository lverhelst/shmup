package Input;

import gameObjects.Car;
import gameObjects.ShmupActor;
import ecs.Entity;
import ecs.components.SteeringComponent;

/**
 * Created by Orion on 11/20/2015.
 */
public class CarCommands  {

    public class AccelerateCommand implements Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                ((Car) car).accelerate();
            }
        }
    }

    public class DecellerateCommand implements Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                ((Car) car).deccelerate();
            }
        }
    }

    public class LeftTurnCommand implements Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                ((Car) car).turnLeft();
            }
        }

        public void execute(Entity entity){
            if(entity.has(SteeringComponent.class)){
                ((SteeringComponent)entity.get(SteeringComponent.class)).setSteeringDirection(SteeringComponent.DIRECTION.LEFT);
            }
        }
    }

    public class RightTurnCommand implements Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                ((Car) car).turnRight();
            }
        }

        public void execute(Entity entity){
            if(entity.has(SteeringComponent.class)){
                ((SteeringComponent)entity.get(SteeringComponent.class)).setSteeringDirection(SteeringComponent.DIRECTION.RIGHT);
            }
        }
    }

    public class PowerSteerCommand implements  Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                //send the car to Gay Camp
                ((Car) car).turnStraight();
            }
        }

        public void execute(Entity entity){
            if(entity.has(SteeringComponent.class)){
                ((SteeringComponent)entity.get(SteeringComponent.class)).setSteeringDirection(SteeringComponent.DIRECTION.STRAIGHT);
            }
        }
    }

    public class destructCommand implements  Command {
        public void execute(ShmupActor car) {
            if (car instanceof Car) {
                //send the car to the crusher's
                ((Car) car).destruct();
            }
        }
    }

    public class FireCommand implements  Command{
        @Override
        public void execute(ShmupActor a) {
            if(a instanceof  Car){
                ((Car)a).fire();
            }
        }
    }
}
