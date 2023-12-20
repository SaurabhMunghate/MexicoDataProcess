package com.shatam.geoboundary;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.shatam.utils.U;
/**
 * Haversine formula
 */
public class DistanceCalculator {
/*	public static void main (String[] args) throws java.lang.Exception{
		System.out.println(distance(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798, "M") + " Miles\n");
		System.out.println(distance(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798, "K") + " Kilometers\n");
		System.out.println(distance(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798, "N") + " Nautical Miles\n");
		
		
		U.log(calculateDistanceInKilometer(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798)+" km");
		U.log(haversineMiles(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798)+" m");
		
		U.log("miles::"+haversineDistance(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798, Distance.MILES));
		U.log("Km::"+haversineDistance(19.439743386643876, -99.19186592102051, 19.4395071,-99.2041798, Distance.KILOMETER));
		U.log("Km::"+haversineDistance("19.439743386643876", "-99.19186592102051", "19.4395071","-99.2041798", Distance.KILOMETER));
	}

	private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		if (unit == "K") {
			dist = dist * 1.609344;
		} else if (unit == "N") {
			dist = dist * 0.8684;
		}
		return (dist);
	}

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts decimal degrees to radians						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*	private static double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}*/

	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
	/*::	This function converts radians to decimal degrees						 :*/
	/*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
/*	private static double rad2deg(double rad) {
		return (rad * 180 / Math.PI);
	}*/
	
/*	public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
	
	public static final double RKilometers = 6372.8; // In kilometers
	public static final double RMiles = 3959.87433; // In miles
	
	public static double calculateDistanceInKilometer(double userLat, double userLng, double venueLat, double venueLng) {

		double latDistance = Math.toRadians(userLat - venueLat);
		double lngDistance = Math.toRadians(userLng - venueLng);

		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
		+ Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
		* Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		return (AVERAGE_RADIUS_OF_EARTH_KM * c);
	}
	
	
	public static double haversineMiles(double lat1, double lon1, double lat2, double lon2) {
		double dLat = Math.toRadians(lat2 - lat1);
		
		double dLon = Math.toRadians(lon2 - lon1);

		lat1 = Math.toRadians(lat1);
		lat2 = Math.toRadians(lat2);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
		
		double c = 2 * Math.asin(Math.sqrt(a)); //or  Dim c As Double = 2 * Math.Asin(Math.Min(1, Math.Sqrt(a)))
		return RMiles * c;
	}
*/	
	private static NumberFormat numberFormat = new DecimalFormat(".000");
	public static final double haversineDistance(double lat1, double lon1, double lat2, double lon2, Distance distance){
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) 
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) 
				* Math.sin(dLon/2) * Math.sin(dLon / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		
		return getDistance(distance.getRadius() * c);
	}
	
	public static final String haversineDistance(String lat1, String lon1, String lat2, String lon2, Distance distance){
		return String.valueOf(haversineDistance(Double.parseDouble(lat1), Double.parseDouble(lon1), Double.parseDouble(lat2), Double.parseDouble(lon2), distance));
	}
	
	private static double getDistance(double distance){
		return Double.valueOf(numberFormat.format(distance));
	}
}
