package ecs.components;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

import ecs.Component;

/**
 * Created by Orion on 11/23/2015.
 * Used to flag an entity as being followed by the main camera in the camera sytem
 */
public class CameraAttachmentComponent extends Component {
    Vector2 sourcePosition, endPosition;
    long slideTime;
    long startTime;

    public void initiateSlide(Vector2 src, Vector2 tar, long slideTime){
        sourcePosition = src;
        endPosition = tar;
        this.slideTime = slideTime;
        startTime = System.currentTimeMillis();
    }

    public Vector2 getSlidePosition(){
        if(System.currentTimeMillis() < startTime)
            return sourcePosition;
        if(System.currentTimeMillis() > startTime + slideTime)
            return endPosition;


        float percentage = 1.0f - ((float)(slideTime + startTime - System.currentTimeMillis()) / (float)slideTime);


        System.out.println(percentage + " " + (endPosition.x -  sourcePosition.x) + " " + sourcePosition.x);

        return new Vector2((endPosition.x -  sourcePosition.x) * percentage + sourcePosition.x,  (endPosition.y - sourcePosition.y) * percentage + sourcePosition.y);
    }

    public boolean isSliding(){
        return System.currentTimeMillis() > startTime && System.currentTimeMillis() < startTime + slideTime;
    }


}
