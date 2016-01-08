package ecs.components;

import java.util.UUID;

import ecs.Component;

/**
 * Created by Orion on 1/7/2016.
 */
public class FlagComponent extends Component {

    private UUID heldBy; //Entity the flag is held by

    public UUID getHeldBy() {
        return heldBy;
    }

    public void setHeldBy(UUID heldBy) {
        this.heldBy = heldBy;
    }

    @Override
    public void dispose() {
        heldBy = null; //remove held by
        super.dispose();
    }
}
