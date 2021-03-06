package Editor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by emery on 2015-12-09.
 */
public abstract class Selectable {
    protected static Color selectedColor = Color.WHITE;
    protected Color defaultColor = Color.RED;
    protected Color currentColor = defaultColor;
    public float x, y;
    private boolean selected;

    public Selectable(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public boolean isSelected() {
        return selected;
    }

    public void toggleSelect() {
        selected = !selected;
        currentColor = selected ? selectedColor : defaultColor;
    }

    public void translate(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }

    public abstract boolean contains(float x, float y);
    public abstract void render(ShapeRenderer renderer);
    public abstract String toJson();
}