package com.jdrx.gis.beans.entity.cad;


public class Vertex extends Entity{
	public Vertex(double x, double y, String layer) throws UnexpectedElement
	{
		super("VERTEX",layer);
		int[] it =new int[] { 10, 20, 30, 70, 40, 41, 42, 50, 71, 72, 73, 74,  };
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
		this.AddReplace(10,x);
		this.AddReplace(20,y);
	}
}
