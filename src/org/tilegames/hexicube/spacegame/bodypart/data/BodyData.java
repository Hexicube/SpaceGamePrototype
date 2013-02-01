package org.tilegames.hexicube.spacegame.bodypart.data;

import com.badlogic.gdx.graphics.Color;

public class BodyData
{
	public Color colour;
	
	public double heat, heatRate;
	
	public BodyData(Color colour, double heatRate)
	{
		this.colour = colour;
		this.heatRate = heatRate;
	}
}