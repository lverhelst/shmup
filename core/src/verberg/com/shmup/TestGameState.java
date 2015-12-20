package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

import Factories.CarFactory;
import Level.Level;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.CameraAttachmentComponent;
import ecs.components.ControlledComponent;
import ecs.components.PhysicalComponent;
import ecs.subsystems.CameraSystem;
import ecs.subsystems.ContactSystem;
import ecs.subsystems.InputSystem;
import ecs.subsystems.PowerUpSystem;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.RenderSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import AI.AI;

import Level.NavigationNode;

/**
 * Created by Orion on 12/19/2015.
 */
public class TestGameState extends GameState {
    public static MessageManagement.MessageManager slightlyWarmMail = MessageManager.getInstance();
    BitmapFont bf;
    InputSystem inputSystem = new InputSystem();
    CameraSystem cameraSystem = new CameraSystem();
    PowerUpSystem powerupSystem = new PowerUpSystem();
    RenderSystem renderSystem;
    Box2DDebugRenderer debugRenderer;
    Level test;
    private static World world;
    Entity testCar;


    public TestGameState(GameStateManager gsm){
        super(gsm);
        bf = new BitmapFont();

        slightlyWarmMail.addSystem(SteeringSystem.class, new SteeringSystem());
        slightlyWarmMail.addSystem(WeaponSystem.class, new WeaponSystem());
        slightlyWarmMail.addSystem(RemovalSystem.class, new RemovalSystem());
        slightlyWarmMail.addSystem(SpawnSystem.class, new SpawnSystem());



        this.world = gsm.game().getWorld();
        world.setVelocityThreshold(0.01f);
        world.setContactListener(new ContactSystem());

        test = new Level();
        test.create(world, "blacklevel.lvl");

        CarFactory carFactory = new CarFactory();
        testCar = carFactory.produceCarECS(new AI());
        testCar.addComponent(new CameraAttachmentComponent());




        debugRenderer = new Box2DDebugRenderer();
        renderSystem = new RenderSystem();


        setInputProcessor(new InputAdapter(){


            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {


                Vector3 touchDown = new Vector3();
                touchDown.set(screenX, screenY, 0);
                cam.unproject(touchDown);

                NavigationNode n = new NavigationNode((int)touchDown.x,(int) touchDown.y, 3);
                ((AI)testCar.get(ControlledComponent.class).ig).setTarget(new Entity(new PhysicalComponent(n.createBox2dBody(world))));
                Level.getNavNodes().add(n);



                return false;// super.touchUp(screenX, screenY, pointer, button);
            }
        });

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

        cameraSystem.update(EntityManager.getInstance().entityList(), cam);
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();

        bf.draw(sp, "TEST GAME STATE", 600, 400);

        sp.end();





        ShapeRenderer shapeRenderer = gsm.game().getShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);



            Entity debug = testCar;
            Body entity = debug.get(PhysicalComponent.class).getBody();

            float adjustX = (float) (Math.cos(entity.getAngle() + Math.PI / 2) * 60f / Constants.PPM + entity.getPosition().x);

            float adjustY = (float) (Math.sin(entity.getAngle() + Math.PI / 2) * 60f / Constants.PPM + entity.getPosition().y);

            shapeRenderer.line(entity.getPosition().x, entity.getPosition().y, adjustX, adjustY);

            shapeRenderer.setColor(Color.CYAN);
            ArrayList<Vector2> temp = ((AI) debug.get(ControlledComponent.class).ig).path;
            for (int i = 0; i < temp.size(); i++) {
                if (i >= 1) {
                    shapeRenderer.line(temp.get(i - 1).x, temp.get(i - 1).y, temp.get(i).x, temp.get(i).y);
                    //System.out.print(temp.get(i - 1) + " " + temp.get(i) + "|");
                }
            }

            shapeRenderer.setColor(Color.FIREBRICK);
            if (temp.size() > 1)
                shapeRenderer.line(temp.get(0).x, temp.get(0).y, temp.get(temp.size() - 1).x, temp.get(temp.size() - 1).y);

        shapeRenderer.end();






        debugRenderer.render(world, cam.combined);
    }

    @Override
    public void dispose() {


    }


}