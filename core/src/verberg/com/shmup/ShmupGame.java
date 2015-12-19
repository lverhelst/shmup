package verberg.com.shmup;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class ShmupGame extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera cam;
    OrthographicCamera hudCam;
    ShapeRenderer shapeRenderer;
    Texture img;
    static World world;

    //move to static variables class
    private static final float STEP = 1/60f;
    public static final int V_WIDTH = 1280/Constants.PPM;
    public static final int V_HEIGHT = 768/Constants.PPM;

    private GameStateManager gsm;


	@Override
	public void create () {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        world = new World(new Vector2(0f, 0f), true); //shmup bros has no downward gravity

        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();

        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        gsm = new GameStateManager(this);

	}



    @Override
    public void render () {
        gsm.update(STEP);
        gsm.render(STEP);
    }


    public SpriteBatch getBatch() {
        return batch;
    }

    public OrthographicCamera getCam() {
        return cam;
    }

    public OrthographicCamera getHudCam() {
        return hudCam;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public static World getWorld() {  return world; }
}
