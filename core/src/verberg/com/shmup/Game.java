package verberg.com.shmup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import Input.Command;
import components.AI;
import components.Car;
import components.MyInputAdapter;
import components.ShmupActor;
import systems.CarFactory;
import systems.MyContactListener;
import systems.WorldSystem;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;


    private static ArrayList<ShmupActor> actors = new ArrayList<ShmupActor>();



    //move to static shit class
    private static final float STEP = 1/60f;
    public static final int V_WIDTH = 620/5;
    public static final int V_HEIGHT = 480/5;

    //move to render component
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera camera;
    OrthographicCamera cam;


    public ArrayList<AI> aiList;


    //don't think we need multiple worlds am I right?
	public static World world;
	Car car;

    public static World getWorld(){
        return world;
    }
    MyInputAdapter playerInput;

	@Override
	public void create () {
		batch = new SpriteBatch();

        world = new World(new Vector2(0f, 0f), true); //shmup bros has no downward gravity
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new MyContactListener());

        WorldSystem test = new WorldSystem();
        test.create(world);

        CarFactory carFactory = new CarFactory();
        car = carFactory.produceCar();

        aiList = new ArrayList<AI>();
        for(int i = 0; i < 3; i++){
            AI ai = new AI();
            ai.setInControlof(carFactory.produceCar());
            aiList.add(ai);
        }

        //This seems back-asswards
       // car = new Car(world);

        //debug renderer, make sure to move this later
        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();

        Gdx.input.setInputProcessor(playerInput = new MyInputAdapter());
	}


    public static synchronized void addActor(ShmupActor shmupActor){
        actors.add(shmupActor);
    }


    public static synchronized void removeActor(ShmupActor shmupActor){
        actors.add(shmupActor);
    }

    public void update(){
        car.update(); //apply friction first since it uses the speed of the car
        //have to properly link car to player
        for(Command cmd : playerInput.getCommands()){
            cmd.execute(car);
        }

        //for AI in AIList
        //AI.getcommands
        //cmd.execute(AI.car)
        for(AI ai : aiList){
            for(Command cmd : ai.getCommands()){
                cmd.execute(ai.getInControlof());
            }
        }


        Iterator<ShmupActor> actorIterator = actors.iterator();

        while(actorIterator.hasNext()){
            if(actorIterator.next().isRemoveable()){
                actors.remove(actorIterator.next());
            }
        }

    }


	@Override
	public void render () {
        update();

        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(STEP, 6, 2);
        cam.position.set(car.getBody().getPosition().x, car.getBody().getPosition().y, 10);

        cam.update();

        debugRenderer.render(world, cam.combined);


        //Gdx.gl.glClearColor(1, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //batch.begin();
        //batch.draw(img, 0, 0);
        //batch.end();
	}
}
