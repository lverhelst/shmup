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

    public String ToString(){

        return (type == 1 ? "Beach Ball Type Component" : "Unknown Type");
    }


}
