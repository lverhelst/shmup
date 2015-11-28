package Factories;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Random;

import AI.IntentGenerator;
import AI.AI;
import ecs.components.ChildEntityComponent;
import ecs.components.HealthComponent;
import gameObjects.Car;
import ecs.components.CameraAttachmentComponent;
import ecs.Entity;
import ecs.components.JointComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.ControlledComponent;
import ecs.components.SteeringComponent;
import ecs.components.WeaponComponent;
import verberg.com.shmup.Game;

/**
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
        bdef.position.set(new Vector2(random.nextInt(128), random.nextInt(128))); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body carbody = Game.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        Fixture f = carbody.createFixture(pShape, 0.1f);
        f.setDensity(density);
        Entity carBodyEntity = null;
        ChildEntityComponent cec = new ChildEntityComponent();
        if(!(ig instanceof AI)){
            carBodyEntity = new Entity(new PhysicalComponent(carbody), new CameraAttachmentComponent(), new WeaponComponent(), new HealthComponent(100), new ControlledComponent(ig),cec );
        }else{
            carBodyEntity = new Entity(new PhysicalComponent(carbody), new WeaponComponent(), new ControlledComponent(ig), new HealthComponent(100),cec);
        }
        if(carBodyEntity == null)
            return;
        ((PhysicalComponent)carBodyEntity.get(PhysicalComponent.class)).isRoot = true;


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
            Fixture fixture = tireBody.createFixture(tireShape, 1f);

            //really you just control the tires
            Entity tireEntity = new Entity(tValue.getString("name"), steering, new PhysicalComponent(tireBody), new ControlledComponent(ig), new HealthComponent(10));
            fixture.setUserData(tireEntity);

            Entity jointEntity = new Entity("JointEntity", new JointComponent(carbody, tireBody, v2));
            tireEntity.addComponent(new ParentEntityComponent(jointEntity));


            jointEntity.addComponent(new ParentEntityComponent(carBodyEntity));
            jointEntity.addComponent(new ChildEntityComponent(tireEntity));

            cec.childList.add(jointEntity);

        }
    }

    public void applyLifeTimeWarranty(Entity e){
        ControlledComponent cc = null;
        if(e.has(ControlledComponent.class)) {
            cc = e.get(ControlledComponent.class);
        }
        if(cc == null){
            //Error, can't apply warranty to uncontrolled object
            //TODO: Err Msg/Assert
            return;
        }




        Game.removeEntity(e);

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


        Random random = new Random();
        bdef.position.set(new Vector2(random.nextInt(128), random.nextInt(128))); //add message to spawn system queue so they can reposition the entity this body goes to on spawn
        Body carbody = Game.getWorld().createBody(bdef);

        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        Fixture f = carbody.createFixture(pShape, 0.1f);
        f.setDensity(density);
        ChildEntityComponent cec = new ChildEntityComponent();
        if(!(cc.ig instanceof AI)){
            e = new Entity(new PhysicalComponent(carbody), new CameraAttachmentComponent(), new WeaponComponent(), new HealthComponent(100), new ControlledComponent(cc.ig),cec );
        }else{
            e = new Entity(new PhysicalComponent(carbody), new WeaponComponent(), new ControlledComponent(cc.ig), new HealthComponent(100),cec);
        }
        if(e == null)
            return;
        ((PhysicalComponent)e.get(PhysicalComponent.class)).isRoot = true;


        f.setUserData(e);


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
            Fixture fixture = tireBody.createFixture(tireShape, 1f);

            //really you just control the tires
            Entity tireEntity = new Entity(tValue.getString("name"), steering, new PhysicalComponent(tireBody), new ControlledComponent(cc.ig), new HealthComponent(10));
            fixture.setUserData(tireEntity);

            Entity jointEntity = new Entity("JointEntity", new JointComponent(carbody, tireBody, v2));
            tireEntity.addComponent(new ParentEntityComponent(jointEntity));


            jointEntity.addComponent(new ParentEntityComponent(e));
            jointEntity.addComponent(new ChildEntityComponent(tireEntity));

            cec.childList.add(jointEntity);

        }

    }


}
