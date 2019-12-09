package com.jdrx.gis.beans.entity.cad;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class Writer {
	public Writer()
	{
	}
	public static void Write(Document d, OutputStream s) throws IOException
	{
		OutputStreamWriter sr = new OutputStreamWriter(s);

		WriteHeader(d.header,sr);
		WriteTables(d.tables,sr);
		WriteBlocks(d.blocks,sr);
		WriteEntities(d.entities,sr);

		WriteData(new Data(0,"EOF"),sr);
		sr.close();
	}
	private static void WriteHeader(Header h,OutputStreamWriter sr) throws IOException
	{
		if(h==null) return;
		WriteElement(h,sr);
	}
	private static void WriteEntities(Entities e, OutputStreamWriter sr) throws IOException
	{
		if(e==null) return;
		WriteElement(e,sr);
	}
	private static void WriteBlocks(Blocks b, OutputStreamWriter sr) throws IOException
	{
		if(b==null) return;
		WriteElement(b,sr);
	}
	private static void WriteTables(Element t, OutputStreamWriter sr) throws IOException
	{
		if(t==null) return;
		WriteElement(t,sr);
	}
	private static void WriteElement(Element e, OutputStreamWriter sr) throws IOException
	{
		if(e==null) return;
		if(e.s!=null) 
		{
			sr.write(e.s);
			return;
		}
		WriteData(e.startTag,sr);

		for(int i=0;i<e.DataCount();i++)
		{
			//System.out.println(e.DataCount());
			//System.out.println(e.GetData(i).data);
			WriteData(e.GetData(i),sr);
		}
		//System.out.println(e.ElementCount());
		for(int i=0;i<e.ElementCount();i++)
			WriteElement(e.GetElement(i),sr);
		WriteData(e.endTag,sr);
	}
	private static void WriteData(Data d, OutputStreamWriter sr) throws IOException
	{
		if(d.code == -10) return;
		sr.write(d.code+"");
		sr.write("\n");
		//System.out.println(d.data);
		if(d.data instanceof String)
			sr.write(((String) d.data).toString());
			
		else
			sr.write(d.data.toString()+"");
		sr.write("\n");
	}
}
