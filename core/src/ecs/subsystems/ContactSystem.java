package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import gameObjects.Physical;

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


        if(fixtureA.getUserData() instanceof Physical){
            ((Physical)fixtureA.getUserData()).checkCollision(fixtureB.getUserData());
        }
        if(fixtureB.getUserData() instanceof Physical){
            ((Physical)fixtureB.getUserData()).checkCollision(fixtureA.getUserData());
        }
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
}
