package components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.ArrayList;

import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/18/2015.
 */
public class Car {

    Body body;
    RevoluteJoint flJoint, frJoint;

    private String name;
    private float density;
    private Vector2[] vertices;
    private Tire[] tires;
    RevoluteJoint[] joints;


    //JSON INSTANTIATION
    public Car(){
        //used for instantiating from Json
    }

    public void setProperties(String name, float density, Vector2[] vertices){
        this.name = name;
        this.density = density;
        this.vertices = vertices;
    }

    public void assemble(Tire[] tires){
        //create car body
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(640/5, 480/5));
        body = Game.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        body.createFixture(pShape, 0.1f);

        //common joint shit
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero(); //center of tire (apparently)

        this.tires = tires;



        ArrayList<RevoluteJoint> rjs = new ArrayList<RevoluteJoint>();
        for(Tire t : tires){
            jointDef.bodyB = t.getBody();
            jointDef.localAnchorA.set(t.location);
            rjs.add((RevoluteJoint)Game.getWorld().createJoint(jointDef));
        }
        joints = rjs.toArray(new RevoluteJoint[rjs.size()]);

    }

    public void setTires(Tire[] tires){
        this.tires = tires;
    }


    public Car(World world){
        //create car body
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(640/5, 480/5));
        body = world.createBody(bdef);

        Vector2[] vertices = new Vector2[8];
        vertices[0] = new Vector2( 1.5f,   0f);
        vertices[1] = new Vector2(   3f, 2.5f);
        vertices[2] = new Vector2( 2.8f, 5.5f);
        vertices[3] = new Vector2(   1f,  10f);
        vertices[4] = new Vector2(  -1f,  10f);
        vertices[5] = new Vector2(-2.8f, 5.5f);
        vertices[6] = new Vector2(  -3f, 2.5f);
        vertices[7] = new Vector2( 1.5f,   0f);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        body.createFixture(pShape, 0.1f);

        //common joint shit
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero(); //center of tire (apparently)

        tires = new Tire[4];
        //rear left tire
        Tire rltire = new Tire(world);
        jointDef.bodyB = rltire.getBody();
        jointDef.localAnchorA.set(-3,0.75f);
        world.createJoint(jointDef);
        rltire.setCharacteristics(250,-40,500,9.5f);
        tires[0] = rltire;

        //rear right tire
        Tire rrtire = new Tire(world);
        jointDef.bodyB = rrtire.getBody();
        jointDef.localAnchorA.set(3,0.75f);
        world.createJoint(jointDef);
        rrtire.setCharacteristics(250, -40, 500,9.5f);
        tires[1] = rrtire;

        //front left tire
        Tire fltire = new Tire(world);
        jointDef.bodyB = fltire.getBody();
        jointDef.localAnchorA.set(-3,8.5f);
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        fltire.setCharacteristics(250,-40,300,7.5f);
        tires[2] = fltire;

        //front right tire
        Tire frtire = new Tire(world);
        jointDef.bodyB = frtire.getBody();
        jointDef.localAnchorA.set(3,8.5f);
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        frtire.setCharacteristics(250,-40,300,7.5f);
        tires[3] = frtire;
    }

    public Body getBody(){
        return body;
    }

    public void update(){
        for(Tire t: tires){
            t.updateFriction();
        }
        for(Tire t: tires){
            t.updateDrive();
        }

        float lockAngle = (float)Math.toRadians(35);
        float desiredAngle = 0;

        if(MyInputAdapter.getKeysdown()[Input.Keys.LEFT]){
            //move left
            desiredAngle = lockAngle;
        }
        if(MyInputAdapter.getKeysdown()[Input.Keys.RIGHT]){
            //move right
            desiredAngle = -lockAngle;
        }
        for(int i = 0; i < joints.length; i++ ){
            if(tires[i].canTurn){
                joints[i].setLimits(desiredAngle, desiredAngle);
            }
        }


    }



}
