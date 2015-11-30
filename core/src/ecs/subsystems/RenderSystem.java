package ecs.subsystems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.ArrayList;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.ControlledComponent;
import ecs.components.HealthComponent;
import ecs.components.PhysicalComponent;

/**
 * Created by Orion on 11/29/2015.
 */
public class RenderSystem extends SubSystem {

    private NinePatchDrawable loadingBar;


    public RenderSystem(){
        FileHandle fh = Gdx.files.internal("RedPixel.png");
        Texture tex = new Texture(fh);
        NinePatch loadingBarPatch = new NinePatch(tex, 0, 0, 0, 0);
        loadingBar = new NinePatchDrawable(loadingBarPatch);

    }


    public void render(ArrayList<Entity> entities, Batch batch){
        for(Entity e : entities) {
            if(e.has(HealthComponent.class) && e.has(PhysicalComponent.class)){
                if(e.get(PhysicalComponent.class).isRoot)
                    renderHealthComponent(e.get(HealthComponent.class), e.get(PhysicalComponent.class), batch);
            }
        }
    }

    private void renderHealthComponent(HealthComponent hc, PhysicalComponent pc, Batch batch){
        //replace 5 with PPM from static class
        float adjx = (float)(Math.cos(pc.getBody().getAngle() + Math.PI/2)) - 5f;
        float adjy = (float)(Math.sin(pc.getBody().getAngle() + Math.PI/2)) * 2 + 5f;
        if(hc.cur_health > 0)
            loadingBar.draw(batch, pc.getBody().getPosition().x + adjx, pc.getBody().getPosition().y + adjy, ((float)hc.cur_health / (float)hc.max_health) * 10 , 1);

    }
}
