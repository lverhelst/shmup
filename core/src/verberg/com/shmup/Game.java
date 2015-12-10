package verberg.com.shmup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import AI.AI;
import AI.IntentGenerator;
import Editor.NavigationNode;
import ecs.components.ChildEntityComponent;
import ecs.subsystems.InputSystem;
import Input.MyInputAdapter;
import ecs.subsystems.CameraSystem;
import ecs.Entity;
import ecs.subsystems.PowerUpSystem;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.RenderSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import Factories.CarFactory;
import ecs.subsystems.ContactSystem;

public class Game extends ApplicationAdapter {
    public static MessageManager slightlyWarmMail = new MessageManager();
	SpriteBatch batch;
    ShapeRenderer shapeRenderer;
	Texture img;


    private static ArrayList<Entity> entities = new ArrayList<Entity>();

    //move to static variables class
    private static final float STEP = 1/60f;
    public static final int V_WIDTH = 620/Constants.PPM;
    public static final int V_HEIGHT = 480/Constants.PPM;

    //move to render Component
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;


    public ArrayList<AI> aiList;

    //Initialize systems for ECS

    InputSystem inputSystem = new InputSystem();
    CameraSystem cameraSystem = new CameraSystem();
    PowerUpSystem powerupSystem = new PowerUpSystem();
    RenderSystem renderSystem;

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
        shapeRenderer = new ShapeRenderer();

        slightlyWarmMail.addSystem(SteeringSystem.class, new SteeringSystem());
        slightlyWarmMail.addSystem(WeaponSystem.class, new WeaponSystem());
        slightlyWarmMail.addSystem(RemovalSystem.class, new RemovalSystem());
        slightlyWarmMail.addSystem(SpawnSystem.class, new SpawnSystem());

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

        renderSystem = new RenderSystem();

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
        powerupSystem.update();
        //steeringSystem.update(entities);
        slightlyWarmMail.update();

        cameraSystem.update(entities, cam);
        //weaponSystem.update(entities);

        //messageManager.clearMessages();
    }


    @Override
    public void render () {
        update();
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        renderSystem.render(entities, batch);

        batch.end();


        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(NavigationNode n : test.getNavNodes())
        {
            n.render(shapeRenderer);
        }

        shapeRenderer.end();


        debugRenderer.render(world, cam.combined);

    }
}
