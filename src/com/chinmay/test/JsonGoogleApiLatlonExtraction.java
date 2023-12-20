package com.chinmay.test;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.shatam.utils.ApiKey;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class JsonGoogleApiLatlonExtraction {
	public static void main(String[] args) {
		try {
			String[] header= {"STREET_ADDRESS","NEIGHBOURHOOD","CITY","STATE","ZIP","KEY","FORMATTED_ADDRESS","NEIGHBOURHOOD_Google","ZIP_Google","LATITUDE","LONGITUDE"};
			StringWriter sw=new StringWriter();
			com.opencsv.CSVWriter writer=new com.opencsv.CSVWriter(sw);
			writer.writeNext(header);
			HashSet<String> uniqueAddress=new HashSet<>();
			String fileName="/home/mypremserver/MexicoCache/Tiendo/Tiendeo-ToysAndBabies/Tiendeo-ToysAndBabies.csv";
			ArrayList<String[]>inputRecord=(ArrayList<String[]>) U.readCsvFile(fileName);
			int i=0;
			for (String[] input : inputRecord) {
				if (input[0].contains("SrNo=id")) {
					//writer.writeNext(input);
					continue;
				}
				//U.log(Arrays.toString(input));
				/*if (++i==1000) {
					break;
				}*/
				String add[]= {input[8].replace("#", ""),input[10],input[11]};
				String key=input[8]+input[9]+input[10]+input[11];
				
				if (uniqueAddress.add(key)) {
					String googleData[]=testJsonCode(add);
					String output[]= {input[8],input[9],input[10],input[11],input[12],key.toLowerCase(),googleData[0],googleData[1],googleData[2],googleData[3],googleData[4]};
					writer.writeNext(output);
				}
				//break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_AddressFile.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String[] testJsonCode(String add[]) throws Exception{
		String address=add[0]+","+add[1];
		String addURL= "https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(address,StandardCharsets.UTF_8.toString())+"&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s";
		U.log(addURL);
		String fileName = U.getCache(addURL);
		File cacheFile = new File(fileName);
		String json = "";
		InputStream inputStream = null;
		HttpPost post=null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response=null;
		HttpEntity entity =null;
		BufferedReader reader=null;
		if (cacheFile.exists()) {
			if (cacheFile.isFile()) {
	    		if(cacheFile.length() < 400){
	    			if (FileUtil.readAllText(fileName).contains("ZERO_RESULTS")) {
	    				json=FileUtil.readAllText(fileName);
					}else if(cacheFile.delete()){
	    				testJsonCode(add);
	    			}
	    		}else {
	    			json=FileUtil.readAllText(fileName);
	    		}
			}
		}else{
	        try {           
	            post = new HttpPost(addURL);
	            response = client.execute(post);
	            entity = response.getEntity();
	            inputStream = entity.getContent();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
	        try {           
	        	reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
	            StringBuilder addressSB = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	            	addressSB.append(line);
	            }
	            inputStream.close();
	            json = addressSB.toString();
	            if (!cacheFile.exists())
					FileUtil.writeAllText(fileName, json);
	            reader.close();
	            inputStream.close();
	            EntityUtils.consume(entity);
	            post.releaseConnection();
	        } catch(Exception e) {
	        	e.printStackTrace();
	        }
		}
//		U.log(json);
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(json);
        JSONObject jb = (JSONObject) obj;
        
        JSONArray jsonObject1 = (JSONArray) jb.get("results");
        if (jsonObject1.size()==0) {
        	//return new String[]{formattedAddress,neighbourhood,zip,latlng[0],latlng[1]};
			return new String[]{"","","","","","","",""};
		}
        String zip="";
        String city="";
        String state="";
        String neighbourhood="";
        String formattedAddress="";
        JSONObject jsonObject2 = (JSONObject)jsonObject1.get(0);
        JSONObject jsonObject3 = (JSONObject)jsonObject2.get("geometry");
        JSONObject location = (JSONObject) jsonObject3.get("location");
        System.out.println( "Lat = "+location.get("lat"));
        System.out.println( "Lng = "+location.get("lng"));
        String latlng[]= {location.get("lat").toString(),location.get("lng").toString()};
        JSONArray jsonObject4 = (JSONArray)jsonObject2.get("address_components");
        
        for (int i = 0; i < jsonObject4.size(); i++) {
        	JSONObject Add1 = (JSONObject)jsonObject4.get(i);
        	JSONArray types = (JSONArray)Add1.get("types");
        	if (types.toString().contains("[]")) {
				continue;
			}
        	//U.log("-----------------------"+types.toString());
        	String type = (String)types.get(0);
        	if (type.contains("postal_code")) {
        		zip = (String)Add1.get("long_name");
//        		U.log(zip);
//				U.log(type);
			}else if(types.toString().contains("[\"political\",\"sublocality\",\"sublocality_level_1\"]")||types.toString().contains("\"neighborhood\"")){
//				U.log("-----------------------"+types.toString());
				neighbourhood = (String)Add1.get("long_name");
//        		U.log(neighbourhood);
				/*for (int j = 0; j < types.size(); j++) {
					U.log("-----------------------");
					String typ = (String)types.get(j);
					U.log(typ+"---->"+types.size());
				}*/
			}else if(types.contains("country")) {
				String country=(String)Add1.get("long_name");
				if (!country.toLowerCase().contains("mexico")) {
					return new String[]{"","","","","","","",""};
				}
			}else if(types.toString().contains("[\"locality\",\"political\"]")){
//				U.log("-----------------------"+types.toString());
				city = (String)Add1.get("long_name");
        		U.log(city);
			}else if(types.toString().contains("[\"administrative_area_level_1\",\"political\"]")){
//				U.log("-----------------------"+types.toString());
				state = (String)Add1.get("long_name");
        		U.log(state);
			}
//        	U.log(types.toString());
		}
        formattedAddress = (String)jsonObject2.get("formatted_address");
        U.log(formattedAddress);
        if (zip!=null||zip.trim().length()==0) {
        	return new String[]{formattedAddress,neighbourhood,zip,latlng[0],latlng[1],city,state};
		}
        //https://maps.googleapis.com/maps/api/geocode/json?latlng=
        String latlngURl="https://maps.googleapis.com/maps/api/geocode/json?latlng="+latlng[0]+","+latlng[1];
        fileName = U.getCache(latlngURl);
		cacheFile = new File(fileName);
		if (cacheFile.exists())
			 json=FileUtil.readAllText(fileName);
		else {
	        try {
		        post = new HttpPost(latlngURl+"&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
		        response = client.execute(post);
				entity = response.getEntity();
				inputStream = entity.getContent();
				reader = new BufferedReader(new InputStreamReader(inputStream,"utf-8"),8);
	            StringBuilder sbuild = new StringBuilder();
	            String line = null;
	            while ((line = reader.readLine()) != null) {
	                sbuild.append(line);
	            }
	            //U.log(sbuild);
	            reader.close();
	            inputStream.close();
	            json = sbuild.toString();
	            EntityUtils.consume(entity);
	            post.releaseConnection();
	            if (!cacheFile.exists())
					FileUtil.writeAllText(fileName, json);
	        } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
            Object obj1 = parser.parse(json);
            JSONObject jb1 = (JSONObject) obj1;
            JSONArray jsonObjectAdd = (JSONArray) jb1.get("results");
            JSONObject jsonObjectAdd1 = (JSONObject)jsonObjectAdd.get(0);
            JSONArray jsonObjectAdd2 = (JSONArray)jsonObjectAdd1.get("address_components");
            for (int i = 0; i < jsonObjectAdd2.size(); i++) {
            	JSONObject Add1 = (JSONObject)jsonObjectAdd2.get(i);
            	JSONArray types = (JSONArray)Add1.get("types");
            	String type = (String)types.get(0);
            	if (type.contains("postal_code")) {
            		zip = (String)Add1.get("long_name");
            		U.log(zip);
					U.log(type);
				}
			}
            return new String[]{formattedAddress,neighbourhood,zip,latlng[0],latlng[1],city,state};
	}
}
