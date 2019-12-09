package com.jdrx.gis.beans.entity.cad;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class Element {
	public  Data startTag = new Data(-10,0);
	public  Data endTag = new Data(-10,0);
	protected ArrayList data;
	protected ArrayList elements;
	protected ArrayList dataAcceptanceList;
	protected ArrayList elementAcceptanceList;
	public  String s;

	protected Element()
	{
		data = new ArrayList();
		elements = new ArrayList();
		dataAcceptanceList = new ArrayList();
		elementAcceptanceList = new ArrayList();
	}
	public Element(String s)  // Used for createing an element with user dxf code
	{
		this.s = s;
	}

	public void AddElement(Element e) throws UnexpectedElement
	{
		if(this.IsAccepted(e)) this.elements.add(e);
		else throw new UnexpectedElement();
	}
	public void InsertElement(int index, Element e) throws UnexpectedElement
	{
		if(this.IsAccepted(e))  this.elements.add(index, e);
		else throw new UnexpectedElement();
	}
	public void RemoveElementAt(int index)
	{
		this.elements.remove(index);
	}
	public Element GetElement(int index)
	{
		return (Element) this.elements.get(index);
	}
	public int ElementCount()
	{
		return this.elements.size();
	}

	public void AddData(Data e) throws UnexpectedElement
	{
//		System.out.println(this.IsAccepted(e));
//		System.out.println(data.size());
		if(this.IsAccepted(e)) this.data.add(e);
		else throw new UnexpectedElement();
	}
	public void InsertData(int index, Data e) throws UnexpectedElement
	{
		if(this.IsAccepted(e)) this.data.add(index,e);
		else throw new UnexpectedElement();
	}
	public void RemoveDataAt(int index)
	{
		this.data.remove(index);
	}
	public Data GetData(int index)
	{
		return (Data) this.data.get(index);
	}
	public int GetIndexFor(int code)
	{
		Data d = null;
		Iterator it = this.data.iterator();
		while(it.hasNext())
		{
			d = (Data) it.next();
			if(d.code==code) return this.data.indexOf(d);
		}
		return -1;
	}
	public Data GetDataFor(int code)
	{
		Data d=null;
		Iterator it = this.data.iterator();
		while(it.hasNext())
		{
			d=(Data) it.next();
			if(d.code==code) return d;
		}
		return new Data(-10,0);
	}
	public int DataCount()
	{
		return this.data.size();
	}

	public boolean IsAccepted(Data d) // verifica daca tipul de date ( rep prin cod dxf ) este acceptat de element
	{
//		System.out.println(this.isCorectData(d));
		return (dataAcceptanceList.contains(d.code)&&this.isCorectData(d));
	}
	public boolean IsAccepted(Element e)
	{
		return true; // DEBUG : trebuie modificat
	//	return elementAcceptanceList.Contains(e);
	}
	public boolean isCorectData(Data d) // Verifica daca tipul datei corespunde cu codul acesteia
	{
		if(d.code>=290 && d.code<=299) 
			if(d.data instanceof Boolean) return true;
			else return false;
		if((d.code>=60 && d.code<=79)||(d.code>=270 && d.code<=289)||(d.code>=370 && d.code<=389)||(d.code>=170 && d.code<=179))
		{
			System.out.println(d.data instanceof Short||d.data instanceof Long);
			if(d.data instanceof Short||d.data instanceof Long) return true;
			else return false;
		}
		if((d.code>=90 && d.code<=99)||(d.code == 1071))
			if(d.data instanceof Integer) return true;
			else return false;
		if((d.code>=10 && d.code<=59)||(d.code>=110 && d.code<=149)||(d.code>=210 && d.code<=239)||(d.code>=1010 && d.code<=1059))
			if(d.data instanceof Double) return true;
			else return false;
		if(d.code==100 || d.code==102 ||d.code==105 || d.code==999 || (d.code>=300 && d.code<=369)||(d.code>=390 && d.code<=399)||(d.code>=410 && d.code<=419))
			if((d.data instanceof String)&&((String) d.data).length() <=255) return true;
			else return false;
		if((d.code>=0 && d.code<=9) || (d.code>=1000 && d.code <=1009))
		{
			if(d.data instanceof String)
				return true;
			else return false;
		}
			
		return false;
	}

	public String getName()
	{
			return ((Data)this.data.get(0)).data.toString();
	}
	public void AddReplace(int cod, Object o) throws UnexpectedElement
	{
		int ind = this.GetIndexFor(cod);
		if(ind==-1) this.AddData(new Data(cod,o));
		else
		{
			this.data.remove(ind);
			this.InsertData(ind,new Data(cod,o));
		}
	}
	public void AddData(int cod, Object o) throws UnexpectedElement
	{
		this.AddData(new Data(cod,o));
	}
}
