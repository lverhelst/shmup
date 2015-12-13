package AI;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import Level.Level;
import Level.NavigationNode;
import sun.security.krb5.internal.ASRep;

/**
 * Created by Orion on 12/12/2015.
 */
public class Astar {
    private ArrayList<NavigationNode> openList;
    private ArrayList<NavigationNode> closedList;
    private NavigationNode tar;

    //holds search space
    private static ArrayList<NavigationNode> graph;

    public Astar(){
        if(graph == null){
            graph = Level.getNavNodes();
        }
    }

    /**
     * Find a path between two points on the world.
     * @param source The position of the source entity, usually a car entity
     * @param target The position of the target entity, usually a car or a powerup
     * @return The list of nodes needed to traverse to get to the target point
     */
    public ArrayList<NavigationNode> findPath(Vector2 source, Vector2 target){

    }



}
