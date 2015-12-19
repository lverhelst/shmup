package Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import AI.IntentGenerator;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import verberg.com.shmup.ShmupGame;
import MessageManagement.INTENT;

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
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                if (entity.has(PhysicalComponent.class)) {
                    if ((entity.get(PhysicalComponent.class)).isRoot) {
                        if(keysdown[Input.Keys.Y]) {
                                System.out.println("SPAWN");
                                //is dead respawn
                                MessageManager.getInstance().addMessage(SpawnSystem.class, entity);
                                return;
                            }


                    }
                }
            }
        }

        //Suicide
        if(keysdown[Input.Keys.T]) {
            if (entity.has(PhysicalComponent.class)) {
                if ((entity.get(PhysicalComponent.class)).isRoot) {
                    if (entity.has(HealthComponent.class)) {
                        (entity.get(HealthComponent.class)).setCur_Health(0);
                        MessageManager.getInstance().addMessage(RemovalSystem.class, entity, INTENT.DIED);
                    }
                }
                return;
            }
        }
        if(keysdown[Input.Keys.UP]||keysdown[Input.Keys.W]){
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.ACCELERATE);
        }
        if(keysdown[Input.Keys.SHIFT_LEFT]){
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.BOOST);
        }
        if(keysdown[Input.Keys.DOWN]||keysdown[Input.Keys.S]){
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.DECELERATE);
        }
        boolean didTurn = false;
        if(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.A]){
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.LEFTTURN);
            didTurn |= true;
        }
        if(keysdown[Input.Keys.RIGHT]||keysdown[Input.Keys.D]){
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.RIGHTTURN);
            didTurn |= true;
        }
        if(!didTurn) {
            MessageManager.getInstance().addMessage(SteeringSystem.class, entity, INTENT.STRAIGHT);
        }
        if(keysdown[Input.Keys.SPACE]||keysdown[CONTROL_RIGHT]){
            MessageManager.getInstance().addMessage(WeaponSystem.class, entity);
        }
    }
}
