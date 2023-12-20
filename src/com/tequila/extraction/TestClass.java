package com.tequila.extraction;

import java.util.Arrays;
import java.util.List;

import com.shatam.utils.U;

public class TestClass {
	public static void main(String[] args) {
//		U.log(U.getTodayDate());
//		U.log(Arrays.toString(U.getAddress("Av. Constituyentes Oriente Villas del sol Mercurio CP 76046 Santiago de Querétaro, Querétaro")));
//		U.log("Multiplaza Valle Dorado, Avila Camacho Nº 3227, ".replaceAll(",\\s+$", ""));
		try {
//			String html=U.getHTML("https://maps.googleapis.com/maps/api/js/GeocodeService.Search?4s06050%20Mexico&7sMX&9sen-GB&callback=_xdc_._zelb8a&key=AIzaSyCKcKeb6u_jelrqXHu4LBaPDy9UeZwjogw&token=113309");
//			U.log(html);
			int counter=0;
			List<String[]> zipCsv=U.readCsvFile("/home/mypremserver/DatabasesTequila/DatabaseCSv/zipdataset.csv");
			for (String[] zip : zipCsv) {
				if(zip[1].contains("LATITUDE")) continue;
				counter++;
				if (zip[1].contains("19.2923708")) {
					break;
				}
			}
			U.log(counter);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
