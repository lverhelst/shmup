package Level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.components.DamageComponent;
import ecs.components.PhysicalComponent;
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import verberg.com.shmup.Constants;

/**
 * Created by Orion on 11/17/2015.
 */
public class Level {
    private HashMap<String, Body> bodies;
    private ArrayList<Point> pointList;
    private Stack<NavigationNode> navNodeStack;
    private static ArrayList<NavigationNode> navNodes;

    public static World world;
    private Body blade;

    //TODO: Make creation of GOALS use something in the file to determine team
    int teamNum = 0;

    public void create(World world) {
        //one time method
        bodies = new HashMap<String, Body>();
        pointList = new ArrayList<Point>();
        navNodeStack = new Stack<NavigationNode>();
        navNodes = new ArrayList<NavigationNode>();

        this.world = world;
    }

    public void genLevel(String blockFile) {
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(Gdx.files.internal(blockFile));

        JsonValue blocks = json.get("block");

        HashMap<String, ArrayList<JsonValue>> blockList = new HashMap<String, ArrayList<JsonValue>>();
        Random rand = new Random();
        ArrayList<JsonValue> cellList;
        String mask;

        if(blocks != null) {
            for (JsonValue block : blocks) {
                mask = block.get("mask").asString();

                if(mask != null) {
                    if(!blockList.containsKey(mask)) {
                        blockList.put(mask, new ArrayList<JsonValue>());
                    }

                    JsonValue cells = block.get("definition");

                    for(JsonValue cell: cells) {
                        blockList.get(mask).add(cell);
                    }
                }
            }
        }

        JsonValue settings = reader.parse(Gdx.files.internal("generator.cfg"));
        settings = settings.get("settings");

        int width = settings.getInt("width");
        int height = settings.getInt("height");
        float upperRate = settings.getFloat("upperRate");
        float lowerRate = settings.getFloat("lowerRate");
        int density = settings.getInt("density");
        int smooth = settings.getInt("smooth");

        Generator gen = new Generator(width, height);
        gen.upperRate = upperRate;
        gen.lowerRate = lowerRate;
        gen.density = density;
        gen.smooth(smooth);
        gen.fill();

        String[][] map = gen.getMarchingMap();

        for(int i = 0; i < map.length - 1; ++i) {
            for(int j = 0; j < map[i].length - 1; ++j) {
                cellList = blockList.get(map[i][j]);

                if(cellList == null) {
                    System.out.println(map[i][j]);
                }

                if(cellList.size() > 0) {
                    JsonValue cell = cellList.get(rand.nextInt(cellList.size()));

                    if(cell != null) {
                        float x = j * 4;
                        float y = map.length - i * 4;

                        loadShapes(cell, x, y);
                        loadPoints(cell, x, y);
                    }
                }
            }
        }

        generateNavigationGraph();
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
        loadPoints(map, 0, 0);

        //TODO: add joint handling and creation from file
        Body circle = createCircle("WALL", 15.5f, 15.5f, 0.5f, 0.1f, 0.1f, BodyType.StaticBody);

        /**
         * The navigation graph needs to detect the circle, but not detect the blade.
         */
        generateNavigationGraph();


        if(!levelName.equals("blacklevel.lvl")) {

            blade = createBox("WALL", 16, 15.45f, 15, 0.2f, 0f, 0.1f, BodyType.DynamicBody);

            RevoluteJointDef joint = new RevoluteJointDef();
            joint.bodyA = circle;
            joint.bodyB = blade;
            joint.localAnchorB.set(0, 0);
            joint.enableMotor = true;
            joint.maxMotorTorque = 100000.0f;
            joint.motorSpeed = 2f;
            world.createJoint(joint);
        }


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
                boolean dynamic = shapeVal.get("dynamic").asBoolean();
                BodyDef.BodyType bodyType =  dynamic ? BodyType.DynamicBody : BodyType.StaticBody;

                JsonValue jsonName = shapeVal.get("name");

                if(shape.equals("box")) {
                    float[] size = shapeVal.get("size").asFloatArray();
                    body = createBox(type, pos[0] + x, pos[1] + y, size[0], size[1], friction, density, bodyType);

                } else if(shape.equals("circle")) {
                    float radius = shapeVal.get("radius").asFloat();
                    body = createCircle(type, pos[0] + x, pos[1] + y, radius, friction, density, bodyType);
                }

                //Only add named blocks
                if(jsonName != null && body != null) {
                    String name = shapeVal.get("name").asString();
                }
            }
        }
    }


    static int loadPoint, numPnts = 0;
    public void loadPoints(JsonValue maps, float x, float y){
        JsonValue points = maps.get("points");

        if(points != null) {
            for (JsonValue point : points) {

                float[] pos = point.get("location").asFloatArray();
                String type = point.get("type").asString();
                String subtype = point.get("subtype").asString();

                System.out.println(type + " " + subtype + " point: " + ++numPnts);

                NavigationNode nNode = new NavigationNode((int) (pos[0] + x), (int) (pos[1] + y), 0.4f);
                navNodes.add(nNode);
                nNode.createBox2dBody(world);

                if (!type.equals("NODE")) {
                    Point newPoint = new Point();
                    newPoint.create(type, subtype, (int) (pos[0] + x), (int) (pos[1] + y));

                    if (type.equals("PICKUP")) {
                        //TODO: pick powerup type from subtype
                    } else {
                        System.out.println("Adding spawn: " + ++loadPoint);
                        MessageManager.getInstance().addMessage(INTENT.ADDSPAWN, newPoint);
                    }
                }
            }
        }
    }

    /**
     * Generates the navigation graph by raycasting nodes
     * Step 1: pick a source node, pass it an empty list
     * Step 2: ray cast from source towards all nodes
     * Step 3: add line of sight nodes to source node's outnodes
     * Step 4: line of sight node generateNagivation to all nodes not in the list passed in
     */
    private static void generateNavigationGraph(){
        for(NavigationNode n : navNodes){
            for(NavigationNode n1 : navNodes){
                if(!n1.equals(n)) {
                    RayCast rayCast = new RayCast(n, n1);
                    world.rayCast(rayCast, n.getBody().getPosition(), n1.getBody().getPosition());
                    if(rayCast.canSee){
                        n.addEdge(n1);
                        n1.addEdge(n);
                    }
                }
            }
        }
    }

    public static void addNavigationNode(NavigationNode n){
        navNodes.add(n);
        generateNavigationGraph();
    }



    public static ArrayList<NavigationNode> getNavNodes() {
        return navNodes;
    }



    public Body createBox(String type, float x, float y, float w, float h, float friction, float density, BodyType bodyType) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = density;

        if(type.equals("GROUND") || type.equals("DEATH") || type.equals("GOAL")){
            fixtureDef.isSensor = true;
            //Death ground is only collidable in the same manner as powerups
            fixtureDef.filter.categoryBits = Constants.GROUND_BIT;
            fixtureDef.filter.maskBits = Constants.GROUND_MASK;

        }

        fixtureDef.friction = friction;
        Fixture fixture = body.createFixture(fixtureDef);

        Entity entity = new Entity("GROUND_" + type);
        entity.addComponent(new PhysicalComponent(body));

        if(type.equals("DEATH")) {
            entity.addComponent(new DamageComponent(50));
        }
        if(type.equals("GOAL")){
            entity.addComponent(new TypeComponent(2));
            entity.addComponent(new TeamComponent(++teamNum));
        }

        fixture.setUserData(entity);
        box.dispose();

        return body;
    }



    public Body createCircle(String type, float x, float y, float r, float friction, float density, BodyType bodyType) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(new Vector2(x, y));

        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = circle;
        fixture.density = density;
        fixture.friction = friction;

        if(type.equals("GROUND") || type.equals("DEATH")){
            fixture.isSensor = true;

            if(type.equals("DEATH")) {
                //Death ground is only collidable in the same manner as powerups
                fixture.filter.categoryBits = Constants.POWERUP_BIT;
                fixture.filter.maskBits = Constants.POWERUP_MASK;
            }
        }

        body.createFixture(fixture);
        circle.dispose();

        return body;
    }

    public void update(){
        if(blade != null)
            blade.setAngularVelocity((float)(Math.PI/2));
    }
}