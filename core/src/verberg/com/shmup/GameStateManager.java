package verberg.com.shmup;

/**
 * Created by Orion on 12/19/2015.
 */

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import java.util.Stack;

public class GameStateManager {

    private Stack<GameState> gameStates;
    private ShmupGame game;

    public static final int MAINMENU = 0;
    public static final int PLAY = 1;
    public static final int TEST = 2;
    public static final int EDITOR = 3;
    public static final int ENDGAME = 4;

    boolean popOnUpdate;


    public GameStateManager(ShmupGame game){
        this.game = game;
        gameStates = new Stack<GameState>();
        pushState(MAINMENU);
    }

    public void update(float dt){
            gameStates.peek().handleInput(); //check global input keys
            gameStates.peek().update(dt);



    }

    public void render(float dt){
        gameStates.peek().render(dt);
    }

    public ShmupGame game(){
        return game;
    }

    private GameState getState(int state){
        switch(state){
            case MAINMENU:  return new MainMenuGameState(this);
            case PLAY:      return new PlayGameState(this);
            case TEST:      return new TestGameState(this);
            case EDITOR:    return new LevelEditorGameState(this);
            case ENDGAME:   return new EndGameState(this);
            default:        return null;
        }
    }

    public void setState(int state){
        popState();
        pushState(state);
    }

    public void pushState(int state){
        gameStates.push(getState(state));
    }

    public void popState(){
        gameStates.pop().dispose();
        Gdx.input.setInputProcessor(gameStates.peek().inputProcessor);
    }
}