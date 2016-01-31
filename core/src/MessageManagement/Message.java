package MessageManagement;

/**
 * Created by emery on 2015-12-03.
 */
public class Message {
    public INTENT intent;
    public Object[] parameters;

    public Message(INTENT intent, Object... parameters) {
        this.intent = intent;
        this.parameters = parameters;
    }

    public INTENT getIntent() { return intent; }
    public Object[] getParameters() { return parameters; }

    public void setParameters(Object ... parameters) {this.parameters = parameters; }

    @Override
    public String toString() {
        String ret = intent.name() + " Params: ";
        if(parameters != null) {
            for (Object i : parameters) {
                ret += (i != null ? i.toString() : "null parameter") + ";";
            }
        }
        return ret;

    }
}