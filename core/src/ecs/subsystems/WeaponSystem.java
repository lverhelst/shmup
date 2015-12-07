package ecs.subsystems;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.DamageComponent;
import ecs.components.HealthComponent;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.WeaponComponent;
import verberg.com.shmup.Constants;
import verberg.com.shmup.Game;

/**
 * Created by Orion on 11/23/2015.
 * Used to fire weapons
 */
public class WeaponSystem implements SubSystem {

    public void processMessage(Object ... list) {
        if(list[0].getClass() == Entity.class) {
            Entity e = (Entity)list[0];
            fireStuff(e);
        }
    }

    public void fireStuff(Entity entity){
        if(entity.has(HealthComponent.class)){
            if((entity.get(HealthComponent.class)).getHealthState() == HealthComponent.HEALTH_STATE.DEAD){
                //Ya can't shoot if your dead
                return;
            }
        }

        if(entity.has(WeaponComponent.class)){
            WeaponComponent wc = entity.get(WeaponComponent.class);
            //Move this check to an input system
            //The input system/AI should create intents

            if(wc.lastFire + wc.firingDelay < System.currentTimeMillis()){
                Body sourceBody = null;
                if(entity.has(PhysicalComponent.class)){
                    PhysicalComponent pc = entity.get(PhysicalComponent.class);
                    sourceBody = pc.getBody();
                }
                if(sourceBody == null)
                    return;

                BodyDef bodyDef = new BodyDef();
                bodyDef.type = BodyDef.BodyType.DynamicBody;

                //add 90 since car definition is 90 deg off 0
                float direction = sourceBody.getAngle() + (float)Math.PI/2;
                float addx = (float)(Math.cos(direction)) * 8f;
                float addy = (float)(Math.sin(direction)) * 8f;

                Body bulletbody = Game.getWorld().createBody(bodyDef);
                bulletbody.setTransform(sourceBody.getPosition().add(addx, addy), direction);

                CircleShape circle = new CircleShape();
                circle.setRadius(1f);
                FixtureDef fixture = new FixtureDef();
                fixture.shape = circle;
                fixture.density = 1f;
                fixture.friction = 0.1f;
                fixture.filter.categoryBits = Constants.BULLET_BIT;
                fixture.filter.maskBits = Constants.BULLET_MASK;

                Fixture bulletFixture = bulletbody.createFixture(fixture);


                PhysicalComponent pc = new PhysicalComponent(bulletbody);
                pc.maxContacts = 1; //Bullets can only hit 1 object before being destroyed


                bulletbody.applyLinearImpulse(((float) Math.cos(direction)) * 15000f, ((float) Math.sin(direction)) * 15000f, bulletbody.getWorldCenter().x, bulletbody.getWorldCenter().y, true);
                circle.dispose();

                wc.lastFire = System.currentTimeMillis();

                Entity bEntity = new Entity(pc, new ParentEntityComponent(entity), new DamageComponent((int)(20 * wc.multiplier)));
                bulletFixture.setUserData(bEntity);
            }
        }
    }
}