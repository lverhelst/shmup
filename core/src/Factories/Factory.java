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
import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.components.ChildEntityComponent;
import ecs.components.DamageComponent;
import ecs.components.FlagComponent;
import ecs.components.HealthComponent;
import ecs.components.CameraAttachmentComponent;
import ecs.Entity;
import ecs.components.JointComponent;
import ecs.components.KDAComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.ControlledComponent;
import ecs.components.SteeringComponent;
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import ecs.components.WeaponComponent;
import ecs.subsystems.SpawnSystem;
import verberg.com.shmup.Constants;
import verberg.com.shmup.ShmupGame;
import Level.Point;

/**
 *  TODO: Full scale rewrite that allows for less reparsing all the time
 * While this isn't technically a Factory class, I think it's fun to name something that reads in a list of attributes
 * and pumps out cars a "factory"
 * Created by Orion on 11/19/2015.
 */
public class Factory {

    JsonReader jr = new JsonReader();
    JsonValue jv;
    JsonValue jCar;
    ArrayList<JsonValue> jTires;
    int team_num = 1;
    String gameMode;

    public Factory(String gameMode){
        FileHandle fileHandle = Gdx.files.internal("carlist");
        jv = jr.parse(fileHandle);
        jCar = jv.get("car");
        jTires = new ArrayList<JsonValue>();
        for(JsonValue tire : jCar.get("tires")) {
            jTires.add(tire);
        }
        this.gameMode = gameMode;
    }

    public Entity produceCarECS(IntentGenerator ig){
        //Generate a spawn point, since this has no access to them (As its not part of the spawn system)
        //Add message for spawning
        /*Random rand = new Random();
        Point spawn = new Point();
        spawn.create("SPAWN", "RED", 1f + rand.nextInt(12), 1f + rand.nextInt(12));
        */
        Entity carBodyEntity = assembleCarBody(ig);
        for(JsonValue tValue : jTires){
            assembleTire(tValue, carBodyEntity, new Entity());
        }

        return carBodyEntity;
    }

    public Entity applyLifeTimeWarranty(Entity e){
        /***
         * Reload from file so that we test easily
         */
        FileHandle fileHandle = Gdx.files.internal("carlist");
        jv = jr.parse(fileHandle);
        jCar = jv.get("car");
        jTires = new ArrayList<JsonValue>();
        for(JsonValue tire : jCar.get("tires")) {
            jTires.add(tire);
        }

        ControlledComponent cc = null;
        if(e.has(ControlledComponent.class)) {
            cc = e.get(ControlledComponent.class);
        }else{
            System.out.println("Can't build car with no controlled component");
            return null;
        }


        //remove old car from world
        //ShmupGame.removeEntityTree(e);
        e.removeAllComponents();

        Entity carBodyEntity = assembleCarBody(cc.ig);
        System.out.println("In applyLifeTimeWarranty after assemble car body " + carBodyEntity.has(ControlledComponent.class));

        for(JsonValue tValue : jTires){
            assembleTire(tValue, carBodyEntity, new Entity());
        }
        System.out.println("In applyLifeTimeWarranty after tires " + carBodyEntity.has(ControlledComponent.class));
        return carBodyEntity;
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


    int team = 0;
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
            carBodyEntity = new Entity("PlayerControlled",new PhysicalComponent(carbody), new CameraAttachmentComponent(), new ControlledComponent(ig),cec);
        }else{
            carBodyEntity = new Entity("AICarTest" + random.nextInt(1000),new PhysicalComponent(carbody), new ControlledComponent(ig), cec);
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
        joint.addComponent(new ParentEntityComponent(carBodyEntity, carBodyEntity.getUuid()));
        weaponEntity.addComponent(new ParentEntityComponent(joint, carBodyEntity.getUuid()));
        carBodyEntity.get(ChildEntityComponent.class).childList.add(joint);
        carBodyEntity.get(ChildEntityComponent.class).childList.add(weaponEntity);
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
        tireShape.setAsBox(tValue.get("size").asFloatArray()[0], tValue.get("size").asFloatArray()[1]);

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

        tireEntity.addComponent(new ParentEntityComponent(joint,carBodyEntity.getUuid()));
        joint.addComponent(new ParentEntityComponent(carBodyEntity,carBodyEntity.getUuid()));
        joint.addComponent(new ChildEntityComponent(tireEntity));
        carBodyEntity.get(ChildEntityComponent.class).childList.add(joint);

    }


    /***
     * Spawns a cirlce with damage component thing
     */
    public static Entity makeDamageOrb(int dmgAmt){

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body spikyBody = ShmupGame.getWorld().createBody(bdef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.4f); //Make it real big

        Fixture f = spikyBody.createFixture(circleShape, 2.0f);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.POWERUP_BIT;
        filter2.maskBits = Constants.POWERUP_MASK;
        f.setFilterData(filter2);
        PhysicalComponent pc = new PhysicalComponent(spikyBody);
        pc.maxContacts = 1;
        Entity deadlySpike = new Entity(pc, new DamageComponent(dmgAmt));
        f.setUserData(deadlySpike);
        return deadlySpike;
    }

    /***
     * Spawns an oversized beach ball
     */
    public static Entity  makeBeachBall(){

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body beachBody = ShmupGame.getWorld().createBody(bdef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1f); //Make it real big

        //very little density since it's filled with air
        Fixture f = beachBody.createFixture(circleShape, 0.00001f);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.CAR_BIT;
        filter2.maskBits = Constants.CAR_MASK;
        f.setFilterData(filter2);
        PhysicalComponent pc = new PhysicalComponent(beachBody);
        //steering component so that it slows down
        Entity peachyBeachyBall = new Entity(pc, new TypeComponent(1), new SteeringComponent());
        f.setUserData(peachyBeachyBall);
        return peachyBeachyBall;
    }


    public static Entity makeFlag(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        Body flagBody = ShmupGame.getWorld().createBody(bodyDef);

        PolygonShape flagShape = new PolygonShape();
        flagShape.set(new float[]{-.25f, 0f, .25f, 0f, 0f, 1f});

        Fixture f = flagBody.createFixture(flagShape, 0.1f);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.POWERUP_BIT;
        filter2.maskBits = Constants.POWERUP_MASK;
        f.setFilterData(filter2);
        PhysicalComponent pc = new PhysicalComponent(flagBody);
        Entity flagEntity = new Entity("Flag", pc, new FlagComponent());
        f.setUserData(flagEntity);

        return flagEntity;
    }

    public Entity addComponentsForGameMode(Entity actOn){

        if(gameMode.equals("Capture the Flag")){
            addWeapon(actOn);
            actOn.addComponent(new HealthComponent(100));
            actOn.addComponent(new TeamComponent((team_num++ % 2) + 1));
        }

        if(gameMode.equals("Free For All")){
           addWeapon(actOn);
            actOn.addComponent(new HealthComponent(100));
            actOn.addComponent(new TeamComponent(team_num++));
            actOn.addComponent(new KDAComponent());
        }

        if(gameMode.equals("Swarm Attack")){
            if(!(actOn.get(ControlledComponent.class).ig instanceof  AI)){
                addWeapon(actOn);
                actOn.addComponent(new HealthComponent(100));
                actOn.addComponent(new TeamComponent(1));
                actOn.addComponent(new KDAComponent());
            }else{
                actOn.addComponent(new DamageComponent(10));
                actOn.addComponent(new HealthComponent(45));
                actOn.addComponent(new TeamComponent(2));
                actOn.addComponent(new KDAComponent());
            }
        }
        return actOn;
    }

}
