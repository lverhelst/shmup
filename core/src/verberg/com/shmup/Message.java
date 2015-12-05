package verberg.com.shmup;

/**
 * Created by emery on 2015-12-03.
 */
public class Message {
    //TODO static pool for parameters? lower the number of new operator
    public Class system;
    public Object[] parameters;

    public Message(Class system, Object... parameters) {
        this.system = system;
        this.parameters = parameters;
    }

    public Class getSystem() { return system; }
    public Object[] getParameters() { return parameters; }

    public void setSystem(Class system) {this.system = system; }
    public void setParameters(Object ... parameters) {this.parameters = parameters; }
}