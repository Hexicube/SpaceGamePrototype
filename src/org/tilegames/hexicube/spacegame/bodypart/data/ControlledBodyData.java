package org.tilegames.hexicube.spacegame.bodypart.data;

import java.util.ArrayList;

import javax.script.ScriptException;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.JointEdge;
import org.tilegames.hexicube.spacegame.Game;

import com.badlogic.gdx.graphics.Color;

public class ControlledBodyData extends OwnedBodyData
{
	public PartScript scripts;
	
	public Body cockpit, parent;
	public int distFromCockpit;
	
	public ArrayList<Body> subparts;
	public ArrayList<Resource> resources;
	public String[] allowedResources;
	public long maxResources;
	
	public ControlledBodyData(Body body, String owner, int team, Color colour, int health, PartScript scripts, double heatRate, double damageRate, Body cockpit)
	{
		super(body, owner, team, colour, health, heatRate, damageRate);
		this.scripts = scripts;
		if(scripts != null)
		{
			allowedResources = scripts.allowedResources;
			maxResources = scripts.maxResources;
		}
		else
		{
			allowedResources = new String[0];
			maxResources = 0;
		}
		this.cockpit = cockpit;
		subparts = new ArrayList<Body>();
	}
	
	public void calcDistFromCockpit()
	{
		if(cockpit == null) return;
		distFromCockpit = -1;
		if(cockpit == body) distFromCockpit = 0;
		for(JointEdge j = body.getJointList(); j != null; j = j.next)
		{
			Body otherBody = j.other;
			if(otherBody.getUserData() instanceof ControlledBodyData)
			{
				ControlledBodyData data = (ControlledBodyData)otherBody.getUserData();
				if(data.cockpit == cockpit && data.distFromCockpit != -1)
				{
					data.calcDistFromCockpit();
					if(data.distFromCockpit != -1 && data.distFromCockpit < distFromCockpit) distFromCockpit = data.distFromCockpit;
				}
			}
		}
		if(distFromCockpit == -1) setCockpit(null);
	}
	
	public void setCockpit(Body newCockpit)
	{
		if(cockpit != null)
		{
			if(cockpit.getUserData() instanceof ControlledBodyData)
			{
				((ControlledBodyData)cockpit.getUserData()).removeSubPart(body);
			}
		}
		if(newCockpit != null)
		{
			((ControlledBodyData)cockpit.getUserData()).addSubPart(body);
		}
		cockpit = newCockpit;
		calcDistFromCockpit();
	}
	
	public void removeSubPart(Body body)
	{
		if(body != this.body) subparts.remove(body);
	}
	
	public void addSubPart(Body body)
	{
		if(body != this.body) subparts.add(body);
	}
	
	public double requestResource(String name, double amount, boolean acceptPartial)
	{
		if(cockpit != null && cockpit != body)
		{
			if(cockpit.getUserData() instanceof ControlledBodyData)
			{
				ControlledBodyData data = (ControlledBodyData)cockpit.getUserData();
				return data.requestResource(name, amount, acceptPartial);
			}
		}
		if(acceptPartial)
		{
			double collected = removeResource(name, amount);
			int size = subparts.size();
			for(int a = 0; a < size && collected < amount; a++)
			{
				Body subpart = subparts.get(a);
				if(subpart.getUserData() instanceof ControlledBodyData)
				{
					ControlledBodyData data = (ControlledBodyData)subpart.getUserData();
					collected += data.removeResource(name, amount);
				}
			}
			return collected;
		}
		else
		{
			if(getResource(name).amount >= amount) return removeResource(name, amount);
			int size = subparts.size();
			for(int a = 0; a < size; a++)
			{
				Body subpart = subparts.get(a);
				if(subpart.getUserData() instanceof ControlledBodyData)
				{
					ControlledBodyData data = (ControlledBodyData)subpart.getUserData();
					if(data.getResource(name).amount >= amount) return data.removeResource(name, amount);
				}
			}
			return 0;
		}
	}
	
	public double depositResource(String name, double amount, boolean acceptPartial)
	{
		if(cockpit != null && cockpit != body)
		{
			if(cockpit.getUserData() instanceof ControlledBodyData)
			{
				ControlledBodyData data = (ControlledBodyData)cockpit.getUserData();
				return data.depositResource(name, amount, acceptPartial);
			}
		}
		if(acceptPartial)
		{
			double collected = addResource(name, amount, true);
			int size = subparts.size();
			for(int a = 0; a < size && collected < amount; a++)
			{
				Body subpart = subparts.get(a);
				if(subpart.getUserData() instanceof ControlledBodyData)
				{
					ControlledBodyData data = (ControlledBodyData)subpart.getUserData();
					collected += data.addResource(name, amount-collected, true);
				}
			}
			return collected;
		}
		else
		{
			double deposited = addResource(name, amount, false);
			if(deposited != 0) return amount;
			int size = subparts.size();
			for(int a = 0; a < size; a++)
			{
				Body subpart = subparts.get(a);
				if(subpart.getUserData() instanceof ControlledBodyData)
				{
					ControlledBodyData data = (ControlledBodyData)subpart.getUserData();
					if(data.allowedResource(name))
					{
						deposited = data.addResource(name, amount, false);
						if(deposited != 0) return amount;
					}
				}
			}
			return 0;
		}
	}
	
	public boolean allowedResource(String name)
	{
		for(int a = 0; a < allowedResources.length; a++)
		{
			if(allowedResources[a].equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public double addResource(String name, double amount, boolean acceptPartial)
	{
		if(!allowedResource(name)) return 0;
		double cap = maxResources;
		int size = resources.size();
		for(int a = 0; a < size; a++)
		{
			cap -= resources.get(a).amount;
		}
		if(cap < amount)
		{
			if(!acceptPartial) return 0;
			amount = cap;
		}
		Resource r = getResource(name);
		if(r == null) resources.add(new Resource(name, amount));
		else r.amount += amount;
		return amount;
	}
	
	public double removeResource(String name, double amount)
	{
		Resource r = getResource(name);
		if(r != null)
		{
			if(r.amount <= amount)
			{
				resources.remove(r);
				return r.amount;
			}
			r.amount -= amount;
			return amount;
		}
		return 0;
	}
	
	public Resource getResource(String name)
	{
		int size = resources.size();
		for(int a = 0; a < size; a++)
		{
			if(resources.get(a).name.equalsIgnoreCase(name)) return resources.get(a);
		}
		return null;
	}
	
	public void handleControl(int key, boolean down)
	{
		if(scripts == null) return;
		if(health > 0)
		{
			Game.e.put("scriptid", scripts.data.UUID);
			Game.e.put("pressedkey", key);
			Game.e.put("waspress", down);
			try
			{
				Game.e.eval(scripts.controlScript);
			}
			catch (ScriptException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void startTick()
	{
		if(parent != null)
		{
			if(parent.getUserData() instanceof LivingBodyData)
			{
				if(((LivingBodyData)parent.getUserData()).health <= 0) health = 0;
			}
			else health = 0;
		}
		if(health <= 0) return;
		if(scripts == null) return;
		try
		{
			Game.e.put("scriptid", scripts.data.UUID);
			Game.e.eval(scripts.tickScript);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}
	
	public void resetTick()
	{
		if(scripts == null) return;
		Game.e.put("scriptid", scripts.data.UUID);
		Game.e.put("impulse", 0);
		Game.e.put("rotimpulse", 0);
		Game.e.put("needsreset", 1);
		Game.e.put("health", health);
		Game.e.put("heat", heat);
		Game.e.put("body", scripts.body);
		try
		{
			Game.e.eval(scripts.resetScript);
		}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
	}
	
	public void endTick()
	{
		if(scripts == null) return;
		Object o = Game.e.get("health");
		if(o instanceof Integer) health = (Integer)o;
		else if(o instanceof Double) health = ((Double)o).intValue();
		else health = 0;
		if(health > 0)
		{
			o = Game.e.get("heat");
			if(o instanceof Integer) heat = (Integer)o;
			else if(o instanceof Double) heat = ((Double)o).intValue();
			else heat = 0;
			float impulse;
			o = Game.e.get("impulse");
			if(o instanceof Integer) impulse = ((Integer)o).floatValue();
			else if(o instanceof Double) impulse = ((Double)o).floatValue();
			else impulse = 0;
			if(impulse != 0)
			{
				float angle = (float) (body.getAngle()+Math.PI/2);
				Vec2 pos = body.getPosition();
				body.applyForce(new Vec2((float)(impulse*Math.cos(angle)), (float)(impulse*Math.sin(angle))), new Vec2((float)(pos.x-10*Math.cos(angle)), (float)(pos.y-10*Math.sin(angle))));
			}
			o = Game.e.get("rotimpulse");
			if(o instanceof Integer) impulse = ((Integer)o).floatValue();
			else if(o instanceof Double) impulse = ((Double)o).floatValue();
			else impulse = 0;
			if(impulse != 0)
			{
				body.applyAngularImpulse(impulse);
			}
		}
	}
}