package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Orion on 12/6/2015.
 */
public class NavigationNode {
    int x, y, r;
    ArrayList<NavigationNode> outNavigationNodes;
    UUID id;
    Body body;

    public NavigationNode(int x, int y, int r){
        this.x = x;
        this.y = y;
        this.r = r;
        outNavigationNodes = new ArrayList<NavigationNode>();
        id = UUID.randomUUID();
    }

    public NavigationNode(int x, int y, int r, UUID uuid){
        this.x = x;
        this.y = y;
        this.r = r;
        outNavigationNodes = new ArrayList<NavigationNode>();
        id = uuid;
    }

    public NavigationNode(JsonValue nodeDef){
        float[] pos = nodeDef.get("location").asFloatArray();
        float radius = nodeDef.get("r:").asFloat();
        UUID uuid = UUID.fromString(nodeDef.getString("id"));
        //does this work?
        this.x = (int)pos[0];
        this.y = (int)pos[1];
        this.r = (int)radius;
        outNavigationNodes = new ArrayList<NavigationNode>();
        id = uuid;
    }


    public void addEdge(NavigationNode targetNavigationNode){
        if(!outNavigationNodes.contains(targetNavigationNode))
            this.outNavigationNodes.add(targetNavigationNode);
    }

    public void removeEdge(NavigationNode targetNavigationNode){
        if(outNavigationNodes.contains(targetNavigationNode))
            this.outNavigationNodes.remove(targetNavigationNode);
    }

    public Body createBox2dBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(r);

        Body body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.isSensor = true;

        body.createFixture(fixtureDef);
        this.body = body;
        return body;
    }

    public Body getBody() {
        return body;
    }

    public void render(ShapeRenderer renderer) {
        renderer.circle(x, y, r);
        for(NavigationNode n : outNavigationNodes) {
            renderer.line(x, y, n.x, n.y);
        }
    }
}