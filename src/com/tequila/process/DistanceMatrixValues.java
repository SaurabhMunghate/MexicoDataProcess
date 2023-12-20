package com.tequila.process;

public class DistanceMatrixValues {
	private float distanceInMeter = 0.0f;
	private String distanceType = null;
	private float distanceTypeValue = 0.0f;

	public void setDistanceInMeter(float distanceInMeter) {
		this.distanceInMeter = distanceInMeter;
	}

	public void setDistanceType(String distanceType) {
		this.distanceType = distanceType;
	}

	public void setDistanceTypeValue(float distanceTypeValue) {
		this.distanceTypeValue = distanceTypeValue;
	}

	public float getDistanceInMeter() {
		return distanceInMeter;
	}

	public String getDistanceType() {
		return distanceType;
	}

	public float getDistanceTypeValue() {
		return distanceTypeValue;
	}
}
