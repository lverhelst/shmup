package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Rectangle extends Shape {
    public float w, h, rot;

    public Rectangle(TYPE type, float x, float y, float w, float h) {
        super(type, x, y);
        this.w = w;
        this.h = h;
    }

    @Override
    public void resize(float w2, float h2) {
        this.w = Math.abs(w2);
        this.h = Math.abs(h2);
    }

    @Override
    public void rotate(float degree) {
        this.rot = degree;
    }

    @Override
    public boolean contains(float x2, float y2) {
        return (x2 > x && x2 < x + w) && (y2 > y && y2 < y + h);
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(currentColor);
        renderer.rect(x, y, x + (w/2), y + (h/2), w, h, 1, 1, rot);
    }

    @Override
    public String toJson() {
        return "{ \"shape\" : box, \"type\" : " + type + ", \"friction\" : 1, \"density\" : 1, \"dynamic\" : false, \"location\" : [" + x + "," + y +"], \"size\" : [" + w + "," + h + "] }";
    }
}