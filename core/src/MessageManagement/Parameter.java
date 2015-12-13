package MessageManagement;

/**
 * Created by emery on 2015-12-03.
 */
public class Parameter<T> {
    private Class type;
    private T value;

    public Parameter(T object) {
        type = object.getClass();
        value = object;
    }

    public Class getType() { return type; }
    public T getValue() { return value; }

    public void setType(Class type) { this.type = type; }
    public void setValue(T value) { this.value = value; }
}