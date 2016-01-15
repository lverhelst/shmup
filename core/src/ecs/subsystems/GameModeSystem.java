package ecs.subsystems;

import java.util.Timer;
import java.util.TimerTask;

import MessageManagement.INTENT;
import ecs.SubSystem;

/**
 * Created by Orion on 1/9/2016.
 */
public class GameModeSystem implements SubSystem {
    /***
     *  Evaluates Game Mode rules to determine launching difference level events and game mode events (wins etc).
     */

    private long countdown = 5 * 60 * 1000; //5 minutes
    private Timer gameTimer;
    private long timeElapsed = 0;

    /***
     * Variables for win conditions
     * Seriously these all have to be lists for ranking first second third
     */
    //TeamKills
    //IndividualKills
    //Entity Entered




    public GameModeSystem(){

    }

    public void startTimer(){
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeElapsed++;
            }
        }, 1000, countdown);
    }


    @Override
    public void processMessage(INTENT intent, Object... parameters) {

    }

    public void update(){

    }

    public String countdownString(){
        //return mm:ss of countdown
        return (countdown-(timeElapsed * 1000))/(60000) + ":" + (countdown-(timeElapsed * 1000))/1000;
    }


}
