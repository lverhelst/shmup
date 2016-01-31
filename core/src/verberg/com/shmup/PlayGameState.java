package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import AI.AI;
import Factories.Factory;
import Input.MyInputAdapter;
import Level.Level;
import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.CameraAttachmentComponent;
import ecs.components.WeaponComponent;
import ecs.subsystems.CameraSystem;
import ecs.subsystems.ContactSystem;
import ecs.subsystems.InputSystem;
import ecs.subsystems.PowerUpSystem;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.RenderSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;

/**
 * Created by Orion on 12/19/2015.
 */
public class PlayGameState extends GameState {
    public static MessageManagement.MessageManager slightlyWarmMail = MessageManager.getInstance();
    //move to render component
    Box2DDebugRenderer debugRenderer;
    public ArrayList<AI> aiList; //technically we have this list in entities already

    //Initialize systems for ECS

    InputSystem inputSystem = new InputSystem();
    CameraSystem cameraSystem = new CameraSystem();
    PowerUpSystem powerupSystem = new PowerUpSystem();
    WeaponSystem weaponSystem = new WeaponSystem();
    RenderSystem renderSystem;

    //don't think we need multiple worlds am I right?
    private static World world;
    Factory factory;


    static MyInputAdapter playerInput;
    Level test;

    GameStateManager gsm;


    public PlayGameState(GameStateManager gsm){
        super(gsm);
        this.gsm = gsm;
        slightlyWarmMail.clear();
        //Steering
        slightlyWarmMail.registerSystem(INTENT.ACCELERATE, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.BOOST, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.LEFTTURN, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.DECELERATE, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.RIGHTTURN, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.STRAIGHT, new SteeringSystem());

        //Fire
        slightlyWarmMail.registerSystem(INTENT.FIRE, weaponSystem);
        slightlyWarmMail.registerSystem(INTENT.AIM, weaponSystem);

        //Remove
        slightlyWarmMail.registerSystem(INTENT.DIED, new RemovalSystem());
        slightlyWarmMail.registerSystem(INTENT.REMOVE, new RemovalSystem());

        //Spawn
        slightlyWarmMail.registerSystem(INTENT.SPAWN, new SpawnSystem("Swarm Attack"));
        slightlyWarmMail.registerSystem(INTENT.ADDSPAWN, new SpawnSystem("Swarm Attack"));


        EntityManager.getInstance().clear();
        slightlyWarmMail.clearMessages();

        this.world = gsm.game().recreateWorld();
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new ContactSystem());

        test = new Level();
        test.create(world);
        test.genLevel("blocks.blk");

        slightlyWarmMail.update(); //creates the spawn points

        setInputProcessor(playerInput = new MyInputAdapter());

        factory = new Factory("Swarm Attack");

        Entity testCar = factory.produceCarECS(playerInput);
        //factory.addComponentsForGameMode(testCar);
        testCar.addComponent(new CameraAttachmentComponent());
        MessageManager.getInstance().addMessage(INTENT.SPAWN, testCar);

        aiList = new ArrayList<AI>();
        createBots(4);


        //debug renderer, make sure to move this later
        debugRenderer = new Box2DDebugRenderer();
        renderSystem = new RenderSystem();
    }

    private void createBots(int num_Bots){
        Entity aiCar;
        for(int  i = 0; i < num_Bots; i++){
            aiCar = factory.produceCarECS(new AI());
            //factory.addComponentsForGameMode(aiCar);
            MessageManager.getInstance().addMessage(INTENT.SPAWN, aiCar);
        }
    }


    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            gsm.popState();
        }
    }


    @Override
    public void update(float dt) {
            test.update();
            //update collision listener
            world.step(dt, 6, 2);

            inputSystem.update(EntityManager.getInstance().entityList());
            powerupSystem.update();
            slightlyWarmMail.update();

            weaponSystem.update(EntityManager.getInstance().getEntitiesWithComponent(WeaponComponent.class));

            cameraSystem.update(EntityManager.getInstance().getEntitiesWithComponent(CameraAttachmentComponent.class), cam);
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        renderSystem.render(EntityManager.getInstance().entityList(), batch);

        batch.end();
        /*
        ShapeRenderer shapeRenderer = gsm.game().getShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        for(NavigationNode n : test.getNavNodes())
        {
         //   n.render(shapeRenderer);
        }


        /*if(getEntitiesWithComponent(CameraAttachmentComponent.class).size() > 0) {

            Entity debug = getEntitiesWithComponent(CameraAttachmentComponent.class).get(0);
            Body entity = debug.get(PhysicalComponent.class).getBody();

            float adjustX = (float) (Math.cos(entity.getAngle() + Math.PI / 2) * 60f / Constants.PPM + entity.getPosition().x);

            float adjustY = (float) (Math.sin(entity.getAngle() + Math.PI / 2) * 60f / Constants.PPM + entity.getPosition().y);

            shapeRenderer.line(entity.getPosition().x, entity.getPosition().y, adjustX, adjustY);

            shapeRenderer.setColor(Color.CYAN);
            ArrayList<Vector2> temp = ((AI) debug.get(ControlledComponent.class).ig).path;
            for (int i = 0; i < temp.size(); i++) {
                if (i >= 1) {
                    shapeRenderer.line(temp.get(i - 1).x, temp.get(i - 1).y, temp.get(i).x, temp.get(i).y);
                    System.out.print(temp.get(i - 1) + " " + temp.get(i) + "|");
                }
            }

            shapeRenderer.setColor(Color.FIREBRICK);
            if (temp.size() > 1)
                shapeRenderer.line(temp.get(0).x, temp.get(0).y, temp.get(temp.size() - 1).x, temp.get(temp.size() - 1).y);
        }
        shapeRenderer.end();
        */

        debugRenderer.render(world, cam.combined);
    }

    @Override
    public void dispose() {

    }
}
