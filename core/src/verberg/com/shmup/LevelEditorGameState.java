package verberg.com.shmup;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Field;
import java.util.ArrayList;

import Editor.Circle;
import Editor.Point;
import Editor.Rectangle;
import Editor.Selectable;
import Editor.Shape;

/**
 * Created by Orion on 12/19/2015.
 */
public class LevelEditorGameState extends GameState {

    enum E_TOOL { SELECT, PAN, TRANSLATE, SCALE, RECTANGLE, CIRCLE, POINT }
    private E_TOOL current_Tool;
    private Point.TYPE point_Type;
    private Shape.TYPE shape_Type;

    private int gridSize, width, height;
    private float zoom;
    private String filename;

    private static final int V_WIDTH = 620;
    private static final int V_HEIGHT = 480;

    private ArrayList<Selectable> levelObjects;
    private Selectable selection;

    private BitmapFont bitmapFont;

    private Stage stage;

    final Slider gridSlider, zoomSlider;

    public LevelEditorGameState(GameStateManager gsm){
        super(gsm);


        bitmapFont = new BitmapFont();
        levelObjects = new ArrayList<Selectable>();


        stage = new Stage();

        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        Label titleLabel = new Label("Level Editor", skin);
        stage.addActor(titleLabel);
        Table table = new Table();
        stage.addActor(table);
        table.setSize(V_WIDTH / 3, V_HEIGHT);
        table.setPosition(0, 0);
        table.debug();//show debug shit

        int i = 0;
        for(E_TOOL e: E_TOOL.values()){
            //for use in the input listener
            final E_TOOL temp = e;
            TextButton b = new TextButton(e.name(), skin);
            b.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    current_Tool = temp;
                    return false;
                }
            });

            if(++i % 2 == 0){
                table.row();
            }
            table.add(b);
        }


        table.row();

        table.add(new Label("Zoom", skin));
        zoomSlider = new Slider(0.1f,10.0f,0.1f,false, skin);
        zoomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                cam.zoom = zoom = zoomSlider.getValue();
            }
        });
        table.add(zoomSlider);


        table.row();

        table.add(new Label("Grid", skin));
        gridSlider = new Slider(1,20,1,false, skin);
        gridSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gridSize = (int) gridSlider.getValue();
            }
        });
        table.add(gridSlider);

        final List pointTypeList = new List(skin);
        pointTypeList.setItems(Point.TYPE.values());
        pointTypeList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                point_Type = (Point.TYPE)pointTypeList.getSelected();
            }
        });


        final List shapeTypeList = new List(skin);
        shapeTypeList.setItems(Shape.TYPE.values());
        shapeTypeList.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                shape_Type = (Shape.TYPE)shapeTypeList.getSelected();
            }
        });


        table.row();
        table.add(pointTypeList);

        table.row();
        table.add(shapeTypeList);



        current_Tool = E_TOOL.SELECT;
        point_Type = Point.TYPE.SPAWN;
        shape_Type = Shape.TYPE.WALL;

        gridSize = 10;
        width = V_WIDTH;
        height = V_HEIGHT;
        zoom = 1;

        cam.zoom = zoom;

        filename = "blacklevel.lvl";
        loadLevel(filename);

        setInputProcessor(new InputMultiplexer(stage, new EditorInputAdapter()));

    }

    @Override
    public void handleInput() {
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
            gsm.popState();
        }
    }

    @Override
    public void update(float dt) {
        gridSlider.setValue(gridSize);
        zoomSlider.setValue(cam.zoom);

        stage.act();
        cam.update();
    }

    @Override
    public void render(float dt) {
        Gdx.graphics.getGL20().glClear(GL20.GL_COLOR_BUFFER_BIT);

        ShapeRenderer shapeRenderer = this.gsm.game().getShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);

        float factor = zoom > 1 ? zoom : 1;
        for(int x = 0; x < V_WIDTH * factor; x += gridSize) {
            shapeRenderer.line(x,0, x, height * factor);
        }

        for(int y = 0; y < V_HEIGHT * factor; y += gridSize) {
            shapeRenderer.line(0, y, width * factor, y);
        }

        for(Selectable shape : levelObjects) {
            shape.render(shapeRenderer);
        }

        shapeRenderer.end();

        SpriteBatch sp = this.gsm.game().getBatch();
        sp.begin();
        bitmapFont.draw(sp, current_Tool.name(), 20, V_HEIGHT - 20);

        if(current_Tool == E_TOOL.POINT)
            bitmapFont.draw(sp, point_Type.name(), 20, V_HEIGHT - 40);

        if(current_Tool == E_TOOL.RECTANGLE || current_Tool == E_TOOL.CIRCLE)
            bitmapFont.draw(sp, shape_Type.name(), 20, V_HEIGHT - 40);

        if(current_Tool == E_TOOL.SELECT && selection != null) {
            if(selection instanceof Shape)
                bitmapFont.draw(sp, shape_Type.name(), 20, V_HEIGHT - 40);

            if(selection instanceof Point)
                bitmapFont.draw(sp, point_Type.name(), 20, V_HEIGHT - 40);
        }

        sp.end();

        stage.draw();
    }

    @Override
    public void dispose() {

    }


    public void loadLevel(String levelName){
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(Gdx.files.internal((levelName)));

        JsonValue map = json.get("level");
        JsonValue groups = map.get("groups");
        float[] offset;

        if(groups != null) {
            for (JsonValue group : groups) {
                offset = group.get("location").asFloatArray();
                loadShapes(group, offset[0], offset[1]);
            }
        }

        loadShapes(map, 0, 0);
        loadPoints(map);
    }

    public void loadShapes(JsonValue shapes, float x, float y) {
        Body body = null;
        JsonValue types = shapes.get("shapes");

        if(types != null) {
            for (JsonValue shapeVal : types) {
                String shape = shapeVal.get("shape").asString();
                String type = shapeVal.get("type").asString();

                float[] pos = shapeVal.get("location").asFloatArray();
                float friction = shapeVal.get("friction").asFloat();
                float density = shapeVal.get("density").asFloat();

                JsonValue jsonName = shapeVal.get("name");
                Shape.TYPE typeLookup = Shape.TYPE.WALL;

                //looking up enum type
                if(type.equals("GROUND")) {
                    typeLookup = Shape.TYPE.GROUND;
                } else if(type.equals("DEATH")) {
                    typeLookup = Shape.TYPE.DEATH;
                }

                if(shape.equals("box")) {
                    float[] size = shapeVal.get("size").asFloatArray();
                    levelObjects.add(createBox(typeLookup, pos[0] + x, pos[1] + y, size[0], size[1], friction, density));

                } else if(shape.equals("circle")) {
                    float radius = shapeVal.get("radius").asFloat();
                    levelObjects.add(createCircle(typeLookup, pos[0] + x, pos[1] + y, radius, 0, 0));
                }

                //Only add named blocks
                if(jsonName != null && body != null) {
                    String name = shapeVal.get("name").asString();
                }
            }
        }
    }

    public void loadPoints(JsonValue maps){
        JsonValue points = maps.get("points");

        for(JsonValue value : points){
            float[] pos = value.get("location").asFloatArray();
            String type = value.get("type").asString();
            String subtype = value.get("subtype").asString();
            Point.TYPE typeLookup = Point.TYPE.SPAWN;


            //looking up enum type
            if(type.equals("NODE")) {
                typeLookup = Point.TYPE.NODE;
            } else if(type.equals("PICKUP")) {
                typeLookup = Point.TYPE.PICKUP;
            }
            levelObjects.add(createPoint(typeLookup, subtype, pos[0], pos[1]));
        }
    }

    public void saveLevel() {
        String jsonString = "{\n\t\"level\":{\n";
        jsonString += "\t\t\"name\": \"testLevel\",\n";

        //print to separate sections
        String blocks = "\t\t\"shapes\" : [\n ";
        String points = "\t\t\"points\" : [\n ";

        //add to appropriate sections
        for(Selectable lo: levelObjects) {
            if(lo instanceof Shape)
                blocks += "\t\t\t" + lo.toJson() + ",\n";
            else if(lo instanceof Point)
                points += "\t\t\t" + lo.toJson() + ",\n";
        }

        //remove last comma
        blocks = blocks.substring(0, blocks.length() - 2);
        points = points.substring(0, points.length() - 2);

        //finish sections
        blocks += "\n\t\t],\n";
        points += "\n\t\t]\n";

        jsonString += blocks + points + "\t}\n}";

        FileHandle file = Gdx.files.local(filename);
        file.writeString(jsonString, false);//false == overwrite
    }

    public Rectangle createBox(Shape.TYPE type, float x, float y, float w, float h, float friction, float density) {
        return new Rectangle(type,x,y,w,h);
    }

    public Circle createCircle(Shape.TYPE type, float x, float y, float r, float friction, float density){
        return new Circle(type,x,y,r);
    }

    public Point createPoint(Point.TYPE type, String subType, float x, float y){
        return new Point(type,subType,x,y);
    }

    private void setSelection(Selectable o){
        if(selection == null && o != null) {
            o.toggleSelect();
        }
        if(selection != null && o == null) {
            selection.toggleSelect();
        }
        if(selection != null && o != null && o != selection) {
            selection.toggleSelect();
            o.toggleSelect();
        }

        selection = o;
    }

    class EditorInputAdapter implements InputProcessor {
        private Vector3 touchDown = new Vector3(0,0,0);
        private Vector3 touchUp = new Vector3(0,0,0);
        private float mouseX, mouseY, camX, camY;
        private boolean toolLock = false;

        private float snapToGrid(float num) {
            int gridLocation = (int)num % gridSize;
            gridLocation = (int)num - gridLocation;

            return gridLocation;
        }

        @Override
        public boolean keyDown(int keycode) {  return false; }

        @Override
        public boolean keyUp(int keycode) {

            switch(keycode){
                case Input.Keys.FORWARD_DEL:
                    if(selection != null) {
                        levelObjects.remove(selection);
                        setSelection(null);
                    }
                    break;
                case Input.Keys.S:
                    if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)){
                        //save: ctrl+S
                        saveLevel();
                    }
                    break;
                case Input.Keys.LEFT:
                    if(current_Tool == E_TOOL.RECTANGLE || current_Tool == E_TOOL.CIRCLE) {
                        cycleShape(true);
                    } else if(current_Tool == E_TOOL.POINT) {
                        cyclePoint(true);
                    } else if(current_Tool == E_TOOL.SELECT && selection != null) {
                        if(selection instanceof Shape) {
                            shape_Type = ((Shape) selection).type;
                            cycleShape(true);
                            ((Shape) selection).setType(shape_Type);
                        } else if(selection instanceof Point) {
                            point_Type = ((Point) selection).type;
                            cyclePoint(true);
                            ((Point) selection).setType(point_Type);
                        }
                    }
                    break;
                case Input.Keys.RIGHT:
                    if(current_Tool == E_TOOL.RECTANGLE || current_Tool == E_TOOL.CIRCLE) {
                        cycleShape(false);
                    } else if(current_Tool == E_TOOL.POINT) {
                        cyclePoint(false);
                    } else if(current_Tool == E_TOOL.SELECT && selection != null) {
                        if(selection instanceof Shape) {
                            shape_Type = ((Shape) selection).type;
                            cycleShape(false);
                            ((Shape) selection).setType(shape_Type);
                        } else if(selection instanceof Point) {
                            point_Type = ((Point) selection).type;
                            cyclePoint(false);
                            ((Point) selection).setType(point_Type);
                        }
                    }
                    break;
            }
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            switch(character){
                case '1' :
                    if(!toolLock)
                        current_Tool = E_TOOL.SELECT;
                    break;
                case '2' :
                    if(!toolLock)
                        current_Tool = E_TOOL.SCALE;
                    break;
                case '3' :
                    if(!toolLock)
                        current_Tool = E_TOOL.RECTANGLE;
                    break;
                case '4' :
                    if(!toolLock)
                        current_Tool = E_TOOL.CIRCLE;
                    break;
                case '5' :
                    if(!toolLock)
                        current_Tool = E_TOOL.POINT;
                    break;
                case '[':
                    zoom(cam, -0.1f);
                    break;
                case ']':
                    zoom(cam, 0.1f);
                    break;
                case '=':
                case '+':
                    grid(1);
                    break;
                case '-':
                    grid(-1);
                    break;
            }

            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            touchDown.set(screenX, screenY, 0);
            cam.unproject(touchDown);
            toolLock = true;

            camX = cam.position.x;
            camY = cam.position.y;

            switch (current_Tool){
                case SELECT:
                    boolean selected = false;
                    touchDown.set(screenX,screenY,0);
                    cam.unproject(touchDown);

                    for (Selectable n : levelObjects) {
                        if(n.contains(touchDown.x, touchDown.y)){
                            if(n == selection) {
                                current_Tool = E_TOOL.TRANSLATE;
                                selected = true;
                            } else {
                                setSelection(n);
                                selected = true;
                            }
                        }
                    }

                    if(selected == false) {
                        setSelection(null);
                        current_Tool = E_TOOL.PAN;
                    } else {
                        if(selection instanceof Shape) {
                            shape_Type = ((Shape) selection).type;
                        } else if(selection instanceof Point) {
                            point_Type = ((Point) selection).type;
                        }
                    }
                    break;
                case RECTANGLE:
                    Selectable rectangle = createBox(shape_Type, snapToGrid(touchDown.x), snapToGrid(touchDown.y), 2, 2, 0, 0);
                    levelObjects.add(rectangle);
                    setSelection(rectangle);
                    break;
                case CIRCLE:
                    Selectable circle = createCircle(shape_Type, snapToGrid(touchDown.x), snapToGrid(touchDown.y), 2, 0, 0);
                    levelObjects.add(circle);
                    setSelection(circle);
                    break;
                case POINT :
                    Selectable node = createPoint(point_Type, "default", snapToGrid(touchUp.x), snapToGrid(touchUp.y));
                    levelObjects.add(node);
                    setSelection(node);
                    break;
            }

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            touchUp.set(screenX, screenY, 0);
            cam.unproject(touchUp);
            toolLock = false;

            switch (current_Tool) {
                case SCALE:
                case RECTANGLE:
                case CIRCLE:
                    if(selection != null && selection instanceof Shape)
                        resize(selection, touchDown, touchUp);
                    break;
                case POINT :
                    translate(selection, touchUp);
                    break;
                case TRANSLATE:
                    if(selection != null) {
                        translate(selection, touchUp);
                        current_Tool = E_TOOL.SELECT;
                    }
                    break;
                case PAN:
                    if(selection == null) {
                        current_Tool = E_TOOL.SELECT;
                    }
                    break;
                default:
                    if(selection != null)
                        printFeilds(selection);
                    break;
            }

            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            touchUp.set(screenX, screenY, 0);
            cam.unproject(touchUp);

            switch (current_Tool) {
                case SCALE:
                case RECTANGLE:
                case CIRCLE:
                    if(selection != null && selection instanceof Shape)
                        resize(selection, touchDown, touchUp);
                    break;
                case POINT :
                    translate(selection, touchUp);
                    break;
                case TRANSLATE:
                    if(selection != null)
                        translate(selection, touchUp);
                    break;
                case PAN:
                    if(selection == null)
                        pan(cam, touchUp, touchDown, camX, camY);
                    break;
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            mouseX = screenX;
            mouseY = screenY;

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

        public void cycleShape(boolean reverse) {
            if(reverse) {
                switch (shape_Type) {
                    case WALL:
                        shape_Type = Shape.TYPE.DEATH;
                        break;
                    case GROUND:
                        shape_Type = Shape.TYPE.WALL;
                        break;
                    case DEATH:
                        shape_Type = Shape.TYPE.GROUND;
                        break;
                }
            } else {
                switch (shape_Type) {
                    case WALL:
                        shape_Type = Shape.TYPE.GROUND;
                        break;
                    case GROUND:
                        shape_Type = Shape.TYPE.DEATH;
                        break;
                    case DEATH:
                        shape_Type = Shape.TYPE.WALL;
                        break;
                }
            }
        }

        public void cyclePoint(boolean reverse) {
            if(reverse) {
                switch (point_Type) {
                    case SPAWN:
                        point_Type = Point.TYPE.NODE;
                        break;
                    case PICKUP:
                        point_Type = Point.TYPE.SPAWN;
                        break;
                    case NODE:
                        point_Type = Point.TYPE.PICKUP;
                        break;
                }
            } else {
                switch (point_Type) {
                    case SPAWN:
                        point_Type = Point.TYPE.PICKUP;
                        break;
                    case PICKUP:
                        point_Type = Point.TYPE.NODE;
                        break;
                    case NODE:
                        point_Type = Point.TYPE.SPAWN;
                        break;
                }
            }
        }

        public void resize(Selectable selectable, Vector3 touchDown, Vector3 touchUp) {
            float xDown = snapToGrid(touchDown.x);
            float xUp = snapToGrid(touchUp.x);
            float yDown = snapToGrid(touchDown.y);
            float yUp = snapToGrid(touchUp.y);
            float x = xUp - xDown;
            float y = yUp - yDown;

            if(selectable instanceof Rectangle) {
                if (x < 0 && y < 0) {
                    selection.moveTo(xUp, yUp);
                } else if (x < 0) {
                    selection.moveTo(xUp, yDown);
                } else if (y < 0) {
                    selection.moveTo(xDown, yUp);
                } else {
                    selection.moveTo(xDown, yDown);
                }
            }

            ((Shape)selectable).resize(x, y);
        }

        public void translate(Selectable selectable, Vector3 touchUp) {
            float x = snapToGrid(touchUp.x - selectable.x);
            float y = snapToGrid(touchUp.y - selectable.y);

            selectable.translate(x, y);
        }

        public void pan(OrthographicCamera cam, Vector3 touchUp) {
            float x = ((touchUp.x - touchDown.x) - cam.position.x)/100;
            float y = ((touchUp.y - touchDown.y) - cam.position.y)/100;
            cam.translate(x, y);
        }
        public void pan(OrthographicCamera cam, Vector3 touchUp, Vector3 touchDown, float camX, float camY) {
            float x = touchUp.x;
            float y = touchUp.y;
            float x2 = camX;
            float y2 = camY;
            cam.translate(x2 - x, y2 - y);
        }


        public void zoom(OrthographicCamera cam, float amount) {
            zoom += amount;
            zoom = Math.min(Math.max(zoom, 0.1f), 10f);
            cam.zoom = zoom;
        }
        public void grid(int amount){
            gridSize += amount;
            gridSize = Math.min(Math.max(gridSize, 1),20);
        }

        public void printFeilds(Selectable selectable) {
            Field[] fields = selectable.getClass().getFields();
            for(Field field : fields) {
                try {
                    System.out.println(field + " " + field.get(selectable));
                } catch(Exception e) {
                    System.err.println("Issues getting fields for properties");
                }
            }
        }
    }

}
