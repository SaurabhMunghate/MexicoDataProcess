package com.shatam.geoboundary;

public enum Distance {
	KILOMETER(6371),
	MILES(3960);
	
	private int radius = 0;
	
	private Distance(int radius) {
		this.radius = radius;
	}
	
	public int getRadius(){
		return this.radius;
	}
}
