package gameObjects;

import ecs.Entity;
import ecs.components.WeaponComponent;

/**
 * Created by Orion on 11/30/2015.
 */
public class FireRatePowerUp extends PowerUp {

    int originalFireDelay = 100;

    public FireRatePowerUp() {
        super(WeaponComponent.class);
    }

    /**
     * Reset fields
     */
    @Override
    public void despawn() {
        if(this.pickedUpEntity != null){
            if(this.pickedUpEntity.has(this.forComponent)){
                System.out.println("Resetting to " + originalFireDelay);
                ((WeaponComponent)this.pickedUpEntity.get(forComponent)).firingDelay = originalFireDelay;
            }
        }
        super.despawn();
    }

    /**
     * Apply to entity and save the old multiplier
     * @param e Entity that has powerup.
     */
    @Override
    public void applyToEntity(Entity e) {
        this.pickedUpEntity = e;
        this.timePickedUp = System.currentTimeMillis();
        this.timeSpawned = System.currentTimeMillis();
        if(!this.pickedUpEntity.has(this.forComponent)){
            return ;
        }
        originalFireDelay = ((WeaponComponent)this.pickedUpEntity.get(forComponent)).firingDelay;
        ((WeaponComponent)this.pickedUpEntity.get(forComponent)).firingDelay = originalFireDelay/2;
        this.destroyBodyOnUpdate = true;
    }
}
