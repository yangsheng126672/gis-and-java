package com.jdrx.gis.beans.entity.cad;

public class Entities extends Section{
	public Entities()
	{
		super("ENTITIES");
		// 
		// TODO: Add constructor logic here
		//
	}
	public void AddEntity(Entity e) throws UnexpectedElement
	{
		this.AddElement(e);
	}
}
