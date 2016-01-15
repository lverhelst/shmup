package Editor;

import java.util.ArrayList;

/**
 * Created by emery on 2016-01-07.
 */
public class EditorController {
    public ArrayList<Command> commandList = new ArrayList<Command>();
    public ArrayList<Selectable> levelObjects = new ArrayList<Selectable>();
    public Selectable selection;
    public int pointer = 0;
    public Command command;

    public void addCommand(Command cmd) {
        while(pointer != commandList.size())
            commandList.remove(pointer);

        commandList.add(cmd);
        pointer = commandList.size();
    }

    public void setSelection(Selectable o){
        if(selection == null && o != null) {
            o.toggleSelect();
        }
        if(selection != null && o == null) {
            selection.toggleSelect();
        }
        if(selection != null && o != null && o != selection) {
            selection.toggleSelect();
            o.toggleSelect();
        }

        selection = o;
    }

    public boolean isSelectionPoint() {
        return selection instanceof Point;
    }

    public boolean isSelectionShape() {
        return selection instanceof Shape;
    }

    public void undo() {
        command = commandList.get(pointer - 1);
        command.undo();
        System.out.println("Undo" + pointer + " " + command.getClass().getName() + " " + commandList.size());

        pointer = Math.max(pointer - 1, 0);
    }

    public void redo() {
        if(pointer < commandList.size()) {
            command = commandList.get(pointer);
            command.redo();
            System.out.println("Redo " + pointer + " " + command.getClass().getName() + " " + commandList.size());

            pointer++;
        }
    }

    /*
     * All the functions for creating and calling the command objects
     */

    public void createBox(Shape.TYPE type, float x, float y, float w, float h, float friction, float density) {
        command = new CreateBoxCommand();
        ((CreateBoxCommand) command).createBox(type, x, y, w, h, friction, density);
        commandComplete();
    }

    public void createCircle(Shape.TYPE type, float x, float y, float r, float friction, float density){
        command = new CreateCircleCommand();
        ((CreateCircleCommand) command).createCircle(type, x, y, r, friction, density);
        commandComplete();
    }

    public void createPoint(Point.TYPE type, String subType, float x, float y){
        command = new CreatePointCommand();
        ((CreatePointCommand) command).createPoint(type, subType, x, y);
        commandComplete();
    }

    public void cycleType(boolean reverse) {
        if(!(command instanceof CycleTypeCommand))
            command = new CycleTypeCommand(selection, reverse);

        ((CycleTypeCommand)command).CycleType(reverse);
    }

    public void resize(float xDown, float xUp, float yDown, float yUp) {
        if(!(command instanceof ResizeCommand))
            command = new ResizeCommand((Shape)selection);

        ((ResizeCommand) command).resize(xDown, xUp, yDown, yUp);
    }


    public void translate(float x, float y) {
        if(!(command instanceof TranslateCommand))
            command = new TranslateCommand(selection);

        ((TranslateCommand)command).translate(x, y);
    }

    public void commandComplete() {
        if(command != null) {
            command.finalize();
            addCommand(command);
        }

        command = null;
    }

    /*
     * All the Command classes internal to the controller
     */

    public interface Command {
        public void redo();
        public void undo();
        public void finalize();
    }

    public class ResizeCommand implements Command {
        Shape selectable;
        float orgX, orgY, orgW, orgH, x, y, w, h;

        public ResizeCommand(Shape selectable) {
            this.selectable = selectable;

            this.orgX = selectable.getX();
            this.orgY = selectable.getY();

            if(selectable instanceof Circle) {
                this.orgW = ((Circle)selectable).r;
                this.orgH = orgW;
            } else if (selectable instanceof Rectangle) {
                this.orgW = ((Rectangle)selectable).w;
                this.orgH = ((Rectangle)selectable).h;
            }
        }

        public void redo() {
            selectable.moveTo(x, y);
            selectable.resize(w, h);
        }

        public void undo() {
            selectable.moveTo(orgX, orgY);
            selectable.resize(orgW, orgH);
        }

        public void finalize() {
            this.x = selectable.getX();
            this.y = selectable.getY();

            if(selectable instanceof Circle) {
                this.w = ((Circle)selectable).r;
                this.h = w;
            } else if (selectable instanceof Rectangle) {
                this.w = ((Rectangle)selectable).w;
                this.h = ((Rectangle)selectable).h;
            }
        }

        public void resize(float xDown, float xUp, float yDown, float yUp) {
            float x = xUp - xDown;
            float y = yUp - yDown;

            if(selectable instanceof Rectangle) {
                if (x < 0 && y < 0) {
                    selectable.moveTo(xUp, yUp);
                } else if (x < 0) {
                    selectable.moveTo(xUp, yDown);
                } else if (y < 0) {
                    selectable.moveTo(xDown, yUp);
                } else {
                    selectable.moveTo(xDown, yDown);
                }
            }

            selectable.resize(x, y);
        }
    }

    public class TranslateCommand implements Command {
        Selectable selectable;
        float orgX, orgY, x, y;

        public TranslateCommand(Selectable selectable) {
            this.selectable = selectable;
            this.orgX = selectable.getX();
            this.orgY = selectable.getY();
        }

        public void redo() {
            selectable.moveTo(x, y);
        }

        public void undo() {
            selectable.moveTo(orgY, orgY);
        }

        public void finalize() {
            this.x = selectable.getX();
            this.y = selectable.getY();
        }

        public void translate(float x, float y) {
            selectable.translate(x, y);
        }
    }

    public class CycleTypeCommand implements Command {
        Selectable selectable;
        boolean reverse;

        public CycleTypeCommand(Selectable selectable, boolean reverse) {
            this.selectable = selectable;
            this.reverse = reverse;
        }

        public void redo() {
            CycleType(reverse);
        }

        public void undo() {
            CycleType(!reverse);
        }

        public void finalize() {}

        public void CycleType(boolean reverse) {
            if(selection instanceof Shape) {
                Shape.TYPE type = ((Shape) selection).type;
                type = reverse ? type.cyclePrev() : type.cycleNext();
                ((Shape) selection).setType(type);
            } else if (selection instanceof Point) {
                Point.TYPE type = ((Point) selection).type;
                type = reverse ? type.cyclePrev() : type.cycleNext();
                ((Point)selection).setType(type);
            }
        }
    }

    public class CreateBoxCommand implements Command {
        Rectangle rectangle;
        Shape.TYPE type;
        float x, y, w, h, friction, density;

        public CreateBoxCommand() { }

        public void redo() {
            levelObjects.add(rectangle);
        }

        public void undo() {
            levelObjects.remove(rectangle);
        }
        public void finalize() {
            this.type = rectangle.type;
            this.x = rectangle.getX();
            this.y = rectangle.getY();
            this.w = rectangle.w;
            this.h = rectangle.h;
            this.friction = 1;
            this.density = 1;
        }

        public void createBox(Shape.TYPE type, float x, float y, float w, float h, float friction, float density) {
            rectangle = new Rectangle(type,x,y,w,h);
            setSelection(rectangle);
            levelObjects.add(rectangle);
        }
    }

    public class CreateCircleCommand implements Command {
        Circle circle;
        Shape.TYPE type;
        float x, y, r, friction, density;

        public CreateCircleCommand() { }

        public void redo() {
            levelObjects.add(circle);
        }

        public void undo() {
            levelObjects.remove(circle);
        }
        public void finalize() {
            this.type = circle.type;
            this.x = circle.getX();
            this.y = circle.getY();
            this.r = circle.r;
            this.friction = 1;
            this.density = 1;
        }

        public void createCircle(Shape.TYPE type, float x, float y, float r, float friction, float density) {
            circle = new Circle(type,x,y,r);
            setSelection(circle);
            levelObjects.add(circle);
        }
    }

    public class CreatePointCommand implements Command {
        Point point;
        Point.TYPE type;
        String subType;
        float x, y;

        public CreatePointCommand() { }

        public void redo() {
            levelObjects.add(point);
        }

        public void undo() {
            levelObjects.remove(point);
        }
        public void finalize() {
            this.type = point.type;
            this.subType = point.subType;
            this.type = point.type;
            this.x = point.getX();
            this.y = point.getY();
        }

        public void createPoint(Point.TYPE type, String subType, float x, float y) {
            point = new Point(type,subType,x,y);
            setSelection(point);
            levelObjects.add(point);
        }
    }
}