package org.tilegames.hexicube.spacegame.bodypart.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.script.ScriptException;

import org.jbox2d.dynamics.Body;
import org.tilegames.hexicube.spacegame.Game;
import org.tilegames.hexicube.spacegame.ScriptData;

public class PartScript
{
	private String name;
	
	public String controlScript, tickScript, resetScript;
	public ScriptData data;
	
	public String[] allowedResources;
	public long maxResources;

	public Body body;
	
	public String getName()
	{
		return name;
	}
	
	public PartScript() {}
	
	public PartScript(String partName)
	{
		name = partName;
		
		controlScript = "";
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+partName+"_control.js")));
			while(in.ready()) controlScript += in.readLine()+"\n";
			in.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e)
		{
			e.printStackTrace();
			controlScript = "";
		}

		tickScript = "";
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+partName+"_tick.js")));
			while(in.ready()) tickScript += in.readLine()+"\n";
			in.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e)
		{
			e.printStackTrace();
			tickScript = "";
		}

		resetScript = "";
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+partName+"_reset.js")));
			while(in.ready()) resetScript += in.readLine()+"\n";
			in.close();
		}
		catch (FileNotFoundException e) {}
		catch (IOException e)
		{
			e.printStackTrace();
			resetScript = "";
		}
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		if(o instanceof PartScript)
		{
			return name.equalsIgnoreCase(((PartScript)o).getName());
		}
		if(o instanceof String)
		{
			return name.equalsIgnoreCase((String)o);
		}
		return false;
	}

	public PartScript copy()
	{
		PartScript script = new PartScript();
		script.name = name;
		script.controlScript = controlScript;
		script.tickScript = tickScript;
		script.resetScript = resetScript;
		script.data = new ScriptData();
		Game.scriptData.add(script.data);
		
		try
		{
			String prepScript = "";
			BufferedReader in = new BufferedReader(new FileReader(new File("parts"+File.separator+name+"_define.js")));
			while(in.ready()) prepScript += in.readLine()+"\n";
			in.close();
			Object temp = Game.e.get("scriptid");
			Game.e.put("scriptid", script.data.UUID);
			Game.e.eval(prepScript);
			String allowed = (String)Game.e.get("allowedresources");
			if(allowed != null)
			{
				allowedResources = allowed.split(",");
				maxResources = Long.parseLong((String)Game.e.get("maxresources"));
			}
			else
			{
				allowedResources = new String[0];
				maxResources = 0;
			}
			Game.e.put("scriptid", temp);
		}
		catch (FileNotFoundException e) {}
		catch (IOException e) {}
		catch (ScriptException e)
		{
			e.printStackTrace();
		}
		
		return script;
	}
}