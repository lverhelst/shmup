package verberg.com.shmup;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;


import java.util.List;
import java.util.UUID;

import Input.MyInputAdapter;
import Factories.CarFactory;
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
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import ecs.subsystems.CameraSystem;
import ecs.subsystems.ContactSystem;
import ecs.subsystems.FlagUpdateSystem;
import ecs.subsystems.InputSystem;
import ecs.subsystems.PowerUpSystem;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.RenderSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import AI.AI;
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
    RenderSystem renderSystem;
    Box2DDebugRenderer debugRenderer;
    Level test;
    private static World world;
    Entity testCar;
    boolean gameover;
    TeamCTFCondition ctf_cond;

    public TestGameState(GameStateManager gsm){
        super(gsm);
        bf = new BitmapFont();

        slightlyWarmMail.clear();
        gameover = false;
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
        slightlyWarmMail.registerSystem(INTENT.SPAWN, new SpawnSystem());
        slightlyWarmMail.registerSystem(INTENT.ADDSPAWN, new SpawnSystem());

        slightlyWarmMail.registerSystem(INTENT.TEAM_CAPTURE, ctf_cond = new TeamCTFCondition(2, 3));
        slightlyWarmMail.registerSystem(INTENT.WIN_COND_MET, this);

        EntityManager.getInstance().clear();
        slightlyWarmMail.clearMessages();

        this.world = gsm.game().recreateWorld();
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new ContactSystem());

        test = new Level();
        test.create(world, "blacklevel.lvl");

        slightlyWarmMail.update();

        CarFactory carFactory = new CarFactory();
        MyInputAdapter playerInput;



        testCar = carFactory.produceCarECS(playerInput = new MyInputAdapter());
        testCar.addComponent(new CameraAttachmentComponent());

        for(int  i = 0; i < 5; i++){
            carFactory.produceCarECS(new AI());
        }


        MessageManager.getInstance().addMessage(INTENT.SPAWN, carFactory.makeFlag());



        debugRenderer = new Box2DDebugRenderer();
        renderSystem = new RenderSystem();

        setInputProcessor(playerInput);


        carFactory.spawnBeachBall();

/*
        setInputProcessor(new InputAdapter(){


            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {


                Vector3 touchDown = new Vector3();
                touchDown.set(screenX, screenY, 0);
                cam.unproject(touchDown);

                NavigationNode n = new NavigationNode((int)touchDown.x,(int) touchDown.y, 3);
                ((AI)testCar.get(ControlledComponent.class).ig).setTarget(new Entity(new PhysicalComponent(n.createBox2dBody(world))));
                Level.addNavigationNode(n);

                return false;// super.touchUp(screenX, screenY, pointer, button);
            }


            @Override
            public boolean scrolled(int amount) {
                zoom(cam, 0.1f * amount);
                return false;
            }


        });*/

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

        cameraSystem.update(EntityManager.getInstance().entityList(), cam);
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();

        bf.draw(sp, "TESTBED", 50, 50);
        sp.setProjectionMatrix(hudcam.combined);
        if(gameover)
            bf.draw(sp, "GAME OVER (ESC TO EXIT)", hudcam.viewportWidth/2, hudcam.viewportHeight/2);

        sp.end();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        renderSystem.render(EntityManager.getInstance().entityList(), batch);

        batch.end();



/*

        ShapeRenderer shapeRenderer = gsm.game().getShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.setColor(Color.CYAN);


            Entity debug = testCar;
            Body entity = debug.get(PhysicalComponent.class).getBody();

            float adjustX = (float) (Math.cos(entity.getAngle() + Math.PI / 2) * 20f / Constants.PPM + entity.getPosition().x);

            float adjustY = (float) (Math.sin(entity.getAngle() + Math.PI / 2) * 20f / Constants.PPM + entity.getPosition().y);

            shapeRenderer.line(entity.getPosition().x, entity.getPosition().y, adjustX, adjustY);

           ArrayList<Vector2> temp = ((AI) debug.get(ControlledComponent.class).ig).path;

            if(temp != null) {
                float pathdist = 0;
                for (int i = 0; i < temp.size(); i++) {
                    if (i >= 1) {
                        shapeRenderer.line(temp.get(i - 1).x, temp.get(i - 1).y, temp.get(i).x, temp.get(i).y);
                        //System.out.print(temp.get(i - 1) + " " + temp.get(i) + "|");
                        pathdist += Vector2.dst2(temp.get(i - 1).x, temp.get(i - 1).y, temp.get(i).x, temp.get(i).y);
                    }
                }

                //  System.out.println("Path length" + pathdist);

                shapeRenderer.setColor(Color.FIREBRICK);
                if (temp.size() > 1)
                    shapeRenderer.line(temp.get(0).x, temp.get(0).y, temp.get(temp.size() - 1).x, temp.get(temp.size() - 1).y);
            }
        //right
        shapeRenderer.setColor(Color.YELLOW);
        adjustX = (float)(Math.cos(entity.getAngle() + Math.toRadians(-1 * 15) + Math.PI/2) * 1f +  entity.getPosition().x);
        adjustY = (float)(Math.sin(entity.getAngle() + Math.toRadians(-1 * 15) + Math.PI/2) * 1f +  entity.getPosition().y);

        shapeRenderer.line(entity.getPosition().x, entity.getPosition().y, adjustX, adjustY);

        //left
        shapeRenderer.setColor(Color.LIME);
        adjustX = (float)(Math.cos(entity.getAngle() + Math.toRadians(1 * 15) + Math.PI/2) * 1f +  entity.getPosition().x);
        adjustY = (float)(Math.sin(entity.getAngle() + Math.toRadians(1 * 15) + Math.PI/2) * 1f +  entity.getPosition().y);

        shapeRenderer.line(entity.getPosition().x, entity.getPosition().y, adjustX, adjustY);



        shapeRenderer.end();

        */

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
            int winners = ctf_cond.getWinningTeam();
            uuids = EntityManager.getInstance().getEntitiesWithComponents(TeamComponent.class, PhysicalComponent.class, TypeComponent.class);
            for(UUID uid : uuids){
                if(EntityManager.getInstance().getComponent(uid, TypeComponent.class).getType() == 2
                            && EntityManager.getInstance().getComponent(uid, TeamComponent.class).getTeamNumber() == winners){
                    tar = EntityManager.getInstance().getComponent(uid, PhysicalComponent.class).getBody().getPosition();
                    EntityManager.getInstance().addComponent(uid, new CameraAttachmentComponent());
                    EntityManager.getInstance().getComponent(uid, CameraAttachmentComponent.class).initiateSlide(src, tar, 3000);
                }
            }
        }
    }
}
