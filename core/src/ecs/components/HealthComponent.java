package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 11/26/2015.
 */
public class HealthComponent extends Component {
    public enum HEALTH_STATE {
        FULL,
        MODERATE,
        SEVERE,
        DEAD
    }

    public int max_health;
    private int cur_health;

    public HealthComponent(int max_health){
        this.max_health = max_health;
        this.cur_health = max_health;
    }

    public HEALTH_STATE getHealthState(){
        if(cur_health < 0) cur_health = 0;
        float state = (float)cur_health/(float)max_health;
        if(state == 1)
        {
            return HEALTH_STATE.FULL;
        }
        if(state < 1 && state >= 0.5){
            return HEALTH_STATE.MODERATE;
        }
        if(state < 0.5 && state >= 0.1)
        {
            return HEALTH_STATE.SEVERE;
        }
        return HEALTH_STATE.DEAD;
    }

    public int getCur_health(){
        return cur_health;
    }

    public int setCur_Health(int health){
        cur_health = health;
        return cur_health;
    }

    /**
     *
     * @param health Amount of health to reduce, negative values will increase health
     * @return current health
     */
    public int reduceCur_Health(int health){

        cur_health -= health;
        if(cur_health < 0)
            cur_health = 0;
        if(cur_health > max_health)
            cur_health = max_health;
        return cur_health;
    }
}
