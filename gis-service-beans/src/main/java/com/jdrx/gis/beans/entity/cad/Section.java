package com.jdrx.gis.beans.entity.cad;

import java.util.ArrayList;

public class Section extends Element{
	public Section(String s)
	{
		super();
		startTag = new Data(0,"SECTION");
		endTag = new Data(0,"ENDSEC");

		data = new ArrayList();
		elements = new ArrayList();
		data.add(new Data(2,s));
	}
}
