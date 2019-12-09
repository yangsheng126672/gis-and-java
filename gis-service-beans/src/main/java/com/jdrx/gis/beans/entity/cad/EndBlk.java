package com.jdrx.gis.beans.entity.cad;

public class EndBlk extends Element{ 
	private Data startTag;
	public EndBlk()
	{
		int[] f=new int[]{ 0, 5, 8, 102, 330, 100};
		this.dataAcceptanceList.add(f);
		this.startTag = new Data(0,"ENDBLK");
	}
	public EndBlk(Block b) throws UnexpectedElement
	{
		this();
		if(b.GetIndexFor(5)!=-1) this.AddData(b.GetDataFor(5));
		if(b.GetIndexFor(8)!=-1) this.AddData(b.GetDataFor(8));
	}
}
