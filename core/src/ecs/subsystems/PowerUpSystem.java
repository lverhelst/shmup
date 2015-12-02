package ecs.subsystems;

import java.util.Random;

import gameObjects.DoubleDamagePowerUp;
import gameObjects.FireRatePowerUp;
import gameObjects.GradualHealPowerUp;
import gameObjects.PowerUp;

/**
 * Manages powerups
 * Created by Orion on 11/30/2015.
 */
public class PowerUpSystem {
    Random rand;


    //One at a time for now
    PowerUp[] livePowerUp;

    public PowerUpSystem(){
        rand = new Random();
        livePowerUp = new PowerUp[4];
    }

    /**
     * Updates powerups
     * Spawns new powerups if null or inactive
     */
    public void update(){
        for (int i = 0; i < livePowerUp.length; i++){
            if(livePowerUp[i] == null || !livePowerUp[i].isActive()){
                livePowerUp[i] = randomPowerUp();
                livePowerUp[i].spawn(64 + rand.nextInt(64), 64 + rand.nextInt(64));
            }
            //update powerup
            livePowerUp[i].update();
        }
    }

    /***
     * @return New instance of a random powerup
     */
    private PowerUp randomPowerUp(){
        //increase the below integer as more powerups are added
        //can also change spawn distribution here
        switch(rand.nextInt(3)){
            case 0: System.out.println("spawning fire rate");    return new FireRatePowerUp();
            case 1: System.out.println("spawning double damage");return new DoubleDamagePowerUp();
            case 2: System.out.println("spawning health");       return new GradualHealPowerUp();
        }
        return null;
    }

}
