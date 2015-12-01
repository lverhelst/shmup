package ecs.subsystems;

import gameObjects.FireRatePowerUp;
import gameObjects.PowerUp;

/**
 * Manages powerups
 * Created by Orion on 11/30/2015.
 */
public class PowerUpSystem {

    //One at a time for now
    PowerUp livePowerUp;

    public PowerUpSystem(){

    }

    public void update(){
        if(livePowerUp == null || !livePowerUp.isActive()){
            livePowerUp = new FireRatePowerUp();
            //this will be fire a message to the spawn system eventually
            livePowerUp.spawn(64,64);
        }
        livePowerUp.update();
        //take contact message (entity, powerup)
        //do powerUp.applyToEntity(entity);
    }

}
