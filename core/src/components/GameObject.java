package components;

import com.badlogic.gdx.physics.box2d.Body;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
/**
 * Created by Orion on 11/17/2015.
 */
public class GameObject {
    Body body;
    InputComponent input;

    public GameObject(Body inbody){
        body = inbody;
        input = new InputComponent();
    }

    public Body getBody(){
        return body;
    }

    public void process() {

    }

}
