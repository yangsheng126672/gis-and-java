package com.jdrx.gis.beans.entity.cad;


import java.util.ArrayList;

public class PolyLine extends Entity{
	public int Flag;
	public String colornum;
	public String lwidth;
	public String LName;
	public ArrayList<Point> pointList = new ArrayList<Point>();
	public PolyLine(String layer,short pfg) throws UnexpectedElement
	{
		super("POLYLINE",layer);
		int[] it = new int[] { 66, 10, 20, 30, 39, 70, 40, 41, 71, 72, 73, 74, 75, 210, 220, 230,370,62};
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
		this.AddReplace(70, pfg);
		this.AddElement(new SeqEnd(layer));
		this.AddReplace(66,(short)1);
	}
	public PolyLine() {
	}
	public void AddVertex(Vertex v) throws UnexpectedElement
	{
		this.InsertElement(this.ElementCount()-1,v);
	}
	public void AddVertex(double x, double y) throws UnexpectedElement
	{
		this.AddVertex(new Vertex(x,y,this.getLayer()));
	}
	
	
	
	public void setLwidth(String lwidth) throws UnexpectedElement
	{
		this.lwidth = lwidth;
		this.AddReplace(62, Short.valueOf(lwidth));
	}
	
	public void setColornum(String colornum) throws UnexpectedElement
	{
		this.colornum = colornum;
     	this.AddReplace(370, Long.valueOf(colornum));
	}
	
	public String toString()
	{
		String s1 = LName+" "+colornum+" "+lwidth+" "+Flag +" ";
		for(int i=0;i<pointList.size();i++)
		{
			s1+="("+pointList.get(i).PointX+","+pointList.get(i).PointY+") ";
		}
		return s1;
	}
}
