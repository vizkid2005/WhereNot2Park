package com.firsttry.mumbaiparking.helpers;

public class ParkingOffence {

	String offence;
	String section;
	String penalty;
	String type;
	
	public ParkingOffence(String offence, String section, String penalty, String type)
	{
		this.offence=offence;
		this.section=section;
		this.penalty=penalty;
		this.type=type;
		
	}

	public String getOffence() {
		return offence;
	}

	public String getSection() {
		return section;
	}
	
	public String getPenalty() {
		return penalty;
	}
	public String getType() {
		return type;
	}
	
	
}
