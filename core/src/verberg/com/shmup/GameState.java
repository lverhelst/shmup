package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


/**
 * Created by Orion on 2/7/2015.
 */
public abstract class GameState {

    protected final GameStateManager gsm;
    protected ShmupGame shmupGame;

    protected SpriteBatch batch;
    protected OrthographicCamera cam;
    protected OrthographicCamera hudcam;
    protected InputProcessor inputProcessor;

    protected GameState(GameStateManager gsm1){
        this.gsm = gsm1;
        shmupGame = gsm1.game();
        batch = shmupGame.getBatch();
        cam = shmupGame.getCam();
        hudcam = shmupGame.getHudCam();
    }

    public void setInputProcessor(InputProcessor ip){
        this.inputProcessor = ip;
        Gdx.input.setInputProcessor(ip);
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render(float dt);
    public abstract void dispose();

}