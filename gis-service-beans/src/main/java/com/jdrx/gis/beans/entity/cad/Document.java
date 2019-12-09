package com.jdrx.gis.beans.entity.cad;

public class Document {
	public Header header;
	public Entities entities;
	public Blocks blocks;
	public Element tables;
	public Document()
	{
		this.entities = new Entities();
	}
	public void SetHeader(Header h)
	{
		this.header = h;
	}
	public void SetEntities(Entities e)
	{
		this.entities = e;
	}
	public void SetBlocks(Blocks b)
	{
		this.blocks = b;
	}
	public void SetTables(Element t)
	{
		this.tables = t;
	}
	public void add(Entity e) throws UnexpectedElement
	{
		this.entities.AddEntity(e);
	}
	public void add(Block b)
	{
		this.blocks.addBlock(b);
	}
}
