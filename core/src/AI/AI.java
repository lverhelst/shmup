package AI;

import com.badlogic.gdx.math.Vector2;


import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import Input.MyInputAdapter;
import Level.NavigationNode;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.CameraAttachmentComponent;

import ecs.components.ControlledComponent;
import ecs.components.FlagComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.components.SteeringComponent;
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import ecs.components.WeaponComponent;
import ecs.subsystems.RemovalSystem;
import ecs.subsystems.SpawnSystem;
import ecs.subsystems.SteeringSystem;
import ecs.subsystems.WeaponSystem;
import verberg.com.shmup.ShmupGame;
import MessageManagement.INTENT;

/**
 * Roughin in AI
 * Created by Orion on 11/21/2015.
 */
public class AI implements IntentGenerator {

    long lastIntent = 0;
    int intentDelay = 250;
    int bozoNumber = 5;
    boolean waitToRespawn = false;
    long respawnStart = 0;
    int respawnDelay = 5000;
    Entity target;
    Astar pathFinder;
    boolean debug;
    public ArrayList<Vector2> path;

    Random random;

    public AI(){
        random = new Random();
        pathFinder = new Astar();
        path = new ArrayList<Vector2>();
    }


    public void setTarget(Entity e){
       // System.out.println("Set target");
        target = e;
        //debug = true;
    }

    private void selectTarget(Entity thisEntity){
        ArrayList<UUID> targetables = EntityManager.getInstance().getEntitiesWithComponent(FlagComponent.class);
        if(targetables.size() >= 1) {
            Entity e = null;
            for(UUID uuid : targetables){
               e = EntityManager.getInstance().getEntity(uuid);
   //  System.out.println("Entity target: " + e);
            }


            if(e != null) {

                if(e.has(FlagComponent.class))
                {
                    if(e.get(FlagComponent.class).getHeldBy() != null){
                      //  System.out.println("SOMETHING IS HOLDING FLAG " + thisEntity.getUuid() + "( " + thisEntity.getName() + ") flag:" + e.get(FlagComponent.class).getHeldBy() + "(" + e.getName() + ")");
                        if(e.get(FlagComponent.class).getHeldBy() == thisEntity.getUuid()){
                          //  System.out.println("AI IS HOLDING FLAG");
                            targetGoal(thisEntity);
                       }else{
                            setTarget(e);
                        }
                    }else{
                        setTarget(e);
                    //   System.out.println("Targetted flag");
                    }
                }
              //  System.out.println("Tar " + e );
            }
            if (debug) {
              //   System.out.println("Tar " + e );
            }
        }else {
            targetables = EntityManager.getInstance().getEntitiesWithComponent(ControlledComponent.class);
            Entity e = null;
            for(UUID uuid : targetables){
                e = EntityManager.getInstance().getEntity(uuid);
                if(e.has(TeamComponent.class) && thisEntity.has(TeamComponent.class)
                    && e.get(TeamComponent.class).getTeamNumber() == thisEntity.get(TeamComponent.class).getTeamNumber()){
                    continue;
                }else{
                    setTarget(e);
                    break;
                }
            }
        }
    }

    private void targetGoal(Entity thisEntity){

        if(thisEntity.has(TeamComponent.class)){
            int teamId = thisEntity.get(TeamComponent.class).getTeamNumber();
            ArrayList<UUID> targetables = EntityManager.getInstance().getEntitiesWithComponents(PhysicalComponent.class, TypeComponent.class, TeamComponent.class);

            for(UUID uuid : targetables){

                if(EntityManager.getInstance().getComponent(uuid, TeamComponent.class).getTeamNumber() == teamId
                    && EntityManager.getInstance().getComponent(uuid, TypeComponent.class).getType() == 2){
                    setTarget(EntityManager.getInstance().getEntity(uuid));
                   // System.out.println("Targetted Goal");
                    break;
                }
                //  System.out.println("Entity target: " + e);
            }
        }
    }

    private void getPathToTarget(Entity controlledEntity){
        if(target == null){
            System.out.println("Target is null");
            return;

        }

        if(controlledEntity.has(PhysicalComponent.class)){
            if(!controlledEntity.get(PhysicalComponent.class).isRoot)
            {
                return;
            }
        }

        if (target.has(PhysicalComponent.class)){
            if(target.has(HealthComponent.class) && controlledEntity.has(HealthComponent.class) && ((target.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD) || controlledEntity.get(HealthComponent.class).getHealthState() == HealthComponent.HEALTH_STATE.DEAD)) {

                return;
            }else{

                path = pathFinder.findPath(controlledEntity.get(PhysicalComponent.class).getBody().getPosition(), target.get(PhysicalComponent.class).getBody().getPosition());
            }

        }else{
            //wander
        }
    }
    //This is so we only calculate the rotation based on the car body, not on the individual tires
    //The tires still need to be used to make steering messages
    int rotation;
    private void gotoPoint(Vector2 n, Entity controlledEntity){
        //calculate angle to see if we should turn
        //w/i 3 degrees, don't turn.
        //over 90 deg diff, don't accellerate
        float Ax = controlledEntity.get(PhysicalComponent.class).getBody().getPosition().x;
        float Ay = controlledEntity.get(PhysicalComponent.class).getBody().getPosition().y;
        float Bx = n.x;
        float By = n.y;


        if(controlledEntity.get(PhysicalComponent.class).isRoot) {

            int angleToFace = (int) Math.toDegrees(Math.atan2((By - Ay) , (Bx - Ax)));

            int carAngle = (int) (Math.toDegrees(controlledEntity.get(PhysicalComponent.class).getBody().getAngle()) % 360 + 270 ) % 360 ;

            rotation = (carAngle - angleToFace);





            if (Math.abs(rotation) > 180)
                rotation += rotation > 0 ? -360 : 360;

            if(debug && false)
                System.out.println("Angle between " + angleToFace + " Car angle" + carAngle + " rotation needed " + rotation);

        }



        //check angle for turning


        //raycast to check if we're directly facing a wall
        //if so, reverse
        if(lastIntent + intentDelay/2 < System.currentTimeMillis()) {
            //right side
            boolean rightWall = pathFinder.isFacingWall(controlledEntity.get(PhysicalComponent.class).getBody(), -1);
            boolean leftWall = pathFinder.isFacingWall(controlledEntity.get(PhysicalComponent.class).getBody(), 1);
            boolean middleWall = pathFinder.isFacingWall(controlledEntity.get(PhysicalComponent.class).getBody(), 0);
           // System.out.println("Right " + rightWall + " Left " + leftWall);

            if((rightWall && leftWall) || middleWall){
                MessageManager.getInstance().addMessage(INTENT.DECELERATE, controlledEntity);
                MessageManager.getInstance().addMessage(INTENT.STRAIGHT, controlledEntity, 0);
            }else if (rightWall){
                MessageManager.getInstance().addMessage(INTENT.LEFTTURN, controlledEntity, SteeringComponent.DIRECTION.LEFT.getAngle());
                MessageManager.getInstance().addMessage(INTENT.ACCELERATE, controlledEntity);
            }else if (leftWall){
                MessageManager.getInstance().addMessage(INTENT.RIGHTTURN, controlledEntity, SteeringComponent.DIRECTION.RIGHT.getAngle());
                MessageManager.getInstance().addMessage(INTENT.ACCELERATE, controlledEntity);
            }else{
                MessageManager.getInstance().addMessage(INTENT.ACCELERATE, controlledEntity);
                boolean didTurn = false;

                //System.out.println("Setting rotation" + rotation);
                if(rotation > 0){
                    didTurn = true;
                    MessageManager.getInstance().addMessage(INTENT.LEFTTURN, controlledEntity, (180 - rotation));


                }else if(rotation  < 0){
                    didTurn = true;
                    MessageManager.getInstance().addMessage(INTENT.RIGHTTURN, controlledEntity, -(180 + rotation));
                }


               if(Math.abs(rotation) > 172)
                    MessageManager.getInstance().addMessage(INTENT.FIRE, controlledEntity);




                if(!didTurn) {
                    MessageManager.getInstance().addMessage(INTENT.STRAIGHT, controlledEntity, 0);
                }
            }
        }
    }

    @Override
    public void generateIntents(Entity entity) {
        //select some target if current target is deadered


        if(entity.has(HealthComponent.class)){
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                if (entity.has(PhysicalComponent.class)) {
                    if ((entity.get(PhysicalComponent.class)).isRoot) {
                        long time = System.currentTimeMillis();
                        if(!waitToRespawn){
                            respawnStart = time + respawnDelay;
                            waitToRespawn = true;
                        }
                        if((respawnStart) > time){
                            waitToRespawn = true;
                        }else{
                            waitToRespawn = false;
                            MessageManager.getInstance().addMessage(INTENT.SPAWN, entity);
                        }
                    }
                }
                return;
            }
        }
        if(lastIntent + intentDelay < System.currentTimeMillis()) {
            lastIntent = System.currentTimeMillis();
            bozoNumber = random.nextInt(10);

            if(entity.has(CameraAttachmentComponent.class)){
                debug = true;
            }
        }
        if( entity.get(PhysicalComponent.class).isRoot) {
            selectTarget(entity);
            getPathToTarget(entity);
        }
        if(path != null  &&  path.size() > 1)
            gotoPoint(path.get(1), entity);

        if(random.nextInt(10000) == 0){
            if (entity.has(PhysicalComponent.class)) {
                if ((entity.get(PhysicalComponent.class)).isRoot) {
                    if (entity.has(HealthComponent.class)) {
                        ( entity.get(HealthComponent.class)).setCur_Health(0);
                        MessageManager.getInstance().addMessage(INTENT.DIED, entity, entity);
                    }
                }
                return;
            }
        }

        //Add messages to message manager
        /*
        if(bozoNumber < 7){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.ACCELERATE);
        }
        if(bozoNumber == 7){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.DECELERATE);
        }
        boolean didTurn = false;
        if(bozoNumber < 4 ){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.LEFTTURN);
            didTurn |= true;
        } else if(bozoNumber < 7){
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.RIGHTTURN);
            didTurn |= true;
        }
        if(!didTurn) {
            Game.slightlyWarmMail.addMessage(SteeringSystem.class, entity, INTENT.STRAIGHT);
        }
        if(bozoNumber == 4){
            Game.slightlyWarmMail.addMessage(WeaponSystem.class, entity, INTENT.FIRE);
        }

        if(random.nextInt(100000) == 0){
            if (entity.has(PhysicalComponent.class)) {
                if ((entity.get(PhysicalComponent.class)).isRoot) {
                    if (entity.has(HealthComponent.class)) {
                        ( entity.get(HealthComponent.class)).setCur_Health(0);
                        Game.slightlyWarmMail.addMessage(RemovalSystem.class, entity, INTENT.DIED);
                    }
                }
                return;
            }
        }
        */
    }
}