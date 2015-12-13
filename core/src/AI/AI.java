package AI;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import verberg.com.shmup.Game;
import MessageManagement.INTENT;

/**
 * Roughin in AI
 * Created by Orion on 11/21/2015.
 */
public class AI implements IntentGenerator {

    long lastIntent = 0;
    int intentDelay = 250;
    int bozoNumber = 5;
    boolean waitToRespawn = false;
    long respawnStart = 0;
    int respawnDelay = 5000;

    Random random;

    public AI(){
        random = new Random();
    }

    @Override
    public void generateIntents(Entity entity) {

        if(entity.has(HealthComponent.class)){
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){

                if (entity.has(PhysicalComponent.class)) {
                    if ((entity.get(PhysicalComponent.class)).isRoot) {
                        long time = System.currentTimeMillis();
                        if(!waitToRespawn){
                            respawnStart = time + respawnDelay;
                            waitToRespawn = true;
                        }
                        if((respawnStart) > time){
                            waitToRespawn = true;
                        }else{
                            waitToRespawn = false;
                            Game.slightlyWarmMail.addMessage(SpawnSystem.class, entity);
                        }
                    }
                }
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
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.ACCELERATE);
        }
        if(bozoNumber == 7){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.DECELERATE);
        }
        boolean didTurn = false;
        if(bozoNumber < 4 ){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.LEFTTURN);
            didTurn |= true;
        } else if(bozoNumber < 7){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.RIGHTTURN);
            didTurn |= true;
        }
        if(!didTurn) {
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.STRAIGHT);
        }
        if(bozoNumber == 4){
            Game.slightlyWarmMail.addMessage(WeaponSystem.class, entity, INTENT.FIRE);
        }

        if(random.nextInt(100000) == 0){
            if (entity.has(PhysicalComponent.class)) {
                if ((entity.get(PhysicalComponent.class)).isRoot) {
                    if (entity.has(HealthComponent.class)) {
                        ( entity.get(HealthComponent.class)).setCur_Health(0);
                        Game.slightlyWarmMail.addMessage(RemovalSystem.class, entity, INTENT.DIED);
                    }
                }
                return;
            }
        }
    }
}