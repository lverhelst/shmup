package gameObjects;

import MessageManagement.INTENT;
import ecs.SubSystem;

/**
 * Created by Orion on 1/9/2016.
 *
 */
public abstract class Condition implements SubSystem{


    @Override
    public abstract void processMessage(INTENT intent, Object... parameters);
    public abstract boolean hasWinner();
}
