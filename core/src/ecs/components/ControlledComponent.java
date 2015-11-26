package ecs.components;

import Input.MyInputAdapter;
import ecs.Component;
import AI.AI;
import AI.IntentGenerator;

/**
 * Created by Orion on 11/23/2015.
 * Used to flag an entity as player controlled
 */
public class ControlledComponent extends Component {

    enum ControlledBy {
        AI,
        Player
    }

    ControlledBy controlledBy;
    public IntentGenerator ig;

    public ControlledComponent(IntentGenerator intentGenerator){
        if(intentGenerator instanceof AI){
            controlledBy = ControlledBy.AI;
        }else{
            controlledBy = ControlledBy.Player;
        }
        ig = intentGenerator;
    }



}
