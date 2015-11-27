package ecs.subsystems;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import java.util.ArrayList;

import Input.MyInputAdapter;
import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ParentEntityComponent;
import ecs.components.PhysicalComponent;
import ecs.components.WeaponComponent;
import verberg.com.shmup.Game;
import verberg.com.shmup.INTENT;
import verberg.com.shmup.MessageManager;

/**
 * Created by Orion on 11/23/2015.
 * Used to fire weapons
 */
public class WeaponSystem extends SubSystem {

    public void update(Entity entity){
        ArrayList<Entity> createBulletsForThese = new ArrayList<Entity>();
        ArrayList<PhysicalComponent> bullets  = new ArrayList<PhysicalComponent>();
        ArrayList<Fixture> setUserDataForThese  = new ArrayList<Fixture>();

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

                Body bulletbody = Game.world.createBody(bodyDef);
                bulletbody.setTransform(sourceBody.getPosition().add(addx, addy), direction);
                CircleShape circle = new CircleShape();
                circle.setRadius(1f);
                FixtureDef fixture = new FixtureDef();
                fixture.shape = circle;
                fixture.density = 1f;
                fixture.friction = 0.1f;

                Fixture bulletFixture = bulletbody.createFixture(fixture);

                createBulletsForThese.add(entity);
                bullets.add(new PhysicalComponent(bulletbody));
                setUserDataForThese.add(bulletFixture);


                bulletbody.applyLinearImpulse(((float) Math.cos(direction)) * 1500f, ((float) Math.sin(direction)) * 1500f, bulletbody.getWorldCenter().x, bulletbody.getWorldCenter().y, true);
                circle.dispose();

                wc.lastFire = System.currentTimeMillis();
            }
        }

        //create bullets here cause of concurrent modification shit
        for(int i = 0; i < bullets.size(); i++){
            Entity bEntity = new Entity(bullets.get(i), new ParentEntityComponent(createBulletsForThese.get(i)));
            setUserDataForThese.get(i).setUserData(bEntity);
        }
    }
    /*
    public void update(ArrayList<Entity> entities){
        ArrayList<Entity> createBulletsForThese = new ArrayList<Entity>();
        ArrayList<PhysicalComponent> bullets  = new ArrayList<PhysicalComponent>();
        ArrayList<Fixture> setUserDataForThese  = new ArrayList<Fixture>();


        for(Entity e : entities){
            if(e.has(WeaponComponent.class)){
                WeaponComponent wc = e.get(WeaponComponent.class);
                //Move this check to an input system
                //The input system/AI should create intents
                if(MessageManager.hasMessage(e, INTENT.FIRE)){
                    if(wc.lastFire + wc.firingDelay < System.currentTimeMillis()){
                        Body sourceBody = null;
                        if(e.has(PhysicalComponent.class)){
                            PhysicalComponent pc = e.get(PhysicalComponent.class);
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

                        Body bulletbody = Game.world.createBody(bodyDef);
                        bulletbody.setTransform(sourceBody.getPosition().add(addx, addy), direction);
                        CircleShape circle = new CircleShape();
                        circle.setRadius(1f);
                        FixtureDef fixture = new FixtureDef();
                        fixture.shape = circle;
                        fixture.density = 1f;
                        fixture.friction = 0.1f;


                        Fixture bulletFixture = bulletbody.createFixture(fixture);

                        createBulletsForThese.add(e);
                        PhysicalComponent pc = new PhysicalComponent(bulletbody);
                        pc.maxContacts = 1; //Bullets can only hit 1 object before being destroyed
                        bullets.add(pc);
                        setUserDataForThese.add(bulletFixture);


                        bulletbody.applyLinearImpulse(((float) Math.cos(direction)) * 1500f, ((float) Math.sin(direction)) * 1500f, bulletbody.getWorldCenter().x, bulletbody.getWorldCenter().y, true);
                        circle.dispose();


                        wc.lastFire = System.currentTimeMillis();
                    }
                }
            }
        }
        //create bullets here cause of concurrent modification shit
        for(int i = 0; i < bullets.size(); i++){
            Entity bEntity = new Entity(bullets.get(i), new ParentEntityComponent(createBulletsForThese.get(i)));
            setUserDataForThese.get(i).setUserData(bEntity);
        }



    }
*/

}
