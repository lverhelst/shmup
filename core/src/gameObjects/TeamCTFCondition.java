package gameObjects;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import MessageManagement.INTENT;
import MessageManagement.Message;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.TeamComponent;

/**
 * Created by Orion on 1/9/2016.
 */
public class TeamCTFCondition extends Condition {
    int[] teamCaptures;
    int numCaps;


    public TeamCTFCondition(int numberOfTeams, int numberOfCaptures){
        numCaps = numberOfCaptures;
        teamCaptures = new int[numberOfTeams];
    }

    @Override
    public void processMessage(INTENT intent, Object... parameters) {
        //Object 1 = Entity
        if(parameters[0] instanceof UUID){
            Entity e = EntityManager.getInstance().getEntity((UUID)parameters[0]);
            if(e.has(TeamComponent.class)){
                teamCaptures[e.get(TeamComponent.class).getTeamNumber() - 1]++;
                System.out.println(e.getName() + " captured the flag for TEAM " + e.get(TeamComponent.class).getTeamNumber() +
                        "\r\n" + "The score is " + Arrays.toString(teamCaptures));
                if(numCaps == teamCaptures[e.get(TeamComponent.class).getTeamNumber() -1]){
                    System.out.println("******************** WINNER WINNER CHICKEN DINNER ********************");
                    MessageManager.getInstance().addMessage(INTENT.WIN_COND_MET);
                }
            }else{
                //requires team component
                return;
            }
        }
    }

    @Override
    public boolean hasWinner() {
        for(int i = 0; i < teamCaptures.length; i++){
            if(teamCaptures[i] == numCaps){
                return true;
            }
        }
        return false;
    }

    public int getWinningTeam(){
        for(int i = 0; i < teamCaptures.length; i++){
            if(teamCaptures[i] == numCaps){
                return i + 1;
            }
        }
        return -1;
    }

    public int[] getTeamCaptures(){
        return teamCaptures;
    }
}
