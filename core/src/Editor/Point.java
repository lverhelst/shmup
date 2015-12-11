package Editor;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Point extends Selectable {
    private float r;
    private String type; //spawn point, powerup, Nav
    private String subType; //red, blue, firerate, etc...

    public Point(float x, float y, String type, String subType) {
        super(x, y);
        this.type = type;
        this.subType = subType;
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
        return "{ \"location\" : [" + x + "," + y + "], \"type\" : " + type + ", \"subtype\" : " + subType + " }";
    }
}