package ecs.components;


import ecs.Component;

/**
 * Created by Orion on 1/4/2016.
 */
public class TypeComponent extends Component {

    int type = 0;

    public TypeComponent(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String ToString(){
        switch (type){
            case 0: return "Unk";
            case 1: return "Beach Ball Type Component";
            case 2: return "Goal Ground";
            default: return "Unk";
        }
    }


}
