package Editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public class Point extends Selectable {
    public enum TYPE { SPAWN, PICKUP, NODE }
    public TYPE type; //spawn point, powerup, Nav
    public String subType; //red, blue, firerate, etc...
    private float r;

    public Point(TYPE type, String subType, float x, float y) {
        super(x, y);
        setType(type);
        this.subType = subType;
        this.r = 4;
    }

    public void setType(TYPE type) {
        this.type = type;

        switch (type) {
            case SPAWN:
                setColor(Color.BLUE);
                break;
            case PICKUP:
                setColor(Color.YELLOW);
                break;
            case NODE:
                setColor(Color.BROWN);
                break;
        }
    }

    public void setColor(Color color) {
        defaultColor = color;
        currentColor = color;
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
        return "{ \"type\" : " + type + ", \"subtype\" : " + subType + ", \"location\" : [" + x + "," + y + "] }";
    }
}