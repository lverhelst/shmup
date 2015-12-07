package Editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import verberg.com.shmup.Constants;

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

    @Override
    public void create() {
        current_Tool = E_TOOL.WALL;
        sp = new SpriteBatch();
        bitmapFont = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();

        objects = new ArrayList<LevelObject>();

        Gdx.input.setInputProcessor(new EditorInputAdapter());

        loadLevel("defaultLevel");

    }

    private void update(){
        cam.update();
    }


    @Override
    public void render() {
        update();
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);




        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for(LevelObject lo  : objects) {
            shapeRenderer.setColor(lo.color);
            if(lo instanceof Node){
                shapeRenderer.circle(lo.x, lo.y, ((Node)lo).r);
                shapeRenderer.setColor(Color.GREEN);
                for(Node n : ((Node)lo).outNodes) {
                    shapeRenderer.line(lo.x, lo.y, n.x, n.y);
                }

            }else if (lo instanceof Wall){
                shapeRenderer.rect(lo.x, lo.y, ((Wall)lo).w, ((Wall)lo).h);
            }
        }
        if(current_Tool == E_TOOL.EDGE && activeObject != null && activeObject instanceof Node){
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.line(activeObject.x,activeObject.y,x,y);
        }

        shapeRenderer.end();

        sp.begin();
        bitmapFont.draw(sp, current_Tool.name(),50,3 * V_HEIGHT/4);
        sp.end();
    }

    int scale = 15;
    private int round(int num) {
        int temp = num % scale;
        if (temp < Math.floor(scale/2) - 1)
            return num-temp;
        else
            return num+ (scale - 1)-temp;
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

    public Wall createBox(float x, float y, float w, float h, float friction, float density) {
        return new Wall((int)x,(int)y,(int)w,(int)h);
    }

    public Node createCircle(float x, float y, float r, float friction, float density){
        return new Node((int)x,(int)y,(int)r);
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
            System.out.println(keycode + " " + Input.Keys.toString(keycode));
            switch(keycode){
                case Input.Keys.FORWARD_DEL:

                    activeObject.delete();
                    objects.remove(activeObject);
                    activeObject = null;
                    break;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            System.out.println(character);
            switch(character){
                case '1' : current_Tool = E_TOOL.SELECT;
                    break;
                case '2' : current_Tool = E_TOOL.WALL;
                    break;
                case '3' : current_Tool = E_TOOL.NODE;
                    break;
                case '4' : current_Tool = E_TOOL.EDGE;
                    break;

            }
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
           switch (current_Tool){
                case SELECT:
                    for (LevelObject n : objects) {
                        if(n.hit(screenX, V_HEIGHT - screenY)){
                            setActiveObject(n);
                        }
                    }
                    break;
                case WALL:
                    x = round(screenX) ;
                    y = round(V_HEIGHT - screenY);
                    w = 2;
                    h = 2;

                    Wall wall = createBox(x,y,w,h,0,0);
                    objects.add(wall);
                    setActiveObject(wall);

                    break;
                case NODE :
                    x = screenX ;
                    y = V_HEIGHT - screenY;
                    if(button == 0) {
                        Node n = createCircle((int) x, (int) y, (int)4, 0,0);
                        objects.add(n);
                        setActiveObject(n);
                    }
                    //if(button == 1)
                        //right click deletes a node?
                break;
               case EDGE:
                    x = screenX ;
                    y = V_HEIGHT - screenY;
                    if(button == 1){
                        setActiveObject(null);
                    }else {
                        if(activeObject == null || activeObject instanceof Node) {
                            for (LevelObject n : objects) {
                                if(n instanceof  Node) {
                                    if (n.hit(x, y)) {
                                        System.out.println(activeObject + "  " + n);
                                        if (activeObject == null) {
                                            setActiveObject(n);
                                        } else if (!n.equals(activeObject)) {
                                            ((Node)n).addEdge((Node) activeObject);
                                            ((Node) activeObject).addEdge(((Node)n));
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
            x = round(screenX) ;
            y = round(V_HEIGHT - screenY);
            if(current_Tool == E_TOOL.WALL){

                activeObject.resize(screenX - activeObject.x , (V_HEIGHT - screenY) - activeObject.y);
            }


            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {

            if(current_Tool == E_TOOL.WALL){
                activeObject.resize(screenX - activeObject.x , (V_HEIGHT - screenY) - activeObject.y);
            }
            x = round(screenX) ;
            y = round(V_HEIGHT - screenY);
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            if(current_Tool == E_TOOL.EDGE){
                x = screenX ;
                y = V_HEIGHT - screenY;
            }
            return false;
        }
    }
}
