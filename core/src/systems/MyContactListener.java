package systems;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import components.Bullet;
import components.Physical;
import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/21/2015.
 */
public class MyContactListener implements ContactListener{
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
