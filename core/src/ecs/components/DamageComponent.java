package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 11/26/2015.
 */
public class DamageComponent extends Component {
    public int damage;

    public DamageComponent(int damage){
        this.damage = damage;
    }
}
