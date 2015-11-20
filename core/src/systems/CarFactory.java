package systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import components.Car;
import components.Tire;

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

    public Car produceCar(){
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
        Car car = new Car();
        car.setProperties(name, density, vertices);

        //grab tires definitions
        ArrayList<Tire> tires = new ArrayList<Tire>();
        Tire tire;
        Vector2 v2;
        for(JsonValue tValue : jCar.get("tires")){
            tire = new Tire();
            tValue.get(name);

            v2 = new Vector2(tValue.get("location").asFloatArray()[0],tValue.get("location").asFloatArray()[1]);
            tire.setCharacteristics(tValue.getString("name"),tValue.getBoolean("canTurn"),v2,tValue.getInt("maxForwardSpeed"),tValue.getInt("maxBackwardsSpeed")
                    ,tValue.getInt("maxDriveForce"),tValue.getFloat("maxLateralImpulse"));

            tires.add(tire);
        }
        car.assemble(tires.toArray(new Tire[tires.size()]));
        return car;
    }

}
