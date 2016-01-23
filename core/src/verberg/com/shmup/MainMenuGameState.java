package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Orion on 12/19/2015.
 */
public class MainMenuGameState extends GameState {

    BitmapFont bf;

    public MainMenuGameState(GameStateManager gsm){
        super(gsm);
        bf = new BitmapFont();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, ShmupGame.V_WIDTH,ShmupGame.V_HEIGHT);
        cam.zoom = 2f;
        cam.update();
        hudcam.zoom = 2f;hudcam.update();

        final GameStateManager gsm2 = gsm;



        setInputProcessor(new InputAdapter(){

            @Override
            public boolean keyUp(int keycode) {

                switch(keycode){
                    case Input.Keys.NUM_1: gsm2.pushState(1);
                        break;
                    case Input.Keys.NUM_2: gsm2.pushState(2);
                        break;
                    case Input.Keys.NUM_3: gsm2.pushState(3);
                        break;
                }
                return false;
            }


            @Override
            public boolean scrolled(int amount) {
                //TODO: this is hard to make zoom based on pointer....
                //touchUp.set(mouseX, mouseY, 0);
                //cam.unproject(touchUp);

                //pan(cam, touchUp.x - cam.position.x, touchUp.y - cam.position.y);

                zoom(hudcam, 0.1f * amount);
                return false;
            }

            public void zoom(OrthographicCamera cam, float amount) {
                cam.zoom += amount;
                cam.zoom = Math.min(Math.max(cam.zoom, 0.1f), 10f);
            }
        });



    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        cam.update();
        hudcam.update();
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();
        sp.setProjectionMatrix(hudcam.combined);

        bf.draw(sp, "1: Play,\r\n 2:Testbed\r\n 3: LevelEditor  ESC (returns to this menu from other screen", hudcam.viewportWidth/2, hudcam.viewportHeight/2);

        sp.end();
    }

    @Override
    public void dispose() {

    }


}
