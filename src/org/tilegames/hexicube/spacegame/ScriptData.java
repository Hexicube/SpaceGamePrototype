package org.tilegames.hexicube.spacegame;

import java.util.ArrayList;

public class ScriptData
{
	private static long dataID = 0;
	
	public long UUID;
	public ArrayList<ScriptDataPair> data;
	
	public ScriptData(long ID)
	{
		UUID = ID;
		data = new ArrayList<ScriptDataPair>();
	}
	
	public ScriptData()
	{
		UUID = dataID++;
		data = new ArrayList<ScriptDataPair>();
	}
	
	public Object get(String name)
	{
		int size = data.size();
		for(int a = 0; a < size; a++)
		{
			ScriptDataPair pair = data.get(a);
			if(pair.name.equalsIgnoreCase(name)) return pair.value;
		}
		return null;
	}
	
	public void set(String name, Object value)
	{
		int size = data.size();
		for(int a = 0; a < size; a++)
		{
			ScriptDataPair pair = data.get(a);
			if(pair.name.equalsIgnoreCase(name))
			{
				pair.value = value;
				return;
			}
		}
		ScriptDataPair pair = new ScriptDataPair();
		pair.name = name;
		pair.value = value;
		data.add(pair);
	}
	
	public Object del(String name)
	{
		int size = data.size();
		for(int a = 0; a < size; a++)
		{
			ScriptDataPair pair = data.get(a);
			if(pair.name.equalsIgnoreCase(name))
			{
				data.remove(a);
				return pair.value;
			}
		}
		return null;
	}
}