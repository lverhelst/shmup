package Factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Random;

import javax.naming.ldap.Control;

import AI.IntentGenerator;
import AI.AI;
import ecs.components.ChildEntityComponent;
import ecs.components.DamageComponent;
import ecs.components.HealthComponent;
import ecs.components.CameraAttachmentComponent;
import ecs.Entity;
import ecs.components.JointComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.ControlledComponent;
import ecs.components.SteeringComponent;
import ecs.components.WeaponComponent;
import verberg.com.shmup.Constants;
import verberg.com.shmup.Game;
import verberg.com.shmup.Node;

/**
 *  TODO: Full scale rewrite that allows for less reparsing all the time
 * While this isn't technically a Factory class, I think it's fun to name something that reads in a list of attributes
 * and pumps out cars a "factory"
 * Created by Orion on 11/19/2015.
 */
public class CarFactory {

    JsonReader jr = new JsonReader();
    JsonValue jv;
    JsonValue jCar;
    ArrayList<JsonValue> jTires;

    public CarFactory(){
        FileHandle fileHandle = Gdx.files.internal("carlist");
        jv = jr.parse(fileHandle);
        jCar = jv.get("car");
        jTires = new ArrayList<JsonValue>();
        for(JsonValue tire : jCar.get("tires")) {
            jTires.add(tire);
        }
    }


    //TODO: Move to produce predefined Entity structure class?
    public void produceCarECS(IntentGenerator ig){

        Entity carBodyEntity = assembleCarBody(ig);
        for(JsonValue tValue : jTires){
            assembleTire(tValue, carBodyEntity, new Entity());
        }
    }

    public void applyLifeTimeWarranty(Entity e, Node spawn){
        ControlledComponent cc = null;
        if(e.has(ControlledComponent.class)) {
            cc = e.get(ControlledComponent.class);
        }
        if(cc == null){
            //Error, can't apply warranty to uncontrolled object
            //TODO: Err Msg/Assert
            return;
        }

        //remove old car from world
        Game.removeEntityTree(e);

        Entity carBodyEntity = assembleCarBody(cc.ig);
        for(JsonValue tValue : jTires){
            assembleTire(tValue, carBodyEntity, new Entity());
        }


    }


    public void insuranceClaimForJoint(Entity joint, Entity carbody){
        for(JsonValue tValue : jTires){
            String name = tValue.getString("name");
            if(joint == null){
                System.out.println("entity null");
                return;
            }

            if(joint.get(JointComponent.class) == null){
                System.out.println("no joint component");

                return;
            }
            if(joint.get(JointComponent.class).name == null){
                System.out.println("name null");
                return;
            }

            String jointName = joint.get(JointComponent.class).name;
            System.out.println(name + " " + jointName);
            if(name.equals(jointName)){
                this.assembleTire(tValue, carbody, joint);
            }
        }
    }

    /***
     * Spawns a spiky thing
     */
    public static void getMoreCarsIntTheShopExe(){

        //spawning location stuff
        Random random = new Random();

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(new Vector2(random.nextInt(128) + 64, random.nextInt(128) + 64)); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body spikyBody = Game.getWorld().createBody(bdef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(4f); //Make it real big

        Fixture f = spikyBody.createFixture(circleShape, 10.0f);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.POWERUP_BIT;
        filter2.maskBits = Constants.POWERUP_MASK;
        f.setFilterData(filter2);
        PhysicalComponent pc = new PhysicalComponent(spikyBody);
        pc.maxContacts = 1;
        f.setUserData(new Entity(pc, new DamageComponent(86)));
    }

    private Entity assembleCarBody(IntentGenerator ig){

        JsonValue jVertices  = jCar.get("vertices");

        String name = jCar.getString("name");
        float density = jCar.getFloat("density");

        float[] x_vertices = jVertices.child().asFloatArray() ;
        float[] y_vertices = jVertices.child().next().asFloatArray();
        Vector2[] vertices = new Vector2[x_vertices.length];

        for(int x = 0; x < x_vertices.length; x++){
            vertices[x] = new Vector2(x_vertices[x], y_vertices[x]);
        }

        //create car body
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;

        Random random = new Random();
        bdef.position.set(new Vector2(random.nextInt(128) + 64, random.nextInt(128) + 64)); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body carbody = Game.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);


        Fixture f = carbody.createFixture(pShape, density);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.CAR_BIT;
        filter2.maskBits = Constants.CAR_MASK;
        f.setFilterData(filter2);


        Entity carBodyEntity = null;
        ChildEntityComponent cec = new ChildEntityComponent();
        if(!(ig instanceof AI)){
            carBodyEntity = new Entity(new PhysicalComponent(carbody), new CameraAttachmentComponent(), new WeaponComponent(), new HealthComponent(100), new ControlledComponent(ig),cec );
        }else{
            carBodyEntity = new Entity(new PhysicalComponent(carbody), new WeaponComponent(), new ControlledComponent(ig), new HealthComponent(100),cec);
        }
        if(carBodyEntity == null)
            return null;
        (carBodyEntity.get(PhysicalComponent.class)).isRoot = true;


        f.setUserData(carBodyEntity);


        return carBodyEntity;
    }

    public void assembleTire(JsonValue tire, Entity carBodyEntity, Entity joint){
        Vector2 v2;
        JsonValue tValue = tire;

        String tireName = tValue.getString("name");

        v2 = new Vector2(tValue.get("location").asFloatArray()[0],tValue.get("location").asFloatArray()[1]);

        //Tire entity
        SteeringComponent steering = new SteeringComponent();
        steering.canTurn = tValue.getBoolean("canTurn");
        steering.maxForwardSpeed = tValue.getInt("maxForwardSpeed");
        steering.maxBackwardsSpeed = tValue.getInt("maxBackwardsSpeed");
        steering.maxDriveForce = tValue.getInt("maxDriveForce");
        steering.maxLateralImpulse = tValue.getFloat("maxLateralImpulse");
        //max box2d body of tire
        BodyDef tireBodyDef = new BodyDef();
        tireBodyDef.type = BodyDef.BodyType.DynamicBody;
        tireBodyDef.position.set(carBodyEntity.get(PhysicalComponent.class).getBody().getPosition()); //set tire location to car body location
        Body tireBody = Game.getWorld().createBody(tireBodyDef);

        PolygonShape tireShape = new PolygonShape();
        tireShape.setAsBox(tValue.get("size").asFloatArray()[0],tValue.get("size").asFloatArray()[1]);

        Fixture fixture = tireBody.createFixture(tireShape,0.1f);
        Filter filter = new Filter();
        filter.categoryBits = Constants.TIRE_BIT;
        filter.maskBits = Constants.TIRE_MASK;
        fixture.setFilterData(filter);


        //really you just control the tires
        Entity tireEntity = new Entity(tireName, steering, new PhysicalComponent(tireBody), new ControlledComponent(carBodyEntity.get(ControlledComponent.class).ig), new HealthComponent(10));
        fixture.setUserData(tireEntity);

        boolean addTochild = false;

        carBodyEntity.get(ChildEntityComponent.class).childList.remove(joint);


        joint.removeAllComponents();
        joint = new Entity("JointEntity", new JointComponent(carBodyEntity.get(PhysicalComponent.class).getBody(), tireBody, v2, tireName));

        tireEntity.addComponent(new ParentEntityComponent(joint));


        joint.addComponent(new ParentEntityComponent(carBodyEntity));
        joint.addComponent(new ChildEntityComponent(tireEntity));




        carBodyEntity.get(ChildEntityComponent.class).childList.add(joint);

    }
}
