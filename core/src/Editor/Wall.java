package Editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Orion on 12/6/2015.
 */
public class Wall extends LevelObject {
    int w, h;

    public Wall(int x, int y, int w, int h){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        color = Color.RED;
        originalColor = Color.RED;
    }

    @Override
    protected void generateGrabPoints() {
        grabPoints = new int[4]; //1 grab point for each line
        grabPoints[0] = x + w / 2; //top
        grabPoints[1] = x + h / 2; //left
        grabPoints[2] = x + w + h/2; //right
        grabPoints[3] = x + h + w/2; //bottom
    }

    @Override
    public void resize(int w, int h) {
        //Keep it all positive
        this.w = w;
        this.h = h;
    }

    @Override
    public boolean contains(int screenX, int screenY) {
       int posx,posy,posw,posh;
        posx = x;
        posw = w;
        posy = y;
        posh = h;

        if(w < 0){
            posx = x + w;
            posw = Math.abs(w);
        }
        if(h < 0) {
            posy = y + h;
            posh = Math.abs(h);
        }

        return screenX > posx && screenX < posx + posw && screenY > posy && screenY < posy + posh;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(color);
        renderer.rect(x, y, w, h);
    }
}
