package ecs.subsystems;

import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ecs.Entity;
import ecs.EntityManager;
import ecs.SubSystem;
import ecs.components.CameraAttachmentComponent;
import ecs.components.PhysicalComponent;

/**
 * Created by Orion on 11/23/2015.
 * Used to set the provided camera on an entity
 */
public class CameraSystem {


        float playerAngle, camAngle, lastSpeed, zoom;

        public void update(ArrayList<UUID> entities, OrthographicCamera camera){
            for(UUID ent : entities){
                Entity e = EntityManager.getInstance().getEntity(ent);
                if(e.has(CameraAttachmentComponent.class)){
                    if(e.has(PhysicalComponent.class)){
                        PhysicalComponent pc = e.get(PhysicalComponent.class);
                        if(e.get(CameraAttachmentComponent.class).isSliding()){
                            camera.position.set(e.get(CameraAttachmentComponent.class).getSlidePosition(), 10);
                        }else{


                            float velX = pc.getBody().getLinearVelocity().x;
                            float velY = pc.getBody().getLinearVelocity().y;

                            float speed =  (float)Math.sqrt(velX * velX + velY * velY);




                            float adjustX = (float) (Math.cos(pc.getBody().getAngle() + Math.PI / 2) * 3f) * 0f;

                            float adjustY = (float) (Math.sin(pc.getBody().getAngle() + Math.PI / 2) * 3f) * 0f;




                            camera.position.set(pc.getBody().getPosition().x + adjustX, pc.getBody().getPosition().y + adjustY, 10);
                            //camera.up.set(0,1,0);
                            //
                            camera.zoom = Math.min(Math.max(camera.zoom += (float)(lastSpeed > speed ? -0.01 : 0.01),2f), 2.5f) ;
                            lastSpeed = speed;

                            playerAngle = (float)Math.toDegrees(pc.getBody().getAngle());
                            while(playerAngle<=0){
                                playerAngle += 360;
                            }
                            while(playerAngle>360){
                                playerAngle -= 360;
                            }
                            camAngle = -getCameraCurrentXYAngle(camera) + 180;
                            camera.rotate((camAngle-playerAngle)+180);
                        }
                        camera.update();
                    }
                }
            }
    }

    public float getCameraCurrentXYAngle(OrthographicCamera cam)
    {
        return (float) Math.toDegrees(Math.atan2(cam.up.x, cam.up.y));
    }


}


