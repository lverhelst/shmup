package verberg.com.shmup;

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
import java.util.Stack;

import Editor.NavigationNode;
import ecs.subsystems.SpawnSystem;

/**
 * Created by Orion on 11/17/2015.
 */
public class Level {
    //move to definitions class
    final int PPM = 5;

    private HashMap<String, Body> bodies;
    private ArrayList<Point> pointList;
    private Stack<NavigationNode> navNodeStack;
    private ArrayList<NavigationNode> navNodes;
    private String filename;

    public World world;
    private Body blade;

    public void create(World world) {
        bodies = new HashMap<String, Body>();
        pointList = new ArrayList<Point>();
        navNodeStack = new Stack<NavigationNode>();
        navNodes = new ArrayList<NavigationNode>();

        this.world = world;

        filename = "savedlevel2.lvl";
        loadLevel(filename);
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
        generateNavigationGraph();

        //TODO: add joint handling and creation from file
        Body circle = createCircle("WALL", 160, 155, 5, 1, 1, BodyType.StaticBody);
        blade = createBox("WALL", 165, 154.5f, 150, 2, 0, 1, BodyType.DynamicBody);

        RevoluteJointDef joint = new RevoluteJointDef();
        joint.bodyA = circle;
        joint.bodyB = blade;
        joint.localAnchorB.set(0,0);
        joint.enableMotor = true;
        joint.maxMotorTorque = 100000.0f;
        joint.motorSpeed = 2f;
        world.createJoint(joint);
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

    public void loadPoints(JsonValue maps){
        JsonValue points = maps.get("points");

        for(JsonValue point : points){
            float[] pos = point.get("location").asFloatArray();
            String type = point.get("type").asString();
            String subtype = point.get("subtype").asString();

            NavigationNode nNode = new NavigationNode(pos[0], pos[1], 4);
            navNodes.add(nNode);
            nNode.createBox2dBody(world);

            if(!type.equals("NODE")) {
                Point newPoint = new Point();
                newPoint.create(type, subtype, pos[0], pos[1]);

                if(type.equals("PICKUP")) {
                    //send pickup point???
                } else {
                    Game.slightlyWarmMail.addMessage(SpawnSystem.class, newPoint);
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
    public void generateNavigationGraph(){
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

    public ArrayList<NavigationNode> getNavNodes() {
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

        if(type.equals("GROUND") || type.equals("DEATH")){
            fixtureDef.isSensor = true;
            //Death ground is only collidable in the same manner as powerups
            fixtureDef.filter.categoryBits = Constants.GROUND_BIT;
            fixtureDef.filter.maskBits = Constants.GROUND_MASK;

        }

        fixtureDef.friction = friction;
        Fixture fixture = body.createFixture(fixtureDef);

        //TODO: make this better...
        if(type.equals("DEATH")) {
            fixture.setUserData(1);
        }

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
        blade.setAngularVelocity((float)(Math.PI/2));
    }
}