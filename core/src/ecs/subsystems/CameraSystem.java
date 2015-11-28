package ecs.subsystems;

import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;

import ecs.Entity;
import ecs.SubSystem;
import ecs.components.CameraAttachmentComponent;
import ecs.components.PhysicalComponent;

/**
 * Created by Orion on 11/23/2015.
 * Used to set the provided camera on an entity
 */
public class CameraSystem extends SubSystem {

        public void update(ArrayList<Entity> entities, OrthographicCamera camera){
            for(Entity e : entities){
                if(e.has(CameraAttachmentComponent.class)){
                    if(e.has(PhysicalComponent.class)){
                        PhysicalComponent pc = e.get(PhysicalComponent.class);
                        camera.position.set(pc.getBody().getPosition().x, pc.getBody().getPosition().y, 10);
                        camera.update();
                    }
                }
            }
    }

}


