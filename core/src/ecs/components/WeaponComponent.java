package ecs.components;

import com.badlogic.gdx.physics.box2d.Body;

import ecs.Component;
import ecs.Entity;

/**
 * Created by Orion on 11/23/2015.
 * Used for weapons
 */
public class WeaponComponent extends Component {
    public long lastFire = 0;
    public int firingDelay = 100; //in ms
    public float multiplier = 1;
    public float angle = 0; //angle of weapon

    public Entity weaponEntity;

    public WeaponComponent(Entity weaponEntity){
        this.weaponEntity = weaponEntity;
    }



}
