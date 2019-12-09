package com.jdrx.gis.beans.entity.cad;

public class Entity extends Element{
	public Entity(String name, String layer) throws UnexpectedElement
	{
		super();
		int[] it = new int[] 
		       			{ 0 , 5 , 102 , 330 , 360 , 100 , 67 , 410 , 8 , 6 , 62 , 370 , 48 , 60 , 92 , 310} ;
		for(int j = 0;j<it.length;j++)
		{
			this.dataAcceptanceList.add(it[j]);
		}
//		if(!(name.equals("VERTEX")||name.equals("SEQEND")))
//		{
			this.startTag = new Data(0,name);
//			if(name.equals("LWPOLYLINE"))
//			{
//				this.AddReplace(5, "2D");
//				this.AddReplace(330,"37");
//				this.AddReplace(100, "AcDbEntity");
//
//			}
			this.AddData(new Data(8, layer));
//		}
		
	}

	
	public Entity()
	{
	}
	
	public String getLayer()
	{
			return (String)(this.GetDataFor(8)).data;
		}
	public void	setLayer(String s) throws UnexpectedElement
	{
		this.AddReplace(8,s);
	}
}
