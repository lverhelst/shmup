package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Circle extends Shape {
    public float r, rot;

    public Circle(TYPE type, float x, float y, float r) {
        super(type, x, y);
        this.r = r;
    }

    @Override
    public void resize(float w2, float h2) {
        r = Math.max(Math.abs(w2), Math.abs(h2));
    }

    @Override
    public void rotate(float degree) {
        this.rot = degree;
    }

    @Override
    public boolean contains(float x2, float y2) {
        return (x2 > x - r && x2 < x + r) && (y2 > y - r && y2 < y + r);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(currentColor);
        renderer.circle(x, y, r);
    }

    @Override
    public String toJson() {
        return "{ \"shape\" : circle, \"type\" : " + type + ", \"friction\" : 1, \"density\" : 1, \"dynamic\" : false, \"location\" : [" + x + "," + y +"], \"radius\" : " + r + " }";
    }
}