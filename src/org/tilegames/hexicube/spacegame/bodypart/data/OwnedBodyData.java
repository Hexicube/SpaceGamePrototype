package org.tilegames.hexicube.spacegame.bodypart.data;

import org.jbox2d.dynamics.Body;

import com.badlogic.gdx.graphics.Color;

public class OwnedBodyData extends LivingBodyData
{
	public Body body;
	public int team;
	public String owner;
	
	public OwnedBodyData(Body body, String owner, int team, Color colour, int health, double heatRate, double damageRate)
	{
		super(health, colour, heatRate, damageRate);
		this.body = body;
		this.owner = owner;
		this.team = team;
	}
}