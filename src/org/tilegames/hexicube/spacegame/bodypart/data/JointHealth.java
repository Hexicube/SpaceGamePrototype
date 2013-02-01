package org.tilegames.hexicube.spacegame.bodypart.data;

public class JointHealth
{
	public double health;
	
	public JointHealth(int hp)
	{
		health = hp;
	}
	
	public void hurt(float force)
	{
		health -= force;
		if(health < 0) health = 0;
	}
}