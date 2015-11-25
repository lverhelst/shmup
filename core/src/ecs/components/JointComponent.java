package ecs.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import ecs.Component;

/**
 * Created by Orion on 11/23/2015.
 * Used to represent a box2d join
 */
public class JointComponent extends Component {
    public RevoluteJoint joint;

    public JointComponent(Body bodyA, Body bodyB, Vector2 location){

        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = bodyA;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.localAnchorB.setZero();

        jointDef.bodyB = bodyB;
        jointDef.localAnchorA.set(location);

        joint  = (RevoluteJoint)bodyA.getWorld().createJoint(jointDef);
    }

}
