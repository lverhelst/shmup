package ecs.components;

import com.badlogic.gdx.physics.box2d.Body;

import ecs.Component;


/**
 * Created by Orion on 11/22/2015.
 * A box2d body
 */
public class PhysicalComponent extends Component {
    Body box2dBody;

    public PhysicalComponent(Body body){
        this.box2dBody = body;
    }

    public Body getBody(){
        return box2dBody;
    }

}
