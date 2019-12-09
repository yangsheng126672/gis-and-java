package com.jdrx.gis.beans.entity.cad;

import java.util.Iterator;

public class Header extends Section{
	public Header()
	{
		 super("HEADER");
	}
	public int VariableCount()
	{
		return this.elements.size();
	}
	public Variable getVariable(int index)
	{
		return (Variable)this.elements.get(index);
	}
	public Object valueOf(String varName)
	{
		Variable v;
		Iterator it = this.elements.iterator();
		while(it.hasNext())
		{
			v=(Variable) it.next();
			if(v.getVarName() == varName) return v.getValue();
		}
		return null;
	}
	public void addVariable(Variable v)
	{
		this.elements.add(v);
	}
}
