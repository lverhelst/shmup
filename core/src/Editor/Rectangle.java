package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Rectangle extends Shape {
    private float w, h, rot;

    public Rectangle(float x, float y, float w, float h) {
        super(x, y);
        this.w = w;
        this.h = h;
    }

    @Override
    public void resize(float w2, float h2) {
        this.w = Math.max(w2, 0);
        this.h = Math.max(h2, 0);
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
        return "{ \"type\" : box, \"location\" : [" + x + "," + y +"], \"size\" : [" + w + "," + h + "], \"friction\" : 1, \"density\" : 1, \"dynamic\" : false }";
    }
}