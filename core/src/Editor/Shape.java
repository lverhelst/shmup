package Editor;

/**
 * Created by emery on 2015-12-09.
 */
public abstract class Shape extends Selectable {
    public Shape(float x, float y) {
        super(x, y);
    }

    public abstract void resize(float w, float h);
    public abstract void rotate(float degree);
}