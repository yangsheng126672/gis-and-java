package com.jdrx.gis.beans.entity.cad;


public class Line extends Entity{
	
	public String colornum;
	public String LName;
	public String StartX;
	public String StartY;
	public String EndX;
	public String EndY;
	public String lwidth;
	
	public Line()
	{
		
	}
	
	public String toString()
	{
		return LName+" (" +StartX+","+StartY+") ("+EndX+","+EndY+") "+colornum +" "+lwidth;
	}
	
	public Line(String layer,double xi, double yi, double xf, double yf) throws UnexpectedElement
	{
		
		super("LINE",layer);
		this.LName = layer;
		int[] it =new int[] { 39, 10, 20, 30, 11, 21, 31, 210, 220, 230 };
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
		this.AddReplace(10,xi);
		this.AddReplace(20,yi);
		this.AddReplace(11,xf);
		this.AddReplace(21,yf);
	}

	public void setInitialPoint(double x, double y) throws UnexpectedElement
	{
		this.AddReplace(10,x);
		this.AddReplace(20,x);
	}
	public void setFinalPoint(double x, double y) throws UnexpectedElement
	{
		this.AddReplace(11,x);
		this.AddReplace(21,x);
	}


	public void setLwidth(String lwidth)
	{
		this.lwidth = lwidth;
		try {
			this.AddReplace(62, Short.valueOf(lwidth));
		} catch (UnexpectedElement unexpectedElement) {
			unexpectedElement.printStackTrace();
		}
	}
	
}
