package components;

import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.Random;

import Input.CarCommands;
import Input.Command;

/**
 * Roughin in AI
 * Created by Orion on 11/21/2015.
 */
public class AI {

    ShmupActor inControlof;

    public AI(){

    }

    public ShmupActor getInControlof() {
        return inControlof;
    }

    public void setInControlof(ShmupActor inControlof) {
        this.inControlof = inControlof;
    }

    public Command[] getCommands(){
        ArrayList<Command> cmd = new ArrayList<Command>();
        CarCommands carCmd = new CarCommands();
        Random random = new Random();
        if(random.nextInt(8) < 4){
            cmd.add(carCmd.new AccelerateCommand());
        }
        if(random.nextInt(8) == 4){
            cmd.add(carCmd.new DecellerateCommand());
        }
        boolean turned = false;
        if(random.nextInt(8) == 5){
            cmd.add(carCmd.new LeftTurnCommand());
            turned |= true;
        }
        if(random.nextInt(8) == 6){
            cmd.add(carCmd.new RightTurnCommand());
            turned |= true;
        }
        if(!turned){
            cmd.add(carCmd.new PowerSteerCommand());
        }
        return cmd.toArray(new Command[cmd.size()]);
    }
}
