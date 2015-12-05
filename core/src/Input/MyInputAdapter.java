package Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import AI.IntentGenerator;
import ecs.Entity;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.MessageManager;
import verberg.com.shmup.Parameter;

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
        Parameter paramEntity = new Parameter(entity);

        if(entity.has(HealthComponent.class)){
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                if (entity.has(PhysicalComponent.class)) {
                    if ((entity.get(PhysicalComponent.class)).isRoot) {
                        if(keysdown[Input.Keys.Y]) {
                                System.out.println("SPAWN");
                                //is dead respawn
                                Game.slightlyWarmMail.addMessage(SpawnSystem.class, paramEntity);
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
                        Game.slightlyWarmMail.addMessage(RemovalSystem.class, paramEntity, new Parameter(INTENT.DIED));
                    }
                }
                return;
            }
        }
        if(keysdown[Input.Keys.UP]||keysdown[Input.Keys.W]){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.ACCELERATE));
        }
        if(keysdown[Input.Keys.SHIFT_LEFT]){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.BOOST));
        }
        if(keysdown[Input.Keys.DOWN]||keysdown[Input.Keys.S]){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.DECELERATE));
        }
        boolean didTurn = false;
        if(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.A]){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.LEFTTURN));
            didTurn |= true;
        }
        if(keysdown[Input.Keys.RIGHT]||keysdown[Input.Keys.D]){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.RIGHTTURN));
            didTurn |= true;
        }
        if(!didTurn) {
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, paramEntity, new Parameter(INTENT.STRAIGHT));
        }
        if(keysdown[Input.Keys.SPACE]||keysdown[CONTROL_RIGHT]){
            Game.slightlyWarmMail.addMessage(WeaponSystem.class, paramEntity);
        }
    }
}
