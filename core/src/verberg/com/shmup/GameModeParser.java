package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.ArrayList;

import static verberg.com.shmup.GameModeParameter.*;

/**
 * Created by Orion on 1/24/2016.
 */
public class GameModeParser {

    private String containingFile = "gamemodes";
    JsonReader jr = new JsonReader();
    JsonValue rootValue;


    public GameModeParser() {
        FileHandle fileHandle = Gdx.files.internal(containingFile);
        rootValue = jr.parse(fileHandle);
    }


    public ArrayList<String> getGameModesList(){

        JsonValue obg  = rootValue.get("gamemode");
        ArrayList<String> modes = new ArrayList<String>();
        do{
            String s = obg.child().asString();
            modes.add(s);
            System.out.println(s);
        }while((obg = obg.next()) != null);
        return modes;
    }

    public ArrayList<GameModeParameter> getParametersForMode(String gameMode) {
        JsonValue obg  = rootValue.get("gamemode");
        do{
            if(obg.child().asString().equals(gameMode)){
                obg = obg.child();
                break;
            }
        }while((obg = obg.next()) != null);

        //next = parameters

        obg = obg.next();
        //child = param 1
        //from child, next = param i
        ArrayList<GameModeParameter> params = new ArrayList<GameModeParameter>();
        obg = obg.child();
        do{
            String s = obg.getString("type");
            if (s.equals("int")) {
                params.add(new GameModeParameter.IntParameter(obg));
            } else if (s.equals("string")) {

            } else if (s.equals("spawnables")) {

            } else if (s.equals("powerups")) {

            } else if (s.equals("meh")) {

            } else if (s.equals("nu-huh")) {

            } else {
                System.out.println("Unknown type: " + s);
            }
        }while((obg = obg.next()) != null);

        return params;
    }

    //Rules are just parameters the player can't see and have no control over.
    public ArrayList<GameModeParameter> getRulesForMode(String gameMode) {
        if(true) //fake out the compiler
            return null; //this isn't used currently
        JsonValue obg  = rootValue.get("gamemode");
        do{
            if(obg.child().asString().equals(gameMode)){
                obg = obg.child();
                break;
            }
        }while((obg = obg.next()) != null);

        //next = parameters
        obg = obg.next();
        //next = rules
        obg = obg.next();
        //child = rule 1
        //from child, next = rule i
        ArrayList<GameModeParameter> params = new ArrayList<GameModeParameter>();
        obg = obg.child();
        do{
            String s = obg.getString("type");
            if (s.equals("int")) {
                params.add(new GameModeParameter.IntParameter(obg));
            } else if (s.equals("string")) {

            } else if (s.equals("spawnables")) {

            } else if (s.equals("powerups")) {

            } else if (s.equals("component")) {
                params.add(new GameModeParameter.ComponentParameter(obg));
            } else if (s.equals("conditions")) {


            } else {
                System.out.println("Unknown type: " + s);
            }
        }while((obg = obg.next()) != null);

        return params;

    }
}
