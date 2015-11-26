package Input;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.ControllerAdapter;

import java.util.ArrayList;

import AI.IntentGenerator;
import Input.Command;
import Input.CarCommands;
import ecs.Entity;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.Message;

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

    public Command[] getCommands(){
        ArrayList<Command> cmd = new ArrayList<Command>();
        CarCommands carCmd = new CarCommands();
        if(keysdown[Input.Keys.UP]){
            cmd.add(carCmd.new AccelerateCommand());
        }
        if(keysdown[Input.Keys.DOWN]){
            cmd.add(carCmd.new DecellerateCommand());
        }

        if(keysdown[Input.Keys.LEFT]){
            cmd.add(carCmd.new LeftTurnCommand());

        }
        if(keysdown[Input.Keys.RIGHT]){
            cmd.add(carCmd.new RightTurnCommand());
        }
        if(!(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.RIGHT])) {
            cmd.add(carCmd.new PowerSteerCommand());
        }
        if(keysdown[Input.Keys.SPACE]){
            cmd.add(carCmd.new FireCommand());
        }

        return cmd.toArray(new Command[cmd.size()]);
    }

    /**
     * Should be called by whatever system registers entity as player controlled
     * @param entity
     */
    @Override
    public void generateIntents(Entity entity){
        if(keysdown[Input.Keys.UP]||keysdown[Input.Keys.W]){
            Game.messageManager.addMessage(new Message(entity, INTENT.ACCELERATE));
        }
        if(keysdown[Input.Keys.DOWN]||keysdown[Input.Keys.S]){
            Game.messageManager.addMessage(new Message(entity, INTENT.DECELERATE));
        }

        if(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.A]){
            Game.messageManager.addMessage(new Message(entity, INTENT.LEFTTURN));
        }
        if(keysdown[Input.Keys.RIGHT]||keysdown[Input.Keys.D]){
            Game.messageManager.addMessage(new Message(entity, INTENT.RIGHTTURN));
        }
        if(!(keysdown[Input.Keys.LEFT]||keysdown[Input.Keys.RIGHT])) {
            Game.messageManager.addMessage(new Message(entity, INTENT.STRAIGHT));
        }
        if(keysdown[Input.Keys.SPACE]||keysdown[Input.Keys.CONTROL_RIGHT]){
            Game.messageManager.addMessage(new Message(entity, INTENT.FIRE));
        }
    }
}