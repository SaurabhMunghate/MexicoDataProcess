package com.chinmay.test;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.shatam.utils.ApiKey;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class FindCorrectAddresses {
	static String fileName="/home/chinmay/Mexico/MexicoDataFiles/Data/Jan/Updated/Chinmay_Update_16_JAN_2019.csv";
	public static void main(String[] args) {
		try {
			List<String[]> data=U.readCsvFile(fileName);
			for (String[] da : data) {
				if (da[0].contains("ID")) {
					continue;
				}
				String add[]= {da[3],da[4],da[5],da[6],da[7]};
				String latLon[]= {da[15],da[16]};
				String addReturned[]=getAddressFromLatlonHereApi(latLon);
				String latlonReturned[]=getLatlonFromAddress(add);
				U.log(da[15]+","+da[16]);
				U.log(da[3]+","+da[4]+","+da[5]+","+da[6]+","+da[7]);
				U.log(addReturned[0]+","+addReturned[1]+","+addReturned[2]+","+addReturned[3]+","+addReturned[4]);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String[] getLatlonFromAddress(String add[]) {
		
		try {
			String url="https://geocoder.api.here.com/6.2/geocode.json?searchtext="+URLEncoder.encode(add[0]+" "+add[2]+" "+add[3],StandardCharsets.UTF_8.toString())+"&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1]+"&gen=8";
			U.log(url);
			String html=U.getHTML(url);
			String sec=U.getSectionValue(html, "\"NavigationPosition\":[", "]");
			U.log(sec);
			if(sec!=null) {
				String latlon[]= {U.getSectionValue(sec, "\"Latitude\":", ","),U.getSectionValue(sec, "\"Longitude\":", "}")};
				U.log("--"+Arrays.toString(latlon));
			}
//			U.log(html);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":")) {
				String add=U.getSectionValue(html, "\"Street\":\"", "\"")+" "+U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia=U.getSectionValue(html, "\"District\":\"", "\"");
				String city=U.getSectionValue(html, "\"City\":\"", "\"");
				String state=MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] {add,colonia,city,state,postal};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
