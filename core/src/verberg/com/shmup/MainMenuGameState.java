package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.GL20;
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
        });

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);
        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();

        bf.draw(sp, "1: Play, 2:Testbed 3: LevelEditor ESC (returns to this menu from other screen", 600,400);

        sp.end();
    }

    @Override
    public void dispose() {

    }
}
