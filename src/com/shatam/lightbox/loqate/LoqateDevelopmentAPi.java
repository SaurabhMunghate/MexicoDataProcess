package com.shatam.lightbox.loqate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.xalan.xsltc.dom.MultiValuedNodeHeapIterator;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;


//DependentLocality Neighborhood

public class LoqateDevelopmentAPi {
	//
	final static String LOQATE_KEY = "CH54-GY38-HJ49-JM43";// YD49-RF48-CM98-UW98
	// final static String LOQATE_KEY="MR59-ZN25-NH99-PJ64";
//	final static int noOfRecords = 10000;
//	final static int startPoint = 24001;
//	final static int MAXBATCHSIZE = 300;
	
	final static int noOfRecords = 0;
	final static int startPoint = 10;
	final static int MAXBATCHSIZE = 10;
	
	
	static HashSet<String> uniqueKey = new HashSet<>();
	// It’s still limited to 1500 transactions per day but I’m working on increasing
	// that significantly.
//	static String fileName = "/home/chinmay/Mexico/LightBoxAddress/Jan-17/MexicoAddress_28_FEB.csv";
	static String fileName = "/home/shatam-100/Down/MBD_Sep_8_SubSet_5812_100K.csv";

	
	public static void main(String[] args) {
		long startTime = System.nanoTime();
		
		//Scanner scn = new Scanner(System.in);
		//System.out.println("Enter FileName: ");
		//fileName = scn.nextLine();
		List<String[]> rowData = U.readCsvFile(fileName);
		int batchCOunt = 0;
		
		//scn.close();
		//
		// JSONObject obj = new JSONObject();
		// obj.put("Key", "MR59-ZN25-NH99-PJ64");
		// obj.put(key, value)
		Addresses addresses[] = new Addresses[noOfRecords > MAXBATCHSIZE ? MAXBATCHSIZE : noOfRecords];
		int counter = 0;
		String outputHtml = "";
		int count = 0;
		MultiValueMap<String, String> dataMap = new MultiValueMap<>();
		
		for (String[] row : rowData) 
		{
			// if(count<startPoint&&count>noOfRecords) {count++;continue;}else {count++;}
			// if(counter==noOfRecords)break;
			if (row[0].contains("ID"))
				continue;
			count++;
			if (count % 1000 == 0) {
				U.log(count);
			}
			// U.log(Arrays.toString(row));
			// //Blvd. Gral. Marcelino García Barragán 1455, Prados del Nilo, 44360 San
			// Pedro Tlaquepaque, Jal,Allende No. 110 No. 108	Centro	Irapuato	Guanajuato	36500
			//Emiliano Zapata 60, Centro Histórico de la Cdad. de México, Centro, Cuauhtémoc, 06000 Ciudad de México
			//13 Poniente 505	Puebla Centro	Heroica Puebla De Zaragoza	Puebla	72000
			
			
//			//Adolfo Lopez Mateos 0000103	Sauzal De Rodriguez	Ensenada	Baja California	22800
//			 String address="Adolfo Lopez Mateos 103";
//			 String neighbourhood="";
//			 String city="Ensenada";
//			 String state="Baja California";
//			 String zip="22800";
			String address = row[1].replace("0000", "");
			String neighbourhood = row[2];
			String city = row[3];
			String state = row[4];
			String zip = row[5];
			
			dataMap.put((address +", "+neighbourhood+", "+city+", "+zip).replace("Rancho / Rancheria", "Rancho Rancheria").replace("/Zona", "Zona").replace("\"", "").replace("  ", " ")
					.toLowerCase(), row[0]);
			addresses[counter++] = new Addresses(address, neighbourhood, city, state, zip);
			if (counter == MAXBATCHSIZE-1) {
				U.log("Batch Number " + batchCOunt++ +" "+count);
				LoqateHelper loqate = new LoqateHelper(LOQATE_KEY, true, addresses); 
				outputHtml += verfiyAddressUsingLoqate(loqate);
				addresses = new Addresses[MAXBATCHSIZE];
				counter = 0;
			}
			//break;
		}
		LoqateHelper loqate = new LoqateHelper(LOQATE_KEY, true, addresses);
		outputHtml += verfiyAddressUsingLoqate(loqate);
		//U.log(outputHtml);
		String ouputData[] = U.getValues(outputHtml, "\"Input\":", "]}");
		// U.log(ouputData.length);
		List<String[]> outList = new ArrayList<String[]>();
		String outHeader[] = { "ID", "Input Street", "Input Neigh", "Input City", "Input State", "Input Zip",
				"Street Address", "Neighbourhood", "City", "State", "PostalCode", "AVC", "GAC", "GeoDistance", "Lat",
				"Lon" };
		outList.add(outHeader);
		for (String data : ouputData) {
//			 U.log(data);
			String outSec = U.getSectionValue(data, ",\"Matches\"", "}");
			// String inputStreetAddress=U.getSectionValue(outSec, "\"Address1\":\"", "\"");
			String streetAddress = U.getSectionValue(outSec, "\"Address1\":\"", "\"");
			String neighbourhood = U.getSectionValue(outSec, "\"DependentLocality\":\"", "\"");
			String city = U.getSectionValue(outSec, "\"SubAdministrativeArea\":\"", "\"");
			String postalCode = U.getSectionValue(outSec, "\"PostalCode\":\"", "\"");
			String state = U.getSectionValue(outSec, "\"AdministrativeArea\":\"", "\"")==null||U.getSectionValue(outSec, "\"AdministrativeArea\":\"", "\"").trim().length()==0?"":U.findState(U.getSectionValue(outSec, "\"AdministrativeArea\":\"", "\""));
			String avc = U.getSectionValue(outSec, "\"AVC\":\"", "\"");
			String gac = U.getSectionValue(outSec, "\"GeoAccuracy\":\"", "\"");
			String geoDistance = U.getSectionValue(outSec, "\"GeoDistance\":\"", "\"");
			String lat = U.getSectionValue(outSec, "\"Latitude\":\"", "\"");
			String lon = U.getSectionValue(outSec, "\"Longitude\":\"", "\"");
			String inputAddress[] = U.getSectionValue(data.replace("\\\"", ""), "\"Address\":\"", "\"").split(",");
			if (inputAddress.length != 3) {
				inputAddress = new String[3];
			}
			String inputStreet = U.getSectionValue(data.replace("\\\"", ""), "\"Address\":\"", "\"");
			String inputneigh = inputAddress[1];
			String inputcity = inputAddress[2];
			String inputState = U.getSectionValue(outSec, "\"Address3\":\"", "\"");
			String inputZip = U.getSectionValue(outSec, "\"PostalCode\":\"", "\"");
			Collection<String> ids = dataMap.get(U.getSectionValue(data.replace("Rancho / Rancheria", "Rancho Rancheria").replace("/Zona", "Zona").replace("\\\"", "").replace("\u000b", "").replace("  ", " "), "\"Address\":\"", "\"").toLowerCase());
//			if(data.contains("Avenida Mahahual, Km.2 (Carretera Costera Mahahual - Xcalak)")) continue;
			//U.log(ids+" -->  "+data.replace("Rancho / Rancheria", "Rancho Rancheria").replace("/Zona", "Zona").replace("\\\"", "").replace("\u000b", "").replace("  ", " ")+"|||||");
			for (String id : ids) {
				if (uniqueKey.add(id)) {
					String outArr[] = { id, inputStreet, inputneigh, inputcity, inputState, inputZip, streetAddress,
							neighbourhood, city, state, postalCode, avc, gac, geoDistance, lat, lon };
					outList.add(outArr);
					//break;
				}
			}
			//break;
		}
		U.writeCsvFile(outList, fileName.replace(".csv", "_Output.csv"));
		long stopTime = System.nanoTime();
		System.out.println(stopTime - startTime);
	}

	private static String verfiyAddressUsingLoqate(LoqateHelper loqate) {
		String url = "https://api.addressy.com/Cleansing/International/Batch/v1.00/json4.ws";
		String output = "";
		try {
			String jsonData = new Gson().toJson(loqate);
			output = sendPostRequestToLoqate(url, jsonData.replace(",null", ""));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String sendPostRequestToLoqate(String requestUrl, String payload)
			throws FileNotFoundException, IOException {
		String fileName = U.getCache(requestUrl + payload);
		// U.log("requestUrl :"+requestUrl+payload);
		File cacheFile = new File(fileName);
//		if(payload.contains("Palmilla No. 272"))
		
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		StringBuffer jsonString = new StringBuffer();
	//	U.log(payload);
		try {
			URL url = new URL(requestUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
			writer.write(payload);
			writer.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				jsonString.append(line);
			}
			br.close();
			connection.disconnect();
		} catch (Exception e) {
			//U.log(payload);
			FileUtil.writeAllText("/home/chinmay/Mexico/LightBoxAddress/MexicoAddress_0_599_ERROR.txt", payload);
			e.printStackTrace();
		}
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, jsonString.toString());
		return jsonString.toString();
	}

}
