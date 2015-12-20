package AI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

import java.util.ArrayList;
import java.util.Collections;

import Level.Level;
import Level.NavigationNode;
import verberg.com.shmup.Constants;
import verberg.com.shmup.ShmupGame;

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
        openList = new ArrayList<NavigationNode>();
        closedList = new ArrayList<NavigationNode>();
    }

    /**
     * Find a path between two points on the world.
     * We return a direction
     * @param source The position of the source entity, usually a car entity
     * @param target The position of the target entity, usually a car or a powerup
     * @return The direction to go, in degrees. (from 0 to 360), -1 = no path found, -2 = can see directly
     */
    public ArrayList<NavigationNode> findPath(Vector2 source, Vector2 target){

        openList.clear();
        closedList.clear();
        //reset the graph
        for(NavigationNode n : graph){
            n.setPathFindingParent(null);
            n.setScore(0f);
        }

        //we can consider source to be its own nodes
        //and then startNodes to be the outnodes of start

        NavigationNode startNode = new NavigationNode((int)source.x, (int)source.y,1);
        startNode.createBox2dBody(ShmupGame.getWorld());

        //Get all visible nodes,
        //The closest node is not necessarily the start of the best path
        ArrayList<NavigationNode> startNodes = getVisibleNodes(source);
        if(startNodes.size() < 1){
            //no visible nodes, no path
            System.out.println("No visible nav nodes from starting position");
            startNode.dispose();
            return null;
        }

        openList.add(startNode);
        for(NavigationNode n : startNodes){
            startNode.addEdge(n);
        }

        NavigationNode currentNode = startNode;
        int i = 0;
        float tempscore = 0;
        do{
            currentNode = getLowestScore(currentNode, i++);
            if(currentNode == null || currentNode.getBody() == null){
                startNode.dispose();
                System.out.println("Current Node is null");
                return null; //no path
            }

            closedList.add(currentNode);
            openList.remove(currentNode);

            //Check if we are at the target
            if(currentNode.getBody().getPosition().equals(target)) {
                System.out.println("found path");
                // A path has been found
                ArrayList<NavigationNode> pth = new ArrayList<NavigationNode>();
                while (currentNode != null) {
                    pth.add(currentNode);
                    currentNode = currentNode.getPathFindingParent();
                }
                //since we add from the last node to the original node we reverse so
                //that the original node is first
                Collections.reverse(pth);
                startNode.dispose();
                return pth;
            }


            for(NavigationNode n : currentNode.getOutNavigationNodes()){
                if(closedList.contains(n)){
                    continue;
                }
                tempscore = currentNode.getScore() + currentNode.dist2(n);
                if(!openList.contains(n)){
                    openList.add(n);
                }else if (tempscore >= n.getScore()){
                    continue;
                }
                n.setPathFindingParent(currentNode);
                n.setScore(tempscore);

            }
        }while(!openList.isEmpty());
        startNode.dispose();
        //No path found
        System.out.println("Open list empty");
        return null;
    }

    private ArrayList<NavigationNode> getVisibleNodes(Vector2 point){
        ArrayList<NavigationNode> visibleNodes = new ArrayList<NavigationNode>();
        for(NavigationNode n : graph){
            AstarRayCast rayCast  = new AstarRayCast();
            ShmupGame.getWorld().rayCast(rayCast, point, n.getBody().getPosition());
            if(rayCast.canSee){
                // we can see the target directly
                visibleNodes.add(n);
            }
        }
        return visibleNodes;
    }



    private NavigationNode getLowestScore(NavigationNode source, int i){
        NavigationNode lowest = null;
        double distance = Double.MAX_VALUE;
        for(NavigationNode n : openList){
            float dist = n.getScore();
            if (dist < distance) {
                distance = dist;
                lowest = n;
            }
        }
        return lowest;

    }

    public boolean isFacingWall(Body entity, boolean debug){
        AstarRayCast rayCast  = new AstarRayCast();
        //project a beam 20 units long

        float adjustX = (float)(Math.cos(entity.getAngle() + Math.PI/2) * 20f +  entity.getPosition().x);

        float adjustY = (float)(Math.sin(entity.getAngle() + Math.PI/2) * 20f +  entity.getPosition().y);

        if(false)
            System.out.println("Angle " + entity.getAngle() + " adjX " + adjustX + " adjy " + adjustY);

        ShmupGame.getWorld().rayCast(rayCast, entity.getPosition(), new Vector2(adjustX,adjustY));
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

            //ignore tires

            if(fixture.getFilterData().maskBits == Constants.TIRE_MASK ||fixture.getFilterData().maskBits == Constants.POWERUP_MASK){

                return 1;
            }


            if (++fixturesHit > 0) {
                canSee = false;
                return 0;
            }


            return 1;
        }
    }


}
