package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Orion on 12/19/2015.
 */
public class MainMenuGameState extends GameState {

    BitmapFont bf;
    private Stage stage;
    final GameStateManager gsm2;
    HorizontalGroup horizontalGroup;
    GameModeParser gameModeParser;
    final Skin skin;

    public MainMenuGameState(GameStateManager gsm) {
        super(gsm);

        cam = new OrthographicCamera();
        cam.setToOrtho(false, ShmupGame.V_WIDTH, ShmupGame.V_HEIGHT);
        cam.zoom = 2f;
        cam.update();
        hudcam.zoom = 1f;
        hudcam.update();

        gsm2 = gsm;

        gameModeParser = new GameModeParser();

        /**
         * Start UI Menu
         */
        bf = new BitmapFont();
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        stage = new Stage(new ScreenViewport());

        horizontalGroup = new HorizontalGroup();
        horizontalGroup.setSize(stage.getWidth(), stage.getHeight());
        horizontalGroup.debug();
        horizontalGroup.addActor(setUpMenuLevel1());
        stage.addActor(horizontalGroup);


        setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {

            @Override
            public boolean keyUp(int keycode) {

                switch (keycode) {
                    case Input.Keys.NUM_1:
                        gsm2.pushState(1, null);
                        break;
                    case Input.Keys.NUM_2:
                        gsm2.pushState(2, null);
                        break;
                    case Input.Keys.NUM_3:
                        gsm2.pushState(3, null);
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

                // zoom(hudcam, 0.1f * amount);
                return false;
            }

            public void zoom(OrthographicCamera cam, float amount) {
                cam.zoom += amount;
                cam.zoom = Math.min(Math.max(cam.zoom, 0.1f), 10f);
            }
        }));


    }

    @Override
    public void handleInput() {

    }

    @Override
    public void update(float dt) {
        cam.update();
        hudcam.update();
        stage.act();
    }


    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();

        SpriteBatch sp = this.gsm.game().getBatch();

        sp.begin();
        sp.setProjectionMatrix(hudcam.combined);


        sp.end();
    }

    @Override
    public void dispose() {

    }


    private Table setUpMenuLevel1() {
        Label titleLabel = new Label("Main Menu", skin);
        // stage.addActor(titleLabel);
        Table table = new Table();


        table.setSize(stage.getWidth() / 3, stage.getHeight());
        table.setPosition(0, 0);
        table.debug();//show debug shit

        table.add(titleLabel);
        table.row();

        TextButton host = new TextButton("Host Game", skin);
        host.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setUpMenuLevel2();
                return false;
            }
        });

        table.add(host);
        table.row();

        TextButton join = new TextButton("Join Game", skin);
        join.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                setUpMenuLevel2();
                return false;
            }
        });
        //TODO: Multiplayer
        join.setDisabled(true);
        join.setTouchable(Touchable.disabled);
        table.add(join);
        table.row();

        TextButton lvlEdit = new TextButton("Level Editor", skin);
        lvlEdit.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                gsm2.pushState(3, null);
                return false;
            }
        });
        table.add(lvlEdit);
        table.row();

        return table;

    }

    private Table setUpMenuLevel2() {
        Label titleLabel = new Label("Choose Game Mode", skin);
        Table table = new Table();


        table.setSize(stage.getWidth() / 3, stage.getHeight());
        table.setPosition(0, 0);
        table.debug();//show debug shit

        table.add(titleLabel);
        table.row();


        for(String gm : gameModeParser.getGameModesList()){
            final TextButton gameModeBtn = new TextButton(gm, skin);
            final String gm2 = gm;
            gameModeBtn.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    setUpMenuLevel3(gm2, gameModeParser.getParametersForMode(gm2));
                    return false;
                }
            });
            table.add(gameModeBtn);
            table.row();
        }
        horizontalGroup.addActor(table);
        return table;
    }

    private Table setUpMenuLevel3(String gm, ArrayList<GameModeParameter> params) {
        Label titleLabel = new Label("Set Parameters: " + gm, skin);
        Table table = new Table();


        table.setSize(stage.getWidth() / 3, stage.getHeight());
        table.setPosition(0, 0);
        table.debug();//show debug shit

        table.add(titleLabel);
        table.row();


        for(GameModeParameter parameter : params){

            table.add(parameter.getView(skin));
            table.row();
        }


        final TextButton goBtn = new TextButton("GO!", skin);
        final ArrayList<GameModeParameter> params2 = params;
        final String gm2 = gm;
        goBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                HashMap<String, Object> parameters  = new HashMap<String, Object>();
                for(GameModeParameter gmp : params2){
                    if(gmp instanceof GameModeParameter.IntParameter){
                        parameters.put(gmp.name, ((GameModeParameter.IntParameter) gmp).getValue());
                    }
                }
                for(GameModeParameter gmp : gameModeParser.getRulesForMode(gm2)){
                    if(gmp instanceof GameModeParameter.ComponentParameter){
                        parameters.put(gmp.name, gmp);
                    }
                }
                gsm2.pushState(2, parameters);

                return false;
            }
        });
        table.add(goBtn);
        table.row();

        horizontalGroup.addActor(table);
        return table;
    }



}
