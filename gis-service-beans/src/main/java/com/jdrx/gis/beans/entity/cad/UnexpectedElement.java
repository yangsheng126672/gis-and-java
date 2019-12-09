package com.jdrx.gis.beans.entity.cad;

public class UnexpectedElement extends Exception{
	public UnexpectedElement()
	{
		
		super("A fost intalnit un element Dxf nerecunoscut de aplicatie. \n Va rugam contactati serviciul de relatii cu clientii.");
		System.out.print("A fost intalnit un element Dxf nerecunoscut ");
	}
}
