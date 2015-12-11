package Editor;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

/**
 * Created by Orion on 12/3/2015.

 * Things this has got to do:
 *  Load level from file
 *  Save to file
 *
 *  Click and drag boxes (wall tool)
 *  Click and create node (node tool)
 *  Click a node, then a second node and link them (edge tool)
 */
public class LevelEditor extends ApplicationAdapter {
    enum E_TOOL { SELECT, TRANSLATE, PAN, RECTANGLE, NODE, CIRCLE }
    private E_TOOL current_Tool;

    private int gridSize, width, height;
    private float zoom;
    private String filename;

    private static final int V_WIDTH = 620;
    private static final int V_HEIGHT = 480;

    private ArrayList<Selectable> levelShape;
    private ArrayList<Selectable> levelItems;
    private Selectable selection;

    private ShapeRenderer shapeRenderer;
    private BitmapFont bitmapFont;
    private SpriteBatch sp;
    private OrthographicCamera cam;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        bitmapFont = new BitmapFont();
        sp = new SpriteBatch();

        cam = new OrthographicCamera();
        cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
        cam.update();

        levelShape = new ArrayList<Selectable>();
        levelItems = new ArrayList<Selectable>();
        Gdx.input.setInputProcessor(new EditorInputAdapter());

        current_Tool = E_TOOL.SELECT;
        gridSize = 20;
        width = V_WIDTH;
        height = V_HEIGHT;
        zoom = 1;

        filename = "savedlevel2.lvl";
        loadLevel(filename);
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
            shapeRenderer.line(0, y, width, y);
        }

        for(Selectable shape  : levelShape) {
            shape.render(shapeRenderer);
        }

        for(Selectable item  : levelItems) {
            item.render(shapeRenderer);
        }

        shapeRenderer.end();

        sp.begin();
        bitmapFont.draw(sp, current_Tool.name(),50,3 * V_HEIGHT/4);
        sp.end();
    }

    private float round(float num) {
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
        loadPoints(map);
    }

    public void loadShapes(JsonValue shapes, float x, float y) {
        Body body = null;
        JsonValue types = shapes.get("blocks");

        if(types != null) {
            for (JsonValue shape : types) {
                String type = shape.get("type").asString();
                JsonValue jsonName = shape.get("name");
                float[] pos = shape.get("location").asFloatArray();
                float friction = shape.get("friction").asFloat();
                float density = shape.get("density").asFloat();

                if(type.equals("box")) {
                    float[] size = shape.get("size").asFloatArray();
                    levelShape.add(createBox(pos[0] + x, pos[1] + y, size[0], size[1], friction, density));

                } else if(type.equals("circle")) {
                    float radius = shape.get("radius").asFloat();
                    levelShape.add(createCircle(x,y,radius,0,0));
                }

                //Only add named blocks
                if(jsonName != null && body != null) {
                    String name = shape.get("name").asString();
                }
            }
        }
    }

    public void loadPoints(JsonValue maps){
        JsonValue points = maps.get("points");

        for(JsonValue value : points){
            float[] pos = value.get("location").asFloatArray();

            //TODO: add node handling after making needed changes
            levelItems.add(createCircle(pos[0], pos[1], 5, 0, 0));
        }
    }

    public void saveLevel() {
        String jsonString = "{\n\t\"level\":{\n";
        jsonString += "\t\t\"name\": \"testLevel\",\n";

        //print to separate sections
        String blocks = "\t\t\"blocks\" : [\n ";
        String items = "\t\t\"points\" : [\n ";

        //add to appropriate sections
        for(Selectable lo: levelShape) {
            blocks += "\t\t\t" + lo.toJson() + ",\n";
        }

        for(Selectable item: levelItems) {
            items += "\t\t\t" + item.toJson() + ",\n";
        }

        //remove last comma
        blocks = blocks.substring(0, blocks.length() - 2);
        items = items.substring(0, items.length() - 2);

        //finish sections
        blocks += "\n\t\t],\n";
        items += "\n\t\t]\n";

        jsonString += blocks + items + "\t}\n}";
        System.out.println(jsonString);
    }

    public Rectangle createBox(float x, float y, float w, float h, float friction, float density) {
        return new Rectangle(x,y,w,h);
    }

    public Circle createCircle(float x, float y, float r, float friction, float density){
        return new Circle(x,y,r);
    }

    private void setSelection(Selectable o){
        if(o != null && !o.isSelected()) {
            if(selection != null)
                selection.toggleSelect();
            o.toggleSelect();
        }

        selection = o;
    }

    class EditorInputAdapter implements InputProcessor {
        Vector3 worldCoordinates = new Vector3(0,0,0);
        float x, y, w, h;

        @Override
        public boolean keyDown(int keycode) {  return false; }

        @Override
        public boolean keyUp(int keycode) {
            switch(keycode){
                case Input.Keys.FORWARD_DEL:
                    if(selection != null) {
                        levelShape.remove(selection);
                        levelItems.remove(selection);
                        setSelection(null);
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
                case '2' : current_Tool = E_TOOL.RECTANGLE;
                    break;
                case '3' : current_Tool = E_TOOL.NODE;
                    break;
                case '4' : current_Tool = E_TOOL.CIRCLE;
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
            x = screenX;
            y = screenY;
            worldCoordinates.set(x,y,0);
            cam.unproject(worldCoordinates);

            switch (current_Tool){
                case SELECT:
                    for (Selectable n : levelShape) {
                        if(n.contains(worldCoordinates.x, worldCoordinates.y)){
                            if(n == selection) {
                                current_Tool = E_TOOL.TRANSLATE;
                            } else {
                                setSelection(n);
                            }
                        }
                    }
                    //This kind of sucks... but its a side effect of breaking them apart
                    for (Selectable n : levelItems) {
                        if(n.contains(worldCoordinates.x, worldCoordinates.y)){
                            if(n == selection) {
                                current_Tool = E_TOOL.TRANSLATE;
                            } else {
                                setSelection(n);
                            }
                        }
                    }
                    break;
                case RECTANGLE:
                    w = 2;
                    h = 2;

                    Selectable rectangle = createBox(round((int)worldCoordinates.x),round((int)worldCoordinates.y),w,h,0,0);
                    levelShape.add(rectangle);
                    setSelection(rectangle);
                    break;
                case NODE :
                    if(button == 0) {
                        Selectable node = createCircle(round((int)worldCoordinates.x), round((int) worldCoordinates.y), 4, 0,0);
                        levelItems.add(node);
                        setSelection(node);
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            x = screenX ;
            y = screenY;
            worldCoordinates.set(x, y, 0);
            cam.unproject(worldCoordinates);

            if(selection != null) {
                if (current_Tool == E_TOOL.RECTANGLE && selection instanceof Shape) {
                    ((Shape)selection).resize(round(worldCoordinates.x - selection.x), round(worldCoordinates.y - selection.y));
                }

                if (current_Tool == E_TOOL.TRANSLATE) {
                    selection.translate(round(worldCoordinates.x), round(worldCoordinates.y));
                    current_Tool = E_TOOL.SELECT;
                }
            }

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            x = round(screenX);
            y = round(screenY);
            worldCoordinates.set(x,y,0);
            cam.unproject(worldCoordinates);

            if(selection != null) {
                if (current_Tool == E_TOOL.RECTANGLE && selection instanceof Shape) {
                    ((Shape)selection).resize(round(worldCoordinates.x - selection.getX()), round(worldCoordinates.y - selection.getY()));
                }

                if (current_Tool == E_TOOL.TRANSLATE) {
                    selection.translate(round(worldCoordinates.x), round(worldCoordinates.y));
                }
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) { return false; }

        @Override
        public boolean scrolled(int amount) { return false; }
    }
}