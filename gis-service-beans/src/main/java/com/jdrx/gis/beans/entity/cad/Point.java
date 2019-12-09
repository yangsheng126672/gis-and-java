package com.jdrx.gis.beans.entity.cad;



public class Point extends Entity{
	
	public String colornum;
	public String LName;
	public double PointX;
	public double PointY;
	public String lwidth;
	
	public Point()
	{
		
	}
	
	public String toString()
	{
		return LName +" ("+PointX+","+PointY+") "+colornum+" "+lwidth;
	}
	
	public Point(double x,double y, String layer) throws UnexpectedElement 
	{
		super("POINT", layer);
		this.PointX = x;
		this.PointY = y;
		int[] it = new int[]{10,20,30,39,210,230,50};
		
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
		this.AddReplace(10,x);
		this.AddReplace(20,y);
	}

}
