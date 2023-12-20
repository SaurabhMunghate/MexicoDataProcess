package com.shatam.maps;

import com.shatam.utils.U;

public class BingMap implements Map{

	
	public String[] getLatLong(String[] address) {
		// TODO Auto-generated method stub
		
			String[] latLong = new String[2];
			String addressLine=address[0]+","+address[1]+","+address[2]+","+address[3];
			if (addressLine == null || addressLine.trim().length() == 0)
				return null;
			addressLine = addressLine.trim().replaceAll("\\+", " ");
			String geocodeRequest = "http://dev.virtualearth.net/REST/v1/Locations/'"
					+ addressLine
					+ "'?o=xml&key=Anqg-XzYo-sBPlzOWFHIcjC3F8s17P_O7L4RrevsHVg4fJk6g_eEmUBphtSn4ySg";
			// U.log("-----------------addressline-----------"+geocodeRequest);
			try
			{
			String xml = U.getHTML(geocodeRequest);
			// U.log("--------------------------xml---------------------------------"+xml);
			latLong[0] = U.getSectionValue(xml, "<Latitude>", "</Latitude>");
			latLong[1] = U.getSectionValue(xml, "<Longitude>", "</Longitude>");
			}catch (Exception e) {
				e.printStackTrace();
			}
			return latLong;
		
	}

	
	public String[] getAddress(String[] latlong) {
		// TODO Auto-generated method stub
		
		String[] addr = null;
		try {
		String htm = U.getHTML("http://dev.virtualearth.net/REST/v1/Locations/"
						+ latlong[0]
						+ ","
						+ latlong[1]
						+ "?o=json&jsonp=GeocodeCallback&key=Anqg-XzYo-sBPlzOWFHIcjC3F8s17P_O7L4RrevsHVg4fJk6g_eEmUBphtSn4ySg");
		String[] adds = U.getValues(htm, "formattedAddress\":\"", "\"");
		U.log(htm);
		for (String item : adds) {
			addr = U.getAddress(item);
			if (addr == null || addr[0] == "-")
				continue;
			else {
				U.log("Bing Address =>  Street:" + addr[0] + " City :"
						+ addr[1] + " state :" + addr[2] + " ZIP :" + addr[3]);
				return addr;
			}

		}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return addr;
		
	
	}

}
