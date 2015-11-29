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

import java.util.ArrayList;

import AI.AI;
import AI.IntentGenerator;
import ecs.components.ChildEntityComponent;
import ecs.subsystems.InputSystem;
import Input.MyInputAdapter;
import ecs.subsystems.CameraSystem;
import ecs.Entity;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import Factories.CarFactory;
import ecs.subsystems.ContactSystem;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;


    private static ArrayList<Entity> entities = new ArrayList<Entity>();

    //move to static variables class
    private static final float STEP = 1/60f;
    public static final int V_WIDTH = 620/5;
    public static final int V_HEIGHT = 480/5;

    //move to render Component
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;


    public ArrayList<AI> aiList;

    //Initialize systems for ECS
    InputSystem inputSystem = new InputSystem();
    SteeringSystem steeringSystem = new SteeringSystem();
    CameraSystem cameraSystem = new CameraSystem();
    WeaponSystem weaponSystem = new WeaponSystem();

    //don't think we need multiple worlds am I right?
	private static World world;

    public static World getWorld(){
        return world;
    }
    static MyInputAdapter playerInput;
    Level test;

	@Override
	public void create () {
		batch = new SpriteBatch();

        world = new World(new Vector2(0f, 0f), true); //shmup bros has no downward gravity
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new ContactSystem());

        test = new Level();
        test.create(world);

        Gdx.input.setInputProcessor(playerInput = new MyInputAdapter());

        CarFactory carFactory = new CarFactory();
        carFactory.produceCarECS(playerInput);




        aiList = new ArrayList<AI>();
        for(int i = 0; i < 4; i++){
            carFactory.produceCarECS(new AI());
        }

        //This seems back-asswards
       // car = new Car(world);

        //debug renderer, make sure to move this later
        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();


	}

    public static IntentGenerator getPlayerInput(){
        return playerInput;
    }

    //move to entity manager
    public static synchronized void addEntity(Entity entity){
        entities.add(entity);
    }
    public static synchronized void removeEntity(Entity entity){
        entities.remove(entity);
    }
    public static synchronized void removeEntityTree(Entity entity){
        if(entity.has(ChildEntityComponent.class)){
            for(Entity e : entity.get(ChildEntityComponent.class).childList ) {
                removeEntityTree(e);
            }
        }
        entity.removeAllComponents();
        entities.remove(entity);
    }





    public void update(){
        test.update();
        //update collision listener
        world.step(STEP, 6, 2);
       // System.out.println("Entities: " + entities.size() + " Box2DBodies " + world.getBodyCount());

        inputSystem.update(entities);
        //steeringSystem.update(entities);
        MessageManager.update();

        cameraSystem.update(entities, cam);
        //weaponSystem.update(entities);

        //messageManager.clearMessages();
    }


    @Override
    public void render () {
        update();

        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, cam.combined);


        //Gdx.gl.glClearColor(1, 0, 0, 1);
        //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //batch.begin();
        //batch.draw(img, 0, 0);
        //batch.end();
	}
}
