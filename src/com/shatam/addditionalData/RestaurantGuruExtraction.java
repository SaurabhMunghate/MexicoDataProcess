package com.shatam.addditionalData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.U;

public class RestaurantGuruExtraction {
	
	public static void main(String[] args) {
		int addCol=8;
		String header[], out[];
		String reservation, card, wheelChair, parking, drive, takeaway, wifi,logoURl,email;
		
		int i = 0;
		int count = 0;
		List<String[]> fileRowData = U
				.readCsvFile("/home/chinmay/RestaurantGuruCSV/update/restaurantGuru0-10000data.csv");
		ArrayList<String[]> outData = new ArrayList<>();
		for (String[] rowData : fileRowData) {
			reservation = card = wheelChair = parking = drive = takeaway = wifi =logoURl=email="";
			i = rowData.length;
			if (rowData[0].contains("ID")) {
				header = new String[rowData.length + addCol];
				System.arraycopy(rowData, 0, header, 0, rowData.length);
				header[i++] = "RESERVATION";
				header[i++] = "CARD";
				header[i++] = "WHEEL_CHAIR";
				header[i++] = "PARKING";
				header[i++] = "TAKEAWAY";
				header[i++] = "WIFI";
				header[i++] = "LOGO";
				outData.add(header);
				continue;
			}
			out = new String[rowData.length + addCol];
			System.arraycopy(rowData, 0, out, 0, rowData.length);
			
			
			// for (int i = 0; i < rowData.length; i++) {
			// U.log(i+" ---> "+rowData[i]);
			// }
			try {
				U.log((count++)+" -> "+rowData[30]);
				if(rowData[30].contains("https://restaurantguru.com/las-quekas-cancun-7"))continue;
				String html = U.getHTML(rowData[30]);
				if(html.contains("Our systems have detected unusual traffic from your computer network.")) {
					System.err.println("Our systems have detected unusual traffic from your computer network.");
					break;
				}
				String feature = U.getSectionValue(html, "<div class=\"div_h3\">Features</div>", "</div>");
				//U.log(feature);
				if (feature != null) {
					if (feature.contains("feature_item reservations none\""))
						reservation = "N";
					else if (feature.contains("feature_item reservations\""))
						reservation = "Y";
					if (feature.contains("feature_item cards none"))
						card = "N";
					else if (feature.contains("feature_item cards"))
						card = "Y";
					if (feature.contains("feature_item takeout none"))
						takeaway = "N";
					else if (feature.contains("feature_item takeout"))
						takeaway = "Y";
					if (feature.contains("feature_item wheel_chair none"))
						wheelChair = "N";
					else if (feature.contains("feature_item wheel_chair"))
						wheelChair = "Y";
					if (feature.contains("feature_item parking none"))
						parking = "N";
					else if (feature.contains("feature_item parking"))
						parking = "Y";
					if (feature.contains("feature_item wifi none"))
						wifi = "N";
					else if (feature.contains("feature_item wifi"))
						wifi = "Y";
					
					
					//break;
				}
				String webSec=U.getSectionValue(html, "<div class=\"div_h3\">Website</div>", "</div>");
				if(webSec!=null) {
					String website=U.getSectionValue(webSec, "href=\"", "\"");
					U.log(website);
					//website=U.getRedirectedURL(website, website);
					U.log(website);
//					String webHtml=U.getHTML(website);
					logoURl=website;
					//break;
				}
				out[i++] = reservation;
				out[i++] = card;
				out[i++] = wheelChair;
				out[i++] = parking;
				out[i++] = takeaway;
				out[i++] = wifi;
				out[i++] = logoURl.trim();
				outData.add(out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//if ((count++) == 300)
				//break;
		}
		U.writeCsvFile(outData, "/home/chinmay/RestaurantGuruCSV/update/restaurantGuru0-1000data_OUT.csv");
	}
}
