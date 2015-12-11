package Editor;

/**
 * Created by Orion on 12/6/2015.
 */
public interface Command {
    public void execute();
    public void undo();
}
