package Editor;

/**
 * Created by Orion on 12/6/2015.
 */
public interface Command {
    public LevelObject execute();
    public void undo();
}
