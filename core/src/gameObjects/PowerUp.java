package gameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;

import ecs.Component;
import ecs.Entity;
import ecs.components.PhysicalComponent;
import verberg.com.shmup.Constants;
import verberg.com.shmup.ShmupGame;

/**
 * Created by Orion on 11/29/2015.
 */
public abstract class PowerUp {
    Class<? extends Component> forComponent;
    Entity pickedUpEntity;
    Body body;

    final int maxTimeAlive = 60000; //powerups only show up for a minute before despawning
    final int maxTimeOnPlayer = 15000;
    long timeSpawned;
    long timePickedUp;
    private boolean isActive = false;
    protected boolean destroyBodyOnUpdate = false;


    protected PowerUp(Class forComponent){
        this.forComponent = forComponent;
    }

    //spawn at location
    public Entity createEntity() {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;

        body = ShmupGame.getWorld().createBody(bdef);

        Vector2[] vertices = new Vector2[5];
        vertices[0] = new Vector2(-.1f,0);
        vertices[1] = new Vector2(.1f,0);
        vertices[2] = new Vector2(.2f,.1f);
        vertices[3] = new Vector2(0,.3f);
        vertices[4] = new Vector2(-.2f,.1f);
        PolygonShape pShape = new PolygonShape();
        pShape.set(vertices);

        FixtureDef fd = new FixtureDef();
        fd.isSensor = true;
        fd.shape = pShape;
        fd.filter.categoryBits = Constants.POWERUP_BIT;
        fd.filter.maskBits = Constants.POWERUP_MASK;

        Fixture fixture = body.createFixture(fd);
        fixture.setDensity(0f);
        fixture.setUserData(this);
        Entity powerup = new Entity(this.getClass().getSimpleName(), new PhysicalComponent(body));



        timeSpawned = System.currentTimeMillis();
        timePickedUp = 0;
        isActive = true;
        return powerup;
    }

    public void update(){
        if(destroyBodyOnUpdate){
            if(body != null) {
                ShmupGame.getWorld().destroyBody(body);
                body = null;
            }
            destroyBodyOnUpdate = false;
        }
        if(System.currentTimeMillis() - timeSpawned >= maxTimeAlive || (timePickedUp != 0 &&  System.currentTimeMillis() - timePickedUp >= maxTimeOnPlayer)){
            this.despawn();
        }

    }

    //remove from world
    public void despawn() {
        if(body != null) {
            ShmupGame.getWorld().destroyBody(body);
            body = null;
        }
        isActive = false;
    }

    public abstract void applyToEntity(Entity e);

    public boolean isActive() {
        return isActive;
    }
}
