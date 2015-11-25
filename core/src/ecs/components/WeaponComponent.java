package ecs.components;

import ecs.Component;

/**
 * Created by Orion on 11/23/2015.
 * Used for weapons
 */
public class WeaponComponent extends Component {
    public long lastFire = 0;
    public int firingDelay = 100; //in ms

    public WeaponComponent(){

    }



}
