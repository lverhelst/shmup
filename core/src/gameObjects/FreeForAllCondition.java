package gameObjects;

import java.util.Arrays;
import java.util.UUID;

import MessageManagement.INTENT;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.SubSystem;
import ecs.components.KDAComponent;
import ecs.components.TeamComponent;

/**
 * Created by Orion on 1/20/2016.
 */
public class FreeForAllCondition extends Condition implements SubSystem{

    int[] kills;
    int[] deaths;
    int maxKills;

    public FreeForAllCondition(int numCars, int maxKills){
        this.maxKills = maxKills;
        kills = new int[numCars];
        deaths = new int[numCars];
    }

    @Override
    public void processMessage(INTENT intent, Object... parameters) {
        //Object 1 = Entity
        System.out.println("FFA INTENT " + intent);


        if(parameters.length > 1 && parameters[0] instanceof Entity && parameters[1] instanceof Entity){
            Entity killer = (Entity)parameters[1]; //killer is the second parameter cause we're registering for the died
            Entity victim = (Entity)parameters[0];

            System.out.println(killer.has(KDAComponent.class));
            System.out.println(killer.has(TeamComponent.class));
            System.out.println(victim.has(KDAComponent.class));
            System.out.println(victim.has(TeamComponent.class));


            if(killer.has(KDAComponent.class) && killer.has(TeamComponent.class)
                    && victim.has(KDAComponent.class) && victim.has(TeamComponent.class)){

                if(killer.getUuid() != victim.getUuid()) {
                    kills[killer.get(TeamComponent.class).getTeamNumber() - 1]++;
                    killer.get(KDAComponent.class).incrementKills();
                }
                deaths[victim.get(TeamComponent.class).getTeamNumber() - 1]++;
                victim.get(KDAComponent.class).incrementDeaths();

                System.out.println(killer.getName() + " killed " + victim.getName());
                System.out.println("mk: " + maxKills + " killer kills " +  killer.get(KDAComponent.class).getKills());
                if(maxKills == killer.get(KDAComponent.class).getKills()){
                    System.out.println("******************** WINNER WINNER CHICKEN DINNER ********************");
                    MessageManager.getInstance().addMessage(INTENT.WIN_COND_MET);
                }
            }else{
                //requires team component and kda component
                return;
            }
        }


    }

    @Override
    public boolean hasWinner() {
        for(int i = 0; i < kills.length; i++){
            if(kills[i] == maxKills){
                return true;
            }
        }
        return false;
    }

    public int getWinningTeam(){
        for(int i = 0; i < kills.length; i++){
            if(kills[i] == maxKills){
                return i;
            }
        }
        return -1;
    }

}
