package Factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Random;

import AI.IntentGenerator;
import AI.AI;
import ecs.components.ChildEntityComponent;
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

/**
 *  TODO: Full scale rewrite that allows for less reparsing all the time
 * While this isn't technically a Factory class, I think it's fun to name something that reads in a list of attributes
 * and pumps out cars a "factory"
 * Created by Orion on 11/19/2015.
 */
public class CarFactory {

    String jsonText;

    public CarFactory(){
        FileHandle fileHandle = Gdx.files.internal("carlist");
        jsonText = fileHandle.readString();
    }


    //TODO: Move to produce predefined Entity structure class?
    public void produceCarECS(IntentGenerator ig){
        JsonReader jr = new JsonReader();
        JsonValue jv = jr.parse(Gdx.files.internal("carlist"));
        JsonValue jCar = jv.get("car");
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
            return;
        (carBodyEntity.get(PhysicalComponent.class)).isRoot = true;


        f.setUserData(carBodyEntity);


        Vector2 v2;
        for(JsonValue tValue : jCar.get("tires")){

            tValue.get(name);

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
            tireBodyDef.position.set(carbody.getPosition()); //set tire location to car body location
            Body tireBody = Game.getWorld().createBody(bdef);

            PolygonShape tireShape = new PolygonShape();
            tireShape.setAsBox(0.5f, 1.25f);


            Fixture fixture = tireBody.createFixture(tireShape,0.1f);
            Filter filter = new Filter();
            filter.categoryBits = Constants.TIRE_BIT;
            filter.maskBits = Constants.TIRE_MASK;
            fixture.setFilterData(filter);

            //really you just control the tires
            Entity tireEntity = new Entity(tValue.getString("name"), steering, new PhysicalComponent(tireBody), new ControlledComponent(ig), new HealthComponent(10));
            fixture.setUserData(tireEntity);

            Entity jointEntity = new Entity("JointEntity", new JointComponent(carbody, tireBody, v2, tValue.getString("name")));
            tireEntity.addComponent(new ParentEntityComponent(jointEntity));


            jointEntity.addComponent(new ParentEntityComponent(carBodyEntity));
            jointEntity.addComponent(new ChildEntityComponent(tireEntity));

            cec.childList.add(jointEntity);

        }
    }

    public void applyLifeTimeWarranty(Entity e, Rectangle spawn){
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

        //root check was done in the spawn system
        //could also do that here
        JsonReader jr = new JsonReader();
        JsonValue jv = jr.parse(Gdx.files.internal("carlist"));
        JsonValue jCar = jv.get("car");
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

        //spawning location stuff
        Random random = new Random();

        //TODO: maybe make a wrapper class for Box (body) which allows access to the height and width (store them as ints for this purpose)
        bdef.position.set(new Vector2(random.nextInt((int)spawn.getWidth()) + spawn.getX(), random.nextInt((int)spawn.getHeight()) + spawn.getY())); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body carbody = Game.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        Fixture f = carbody.createFixture(pShape, density);
        Filter filter2 = new Filter();
        filter2.categoryBits = Constants.CAR_BIT;
        filter2.maskBits = Constants.CAR_MASK;
        f.setFilterData(filter2);

        ChildEntityComponent cec = new ChildEntityComponent();
        if(!(cc.ig instanceof AI)){
            e = new Entity(new PhysicalComponent(carbody), new CameraAttachmentComponent(), new WeaponComponent(), new HealthComponent(100), new ControlledComponent(cc.ig),cec );
        }else{
            e = new Entity(new PhysicalComponent(carbody), new WeaponComponent(), new ControlledComponent(cc.ig), new HealthComponent(100),cec);
        }
        if(e == null)
            return;
        (e.get(PhysicalComponent.class)).isRoot = true;


        f.setUserData(e);


        Vector2 v2;
        for(JsonValue tValue : jCar.get("tires")){

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
            tireBodyDef.position.set(carbody.getPosition()); //set tire location to car body location
            Body tireBody = Game.getWorld().createBody(tireBodyDef);

            PolygonShape tireShape = new PolygonShape();
            tireShape.setAsBox(0.5f, 1.25f);

            Fixture fixture = tireBody.createFixture(tireShape,0.1f);
            Filter filter = new Filter();
            filter.categoryBits = Constants.TIRE_BIT;
            filter.maskBits = Constants.TIRE_MASK;
            fixture.setFilterData(filter);


            //really you just control the tires
            Entity tireEntity = new Entity(tireName, steering, new PhysicalComponent(tireBody), new ControlledComponent(cc.ig), new HealthComponent(10));
            fixture.setUserData(tireEntity);

            Entity jointEntity = new Entity("JointEntity", new JointComponent(carbody, tireBody, v2, tireName));
            tireEntity.addComponent(new ParentEntityComponent(jointEntity));


            jointEntity.addComponent(new ParentEntityComponent(e));
            jointEntity.addComponent(new ChildEntityComponent(tireEntity));

            cec.childList.add(jointEntity);
        }
    }


    public static void insuranceClaimForJoint(Entity joint, Entity carbody){
        JsonReader jr = new JsonReader();
        JsonValue jv = jr.parse(Gdx.files.internal("carlist"));
        JsonValue jCar = jv.get("car");


        for(JsonValue tValue : jCar.get("tires")){



            String tireName = tValue.getString("name");
            //If not the join we're looking for, then restart
            if(!tireName.equals(joint.get(JointComponent.class).name)){
                continue;
            }


            Vector2 v2 = new Vector2(tValue.get("location").asFloatArray()[0],tValue.get("location").asFloatArray()[1]);

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
            tireBodyDef.position.set(carbody.get(PhysicalComponent.class).getBody().getPosition()); //set tire location to car body location
            Body tireBody = Game.getWorld().createBody(tireBodyDef);

            PolygonShape tireShape = new PolygonShape();
            tireShape.setAsBox(0.5f, 1.25f);

            Fixture fixture = tireBody.createFixture(tireShape,0.1f);
            Filter filter = new Filter();
            filter.categoryBits = Constants.TIRE_BIT;
            filter.maskBits = Constants.TIRE_MASK;
            fixture.setFilterData(filter);


            //really you just control the tires
            Entity tireEntity = new Entity(tireName, steering, new PhysicalComponent(tireBody), new ControlledComponent(carbody.get(ControlledComponent.class).ig), new HealthComponent(10));
            fixture.setUserData(tireEntity);


            joint.removeAllComponents();

            joint = new Entity("JointEntity", new JointComponent(carbody.get(PhysicalComponent.class).getBody(), tireBody, v2, tireName));
            tireEntity.addComponent(new ParentEntityComponent(joint));


            joint.addComponent(new ParentEntityComponent(carbody));
            joint.addComponent(new ChildEntityComponent(tireEntity));

        }


    }
}
