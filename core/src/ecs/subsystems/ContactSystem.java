package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import ecs.Entity;
import ecs.components.PhysicalComponent;
import gameObjects.Physical;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.Message;
import verberg.com.shmup.MessageManager;

/**
 * Created by Orion on 11/21/2015.
 * TODO: Make it check entity components
 * TODO: Rename to Contact System -- This is world.step
 * TODO: Make it send messages to message system
 */
public class ContactSystem implements ContactListener{
    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        handleFixture(fixtureA);
        handleFixture(fixtureB);
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
                    MessageManager.addMessage(new Message(ecs, INTENT.REMOVE_ENTITY));
                }
            }

        }
    }
}
