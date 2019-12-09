package com.jdrx.gis.beans.entity.cad;

public class Blocks extends Section{
	public Blocks()
	{
		super("BLOCKS");
		// 
		// TODO: Add constructor logic here
		//
	}
	public void addBlock(Block b)
	{
		this.elements.add(b);
	}
}
