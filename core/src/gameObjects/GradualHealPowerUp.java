package gameObjects;

import ecs.Entity;
import ecs.components.HealthComponent;

/**
 * Created by Orion on 12/1/2015.
 */
public class GradualHealPowerUp extends PowerUp{

    int lasttick = 0;

    public GradualHealPowerUp(){
        super(HealthComponent.class);
    }

    @Override
    public void update() {

        //theoretically this should update every second
        if(timePickedUp != 0 && (System.currentTimeMillis() - timePickedUp)/1000 > lasttick ) {
            lasttick++;
            if(pickedUpEntity != null){
                if(pickedUpEntity.has(forComponent)) {
                    if(pickedUpEntity.get(HealthComponent.class).getHealthState() != HealthComponent.HEALTH_STATE.DEAD) {
                        pickedUpEntity.get(HealthComponent.class).reduceCur_Health(-5);
                    }
                }
            }
        }
        super.update();
    }

    @Override
    public void applyToEntity(Entity e) {
        this.pickedUpEntity = e;
        this.timePickedUp = System.currentTimeMillis();
        this.timeSpawned = System.currentTimeMillis();
        //safety check
        if(!this.pickedUpEntity.has(this.forComponent)){
            return ;
        }
        this.destroyBodyOnUpdate = true;
    }
}
