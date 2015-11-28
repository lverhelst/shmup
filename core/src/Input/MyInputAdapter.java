package Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.ArrayList;

import AI.IntentGenerator;
import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.Message;
import verberg.com.shmup.MessageManager;
import verberg.com.shmup.RemoveMessage;
import verberg.com.shmup.SpawnMessage;
import verberg.com.shmup.SteeringMessage;
import verberg.com.shmup.WeaponMessage;

import static com.badlogic.gdx.Input.Keys.CONTROL_RIGHT;

/**
 * Created by Orion on 11/17/2015.
 */
public class MyInputAdapter extends InputAdapter implements IntentGenerator {

    private static boolean[] keysdown =  new boolean[256];

    public static boolean[] getKeysdown(){
        return keysdown;
    }

    @Override
    public boolean keyDown(int keycode) {
        keysdown[keycode] = true;

        return super.keyDown(keycode);
    }

    @Override
    public boolean keyUp(int keycode) {
        keysdown[keycode] = false;
        return false;
    }

    /**
     * Should be called by whatever system registers entity as player controlled
     * @param entity
     */
    @Override
    public void generateIntents(Entity entity){

        if(entity.has(HealthComponent.class)){
            if(((HealthComponent)entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                if (entity.has(PhysicalComponent.class)) {
                    if (((PhysicalComponent) entity.get(PhysicalComponent.class)).isRoot) {
                        if(keysdown[Input.Keys.Y]) {
                                System.out.println("SPAWN");
                                //is dead respawn
                                MessageManager.addMessage(new SpawnMessage(entity));
                                return;
                            }

                        
                    }
                }
            }
        }

        //Suicide
        if(keysdown[Input.Keys.T]) {
            if (entity.has(PhysicalComponent.class)) {
                if (((PhysicalComponent) entity.get(PhysicalComponent.class)).isRoot) {
                    if (entity.has(HealthComponent.class)) {
                        ((HealthComponent) entity.get(HealthComponent.class)).cur_health = 0;
                        MessageManager.addMessage(new RemoveMessage(entity,INTENT.DIED));
                    }
                }
                return;
            }
        }
        if(keysdown[Input.Keys.UP]||keysdown[Input.Keys.W]){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.ACCELERATE));
        }
        if(keysdown[Input.Keys.DOWN]||keysdown[Input.Keys.S]){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.DECELERATE));
        }
        boolean didTurn = false;
        if(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.A]){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.LEFTTURN));
            didTurn |= true;
        }
        if(keysdown[Input.Keys.RIGHT]||keysdown[Input.Keys.D]){
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.RIGHTTURN));
            didTurn |= true;
        }
        if(!didTurn) {
            MessageManager.addMessage(new SteeringMessage(entity, INTENT.STRAIGHT));
        }
        if(keysdown[Input.Keys.SPACE]||keysdown[CONTROL_RIGHT]){
            MessageManager.addMessage(new WeaponMessage(entity));
        }
    }
}
