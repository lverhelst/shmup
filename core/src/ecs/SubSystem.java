package ecs;

import MessageManagement.INTENT;

/**
 * Created by Orion on 11/23/2015.
 * TODO: Make into an interface and require an update() method?
 * TODO: Make a SubSystem Manager
 * TODO: Make these orderable
 */
public interface SubSystem {
    void processMessage(INTENT intent, Object... parameters);
}