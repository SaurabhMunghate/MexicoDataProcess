package com.shatam.maps;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class GoogleMap implements Map {
	
	
	public static void main(String[] args) throws IOException {
		GoogleMap map = new GoogleMap();
		String[] latLng= {"22.8972765","-109.9102411"}; 
		String add[] = map.getGoogleAddress(latLng);
		U.log("Add::"+Arrays.toString(add));
	}


	
	public String[] getLatLong(String[] address){
		// TODO Auto-generated method stub
		String[] latlong = {"",""};
		String adr = address[0] + "," + address[1] + "," + address[2] + "," + address[3];
		try {
			String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(adr, "UTF-8")
					+ "&sensor=true";
			
			URL url = new URL(api);
			U.log(url);
			U.log(U.getCache(url.toString()));
			String str = U.getHTML(url.toString());
			
			if (str.contains("<lat>") && str.contains("</lng>")) {
//				U.log(str);
				latlong[0]= U.getSectionValue(str,"<lat>", "</lat>");
				latlong[1]= U.getSectionValue(str,"<lng>", "</lng>");
				U.log(latlong[0]+"  "+latlong[1]);
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return latlong;
	}
	
	public String[] getAddress(String[] latLong) {
		String[] add= {"","","",""};
		String lat = latLong[0]+","+latLong[1];
		try {
		String url="http://maps.googleapis.com/maps/api/geocode/json?latlng="+URLEncoder.encode(lat, "UTF-8")+"&sensor=true";
		U.log(url);
		String str=U.getHTML(url);
		if(str.contains("formatted_address\" : \""))
		{
			str=U.getSectionValue(str,"formatted_address\" : \"","\"");
			String[] add1=str.split(",");
			add[0]=add1[0].trim();
			add[1]=add1[1].trim();
			add[2]=Util.match(add1[2], "[A-Z]{2} ").trim();
			add[3]=add1[2].replace(add[2],"").trim();
		}
		U.log(str);		
		}catch (Exception e) {
			e.printStackTrace();
		}
		return add;
	}
	
	public String [] getGoogleLatLong(String add[]) throws IOException{
		String addr = add[0] + "," + add[1] + "," + add[2];
		addr = "https://maps.googleapis.com/maps/api/geocode/json?address="
				+ URLEncoder.encode(addr, "UTF-8");
		U.log(addr);
		U.log(U.getCache(addr));
		String html = U.getGoogleHTML(addr);

		String sec = U.getSectionValue(html, "location", "status");

		String lat = U.getSectionValue(sec, "\"lat\" :", ",");
		if (lat != null)
			lat = lat.trim();
		String lng = U.getSectionValue(sec, "\"lng\" :", "}");
		if (lng != null)
			lng = lng.trim();
		String latlng[] = {"", ""};
		String status = U.getSectionValue(html, "status\" : \"", "\"");
		if(status.trim().equals("OK")){
			latlng[0] = lat;
			latlng[1] = lng;
			return latlng;
		}else
			return latlng;
	}
	
	public String[] getGoogleAddress(String latLng[]) throws IOException{
		
		String st = latLng[0].trim() + "," + latLng[1].trim();
		String addr = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ st;
		U.log(addr);

		U.log(U.getCache(addr));
		String html = U.getGoogleHTML(addr);
		U.log(html);
		String status = U.getSectionValue(html, "status\" : \"", "\"");
		
		if(status.trim().equals("OK")){
			String txt = U.getSectionValue(html, "formatted_address\" : \"", "\"");
			U.log("txt:: " + txt);
			if (txt != null)
				txt = txt.trim();
			txt = txt.replaceAll("The Arden, |TPC Sugarloaf Country Club, ", "").replace("50 Biscayne, 50", "50");
			txt = txt.replaceAll("110 Neuse Harbour Boulevard, ", "");
			txt = txt
					.replaceAll(
							"Waterview Tower, |Liberty Towers, |THE DYLAN, |Cornerstone, |Roosevelt Towers Apartments, |Zenith, |The George Washington University,|Annapolis Towne Centre, |Waugh Chapel Towne Centre,|Brookstone, |Rockville Town Square Plaza, |University of Baltimore,|The Galleria at White Plains,|Reston Town Center,",
							"");
			String[] add = txt.split(",");
			add[3] = Util.match(add[2], "\\d+");
			add[2] = add[2].replaceAll("\\d+", "").trim();
			
			return add;
		}else{
			return new String[]{"","","",""};
		}
	}
	
	static int changeProxyIpCount = 0;
	static int keyCount = 0;
	
	public static String[] getGoogleMatrix(String [] add, String [] latLng, boolean withKey) throws IOException, InterruptedException{
		String source = "";
		String destination = "";
		String html = "";

		if(add.length == 4)	source = add[0].trim()+","+ add[1].trim()+","+ add[2].trim(); //+","+ add[3].trim();
		if(latLng.length == 2) destination = latLng[0].trim()+","+latLng[1].trim();
		
		String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + source + "&destinations=" + destination;
		U.log(url);
		String fileName = U.getCache(url);
		
		if(new File(fileName).exists()){
			url = url.replaceAll(" ", "%20");
			 
			// Thread.sleep(4000);
			fileName = U.getCache(url);
			U.log(" .............."+fileName);
			File cacheFile = new File(fileName);
			if (cacheFile.exists())
				html=FileUtil.readAllText(fileName);
		}
		if (html.contains("REQUEST_DENIED")||html.contains("OVER_DAILY_LIMIT")||html.contains("OVER_QUERY_LIMIT")||html.trim().length()==0) {
			if(withKey){
			
				String key = "";
				for(int j = 0; j < keyList.size(); j++){
					if(keyCount ==  j){
						key = "&key="+ keyList.get(j);
						U.log("url with key=="+url + key);
						U.log(fileName);
						html = U.extractHtml(url + key, fileName);
						String st = U.getSectionValue(html, "status\" : \"", "\"");
						if(st != null){
							if("OVER_QUERY_LIMIT".contains(st.trim()) || "OVER_DAILY_LIMIT".contains(st.trim())||"REQUEST_DENIED".contains(st.trim())){
//								keyCount++;
								U.delete(fileName);
							}
						}
					}//eof if
					
				}//eof for
				U.log(fileName);
				
			}else{
				List<String[]> ipList =  ProxyTestMap.findProxyWithHttps(ProxyTestMap.COUNTRY_US);
				if(ipList.size() > 0){
					for(int i = 0; i< ipList.size(); i++){
						if(changeProxyIpCount == i){
							String[] ipPort = ipList.get(i);
							int port = Integer.parseInt(ipPort[1]);
							U.log("url with proxy=="+url);
							U.log(fileName);
							html = U.getHTML(url, ipPort[0].trim(), port);
							if(html == null) html = "";
							String st = U.getSectionValue(html, "status\" : \"", "\"");
							if(st == null) st = "";
							if(st != null){
								if(statusCodeList.contains(st.trim()) || st.isEmpty()){
									changeProxyIpCount++;
									U.delete(fileName);
								}
							}
						}
					}//eof for
					
					if(changeProxyIpCount == ipList.size()){
						ProxyTestMap.delete();
						changeProxyIpCount = 0;
					}
					
				}//eof if inner
			}//eof else inner
		}//eof else outer
		
		if(html == null) html = "";
		
		String status = U.getSectionValue(html, "status\" : \"", "\"");
/*		if(status.contains("OVER_DAILY_LIMIT")){
			key = "&key="+ keyList.get(1);
			if(withKey){
				url = url + key;
			}
			html = U.getHTML(url);
			U.log(U.getCache(url));
		}*/
		
//		U.log(html);
		U.log(status);
		if(status == null) status = "";
		if(status.trim().equals("OK")){
			String originalAddress = U.getSectionValue(html, "origin_addresses\" : [", "],");
			if(originalAddress != null) originalAddress = originalAddress.replaceAll("\"|\n", "").replaceAll("\\s{2,}", " ").trim();

			String destinationAddress = U.getSectionValue(html, "destination_addresses\" : [", "],");
			if(destinationAddress != null) destinationAddress = destinationAddress.replaceAll("\"|\n", "").replaceAll("\\s{2,}", " ").trim();
			
			String dist_mile = "", dist_meter = "";
			String distance = U.getSectionValue(html, "distance\" :", "}");
			if(distance != null){
				dist_mile = U.getSectionValue(distance, "text\" : \"", "\"");
				dist_meter = Util.match(distance, "value\"\\s?:\\s?(\\d+)\\b", 1);
//				U.log(dist_meter);
			}
			if(originalAddress == null) originalAddress = "";
			if(destinationAddress == null) destinationAddress = "";
			
			return new String[]{
				originalAddress.trim(), destinationAddress.trim(), dist_mile.trim(), dist_meter.trim()
			};
		}//eof if

		return new String[]{"", "", "", ""};
	}
}