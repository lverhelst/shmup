package Editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Orion on 12/3/2015.
 *
 */
public class LevelEditor extends ApplicationAdapter {
    /**
     * Things this has got to do:
     *  Load level from file
     *  Save to file
     *
     *  Click and drag boxes (wall tool)
     *  Click and create node (node tool)
     *  Click a node, then a second node and link them (edge tool)
     *
     *
     *  Grid won't be an actual grid, just viewport/some_reasonable_grid_amount, 100? 50?
     *
     *
     */



    enum E_TOOL {
        SELECT,
        TRANSLATE,
        WALL,
        NODE,
        EDGE
    }
    public static final int V_WIDTH = 620;
    public static final int V_HEIGHT = 480;


    private E_TOOL current_Tool;
    private int x, y, w, h;
    private ArrayList<LevelObject> objects;
    private LevelObject activeObject;

    ShapeRenderer shapeRenderer;

    BitmapFont bitmapFont;
    SpriteBatch sp;
    OrthographicCamera cam;
    private int gridSize, width, height;
    private float scale, zoom;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        sp = new SpriteBatch();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();

        objects = new ArrayList<LevelObject>();
        Gdx.input.setInputProcessor(new EditorInputAdapter());

        current_Tool = E_TOOL.WALL;
        gridSize = 20;
        width = V_WIDTH;
        height = V_HEIGHT;
        scale = 1;
        zoom = 1;

        loadLevel("savedlevel.lvl");

    }

    private void update(){
        cam.update();
    }


    @Override
    public void render() {
        update();
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);

        for(int x = 0; x < V_WIDTH; x += gridSize) {
            shapeRenderer.line(x,0, x, height);
        }

        for(int y = 0; y < V_HEIGHT; y += gridSize) {
            shapeRenderer.line(0,y, width, y);
        }

        for(LevelObject lo  : objects) {
            lo.render(shapeRenderer);
        }

        if(current_Tool == E_TOOL.EDGE && activeObject != null && activeObject instanceof NavigationNode){
            shapeRenderer.setColor(Color.GREEN);

            Vector3 worldCoordinates = new Vector3(x,y,0);
            cam.unproject(worldCoordinates);
            shapeRenderer.line(activeObject.x,activeObject.y, worldCoordinates.x, worldCoordinates.y);
        }

        shapeRenderer.end();

        sp.begin();
        bitmapFont.draw(sp, current_Tool.name(),50,3 * V_HEIGHT/4);
        sp.end();
    }

    private int round(int num) {
        int temp = (int)num % gridSize;
        if (temp < Math.floor(gridSize/2))
            return num-temp;
        else
            return num+ (gridSize - 1)-temp;
    }

    public void loadLevel(String levelName){
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(Gdx.files.internal((levelName)));

        JsonValue map = json.get("level");
        JsonValue groups = map.get("groups");
        float[] offset;

        for(JsonValue group : groups) {
            offset = group.get("location").asFloatArray();
            loadShapes(group, offset[0], offset[1]);
        }

        loadShapes(map, 0, 0);
        loadNodes(map);
    }


    public void loadShapes(JsonValue shapes, float x, float y) {
        Body body = null;
        JsonValue types = shapes.get("blocks");
        JsonValue spawns = shapes.get("spawns");

        if(types != null) {
            for (JsonValue shape : types) {
                String type = shape.get("type").asString();
                JsonValue jsonName = shape.get("name");
                float[] pos = shape.get("location").asFloatArray();
                float friction = shape.get("friction").asFloat();
                float density = shape.get("density").asFloat();

                if(type.equals("box")) {
                    float[] size = shape.get("size").asFloatArray();
                    objects.add(createBox(pos[0] + x, pos[1] + y, size[0], size[1], friction, density));

                } else if(type.equals("circle")) {
                    float radius = shape.get("radius").asFloat();
                    objects.add(createCircle(x,y,radius,0,0));
                }

                //Only add named blocks
                if(jsonName != null && body != null) {
                    String name = shape.get("name").asString();
                }
            }
        }
    }

    public void loadNodes(JsonValue maps){
        JsonValue nodes = maps.get("nodes");

        HashMap<UUID, NavigationNode> createdNodes = new HashMap<UUID, NavigationNode>();
        HashMap<UUID, ArrayList<UUID>> edges = new HashMap<UUID, ArrayList<UUID>>();


        for(JsonValue value : nodes){
            float[] pos = value.get("location").asFloatArray();
            float radius = value.get("r:").asFloat();
            UUID uuid = UUID.fromString(value.getString("id"));
            NavigationNode n = createCircle(pos[0], pos[1], radius, 0, 0, uuid);
            createdNodes.put(uuid, n);
            ArrayList<UUID> nodeIDs = new ArrayList<UUID>();
            for(String s : value.get("outNavigationNodes").asStringArray()){
                    nodeIDs.add(UUID.fromString(s));
            }
            edges.put(uuid, nodeIDs);
            objects.add(n);
        }

        for(NavigationNode n : createdNodes.values()){
            for(UUID uid : edges.get(n.id)){
                n.addEdge(createdNodes.get(uid));
            }

        }

    }


    public void saveLevel(){


        ArrayList<Wall> walls = new ArrayList<Wall>();
        ArrayList<NavigationNode> navigationNodes = new ArrayList<NavigationNode>();
        for(LevelObject lo : objects){
            if(lo instanceof  Wall){
                walls.add((Wall)lo);
            }
            if(lo instanceof NavigationNode){
                navigationNodes.add((NavigationNode)lo);
            }
        }



        String jsonString = "{\"level\":{";
        jsonString += "\"name\": \"writtenLeevel\",";

        //start groups
        jsonString += "\"groups\":[],";


        //start walls
        jsonString += "\"blocks\":[";

        // {	"type" : box, "location" : [41.5, 50], 		"size" : [2, 	96],	"friction" : 1, "density" : 1, 	"dynamic" : false },
        for(int i = 0; i < walls.size();i++ ){
            jsonString += "{\"type\":\"box\",\"location\":[" + walls.get(i).x +"," + walls.get(i).y +"]," +
                     "\"size\": [" + walls.get(i).w + "," + walls.get(i).h + "],"  +
                    "\"friction\": 1, \"density\": 1, \"dynamic\":false" +
                     "}" + (i < walls.size() - 1 ? "," : "");
        }
        jsonString += "],";
        //end walls



        //start Nodes
        jsonString += "\"navigationNodes\":[";

        for(int i = 0; i < navigationNodes.size(); i++){
            jsonString += "{\"id\": \"" + navigationNodes.get(i).id +  "\", \"location\": [" + navigationNodes.get(i).x + "," + navigationNodes.get(i).y + "],\"r:\":" + navigationNodes.get(i).r  + ", \"type\":[\"spawn\",\"nav\",\"powerup\"], \"outNavigationNodes\":[";
                        for(int j = 0; j < navigationNodes.get(i).outNavigationNodes.size(); j++){
                            jsonString += "\"" + navigationNodes.get(i).outNavigationNodes.get(j).id + "\"" + (j < navigationNodes.get(i).outNavigationNodes.size() -1 ? ",":"");
                        }
            jsonString += "]}" + (i < navigationNodes.size() -1 ? ",":"") + "\r\n";
        }
        jsonString += "]";
        //end navigationNodes
        jsonString += "\r\n}\r\n}";

        Json jsonPrettyPrinter = new Json();
        System.out.println(jsonPrettyPrinter.prettyPrint(jsonString));

        FileHandle file = Gdx.files.local("savedlevel.lvl");
        file.writeString(jsonString, false);//false == overwrite

    }

    public Wall createBox(float x, float y, float w, float h, float friction, float density) {
        return new Wall((int)x,(int)y,(int)w,(int)h);
    }

    public NavigationNode createCircle(float x, float y, float r, float friction, float density){
        return new NavigationNode((int)x,(int)y,(int)r);
    }

    public NavigationNode createCircle(float x, float y, float r, float friction, float density, UUID uuid){
        return new NavigationNode((int)x,(int)y,(int)r, uuid);
    }

    private void setActiveObject(LevelObject o){
        if(activeObject != null)
            activeObject.unselect();

        activeObject = o;
        if(o != null)
            o.select();
    }


    class EditorInputAdapter extends InputAdapter {
        @Override
        public boolean keyUp(int keycode) {
            switch(keycode){
                case Input.Keys.FORWARD_DEL:
                    if(activeObject != null) {
                        activeObject.delete();
                        objects.remove(activeObject);
                        activeObject = null;
                    }
                    break;
                case Input.Keys.S:
                    if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
                        //save: ctrl+S
                        saveLevel();
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            switch(character){
                case '1' : current_Tool = E_TOOL.SELECT;
                    break;
                case '2' : current_Tool = E_TOOL.WALL;
                    break;
                case '3' : current_Tool = E_TOOL.NODE;
                    break;
                case '4' : current_Tool = E_TOOL.EDGE;
                    break;
                case '[':
                    zoom -= 0.05;
                    break;
                case ']':
                    zoom += 0.05;
                    break;
                case '=':
                case '+':
                    gridSize += 1;
                    break;
                case '-':
                    gridSize -= 1;
                    break;
            }

            zoom = Math.min(Math.max(zoom, 0.1f), 5f);
            gridSize = Math.min(Math.max(gridSize, 1), 10);
            cam.zoom = zoom;

            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            x = screenX ;
            y = screenY;
            Vector3 worldCoordinates = new Vector3(x,y,0);
            cam.unproject(worldCoordinates);

            switch (current_Tool){
                case SELECT:
                    for (LevelObject n : objects) {
                        if(n.contains((int)worldCoordinates.x, (int)worldCoordinates.y)){
                            if(n == activeObject) {
                                current_Tool = E_TOOL.TRANSLATE;
                            } else {
                                setActiveObject(n);
                            }
                        }
                    }
                    break;
                case WALL:
                    w = 2;
                    h = 2;

                    Wall wall = createBox(round((int)worldCoordinates.x),round((int)worldCoordinates.y),w,h,0,0);
                    objects.add(wall);
                    setActiveObject(wall);

                    break;
                case NODE :
                    if(button == 0) {
                        NavigationNode n = createCircle(round((int)worldCoordinates.x), round((int) worldCoordinates.y), (int)4, 0,0);
                        objects.add(n);
                        setActiveObject(n);
                    }
                    //if(button == 1)
                    //right click deletes a node?
                break;
               case EDGE:
                    if(button == 1){
                        setActiveObject(null);
                    }else {
                        if(activeObject == null || activeObject instanceof NavigationNode) {
                            for (LevelObject n : objects) {
                                if(n instanceof NavigationNode) {


                                    if (n.contains((int)worldCoordinates.x, (int)worldCoordinates.y)) {
                                        if (activeObject == null) {
                                            setActiveObject(n);


                                        } else if (!n.equals(activeObject)) {
                                            ((NavigationNode)n).addEdge((NavigationNode) activeObject);
                                            ((NavigationNode) activeObject).addEdge(((NavigationNode)n));
                                            setActiveObject(null);
                                        }
                                    }
                                }
                            }
                        }
                    }
                break;

            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            x = screenX ;
            y = screenY;
            Vector3 worldCoordinates = new Vector3(x,y,0);
            cam.unproject(worldCoordinates);

            if(current_Tool == E_TOOL.WALL){
                activeObject.resize(round((int)worldCoordinates.x - activeObject.x), round((int)worldCoordinates.y - activeObject.y));
            }

            if(current_Tool == E_TOOL.TRANSLATE) {
                activeObject.move(round((int)worldCoordinates.x), round((int)worldCoordinates.y));
                current_Tool = E_TOOL.SELECT;
            }

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            x = round(screenX) ;
            y = round(screenY);
            Vector3 worldCoordinates = new Vector3(x,y,0);
            cam.unproject(worldCoordinates);
            if(current_Tool == E_TOOL.WALL){
                activeObject.resize(round((int)worldCoordinates.x - activeObject.x), round((int)worldCoordinates.y - activeObject.y));
            }

            if(current_Tool == E_TOOL.TRANSLATE) {
                activeObject.move(round((int) worldCoordinates.x), round((int) worldCoordinates.y));
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if(current_Tool == E_TOOL.EDGE){
                x = screenX ;
                y = screenY;
                Vector3 worldCoordinates = new Vector3(x,y,0);
                x = (int)worldCoordinates.x;
                y = (int)worldCoordinates.y;

            }
            return false;
        }
    }
}
