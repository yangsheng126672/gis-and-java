package com.jdrx.gis.beans.entity.cad;

public class Variable extends Element{
	
	private Data startTag;
	private Data endTag;
	public Variable(String nume, int dataType, Object val)
	{
		startTag = new Data(0,0);
		endTag = new Data(0,0);

		this.data.add(new Data(9,nume));
		this.data.add(new Data(dataType,val));
	}
	/// <summary>
	/// The header variable's name. Just get.
	/// </summary>
	public String getVarName()
	{
			return (String)((Data)this.data.get(0)).data;
	}
	/// <summary>
	/// The header variable's value. Just get.
	/// </summary>
	public Object getValue()
	{
			return ((Data)this.data.get(1)).data;
	}
}
