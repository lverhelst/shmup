package verberg.com.shmup;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
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
    private ArrayList<Node> nodeList;
    private Stack<NavigationNode> navNodeStack;
    private ArrayList<NavigationNode> navNodes;
    public World world;

    private Body blade;

    public void create(World world) {
        bodies = new HashMap<String, Body>();
        nodeList = new ArrayList<Node>();
        navNodeStack = new Stack<NavigationNode>();
        navNodes = new ArrayList<NavigationNode>();

        this.world = world;

        loadLevel(Gdx.files.internal("savedlevel.lvl"));
    }

    public void loadLevel(FileHandle level) {
        JsonReader reader = new JsonReader();
        JsonValue json = reader.parse(level);
        JsonValue map = json.get("level");
        JsonValue groups = map.get("groups");
        float[] offset;

        for(JsonValue group : groups) {
            //TODO: move offset into the loadPart method?
            offset = group.get("location").asFloatArray();
            loadParts(group, offset[0], offset[1]);
        }
        //load groups, boxes, nodes
        loadParts(map, 0, 0);




        //add spawn point nodes
        //create navigation graph
        processNodes(nodeList);

        //TODO: add ground creation in file
        createGround(135, 130, 50, 50, 1, 1);

        //TODO: add joint handling and creation from file
        Body circle = createCircle(160, 155, 5, 1, 1, BodyType.StaticBody);
        blade = createBox(165, 154.5f, 150, 2, 0, 1, BodyType.DynamicBody);

        RevoluteJointDef joint = new RevoluteJointDef();
        joint.bodyA = circle;
        joint.bodyB = blade;
        joint.localAnchorB.set(0,0);
        joint.enableMotor = true;
        joint.maxMotorTorque = 100000.0f;
        joint.motorSpeed = 2f;
        world.createJoint(joint);
    }

    public void loadParts(JsonValue shapes, float x, float y) {
        Body body = null;
        JsonValue types = shapes.get("blocks");
        JsonValue nodes = shapes.get("navigationNodes");

        if(types != null) {
            for (JsonValue shape : types) {
                String type = shape.get("type").asString();
                JsonValue jsonName = shape.get("name");
                float[] pos = shape.get("location").asFloatArray();
                float friction = shape.get("friction").asFloat();
                float density = shape.get("density").asFloat();

                BodyDef.BodyType bodyType = shape.get("dynamic").asBoolean() ? BodyType.DynamicBody : BodyType.StaticBody;

                if(type.equals("box")) {
                    float[] size = shape.get("size").asFloatArray();
                    body = createBox(pos[0] + x, pos[1] + y, size[0], size[1], friction, density, bodyType);

                } else if(type.equals("circle")) {
                    float radius = shape.get("radius").asFloat();
                    body = createCircle(pos[0] + x, pos[1] + y, radius, friction, density, bodyType);
                }

                //Only add named blocks
                if(jsonName != null && body != null) {
                    String name = shape.get("name").asString();
                    bodies.put(name, body);
                }
            }
        }

        if(nodes != null) {
            for (JsonValue node : nodes) {
                Node newNode = new Node();
                newNode.create(node);
                nodeList.add(newNode);
                NavigationNode nNode = new NavigationNode(node);
                navNodes.add(nNode);
                nNode.createBox2dBody(world);
            }
        }
    }

    public void processNodes(ArrayList<Node> nodes) {

        generateNavigationGraph();
        System.out.println("Navigation Generation Completation");
        for(Node node : nodes) {
            if(node.isNavigation())
            {

            }

            if(node.isPowerup()) {

            }

            if(node.isSpawn()) {
                Game.slightlyWarmMail.addMessage(SpawnSystem.class, node);
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

    public void loadVariation(JsonValue shapes) {
        //TODO: add loading for variations
    }

    public Body createGround(float x, float y, float w, float h, float friction, int type) {
        //TODO: Make type an actually object, maybe a component, so ICE and stuff can have properties

        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.friction = friction;
        fixtureDef.isSensor = true;
        if(type == 1){
            //Death ground is only collidable in the same manner as powerups
            fixtureDef.filter.categoryBits = Constants.POWERUP_BIT;
            fixtureDef.filter.maskBits = Constants.POWERUP_MASK;
        }

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(type);

        box.dispose();

        return body;
    }

    public Body createBox(float x, float y, float w, float h, float friction, float density, BodyType type) {
        //box2d doubles these when it creates the box... so I am undoing that so coords are consistant
        w /= 2;
        h /= 2;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(new Vector2(x + w, y + h));

        Body body = world.createBody(bodyDef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(w, h);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = box;
        fixture.density = density;
        fixture.friction = friction;

        body.createFixture(fixture);
        box.dispose();

        return body;
    }

    public Body createCircle(float x, float y, float r, float friction, float density, BodyType type) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(new Vector2(x, y));

        Body body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(r);

        FixtureDef fixture = new FixtureDef();
        fixture.shape = circle;
        fixture.density = density;
        fixture.friction = friction;

        body.createFixture(fixture);
        circle.dispose();

        return body;
    }

    public void update(){
        blade.setAngularVelocity((float)(Math.PI/2));
    }
}