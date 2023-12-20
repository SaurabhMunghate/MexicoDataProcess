package com.chinmay.test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class GetDistanceBetweenADd {
	final static int COMPANY_NAME=7;
	final static int ADDRESS=8;
	final static int CITY=10;
	final static int STATE=11;
	final static int LATITUDE=22;
	final static int LONGITUDE=23;
	
	public static void main(String[] args) {
		measureDistance("/home/mypremserver/DatabasesTequila/Ishan/Volkswagon_Distributor2_Unique.csv");
	}

	private static void measureDistance(String fileName) {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		try {
			List<String[]> records=U.readCsvFile(fileName);
			for (String[] record : records) {
				if (record[0].contains("ID"))	continue;
				if (record[LATITUDE].trim().length()==0) {
					continue;
				}
				String add=record[ADDRESS]+","+record[CITY]+","+record[STATE];
				String latLon=record[LATITUDE]+","+record[LONGITUDE];
				String distance=getDistance(add, latLon);
				U.log(distance);
				if (Integer.parseInt(distance)>100&&Integer.parseInt(distance)<1000) {
					String out[]= {record[0],record[COMPANY_NAME],record[ADDRESS],record[CITY],record[STATE],record[LATITUDE],record[LONGITUDE],distance};
					writer.writeNext(out);
				}
//				break;
			}
			FileUtil.writeAllText(fileName.replaceAll(".csv", "Exceeded1.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private static String getDistance(String add, String latLon) throws IOException
	{
		String sb = "";
		U.log("****************************IN the GetDistance****************************");
		U.log(add+" "+latLon);
		String path="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+URLEncoder.encode(add, StandardCharsets.UTF_8.toString())+"&destinations="+URLEncoder.encode(latLon, StandardCharsets.UTF_8.toString())+"&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s";
		U.log("-----------------------------------path----------------------------------------------------> "+path);
		String pathHtml=U.getHTML(path);
//		U.log(pathHtml);
		String status=U.getSectionValue(pathHtml,"\"status\" : \"", "\"");
		if (status.contains("INVALID_REQUEST")||status.contains("ZERO_RESULTS")) {
			sb=status;
			return sb;
		}
		else{
		String distanceSec=U.getSectionValue(pathHtml, "distance\" : {","}");
		String distanceMeter=U.getSectionValue(distanceSec,"\"value\" : "," ");
		sb=distanceMeter;
		return sb.trim();
		}	
	}
}
