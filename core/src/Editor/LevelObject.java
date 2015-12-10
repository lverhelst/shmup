package Editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Orion on 12/6/2015.
 */
public abstract class LevelObject {
    Color originalColor,color; //for shape renderer
    int x,y, angle; //position
    int[] grabPoints;
    boolean selected;
    //hashmap<Property, object>

    public void delete(){
        //this seriously does nothing as it would be
        //handled in the Level itself
    }

    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

    public abstract void  resize(int w, int h);

    public void select(){
        selected = true;
        color = Color.WHITE;
        this.generateGrabPoints();
    }

    public void unselect(){
        selected = false;
        color = originalColor;
        grabPoints = new int[0]; //remove grab points
    }

    public void rotate(int angle){
        this.angle = angle;
    }

    public Body createBox2dBody(World world){
        return null;
    }

    protected abstract void generateGrabPoints();
    public abstract boolean contains(int screenX, int screenY);
    public abstract void render(ShapeRenderer renderer);
}