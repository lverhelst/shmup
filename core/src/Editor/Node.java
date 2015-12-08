package Editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

/**
 * Created by Orion on 12/6/2015.
 */
public class Node extends LevelObject {
    int r;
    ArrayList<Node> outNodes;

    public Node(int x, int y, int r){
        this.x = x;
        this.y = y;
        this.r = r;
        color = Color.CYAN;
        originalColor = Color.CYAN;
        outNodes = new ArrayList<Node>();
    }


    public void addEdge(Node targetNode){
        if(!outNodes.contains(targetNode))
            this.outNodes.add(targetNode);
    }

    public void removeEdge(Node targetNode){
        if(outNodes.contains(targetNode))
            this.outNodes.remove(targetNode);
    }


    @Override
    protected void generateGrabPoints() {
        grabPoints = new int[4]; //1 grab point for each line
        grabPoints[0] = x + r; //top

    }

    @Override
    public void resize(int w, int h) {
        r = (int)Math.tan(w/h); //check your Trig boi
    }

    @Override
    public boolean contains(int screenX, int screenY) {
        return screenX > x - r && screenX < x + r  && screenY > y - r && screenY < y + r;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.circle(x, y, r);
        for(Node n : outNodes) {
            renderer.line(x, y, n.x, n.y);
        }
    }

}
