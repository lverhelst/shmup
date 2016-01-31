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

    public int heat;
    public int recharge = 10;
    public int rechargeDelay = 250; //recharge every 250ms
    public long lastRecharge = 0;
    public int max_heat = 100;

    public int required_heat = 10; //amount of heat 1 bullet reduces heat by

    public Entity weaponEntity;

    public WeaponComponent(Entity weaponEntity){
        this.weaponEntity = weaponEntity;
        heat = max_heat = 100;

    }

    public void applyRecharge(){
        heat += recharge;
        heat = Math.min(max_heat, heat);
        lastRecharge = System.currentTimeMillis();
    }

    public void reduceRecharge(int amount){
        heat -= amount;
        heat = Math.max(heat, 0);
    }

    @Override
    public void dispose() {
        super.dispose();
        weaponEntity.removeAllComponents();
    }
}
