package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import Factories.CarFactory;
import MessageManagement.Message;
import MessageManagement.MessageManager;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.DamageComponent;
import ecs.components.FlagComponent;
import ecs.components.HealthComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.TeamComponent;
import ecs.components.TypeComponent;
import gameObjects.PowerUp;
import verberg.com.shmup.ShmupGame;
import MessageManagement.INTENT;

/**
 * Created by Orion on 11/21/2015.
 */
public class ContactSystem implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        handleFixture(fixtureA);
        handleFixture(fixtureB);

        handleContact(fixtureA, fixtureB); //b hits a
        handleContact(fixtureB, fixtureA); //a hits b
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void handleFixture(Fixture f){
        if(f.getUserData() instanceof Entity){
            Entity ecs = (Entity)f.getUserData();
            if(ecs.has(PhysicalComponent.class)){
                PhysicalComponent pc = ecs.get(PhysicalComponent.class);
                pc.numberOfContact++;
                if(pc.numberOfContact == pc.maxContacts){
                    //Flag entity for deletion
                    MessageManager.getInstance().addMessage(INTENT.REMOVE, ecs);
                }
            }
        }
    }

    public void handleContact(Fixture a, Fixture b){
        if(a.getUserData() instanceof Entity) {
            Entity aEntity = (Entity) a.getUserData();

            if(b.getUserData() instanceof  Entity) {
                Entity bEntity = (Entity) b.getUserData();

                if (aEntity.has(HealthComponent.class) && bEntity.has(DamageComponent.class)) {
                    if ((aEntity.get(HealthComponent.class)).getHealthState() != HealthComponent.HEALTH_STATE.DEAD) {
                        //apply damage
                        (aEntity.get(HealthComponent.class)).reduceCur_Health((bEntity.get(DamageComponent.class)).damage);
                        //if the other entity is now dead, send the dead messagea
                        if ((aEntity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD) {
                            Entity bEntityOwner = null;
                            if(bEntity.has(ParentEntityComponent.class))
                                bEntityOwner = bEntity.get(ParentEntityComponent.class).parent;

                            MessageManager.getInstance().addMessage(INTENT.DIED, aEntity, bEntityOwner);



                        }
                    }
                }
                /*
                    If something with a health component that's not dead and a root physical component hits a non-held flag, make it hold the flag
                 */
                if(aEntity.has(HealthComponent.class) && aEntity.get(HealthComponent.class).getHealthState() != HealthComponent.HEALTH_STATE.DEAD
                        && aEntity.has(PhysicalComponent.class) && aEntity.get(PhysicalComponent.class).isRoot
                        && bEntity.has(FlagComponent.class) && bEntity.get(FlagComponent.class).getHeldBy() == null){
                    bEntity.get(FlagComponent.class).setHeldBy(aEntity.getUuid());
                }
                //For Goals/Target Areas
                /**
                 *  if A has Type Component w/ goal
                 *  if B has Flag Component
                 *
                 */
                if(aEntity.has(TypeComponent.class) && aEntity.get(TypeComponent.class).getType() == 2
                        && bEntity.has(FlagComponent.class) && bEntity.get(FlagComponent.class).getHeldBy() != null
                        && aEntity.has(TeamComponent.class)
                        && aEntity.get(TeamComponent.class).getTeamNumber() == EntityManager.getInstance().getEntity(bEntity.get(FlagComponent.class).getHeldBy()).get(TeamComponent.class).getTeamNumber() ) {
                    System.out.println( aEntity.get(TeamComponent.class).getTeamNumber() + " " + EntityManager.getInstance().getEntity(bEntity.get(FlagComponent.class).getHeldBy()).get(TeamComponent.class).getTeamNumber());
                    System.out.println("CAPTURE");
                    MessageManager.getInstance().addMessage(INTENT.TEAM_CAPTURE, bEntity.get(FlagComponent.class).getHeldBy());
                    //despawn flag
                    bEntity.get(FlagComponent.class).setHeldBy(null);
                    //Move the flag back to spawn location
                    MessageManager.getInstance().addMessage(INTENT.SPAWN, bEntity);
                }
            }

            //entity and powerup
            else if(b.getUserData() instanceof PowerUp){
                //this should be safe since only car bodies and powerups can collide due to their bitmasks and category masks
                ((PowerUp)b.getUserData()).applyToEntity(aEntity);
            }
        }
    }
}