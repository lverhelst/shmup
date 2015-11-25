package ecs.components;

import com.badlogic.gdx.math.Vector2;

import ecs.Component;

/**
 * Created by Orion on 11/23/2015.
 * Supposed to be used to anchor box2d components together
 */
public class AnchorPointComponent extends Component {
    Vector2 anchorPoint;

    public AnchorPointComponent(Vector2 point){
        this.anchorPoint = point;
    }
}
