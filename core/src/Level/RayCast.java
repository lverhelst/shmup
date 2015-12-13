package Level;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

/**
 * Created by Orion on 12/9/2015.
 */
public class RayCast implements RayCastCallback {
    final NavigationNode sourceNode, targetNode;
    boolean canSee;

    public RayCast(NavigationNode source, NavigationNode target){
        this.sourceNode = source;
        this.targetNode = target;
        canSee = false;
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


        //Ray casts appear to report from the normal working back to point
        //So if we hit the target node (which we will on the first report), set cansee to true
        //If we hit any other fixtures, then there's something between normal and point, and we can't see the navigation node
        //so we set cansee  to false
        //check can see after the raycase to see if we are blocked or not

        if(fixture.getBody().equals(targetNode.getBody())){
            //add n1 to current Node, current node to n1
            canSee = true;
            return 1;
        }
        canSee = false;
        return 0;
    }
}
