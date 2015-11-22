package components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.ArrayList;

import Input.Command;
import Input.CarCommands;

/**
 * Created by Orion on 11/17/2015.
 */
public class MyInputAdapter extends InputAdapter {

    private static boolean consumed;
    private static int lastKeycode;

    public static int getLastKeycode() {
        consumed = true;
        return lastKeycode;
    }

    public static boolean isConsumed() {
        return consumed;
    }

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
        consumed = false;
        lastKeycode = keycode;
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
}
