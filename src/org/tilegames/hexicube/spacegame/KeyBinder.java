package org.tilegames.hexicube.spacegame;

public class KeyBinder {
	private char[] binds;
	
	public KeyBinder(String bindList)
	{
		String[] bindValues = bindList.split(":");
		char[] bindNumbers = new char[bindValues.length];
		for(int a = 0; a < bindValues.length; a++)
		{
			bindNumbers[a] = (char) Integer.parseInt(bindValues[a]);
		}
		binds = bindNumbers;
	}
	
	public void setBind(int bindID, char bindValue)
	{
		binds[bindID] = bindValue;
	}
	
	public int getBindID(char bindValue)
	{
		for(int a = 0; a < binds.length; a++)
		{
			if(binds[a] == bindValue) return a;
		}
		return -1;
	}
	
	public char getBindValue(int bindID)
	{
		return binds[bindID];
	}
	
	public char[] getBinds()
	{
		return binds;
	}
	
	public String toString()
	{
		String returnStr = String.valueOf((int)binds[0]);
		for(int a = 1; a < binds.length; a++)
		{
			returnStr += ":" + String.valueOf((int)binds[a]);
		}
		return returnStr;
	}
}