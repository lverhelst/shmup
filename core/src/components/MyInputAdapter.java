package components;

import com.badlogic.gdx.InputAdapter;

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

    private static boolean[] keysdown =  new boolean[128];

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
}
