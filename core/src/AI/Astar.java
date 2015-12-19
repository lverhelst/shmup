package AI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import java.util.ArrayList;

import Level.Level;
import Level.NavigationNode;
import ecs.Entity;
import verberg.com.shmup.Game;

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
     * We return a direction
     * @param source The position of the source entity, usually a car entity
     * @param target The position of the target entity, usually a car or a powerup
     * @return The direction to go, in degrees. (from 0 to 360), -1 = no path found, -2 = can see directly
     */
    public ArrayList<NavigationNode> findPath(Vector2 source, Vector2 target){

        openList = new ArrayList<NavigationNode>();
        closedList = new ArrayList<NavigationNode>();
        //reset the graph
        for(NavigationNode n : graph){
            n.setPathFindingParent(null);
        }

        AstarRayCast rayCast  = new AstarRayCast();

        if(!source.equals(target)) {
            Game.getWorld().rayCast(rayCast, source, target);
            if (rayCast.canSee) {
                // we can see the target directly
               // System.out.println("Can see target without looking at nav nodes");
                return null;
            }
        }



        //we can consider source to be its own nodes
        //and then startNodes to be the outnodes of start

        NavigationNode startNode = new NavigationNode((int)source.x, (int)source.y,1);
        startNode.createBox2dBody(Game.getWorld());

        //Get all visible nodes,
        //The closest node is not necessarily the start of the best path
        ArrayList<NavigationNode> startNodes = getVisibleNodes(source);
        if(startNodes.size() < 1){
            //no visible nodes, no path
            //System.out.println("No visible nav nodes from starting position");

            startNode.dispose();
            return null;
        }
        openList.add(startNode);

        for(NavigationNode n : startNodes){
            startNode.addEdge(n);
            openList.add(n);
        }




        NavigationNode currentNode = startNode;
        int i = 0;
        do{
            currentNode = getLowestScore(currentNode);
            if(currentNode == null || currentNode.getBody() == null){
                startNode.dispose();
               // System.out.println("Current Node is null");
                return null; //no path
            }

            closedList.add(currentNode);
            openList.remove(currentNode);
            //Check if we can see the target from the current node
            rayCast  = new AstarRayCast();
            //raycasting will fail if you try to ray cast a ray from the same source and target
            if(!currentNode.getBody().getPosition().equals(target)) {

                Game.getWorld().rayCast(rayCast, currentNode.getBody().getPosition(), target);

                if (rayCast.canSee) {
                  //  System.out.println("found path");
                    // A path has been found
                    ArrayList<NavigationNode> pth = new ArrayList<NavigationNode>();
                    while (currentNode != null) {
                        pth.add(currentNode);
                        currentNode = currentNode.getPathFindingParent();
                    }
                    startNode.dispose();
                    return pth;
                }
            }

            for(NavigationNode n : currentNode.getOutNavigationNodes()){
                if(closedList.contains(n)){
                    continue;
                }
                if(!openList.contains(n)){
                    n.setPathFindingParent(currentNode);
                    openList.add(n);
                }
            }
        }while(!openList.isEmpty());
        startNode.dispose();
        //No path found
       // System.out.println("Open list empty");
        return null;
    }

    private ArrayList<NavigationNode> getVisibleNodes(Vector2 point){
        ArrayList<NavigationNode> visibleNodes = new ArrayList<NavigationNode>();
        for(NavigationNode n : graph){


            AstarRayCast rayCast  = new AstarRayCast();
            Game.getWorld().rayCast(rayCast, point, n.getBody().getPosition());
            if(rayCast.canSee){
                // we can see the target directly
                visibleNodes.add(n);
            }
        }

        return visibleNodes;
    }


    private NavigationNode getLowestScore(NavigationNode source){
        NavigationNode lowest = null;
        double distance = Double.MAX_VALUE;
        for(NavigationNode n : source.getOutNavigationNodes()){
            if(openList.contains(n)) {
                float dist = Vector2.dst2(source.getBody().getPosition().x, source.getBody().getPosition().y, n.getBody().getPosition().x, n.getBody().getPosition().y);
                if (dist < distance) {
                    distance = dist;
                    lowest = n;
                }
            }
        }
        return lowest;

    }

    public boolean isFacingWall(Body entity, boolean debug){
        AstarRayCast rayCast  = new AstarRayCast();
        //project a beam 20 units long

        float adjustX = (float)(Math.cos(entity.getAngle() + Math.PI/2) * 60f +  entity.getPosition().x);

        float adjustY = (float)(Math.sin(entity.getAngle() + Math.PI/2) * 60f +  entity.getPosition().y);

        if(false)
            System.out.println("Angle " + entity.getAngle() + " adjX " + adjustX + " adjy " + adjustY);

        Game.getWorld().rayCast(rayCast, entity.getPosition(), new Vector2(adjustX,adjustY));
        return !rayCast.canSee;
    }


    private class AstarRayCast implements RayCastCallback
    {
        boolean canSee;
        int fixturesHit;

        public AstarRayCast(){
            canSee = true;
            fixturesHit = 0;
        }

        /**
         * Return options
         * @param fixture Fixture found along line from point to normal
         * @param point Origin of raycast
         * @param normal Target of raycast
         * @param fraction Fraction of line length from point to normal the fixture was at
         * @return
         */
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {

            if(++fixturesHit > 1)
            {
                canSee = false;
                return 0;
            }

            return 1;
        }
    }


}
