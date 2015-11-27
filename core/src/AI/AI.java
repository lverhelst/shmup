package AI;

import java.util.Random;

import ecs.Entity;
import ecs.components.HealthComponent;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.MessageManager;
import verberg.com.shmup.SteeringMessage;
import verberg.com.shmup.WeaponMessage;

/**
 * Roughin in AI
 * Created by Orion on 11/21/2015.
 */
public class AI implements IntentGenerator {

    long lastIntent = 0;
    int intentDelay = 250;
    int bozoNumber = 5;
    Random random;

    public AI(){
        random = new Random();
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
