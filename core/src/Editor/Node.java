package Editor;

import com.badlogic.gdx.graphics.Color;

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
        color = Color.BLUE;
        originalColor = Color.BLUE;
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
    public boolean hit(int screenX, int screenY) {
        return screenX > x - r && screenX < x + r  && screenY > y - r && screenY < y + r;
    }

}
