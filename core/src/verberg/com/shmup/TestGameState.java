package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Input.MyInputAdapter;
import Factories.Factory;
import Level.Level;
import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.SubSystem;
import ecs.components.CameraAttachmentComponent;
import ecs.components.ControlledComponent;
import ecs.components.FlagComponent;
import ecs.components.PhysicalComponent;
import ecs.components.SelfDestructTimerComponent;
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import ecs.subsystems.CameraSystem;
import ecs.subsystems.ContactSystem;
import ecs.subsystems.FlagUpdateSystem;
import ecs.subsystems.InputSystem;
import ecs.subsystems.PowerUpSystem;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.RenderSystem;
import ecs.subsystems.SelfDestructTimerSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import AI.AI;
import gameObjects.Condition;
import gameObjects.FreeForAllCondition;
import gameObjects.TeamCTFCondition;

/**
 * Created by Orion on 12/19/2015.
 */
public class TestGameState extends GameState implements SubSystem {
    public static MessageManagement.MessageManager slightlyWarmMail = MessageManager.getInstance();
    BitmapFont bf;
    InputSystem inputSystem = new InputSystem();
    CameraSystem cameraSystem = new CameraSystem();
    PowerUpSystem powerupSystem = new PowerUpSystem();
    FlagUpdateSystem flagUpdateSystem = new FlagUpdateSystem();
    SelfDestructTimerSystem selfDestructTimerSystem = new SelfDestructTimerSystem();
    RenderSystem renderSystem;
    Box2DDebugRenderer debugRenderer;
    Level test;
    private static World world;
    Entity testCar;
    boolean gameover;
    Condition ctf_cond;
    Factory factory;
    String currentGameMode;

    int team_num = 1;

    /*
        Possible parameters have to be defined in here


     */







     public TestGameState(GameStateManager gsm, HashMap<String, Object> params){
        super(gsm);

        bf = new BitmapFont();

        slightlyWarmMail.clear();
        gameover = false;
        currentGameMode = (String)params.get("gamemode");

        //Steering
        slightlyWarmMail.registerSystem(INTENT.ACCELERATE, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.BOOST, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.LEFTTURN, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.DECELERATE, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.RIGHTTURN, new SteeringSystem());
        slightlyWarmMail.registerSystem(INTENT.STRAIGHT, new SteeringSystem());

        //Fire
        slightlyWarmMail.registerSystem(INTENT.FIRE, new WeaponSystem());
        slightlyWarmMail.registerSystem(INTENT.AIM, new WeaponSystem());

        //Remove
        slightlyWarmMail.registerSystem(INTENT.DIED, new RemovalSystem());
        slightlyWarmMail.registerSystem(INTENT.REMOVE, new RemovalSystem());


         //Spawn
         slightlyWarmMail.registerSystem(INTENT.SPAWN, new SpawnSystem(currentGameMode));
         slightlyWarmMail.registerSystem(INTENT.ADDSPAWN, new SpawnSystem(currentGameMode));


         System.out.println("Current Game Mode: " + currentGameMode);
        if(currentGameMode.equals("Capture the Flag")){
            //default
            slightlyWarmMail.registerSystem(INTENT.TEAM_CAPTURE, ctf_cond = new TeamCTFCondition((params.containsKey("number_of_teams") ? (Integer)params.get("number_of_teams") : 2)
                    , (params.containsKey("number_of_captures") ? (Integer)params.get("number_of_captures") : 3)));

        }else if(currentGameMode.equals("Swarm Attack") || currentGameMode.equals("Free For All")){
            System.out.println("Loaded " + currentGameMode);
            slightlyWarmMail.registerSystem(INTENT.DIED, ctf_cond = new FreeForAllCondition((params.containsKey("number_of_cars") ? (Integer)params.get("number_of_cars") : 5)
                                                                                                , (params.containsKey("number_of_kills") ? (Integer)params.get("number_of_kills") : 3)));
        }

        slightlyWarmMail.registerSystem(INTENT.WIN_COND_MET, this);

        EntityManager.getInstance().clear();
        slightlyWarmMail.clearMessages();

        this.world = gsm.game().recreateWorld();
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new ContactSystem());

        test = new Level();
        test.create(world);


        if(params.containsKey("Level")){
            test.loadLevel((String)params.get("Level"));
        }else{
            //default
            test.loadLevel("blacklevel.lvl");
        }

        slightlyWarmMail.update();


         /**
          * Create cars
          */
        factory = new Factory(currentGameMode);
        MyInputAdapter playerInput;
        testCar = factory.produceCarECS(playerInput = new MyInputAdapter());
        factory.addComponentsForGameMode(testCar);
        testCar.addComponent(new CameraAttachmentComponent());
        MessageManager.getInstance().addMessage(INTENT.SPAWN, testCar);


        if(params.containsKey("number_of_cars")){
            System.out.println(params.get("number_of_cars"));
        }

        if(params.containsKey("number_of_bots")){
            int j =  ((Integer)params.get("number_of_bots"));
            createBots(j);
        }else{
            createBots(2);
        }
        if(currentGameMode.equals("Capture the Flag")) {
            MessageManager.getInstance().addMessage(INTENT.SPAWN, factory.makeFlag());
        }
         MessageManager.getInstance().addMessage(INTENT.SPAWN, factory.makeBeachBall());


        debugRenderer = new Box2DDebugRenderer();
        renderSystem = new RenderSystem();
        setInputProcessor(playerInput);
    }

    private void createBots(int num_Bots){
        Entity aiCar;
        for(int  i = 0; i < num_Bots; i++){
            aiCar = factory.produceCarECS(new AI());
            factory.addComponentsForGameMode(aiCar);
            MessageManager.getInstance().addMessage(INTENT.SPAWN, aiCar);
        }
    }

    float zoom;
    public void zoom(OrthographicCamera cam, float amount) {
        zoom += amount;
        zoom = Math.min(Math.max(zoom, 0.1f), 5f);
        cam.zoom = zoom;
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
        // System.out.println("Entities: " + entities.size() + " Box2DBodies " + world.getBodyCount());

        inputSystem.update(EntityManager.getInstance().entityList());
        powerupSystem.update();
        //steeringSystem.update(entities);
        slightlyWarmMail.update();
        flagUpdateSystem.update(EntityManager.getInstance().getEntitiesWithComponent(FlagComponent.class));
        selfDestructTimerSystem.update(EntityManager.getInstance().getEntitiesWithComponent(SelfDestructTimerComponent.class));
        cameraSystem.update(EntityManager.getInstance().getEntitiesWithComponent(CameraAttachmentComponent.class), cam);
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();

        bf.draw(sp, "TESTBED: " + currentGameMode, 10, 50);
        sp.setProjectionMatrix(hudcam.combined);
        if(gameover)
            bf.draw(sp, "GAME OVER (ESC TO EXIT)", hudcam.viewportWidth/2, hudcam.viewportHeight/2);

        sp.end();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        renderSystem.render(EntityManager.getInstance().entityList(), batch);

        batch.end();

        debugRenderer.render(world, cam.combined);
    }

    @Override
    public void dispose() {


    }

    @Override
    public void processMessage(INTENT intent, Object... parameters) {
        if(intent == INTENT.WIN_COND_MET){
            List<UUID> uuids = EntityManager.getInstance().getEntitiesWithComponent(ControlledComponent.class);
            for(UUID uid : uuids){
                EntityManager.getInstance().removeComponent(uid, ControlledComponent.class);
            }
            gameover = true;
            /**
             * Move the camera to the capture point
             */
            uuids = EntityManager.getInstance().getEntitiesWithComponent(CameraAttachmentComponent.class);
            Vector2 src = new Vector2(0,0), tar = new Vector2(0,0);
            for(UUID uid : uuids){
                EntityManager.getInstance().removeComponent(uid, CameraAttachmentComponent.class);
                if(EntityManager.getInstance().hasComponent(uid, PhysicalComponent.class)){
                    src = EntityManager.getInstance().getComponent(uid, PhysicalComponent.class).getBody().getPosition();
                }
            }
            if(ctf_cond instanceof TeamCTFCondition) {
                int winners = ((TeamCTFCondition) ctf_cond).getWinningTeam();
                uuids = EntityManager.getInstance().getEntitiesWithComponents(TeamComponent.class, PhysicalComponent.class, TypeComponent.class);
                for (UUID uid : uuids) {
                    if (EntityManager.getInstance().getComponent(uid, TypeComponent.class).getType() == 2
                            && EntityManager.getInstance().getComponent(uid, TeamComponent.class).getTeamNumber() == winners) {
                        tar = EntityManager.getInstance().getComponent(uid, PhysicalComponent.class).getBody().getPosition();
                        EntityManager.getInstance().addComponent(uid, new CameraAttachmentComponent());
                        EntityManager.getInstance().getComponent(uid, CameraAttachmentComponent.class).initiateSlide(src, tar, 3000);
                    }
                }
            }
        }
    }





}
