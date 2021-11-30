package edu.claflin.finder.logic.cygrouper;

import java.util.ArrayList;

/* This class was written by Evyatar Saias & Ariel Sari on Nov 19, 2019. */
public class CygrouperNode
{
	public String name;
	public boolean isVisited = false;
	ArrayList<CygrouperNode> connections = new ArrayList<>();
	public String group;

	public int kPartiteGroupNumber;

	public CygrouperNode(String name)
	{
		this.name = name;
	}

	public ArrayList<CygrouperNode> getConnectionsList()
	{
		return this.connections;
	}

	public String toString()
	{
		return "{" + this.name + "}";
	}

	public void setGroup(String AorB)
	{
		this.group = AorB;
	}

	public void setPartiteNumber(int num)
	{
		this.kPartiteGroupNumber = num;
	}

	public int getPartiteNumber()
	{
		return this.kPartiteGroupNumber;
	}

	public boolean isGroup(String groupStr)
	{
		return this.group.equals(groupStr);
	}

	public String getOppositeGroup()
	{
		if (this.group.equals("A"))
		{
			return "B";
		}
		else if (this.group.equals("B"))
		{
			return "A";
		}
		else
		{
			throw new NullPointerException();
		}
	}
}
