package ecs.subsystems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Input.MyInputAdapter;
import ecs.Entity;
import ecs.EntityManager;
import ecs.components.ControlledComponent;
import ecs.components.FlagComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;
import ecs.components.TeamComponent;
import verberg.com.shmup.Constants;
import verberg.com.shmup.ShmupGame;

/**
 * Created by Orion on 11/29/2015.
 */
public class RenderSystem {

    private NinePatchDrawable loadingBar;
    private Sprite arrow;



    public RenderSystem(){
        FileHandle fh = Gdx.files.internal("RedPixel.png");
        Texture tex = new Texture(fh);
        NinePatch loadingBarPatch = new NinePatch(tex, 0, 0, 0, 0);
        loadingBar = new NinePatchDrawable(loadingBarPatch);
        arrow = new Sprite(new Texture("arrow.png"));

        arrow.setSize(arrow.getWidth() * 1f / (Constants.PPM * 2f), arrow.getHeight()* 1f / (Constants.PPM * 2f));
    }


    public void render(ArrayList<Entity> entities, Batch batch){



        ArrayList<UUID> flagEntities = EntityManager.getInstance().getEntitiesWithComponent(FlagComponent.class);
        for(Entity e : entities) {
            if(e.has(HealthComponent.class) && e.has(PhysicalComponent.class)){
                if(e.get(PhysicalComponent.class).isRoot)
                    renderHealthComponent(e.get(HealthComponent.class), e.get(PhysicalComponent.class), batch);
            }

            //if CTF is on
            //Possible efficiency improvement here
            /**
             * Find flag component
             */
            //only happen for player
            if(e.has(ControlledComponent.class) && e.get(ControlledComponent.class).ig instanceof MyInputAdapter
                    && e.has(PhysicalComponent.class) && e.get(PhysicalComponent.class).isRoot
                    ) {
                for (UUID uid : flagEntities) {
                    //check if same flag team
                    if(EntityManager.getInstance().hasComponent(uid, TeamComponent.class) && EntityManager.getInstance().hasComponent(e.getUuid(), TeamComponent.class)
                            && EntityManager.getInstance().getComponent(uid, TeamComponent.class).getTeamNumber() ==   EntityManager.getInstance().getComponent(e.getUuid(), TeamComponent.class).getTeamNumber() ){
                        renderArrow(e.getUuid(), uid, batch);
                    }else{
                        //show arrow to this flag
                        renderArrow(e.getUuid(), uid, batch);
                    }
                }
            }
        }
    }

    private float angle, ang2;
    private Vector2 src,tar;
    private Vector3 flagCoords, srcCoords;

    private void renderArrow(UUID source, UUID target, Batch batch){
        if(EntityManager.getInstance().hasComponent(source, PhysicalComponent.class ) && EntityManager.getInstance().hasComponent(target, PhysicalComponent.class )){

            src =  EntityManager.getInstance().getComponent(source, PhysicalComponent.class).getPosition();
            tar = EntityManager.getInstance().getComponent(target, PhysicalComponent.class).getPosition();
            src = EntityManager.getInstance().getComponent(source, PhysicalComponent.class).facingVector(2f);
            angle = (float)Math.toDegrees(Math.atan2(tar.y - src.y, tar.x - src.x));

            arrow.setPosition(src.x, src.y);
            arrow.setOriginCenter();
            arrow.setRotation(angle);
            arrow.draw(batch);

        }
    }

    private void renderHealthComponent(HealthComponent hc, PhysicalComponent pc, Batch batch){
        //replace 5 with PPM from static class
        float adjx = (float)(Math.cos(pc.getBody().getAngle() + Math.PI/2)) - 1f;
        float adjy = (float)(Math.sin(pc.getBody().getAngle() + Math.PI/2)) + 1.75f;
        adjx *= 0.4;
        adjy *= 0.4;
        if(hc.getCur_health() > 0)
            loadingBar.draw(batch, pc.getBody().getPosition().x + adjx, pc.getBody().getPosition().y + adjy, ((float)hc.getCur_health() / (float)hc.max_health) * 1.0f , 0.25f);

    }
}
