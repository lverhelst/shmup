package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Circle extends Shape {
    private float r, rot;

    public Circle(float x, float y, float r) {
        super(x, y);
        this.r = r;
    }

    @Override
    public void resize(float w2, float h2) {
        r = (int)Math.tan(w2/h2); //check your Trig boi
    }

    @Override
    public void rotate(float degree) {
        this.rot = degree;
    }

    @Override
    public boolean contains(float x2, float y2) {
        return (x2 > x && x2 < x + r) && (y2 > y && y2 < y + r);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(currentColor);
        renderer.circle(x, y, r);
    }

    @Override
    public String toJson() {
        return "{ \"type\" : circle, \"location\" : [" + x + "," + y +"], \"radius\" : " + r + ", \"friction\" : 1, \"density\" : 1, \"dynamic\" : false }";
    }
}