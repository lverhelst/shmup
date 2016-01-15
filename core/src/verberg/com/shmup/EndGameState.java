package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by Orion on 1/13/2016.
 */
public class EndGameState extends GameState {

    BitmapFont bf;

    public EndGameState(GameStateManager gsm){
        super(gsm);
        bf = new BitmapFont();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, ShmupGame.V_WIDTH,ShmupGame.V_HEIGHT);
        cam.update();


        final GameStateManager gsm2 = gsm;
        setInputProcessor(new InputAdapter(){

            @Override
            public boolean keyUp(int keycode) {

                switch(keycode){
                    //pop current state (end) and state before (whatever came before end)
                    case Input.Keys.NUM_1: gsm2.popState(); gsm2.popState();
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

                zoom(cam, 0.1f * amount);
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
    }

    @Override
    public void render(float dt) {
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.setProjectionMatrix(cam.combined);
        sp.begin();

        bf.draw(sp, "Game Over (Press 1 to return to Main Menu)", 6,4);

        sp.end();
    }

    @Override
    public void dispose() {

    }


}
