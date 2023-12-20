package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.chinmay.test.JsonGoogleApiLatlonExtraction;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractFordDistributor {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		HashSet<String>stateSet=MXStates.getAllStates();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		try {
			int i=0;
			for (String state : stateSet) {
				U.log(state);
				String googleUrl="https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(state, StandardCharsets.UTF_8.toString())+"&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s";
				String latllonHtml=U.getHTML(googleUrl);
//				U.log(latllonHtml);
				String latLonSec=U.getSectionValue(latllonHtml, "\"location\"", "\"location_type\"");
//				U.log(latLonSec);
				String lat=U.getSectionValue(latLonSec, "\"lat\" : ", ",").trim();
				String lng=U.getSectionValue(latLonSec, "\"lng\" : ", "}").trim();
				U.log(lat+" "+lng);
				String jsonData=U.getHTML("https://spatial.virtualearth.net/REST/v1/data/1652026ff3b247cd9d1f4cc12b9a080b/FordEuropeDealers_Transition/Dealer?spatialFilter=nearby("+lat+","+lng+",100)&$select=*,__Distance&$filter=CountryCode%20Eq%20%27MEX%27%20And%20Language%20Eq%20%27es%27%20And%20Brand%20Eq%20%27Ford%27&$top=100&$format=json&key=Al1EdZ_aW5T6XNlr-BJxCw1l4KaA0tmXFI_eTl1RITyYptWUS0qit_MprtcG7w2F&Jsonp=processDealerResults");
//				U.log(jsonData);
				String listings[]=U.getValues(jsonData, "{\"__metadata\":", "\"__Distance\"");
				
				for (String listing : listings) {
					listing=StringEscapeUtils.unescapeJava(listing);
					U.log(listing);
					String name=U.getSectionValue(listing, "\"DealerName\":\"", "\"");
					String companyLat=U.getSectionValue(listing, "Latitude\":", ",");
					String companyLng=U.getSectionValue(listing, "Longitude\":", ",");
					String streetAdd=U.getSectionValue(listing, "\"AddressLine1\":\"", "\"");
					String addNeigh[]=SplitNeighborhoodFromAdd.splitColonia(streetAdd);
					String companyState=U.getSectionValue(listing, "\"AdministrativeArea\":\"", "\"");
					String postalCode=U.getSectionValue(listing, "\"PostCode\":\"", "\"");
					String testAdd[]= {companyLat,companyLng};
					String jsonAdd[]=JsonGoogleApiLatlonExtraction.testJsonCode(testAdd);
					U.log(Arrays.toString(jsonAdd));
//					U.log(jsonAdd[1]);
					if (!jsonAdd[1].isEmpty()) {
						streetAdd=streetAdd.toLowerCase().replace(jsonAdd[1].toLowerCase(), "");
//						U.log(streetAdd);
						if (streetAdd.endsWith(",")) {
//							U.log("--");
							streetAdd=streetAdd.substring(0, streetAdd.length()-1);
						}else if (streetAdd.endsWith(",.")) {
//							U.log("---");
							streetAdd=streetAdd.substring(0, streetAdd.length()-2);
						}else if (streetAdd.trim().endsWith(", .")) {
//							U.log("----");
							streetAdd=streetAdd.substring(0, streetAdd.length()-3);
						}
//						U.log(streetAdd);
					}
//					U.log(jsonAdd[5]);
					String fax=U.getSectionValue(listing, "\"Fax\":\"", "\"");
					String phone=U.getSectionValue(listing, "PrimaryPhone\":\"", "\"");
					String website=U.getSectionValue(listing, "\"PrimaryURL\":\"", "\"");
					String email=U.getSectionValue(listing, "PrimaryEmail\":\"", "\"");
					if (companyState.toLowerCase().equals("estado de méxico")) {
						companyState="Mexico State";
					}else if (companyState.toLowerCase().equals("cdmx")) {
						companyState="Mexico City";
						jsonAdd[5]=MXStates.getMexicoCityMunicipalites(postalCode);
					}
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase("Nuevos Y Usados Concesionarios De Coches Y Camiones"),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(jsonAdd[1]),U.toTitleCase(jsonAdd[5]),U.toTitleCase(companyState),postalCode.trim(),U.formatNumbersAsCode(phone),U.formatNumbersAsCode(fax),website,email,null,null,null,null,null,companyLat,companyLng,"https://www.ford.mx/distribuidores/#/search/"+URLEncoder.encode(state, StandardCharsets.UTF_8.toString())+"/",U.getTodayDate()};
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Ford_Distributor.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//U.log(stateSet.size());
	}
	
}
