package org.tilegames.hexicube.spacegame;

import com.badlogic.gdx.graphics.Color;

public class TextItem
{
	public String text;
	public int x, y;
	public boolean doubleScale;
	public Color colour;
	
	public TextItem(String text, int x, int y, boolean doubleScale, Color colour)
	{
		this.text = text;
		this.x = x;
		this.y = y;
		this.doubleScale = doubleScale;
		this.colour = colour;
	}
}