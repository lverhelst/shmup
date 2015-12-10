package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Item extends Selectable {
    private float r;
    private String type;
    private String subtype;

    public Item(float x, float y, String type, String subtype) {
        super(x, y);
        this.type = type;
        this.subtype = subtype;
        this.r = 5;
    }

    @Override
    public boolean contains(float x2, float y2) {
        return x2 > x - r && x2 < x + r  && y2 > y - r && y2 < y + r;
    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.setColor(currentColor);
        renderer.circle(x, y, r);
    }

    @Override
    public String toJson() {
        return "{ \"location\" : [" + x + "," + y + "], \"type\" : [spawn, powerup], \"subtype\" : [red, blue, rapidfire] }";
    }
}