package org.tilegames.hexicube.spacegame.bodypart.data;

import com.badlogic.gdx.graphics.Color;

public class LivingBodyData extends BodyData
{
	public double health, healthMax, damageRate;
	
	public boolean killOnDeath;
	
	public LivingBodyData(int hp, Color colour, double heatRate, double damageRate)
	{
		super(colour, heatRate);
		this.damageRate = damageRate;
		
		if(hp == 0)
		{
			health = 1;
			healthMax = -1;
		}
		else
		{
			health = hp;
			healthMax = hp;
		}
	}
	
	public void hurt(double damage)
	{
		damage *= damageRate;
		if(health == 1 && healthMax == -1) return;
		if(damage < 0) damage = 0;
		health -= damage;
		if(health < 0) health = 0;
	}
	
	public void heal(double amount)
	{
		if(health == 1 && healthMax == -1) return;
		if(amount < 0) amount = 0;
		health += amount;
		if(health > healthMax) health = healthMax;
	}
}