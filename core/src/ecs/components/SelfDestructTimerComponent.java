package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 1/22/2016.
 */
public class SelfDestructTimerComponent extends Component{

    int maxTimeAlive;
    long startTime;

    public SelfDestructTimerComponent(int maxTimeAlive){
        this.maxTimeAlive = maxTimeAlive;
        startTime = System.currentTimeMillis();
    }

    public boolean isDead(){
        return System.currentTimeMillis() > startTime + maxTimeAlive;
    }

}
