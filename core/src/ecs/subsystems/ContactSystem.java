package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import ecs.Entity;
import ecs.components.DamageComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import gameObjects.PowerUp;
import verberg.com.shmup.Game;
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
                    Game.slightlyWarmMail.addMessage(RemovalSystem.class, ecs, INTENT.REMOVE);
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
                            Game.slightlyWarmMail.addMessage(RemovalSystem.class, aEntity, INTENT.DIED);
                        }
                    }
                }
            }

            //entity and powerup
            else if(b.getUserData() instanceof PowerUp){
                //this should be safe since only car bodies and powerups can collide due to their bitmasks and category masks
                ((PowerUp)b.getUserData()).applyToEntity(aEntity);
            }

            //entity and ground
            //TODO: make proper management of ground, instead of haque-y int ground
            else if(b.getUserData() instanceof Integer) {
                //TODO: if not death ground make friction apply to car? (This way rough and slippery ground can be made)
                if (aEntity.has(HealthComponent.class)) {
                    if ((aEntity.get(HealthComponent.class)).getHealthState() != HealthComponent.HEALTH_STATE.DEAD) {
                        //apply damage
                        (aEntity.get(HealthComponent.class)).setCur_Health(0);
                        //if the other entity is now dead, send the dead messagea
                        if ((aEntity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD) {
                            Game.slightlyWarmMail.addMessage(RemovalSystem.class, aEntity, INTENT.DIED);
                        }
                    }
                }
            }
        }
    }
}