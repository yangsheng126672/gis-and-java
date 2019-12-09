package com.jdrx.gis.beans.entity.cad;


public class Circle extends Entity{
	
	public String LName;
	public String CenterX;
	public String CenterY;
	public String Radius;
	public String lwidth;
	
	public Circle()
	{
		
	}
	
	public String toString()
	{
		return LName +" ("+CenterX+","+CenterY+") "+Radius +" "+lwidth;
	}
	
	public Circle(double x, double y, double radius, String layer) throws UnexpectedElement
	{
		super("CIRCLE",layer);
		int[] it = new int[] { 39, 10, 20, 30, 40, 210, 220, 230 };
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
		this.AddReplace(10,x);
		this.AddReplace(20,y);
		this.AddReplace(40,radius);
	}
}
