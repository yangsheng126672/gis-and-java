package com.jdrx.gis.beans.entity.cad;

import java.util.Iterator;

public class Block extends Element{
	private Data startTag;
		public Block()
		{
			super();
			this.dataAcceptanceList.add(new int[]{ 0, 5, 102, 330, 100, 8, 70,10 ,20 ,30 ,3, 1, 4, 2});
			this.startTag = new Data(0,"BLOCK");
		}
		/// <summary>
		/// Used to create an BLOCK with user dxf code.
		/// </summary>
		/// <param name="s">Contains the DXF code that will be writen directly to the dxf file</param>
		public Block(String s)
		{
			super(s);
		}
		public void SetEndBlk(EndBlk eb) 
		{
			if(this.elements.size()>0&&((Element)this.elements.get(this.elements.size()-1)).getName() == "ENDBLK")
			{
				this.elements.remove(this.data.size()-1);
			}
			this.elements.add(eb);
		}
		/// <summary>
		/// Add an dxf entity to a BLOCK
		/// </summary>
		/// <param name="e">The added entity</param>
		public void AddEntity(Entity e)
		{
			if(this.data.size()==0) this.elements.add(e);
			else this.elements.add(this.elements.size()-1,e);
		}
		/// <summary>
		/// Specifies the layer for the block.
		/// </summary>
		/// <param name="l"></param>
		public void SetLayer(String l)
		{
			int ind = this.GetIndexFor(8);
			if(ind>-1)
			{
				this.data.remove(ind);
				this.data.add(ind,new Data(8,l));
			}
			else
				this.data.add(new Data(8,l));
		}
		public void SetPosition(double x, double y, double z)
		{
			Data d = null;
			Data dx=null;
			Data dy=null;
			Data dz=null;
			boolean swx = false,swy = false,swz = false;
			Iterator it = this.data.iterator();
			while(it.hasNext())
			{
				d = (Data) it.next();
				if(d.code==10) 
				{
					dx = d;
					swx = true;
				}
				if(d.code==20) 
				{
					dy = d;
					swy=true;
				}
				if(d.code==30) 
				{
					dz = d;
					swz = true;
				}
			}
			if(swx) dx.data = x;
			else
			{
				dx.code = 10;
				dx.data = x;
				this.data.add(dx);
			}
			if(swy) dy.data = y;
			else
			{
				dy.code = 20;
				dy.data = y;
				this.data.add(dy);
			}
			if(swz) dz.data = z;
			else
			{
				dy.code = 30;
				dy.data = y;
				this.data.add(dy);
			}
		}
		/// <summary>
		/// Sets the block name.
		/// </summary>
		/// <param name="name">Contains the name.</param>
		public void SetName(String name) throws UnexpectedElement
		{
			this.AddReplace(2,name);
		}
		/// <summary>
		/// Sets the handle for the block.
		/// </summary>
		/// <param name="handle">The handle value</param>
		public void SetHandle(String handle) throws UnexpectedElement
		{
			this.AddReplace(5,handle);
		}
		public void SetFlag(short flag) throws UnexpectedElement
		{
			this.AddReplace(70,flag);
		}
}
