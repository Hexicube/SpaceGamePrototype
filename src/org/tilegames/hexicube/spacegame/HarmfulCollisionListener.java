package org.tilegames.hexicube.spacegame;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.joints.JointEdge;
import org.tilegames.hexicube.spacegame.bodypart.data.BodyData;
import org.tilegames.hexicube.spacegame.bodypart.data.JointHealth;
import org.tilegames.hexicube.spacegame.bodypart.data.LivingBodyData;

public class HarmfulCollisionListener implements ContactListener
{
	@Override
	public void beginContact(Contact contact)
	{
	}
	@Override
	public void endContact(Contact contact)
	{
	}
	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
	}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		float force = (float) Math.sqrt(impulse.normalImpulses[0]*impulse.normalImpulses[0]+
				impulse.normalImpulses[1]*impulse.normalImpulses[1]);
		Body b1 = contact.getFixtureA().getBody();
		Body b2 = contact.getFixtureB().getBody();
		if(b1.getUserData() instanceof LivingBodyData ||
		   b2.getUserData() instanceof LivingBodyData)
		{
			if(b1.getUserData() instanceof LivingBodyData)
			{
				LivingBodyData data = (LivingBodyData)b1.getUserData();
				data.hurt(force);
			}
			if(b2.getUserData() instanceof LivingBodyData)
			{
				LivingBodyData data = (LivingBodyData)b2.getUserData();
				data.hurt(force);
			}
		}
		for(JointEdge j = b1.getJointList(); j != null; j = j.next)
		{
			if(j.joint.getUserData() != null && j.joint.getUserData() instanceof JointHealth)
			{
				JointHealth data = (JointHealth)j.joint.getUserData();
				data.hurt(force);
			}
		}
		for(JointEdge j = b2.getJointList(); j != null; j = j.next)
		{
			if(j.joint.getUserData() != null && j.joint.getUserData() instanceof JointHealth)
			{
				JointHealth data = (JointHealth)j.joint.getUserData();
				data.hurt(force);
			}
		}
		if(b1.getUserData() instanceof BodyData &&
		   b2.getUserData() instanceof BodyData)
		{
			BodyData data1 = (BodyData)b1.getUserData();
			BodyData data2 = (BodyData)b2.getUserData();
			double diff = Math.abs(data1.heat - data2.heat);
			if(diff > 0)
			{
				double spread = Math.sqrt(diff);
				if(data1.heat > data2.heat)
				{
					data1.heat -= spread;
					data2.heat += spread;
				}
				else
				{
					data1.heat += spread;
					data2.heat -= spread;
				}
			}
		}
	}
}