package Editor;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by emery on 2015-12-09.
 */
public abstract class Shape extends Selectable {
    public enum TYPE { WALL, GROUND, DEATH }
    public TYPE type;

    public Shape(TYPE type, float x, float y) {
        super(x, y);
        setType(type);
    }

    public void setType(TYPE type) {
        this.type = type;

        switch (type) {
            case WALL:
                setColor(Color.FOREST);
                break;
            case GROUND:
                setColor(Color.GRAY);
                break;
            case DEATH:
                setColor(Color.RED);
                break;
        }
    }

    public void setColor(Color color) {
        defaultColor = color;
        currentColor = color;
    }

    public abstract void resize(float w, float h);
    public abstract void rotate(float degree);
}