package AI;

import com.badlogic.gdx.math.Vector2;


import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import Level.NavigationNode;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.CameraAttachmentComponent;

import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.components.WeaponComponent;
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


    private void selectTarget(){
        ArrayList<UUID> targetables = EntityManager.getInstance().getEntitiesWithComponent(WeaponComponent.class);
        Entity e = EntityManager.getInstance().getEntity(targetables.get(1));
        target = e;
        if(debug){
           // System.out.println("Tar " + e );
        }

    }

    private void getPathToTarget(Entity controlledEntity){
        if(target.has(HealthComponent.class) && target.has(PhysicalComponent.class)){
            if(target.get(HealthComponent.class).getHealthState() != HealthComponent.HEALTH_STATE.DEAD && controlledEntity.get(HealthComponent.class).getHealthState() != HealthComponent.HEALTH_STATE.DEAD){
                ArrayList<NavigationNode> n = pathFinder.findPath(controlledEntity.get(PhysicalComponent.class).getBody().getPosition(), target.get(PhysicalComponent.class).getBody().getPosition());
                path = new ArrayList<Vector2>();
                path.add(controlledEntity.get(PhysicalComponent.class).getBody().getPosition());
                if(n != null && n.size() > 0){
                    for(NavigationNode n1 : n){
                        path.add(n1.getBody().getPosition());
                    }
                }
                path.add(target.get(PhysicalComponent.class).getBody().getPosition());
            }
        }else if (target.has(PhysicalComponent.class)){
            System.out.println("Boring ass target");
        }else{
            //wander
        }
    }

    private void gotoPoint(Vector2 n, Entity controlledEntity){
        //calculate angle to see if we should turn
        //w/i 3 degrees, don't turn.
        //over 90 deg diff, don't accellerate
        float Ax = controlledEntity.get(PhysicalComponent.class).getBody().getPosition().x;
        float Ay = controlledEntity.get(PhysicalComponent.class).getBody().getPosition().y;
        float Bx = n.x;
        float By = n.y;

        double angleBetween = Math.atan((By - Ay) / (Bx - Ax));
        double carAngle = controlledEntity.get(PhysicalComponent.class).getBody().getAngle() + Math.PI/2;

        if(angleBetween < 0)
            angleBetween += (Math.PI * 2);

        double rotation = (angleBetween - carAngle) % Math.PI;


        if(debug)
            System.out.println("Angle between " + angleBetween + " Car angle" + carAngle + " rotation needed " + rotation);

        //check angle for turning
        boolean didTurn = false;
        if(rotation < -0.05){
            didTurn = true;
            MessageManager.getInstance().addMessage(SteeringSystem.class, controlledEntity, INTENT.LEFTTURN);
        }else if(rotation > 0.05){
            didTurn = true;
            MessageManager.getInstance().addMessage(SteeringSystem.class, controlledEntity, INTENT.RIGHTTURN);

        }else{
            MessageManager.getInstance().addMessage(WeaponSystem.class, controlledEntity, INTENT.FIRE);
        }

        if(!didTurn) {
           // Game.slightlyWarmMail.addMessage(SteeringSystem.class, controlledEntity, INTENT.STRAIGHT);
        }

        //raycast to check if we're directly facing a wall
        //if so, reverse
        if(pathFinder.isFacingWall(controlledEntity.get(PhysicalComponent.class).getBody(), debug)){
            if(false) {
                System.out.println("Facing wall, we think");
            }
          //  Game.slightlyWarmMail.addMessage(SteeringSystem.class, controlledEntity, INTENT.DECELERATE);

        }else{
            if(false){
                System.out.println("forward");
            }
            MessageManager.getInstance().addMessage(SteeringSystem.class, controlledEntity, INTENT.ACCELERATE);
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
                            MessageManager.getInstance().addMessage(SpawnSystem.class, entity);
                        }
                    }
                }
                return;
            }
        }
        if(lastIntent + intentDelay < System.currentTimeMillis())
        {
            lastIntent = System.currentTimeMillis();
            bozoNumber = random.nextInt(10);

            if(entity.has(CameraAttachmentComponent.class)){
                debug = true;
            }
            selectTarget();

            getPathToTarget(entity);
        }
        if(path.size() > 1)
            gotoPoint(path.get(1), entity);

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