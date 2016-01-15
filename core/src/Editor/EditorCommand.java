package Editor;

/**
 * Created by emery on 2016-01-07.
 * Created but not used
 */
public class EditorCommand {
    public enum COMMAND { CREATEBOX, CREATECIRCLE, CREATEPOINT, CYCYLESHAPE, CYCLEPOINT, RESIZE, TRANSLATE}
    public COMMAND command;
    public Object[] parameters;

    public EditorCommand(COMMAND command, Object ... parameters) {
        this.command = command;
        this.parameters = parameters;
    }
}