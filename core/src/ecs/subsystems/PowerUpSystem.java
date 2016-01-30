package ecs.subsystems;

import java.util.Random;

import Factories.Factory;
import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.SubSystem;
import gameObjects.DoubleDamagePowerUp;
import gameObjects.FireRatePowerUp;
import gameObjects.GradualHealPowerUp;
import gameObjects.PowerUp;
import gameObjects.RepairPowerUp;

/**
 * Manages power ups
 * Created by Orion on 11/30/2015.
 */
public class PowerUpSystem implements SubSystem{
    private static Random rand = new Random();


    //One at a time for now
    static PowerUp[] livePowerUp;
    int x = 0;


    public PowerUpSystem(){
        livePowerUp = new PowerUp[10];
    }

    public void processMessage(INTENT intent, Object... list) {
        //TODO make messagable
    }

    /**
     * Updates powerups
     * Spawns new powerups if null or inactive
     */
    public void update(){
        for (int i = 0; i < livePowerUp.length; i++){
            if(livePowerUp[i] == null || !livePowerUp[i].isActive()){
                livePowerUp[i] = randomPowerUp();
                MessageManager.getInstance().addMessage(INTENT.SPAWN, livePowerUp[i].createEntity());
            }
            //update powerup
            livePowerUp[i].update();
        }
        if(rand.nextInt(10) == 0 && x < 5){
           // MessageManager.getInstance().addMessage(INTENT.SPAWN, Factory.makeDamageOrb(75));
            x++;
        }
    }

    /***
     * @return New instance of a random powerup
     */
    public static PowerUp randomPowerUp(){
        //increase the below integer as more powerups are added
        //can also change spawn distribution here
        PowerUp ret = null;
        switch(rand.nextInt(4)){
            case 0: ret =  new FireRatePowerUp();
                break;
            case 1: ret =  new DoubleDamagePowerUp();
                break;
            case 2: ret =  new GradualHealPowerUp();
                break;
            case 3: ret =  new RepairPowerUp();
                break;
        }
        return ret;
    }

}
