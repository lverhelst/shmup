package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 1/9/2016.
 */
public class TeamComponent extends Component {
    private int teamNumber;

    public TeamComponent(int teamNumber){
        this.teamNumber = teamNumber;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }
}

