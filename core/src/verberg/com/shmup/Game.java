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

import components.BodyComponent;
import components.Car;
import components.GameObject;
import components.MyInputAdapter;

public class Game extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;

    //move to static shit class
    private static final float STEP = 1/60f;
    public static final int V_WIDTH = 620/5;
    public static final int V_HEIGHT = 480/5;

    //move to render component
    Box2DDebugRenderer debugRenderer;
    OrthographicCamera cam;

    GameObject go;

	World world;
	Car car;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");

        world = new World(new Vector2(0f, 0f), true); //shmup bros has no downward gravity
        world.setVelocityThreshold(0.0f);


        //This seems back-asswards
        car = new Car(world);

      //  go = new GameObject(car.getBody());

        //debug renderer, make sure to move this later
        debugRenderer = new Box2DDebugRenderer();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);

        Gdx.input.setInputProcessor(new MyInputAdapter());
	}

	@Override
	public void render () {
        car.update();
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(STEP, 6, 2);
		//Gdx.gl.glClearColor(1, 0, 0, 1);
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//batch.begin();
		//batch.draw(img, 0, 0);
		//batch.end();
        debugRenderer.render(world,cam.combined);
	}
}
