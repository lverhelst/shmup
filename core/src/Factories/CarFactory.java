package Factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Random;

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
import verberg.com.shmup.ShmupGame;
import Level.Point;

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

    public Entity produceCarECS(IntentGenerator ig){
        //Generate a spawn point, since this has no access to them (As its not part of the spawn system)
        //Add message for spawning
        Random rand = new Random();
        Point spawn = new Point();
        spawn.create("SPAWN", "RED", 12f + rand.nextInt(12), 12f + rand.nextInt(12));

        Entity carBodyEntity = assembleCarBody(ig, spawn);
        addWeapon(carBodyEntity);
        for(JsonValue tValue : jTires){
            assembleTire(tValue, carBodyEntity, new Entity());
        }
        return carBodyEntity;
    }

    public void applyLifeTimeWarranty(Entity e, Point spawn){
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
        //ShmupGame.removeEntityTree(e);
        e.removeAllComponents();

        Entity carBodyEntity = assembleCarBody(cc.ig, spawn);
        addWeapon(carBodyEntity);
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
            if(!joint.has(JointComponent.class)){
                System.out.println("No Joint component");
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
        Body spikyBody = ShmupGame.getWorld().createBody(bdef);

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

    private Entity assembleCarBody(IntentGenerator ig, Point spawn){

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
        bdef.position.set(new Vector2(spawn.position.x, spawn.position.y)); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body carbody = ShmupGame.getWorld().createBody(bdef);

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
            carBodyEntity = new Entity(new PhysicalComponent(carbody), new CameraAttachmentComponent(), new HealthComponent(100), new ControlledComponent(ig),cec );
        }else{
            carBodyEntity = new Entity("AICarTest",new PhysicalComponent(carbody),new DamageComponent(10), new ControlledComponent(ig), new HealthComponent(100),cec);
        }
        (carBodyEntity.get(PhysicalComponent.class)).isRoot = true;


        f.setUserData(carBodyEntity);


        return carBodyEntity;
    }

    public void addWeapon(Entity carBodyEntity){
        JsonValue wValue = jCar.get("weapon");

        JsonValue jVertices  = wValue.get("vertices");

        float[] x_vertices = jVertices.child().asFloatArray() ;
        float[] y_vertices = jVertices.child().next().asFloatArray();
        Vector2[] vertices = new Vector2[x_vertices.length];

        for(int x = 0; x < x_vertices.length; x++){
            vertices[x] = new Vector2(x_vertices[x], y_vertices[x]);
        }

        //create weapon body
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;

        bdef.position.set(carBodyEntity.get(PhysicalComponent.class).getBody().getPosition()); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body weaponBody = ShmupGame.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        Fixture f = weaponBody.createFixture(pShape, 0.01f);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.POWERUP_BIT;
        filter2.maskBits = Constants.POWERUP_MASK;
        f.setFilterData(filter2);


        Entity weaponEntity = new Entity("BoomStick", new PhysicalComponent(weaponBody));

        //create weapon joint
        Entity joint = new Entity("WeaponJoint", new JointComponent(carBodyEntity.get(PhysicalComponent.class).getBody(), weaponBody, new Vector2(0,0), "Weapon", true));
        joint.addComponent(new ParentEntityComponent(carBodyEntity));
        weaponEntity.addComponent(new ParentEntityComponent(joint));
        carBodyEntity.get(ChildEntityComponent.class).childList.add(joint);
        carBodyEntity.addComponent(new WeaponComponent(weaponEntity));

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
        Body tireBody = ShmupGame.getWorld().createBody(tireBodyDef);

        PolygonShape tireShape = new PolygonShape();
        tireShape.setAsBox(tValue.get("size").asFloatArray()[0],tValue.get("size").asFloatArray()[1]);

        Fixture fixture = tireBody.createFixture(tireShape,0.1f);
        Filter filter = new Filter();
        filter.categoryBits = Constants.TIRE_BIT;
        filter.maskBits = Constants.TIRE_MASK;
        fixture.setFilterData(filter);


        //really you just control the tires
        Entity tireEntity = new Entity(tireName, steering, new PhysicalComponent(tireBody), carBodyEntity.get(ControlledComponent.class), new HealthComponent(10));
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
