package AI;

import java.util.ArrayList;
import java.util.Random;

import Input.CarCommands;
import Input.Command;
import ecs.Entity;
import ecs.components.HealthComponent;
import gameObjects.Car;
import gameObjects.ShmupActor;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.Message;
import verberg.com.shmup.MessageManager;
import verberg.com.shmup.SpawnMessage;
import verberg.com.shmup.SteeringMessage;
import verberg.com.shmup.WeaponMessage;

/**
 * Roughin in AI
 * Created by Orion on 11/21/2015.
 */
public class AI implements IntentGenerator {

    ShmupActor inControlof;
    long lastIntent = 0;
    int intentDelay = 250;
    int bozoNumber = 5;
    Random random;

    public AI(){
        random = new Random();
    }

    public ShmupActor getInControlof() {
        return inControlof;
    }

    public void setInControlof(ShmupActor inControlof) {
        this.inControlof = inControlof;
    }

    public Command[] getCommands(){
        if(((Car)inControlof).status == Car.CarStatus.DESTROYED)
            return new Command[0];

        ArrayList<Command> cmd = new ArrayList<Command>();
        CarCommands carCmd = new CarCommands();
        Random random = new Random();
        if(random.nextInt(8) < 2){
            cmd.add(carCmd.new AccelerateCommand());
        }
        if(random.nextInt(8) < 4){
            cmd.add(carCmd.new DecellerateCommand());
        }
        boolean turned = false;
        if(random.nextInt(8) == 5){
            cmd.add(carCmd.new LeftTurnCommand());
            turned |= true;
        }
        if(random.nextInt(8) == 6){
            cmd.add(carCmd.new RightTurnCommand());
            turned |= true;
        }
        if(!turned){
            cmd.add(carCmd.new PowerSteerCommand());
        }
        if(random.nextInt(10000) == 7){
            cmd.add(carCmd.new destructCommand());
        }
        return cmd.toArray(new Command[cmd.size()]);
    }

    @Override
    public void generateIntents(Entity entity) {

        if(entity.has(HealthComponent.class)){
            if(((HealthComponent)entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                //MessageManager.addMessage(new SpawnMessage(entity));
                return;
            }
        }

        if(lastIntent + intentDelay < System.currentTimeMillis())
        {
            lastIntent = System.currentTimeMillis();
            bozoNumber = random.nextInt(10);
        }
        //Add messages to message manager
        if(bozoNumber < 7){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.ACCELERATE));
        }
        if(bozoNumber == 7){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.DECELERATE));
        }
        boolean didTurn = false;
        if(bozoNumber < 4 ){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.LEFTTURN));
            didTurn |= true;
        } else if(bozoNumber < 7){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.RIGHTTURN));
            didTurn |= true;
        }
        if(!didTurn) {
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.STRAIGHT));
        }
        if(bozoNumber == 4){
            MessageManager.addMessage(new WeaponMessage(entity));
        }
    }
}
