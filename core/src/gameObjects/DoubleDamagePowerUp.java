package gameObjects;

import ecs.Entity;
import ecs.components.WeaponComponent;

/**
 * Created by Orion on 12/1/2015.
 */
public class DoubleDamagePowerUp extends PowerUp {
    float original_multiplier = 1;

    public DoubleDamagePowerUp()
    {
        super(WeaponComponent.class);
    }

    /**
     * Reset component fields
     */
    @Override
    public void despawn() {
        if(this.pickedUpEntity != null){
            if(this.pickedUpEntity.has(this.forComponent)){
                System.out.println("Resetting damage to " + original_multiplier);
                ((WeaponComponent)this.pickedUpEntity.get(forComponent)).multiplier = (int)original_multiplier;
            }
        }
        super.despawn();
    }

    /**
     * Apply to relevant component
     * @param e Entity w/ relevant component
     */
    @Override
    public void applyToEntity(Entity e) {
        this.pickedUpEntity = e;
        this.timePickedUp = System.currentTimeMillis();
        this.timeSpawned = System.currentTimeMillis();
        //safety check
        if(!this.pickedUpEntity.has(this.forComponent)){
            return ;
        }
        original_multiplier = ((WeaponComponent)this.pickedUpEntity.get(forComponent)).multiplier;
        ((WeaponComponent)this.pickedUpEntity.get(forComponent)).multiplier = (int)(original_multiplier * 2);
        this.destroyBodyOnUpdate = true;
    }
}
