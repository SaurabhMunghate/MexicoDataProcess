package com.chinmay.test;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.ApiKey;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class CorrectZipUsingHereApi {
	final static String fileName="/home/chinmay/MexicoCache/RestaurantGuruCSV/ExtractedFiles/temp/MERGEDFILE/DOne/SPLITTED/GoogleSearch/Success_Match_Folder/Splitted_240059_GOOGLESearch_Success_Match_Matched.csv";
	public static void main(String[] args) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			List<String[]> data=U.readCsvFile(fileName);
			List<String[]> writeData=new ArrayList<String[]>();
			String header[]= {"ID","COMPANY_NAME","STREETADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","HERE_STREET_ADDRESS","HERE_NEIGHBORHOOD","HERE_CITY","HERE_STATE","HERE_ZIP","HERE_LATITUDE","HERE_LONGITUDE"};
			writer.writeNext(header);
			int count=0;
			for (String[] nextLine : data) {
				U.log(count++);
//				if (count>20) {
//					break;
//				}
				if (nextLine[0].contains("ID")) {
					String outdata[]=new String[nextLine.length+2];
					System.arraycopy(nextLine, 0, outdata, 0, nextLine.length);
					outdata[nextLine.length]="HERE_COL";
					outdata[nextLine.length+1]="HERE_ZIP";
					writeData.add(outdata);
					continue;
				}
				//U.log(nextLine[8]+nextLine[10]+nextLine[11]+nextLine[12]);
				String add[]= {nextLine[8],nextLine[10],nextLine[11],nextLine[12]};
				String returnedAdd1[]=CheckZipUsingGeoCodeingApi(add);
				//String returnedAdd[]=CheckDataUsingGoogleGeoCodeingApi(add);
				if (returnedAdd1==null) {
					returnedAdd1=new String[]{"","","","","","",""};
				}
//				if (returnedAdd==null) {
//					returnedAdd=new String[]{"","","","","","",""};
//				}
//					String city="";
//					if (returnedAdd1[4]!=null&&returnedAdd1[4].trim().length()!=0) {
//						city=MXStates.getMexicoCityMunicipalites(returnedAdd1[4]);
//					}
//					U.log(Arrays.toString(returnedAdd));
				String outdata[]=new String[nextLine.length+2];
				System.arraycopy(nextLine, 0, outdata, 0, nextLine.length);
				outdata[nextLine.length]=returnedAdd1[1];
				outdata[nextLine.length+1]=returnedAdd1[4];
				writeData.add(outdata);
				String out[]= {nextLine[0],nextLine[7],nextLine[8],nextLine[9],nextLine[10],nextLine[11],nextLine[12],returnedAdd1[0],returnedAdd1[1],returnedAdd1[2],returnedAdd1[3],returnedAdd1[4],returnedAdd1[5],returnedAdd1[6]};
				writer.writeNext(out);
				//break;
			}
//			FileUtil.writeAllText(fileName.replace(".csv", "_HERE_API_BOTH.csv"),	sw.toString());
			if(writeData.size()>1)
				U.writeCsvFile(writeData,fileName.replace(".csv", "_FOR_VERIFICATION.csv"));
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String[] CheckZipUsingGeoCodeingApi(String[] add) {
		try {
			String apiUrl="https://geocoder.api.here.com/6.2/geocode.json?searchtext="+URLEncoder.encode(add[0]+" "+add[1]+" "+add[2], StandardCharsets.UTF_8.toString()).toLowerCase()+"&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1]+"&gen=8";
			String html=U.getHTML(apiUrl);
			U.log(html);
			if (html!=null) {
				String data[]= U.getAddressGoogle(U.getSectionValue(html, "\"Label\":\"", "\","));
				String latlonSec=U.getSectionValue(html, "\"DisplayPosition\"", "[");
				String lat=U.getSectionValue(latlonSec, "\"Latitude\":", ",");
				String lng=U.getSectionValue(latlonSec, "\"Longitude\":", "}");
//				String zip=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
//				String neighborhood=U.getSectionValue(html, "\"District\":\"", "\"");
//				String streetAddress=U.getSectionValue(html, "\"Label\":\"", ",");
				U.log(Arrays.toString(data));
				return new String[] {data[0],data[1],data[2],data[3],data[4],lat,lng}; 
				//U.log(zip);
			}
			
		}catch(Exception e) {
			U.log(e);
		}
		//https://geocoder.api.here.com/6.2/geocode.json?searchtext=200%20S%20Mathilda%20Sunnyvale%20CA&app_id=devportal-demo-20180625&app_code=9v2BkviRwi9Ot26kp2IysQ&gen=8
		return null;
	}
//	private String CheckZipUsingPlaceApi(String[] add) {
//		
//		return null;
//	}
	public static String[] CheckDataUsingGoogleGeoCodeingApi(String add[]) throws Exception {
		String addr = add[0] + "," + add[1] + "," + add[2];
		addr = "https://maps.googleapis.com/maps/api/geocode/json?address="
				+ URLEncoder.encode(addr, "UTF-8");
		U.log(addr);
		U.log(U.getCache(addr));
		String html = "";//U.getHTMLForGoogleApiWithKey(addr,"AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
		if (html!=null) {
			//U.log(html);
			String addSec=U.getSectionValue(html, "\"formatted_address\" : \"", "\",");
			U.log(addSec);
			String sec = U.getSectionValue(html, "location", "status");
			/*
			 * Below condition modified by Sawan. On date 26 Sept 2017
			 */
			if(sec != null){
				String lat = U.getSectionValue(sec, "\"lat\" :", ",");
				if (lat != null)
					lat = lat.trim();
				String lng = U.getSectionValue(sec, "\"lng\" :", "}");
				if (lng != null)
					lng = lng.trim();
				 //String latlng[] = { lat, lng };
				 //return latlng;
				 String temp[]=U.getAddressGoogle(addSec);
				 
			return new String[] {temp[0],temp[1],temp[2],temp[3],temp[4],lat,lng}; 
		}
		return null;
		}
		// U.log(lat);
		return null;
	}
}
