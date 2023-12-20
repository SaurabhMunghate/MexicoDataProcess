package com.tequila.address.extraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.database.connection.DBConnection;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;


public class AddressReader {

	private static final String DB_PATH = "/home/glady/mexicoProject/";
	
	private static final String DB_NAME = "boundarymex.db";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String[] addVal = readJson("/home/glady/Downloads/MexicoProject/add.txt");
		U.log(Arrays.toString(addVal));
		
//		new AddressReader().extractBoundaries("/home/glady/MexicoCache/database/city_state_zip.csv");
	}
	
	
	public void extractBoundaries(String inputFile){
		
		List<String[]> readLines = loadReadFile(inputFile);
		 
		List<String[]> writeLinesForDB = new ArrayList<>();
		List<String[]> linesForWrongZip = new ArrayList<>();
		
		HashSet<String> keys = loadKeyFromDB();
		String header[] = readLines.get(0);
		
		readLines.remove(0);
		
		String uniqueKey = null;
		int i = 0;
		for(String [] lines : readLines){
			
			if(lines[2].length() == 4){
				lines[2] = "0"+lines[2].trim();
			}
			if(lines[2].length() == 3){
				lines[2] = "00"+lines[2].trim();
			}
			if(lines[2].length() == 2){
				lines[2] = "000"+lines[2].trim();
			}
			if(lines[2].length() == 1){
				lines[2] = "0000"+lines[2].trim();
			}
			
			uniqueKey = lines[0].trim()+lines[1].trim()+lines[2].trim();
			
			if(!lines[2].isEmpty()){
				if(keys.add(uniqueKey.toLowerCase())){
					String[] add = getGoogleLatLongWithKey(new String[]{lines[1],lines[2]});
					if(add != null){
						if(add[4] == null){
							linesForWrongZip.add(lines);
							continue; //zip
						}
						if(add[6] == null && add[7] == null) continue;
						
//						U.log(Arrays.toString(lines));
//						U.log(Arrays.toString(add));
						String[] data = new String[lines.length + add.length];
						System.arraycopy(lines, 0, data, 0, lines.length);
						System.arraycopy(add, 0, data, lines.length, add.length);
						
						writeLinesForDB.add(data);
						U.log(Arrays.toString(data));
					}
				}
			}//eof if
			
			//set limit here
/*			if(++i == 10000){
				break;
			}*/
			

		}//eof for
		
		/*
		 * Write csv file for wrong zip
		 */
		writeFile(linesForWrongZip, header, "/home/glady/MexicoCache/database/wrong_zip_city_state_2.csv");
		
		/*
		 * Insert at db here
		 */
		if(writeLinesForDB.size() > 1)
			insertDataInDB(writeLinesForDB);
	}
	
	private HashSet<String> loadKeyFromDB(){
		HashSet<String> set = new HashSet<>();
		String query = "select INPUT_CITY,INPUT_STATE,INPUT_ZIP from boundaryset";
		
		String uniqueKey = null;
		try(Connection conn = DBConnection.getConnection(DB_PATH, DB_NAME);
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){
			
			while(rs.next()){
				uniqueKey = rs.getString("INPUT_CITY").trim() + rs.getString("INPUT_STATE").trim() + rs.getString("INPUT_ZIP").trim();
				set.add(uniqueKey.toLowerCase());
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return set;
	}
	
	private List<String[]> loadReadFile(String inputFile){
		List<String[]>  readLines = null;
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(inputFile));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
		U.log("Size of record is ::"+readLines.size());
		return readLines;
	}
	
	public static String[] readJson(String fileName){
	    String city = null, zip = null, neighbourhood = null, subLocality = null, state = null, country = null;
	    String northEastLat = null, northEastLng = null, southWestLat = null, southWestLng = null;
		try {
			JsonElement parser = new JsonParser().parse(new FileReader(fileName));
			JsonObject jsonTree = parser.getAsJsonObject();
			if(jsonTree.isJsonObject()){
			    JsonObject jsonObject = jsonTree.getAsJsonObject();

			    JsonArray result = (JsonArray) jsonObject.get("results");
			    for (JsonElement resultElement : result) {
	            	JsonObject resultJsonObj = resultElement.getAsJsonObject();
	            	JsonArray address_components = (JsonArray) resultJsonObj.get("address_components");

	            	String lngName = null, type = null;
	            	for (JsonElement addressElement : address_components) {
	            		
	            		JsonObject addressJsonObj = addressElement.getAsJsonObject();
	            		
		            	JsonArray types = (JsonArray) addressJsonObj.get("types");
		            	
		            	JsonPrimitive longName = (JsonPrimitive) addressJsonObj.get("long_name");
		            	
		            	for (JsonElement jsonElement : types) {
		            		type = jsonElement.getAsString();
		            		lngName = longName.getAsString();
		            		
		            		if(type.equals("postal_code")){
		            			zip = lngName;
		            		}
		            		if(type.equals("sublocality")){
		            			subLocality = lngName;
		            		}
		            		if(type.equals("locality")){
		            			city = lngName;
		            		}
		            		if(type.equals("administrative_area_level_1")){
		            			state = lngName;
		            		}
		            		if(type.equals("neighborhood")){
		            			neighbourhood = lngName;
		            		}
		            		if(type.equals("country")){
		            			country = lngName;
		            		}
						}
					}//eof for
	            	
//	            	JsonObject geometryJsonObj = (JsonObject) resultJsonObj.get("geometry");
	            	JsonObject boundJsonObj = (JsonObject) ((JsonObject) resultJsonObj.get("geometry")).get("bounds");
	            	try{
		            	JsonObject northeastJsonObj = (JsonObject) boundJsonObj.get("northeast");
		            	
		            	northEastLat = (String) northeastJsonObj.get("lat").getAsString();
		            	northEastLng = (String) northeastJsonObj.get("lng").getAsString();
		            	
		            	JsonObject southwestJsonObj = (JsonObject) boundJsonObj.get("southwest");
		            	
		            	southWestLat = (String) southwestJsonObj.get("lat").getAsString();
		            	southWestLng = (String) southwestJsonObj.get("lng").getAsString();
	            	}catch(NullPointerException e){}
	            	break;
			    }
			}

		} catch (JsonIOException | JsonSyntaxException | FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return new String[]{neighbourhood,subLocality,city,state,zip,country,northEastLat,northEastLng,southWestLat,southWestLng};
	}
	
	/*
	 * List of Google keys
	 */
	private static List<String> googleKeys = new ArrayList<String>(){
		{
//			add("AIzaSyBgHZxKFTDWDtBszK4OnSJi2tyd_Td2Wdw"); //sawan
//			add("AIzaSyBLWy53qG4g1fH5njFVgPZvoNPFCDiZkz0"); //sawan
			add("AIzaSyDiPvWYw9-ZGDnCfQ5kLJ8UUNlntD8doWk");
			add("AIzaSyBViZ3s1tT_H5l9NJdfunLnPkTPugYa8dY");
			add("AIzaSyBKcocJdCdsj23gUmK4oVKKWaEFvOgZ97o");

			add("AIzaSyBFAxxAV3FhN8kcbJSVVAnOhz_fjKfIpik");
			add("AIzaSyAGI0KieiS8xtDOl3_C1v6IRmMEzSJdJgc");
			add("AIzaSyCt_F1Q-L-Qio8bzyNTMWEFq5z_RATXMog");//----

			add("AIzaSyB5I9yzI5ntIi1jracCq0InRQoq5iQBw30");
			add("AIzaSyCiVAxucmZhYbi7pc-t-1Q4Hso1qR3PvsY");
			add("AIzaSyDIP8FM7ct7V1bjQzB1IyzVnOgX-EVFPa8");
			add("AIzaSyAJ0H12su8znvjcq3hC6wDahbVj6EcPX04");
			//builderscode@gmail.com
			add("AIzaSyDIWWnZZXUhO737NjGpH4-kqDyoJPPq-i8");
			add("AIzaSyArmTkVm2SLmYb9B9yyPFpBmvG5mnnDHlE");
			add("AIzaSyCb9P7_QAgzPaooEplNfE0tebiKg7riugU");
			add("AIzaSyDoLWxjEupe1uKJrdoQXR8t4xpUFhJlNBo");
			add("AIzaSyBaYFYV56gDIvHOGDJae4BqgEqju8kNUcI");
			add("AIzaSyDKkD0u8ijKKRus0vBqCNHCB_7I-4zsAf0");
			add("AIzaSyAoPqWhVhtfYArXy5yfNIi0Mk38H3ovCuA");
			add("AIzaSyAK1J_J2P7WHsYJ0na78SC1zOkGmU4mN8s");
			add("AIzaSyDqEAs30nGi4yIJXy7u76GUC9dog6mBvb8");
			add("AIzaSyBnfpUB1oX88bU085KJcbBpOBHrPxlCj80");
			//rj007.abc@gmail.com
			add("AIzaSyAkaGwA4IsIoO6UsUIopTLB1NJvGw5A3Eo");
			add("AIzaSyABxbPwAaRHlu721ZyHQcXijAEQ0tBUvWs");
			add("AIzaSyAcfEZBXa2NgTl5CuXbUuyeLDpOk2sZyeE");
			add("AIzaSyCu6coMvMDXaYsljcRRu660pt6O2En1AxI");
			add("AIzaSyCK3hCUSTNInZr620Xwah5wLBpgjpUljug");
			add("AIzaSyBuF_bjG_jnicawEVdbJelbS7Gk3nINT9k");
			add("AIzaSyD1Vi6j3v7WJG3H32jJSxpFUAo6_oxaJD4");
			add("AIzaSyCRsQ4o5oO8kcIo3dm5j4iGhhARYM183W4");
			add("AIzaSyDIDd98xLKpJwI9nQMLLnLUSq0I0wQW2Ts");
			add("AIzaSyCE_NRenzitjCR0lPSiGgK21XO-yHkTdWA");
			//chinmaywankar7@gmail.com
			add("AIzaSyBC0P26el7L414j3HYrkLzx-r7Ek4G2Vt4");
			add("AIzaSyABIBUNAe7E7_oC9900XUY5v0sj1dV4SPg");
			add("AIzaSyDfCxQ7T_qap5Vso_8glOlQSyGhxkA8ziM");
			add("AIzaSyAlx9-X8eeN5-GlbrK0IMFPhvtK4yvBFyU");
			add("AIzaSyDE9FiOH4AoAJiEebb5yEMqoe6QwtMpDJg");
			add("AIzaSyC8OyAdQDdZcmadOwAOcwPtN9GXtI0pghI");
			add("AIzaSyCeXBAgRIJA4oIRMndFli-xfI1J8kRr3JE");
			add("AIzaSyCSFUrbqTXPPn3pQf-O-VYEU1rSppADW8w");
			add("AIzaSyAeII9qGCgPGxmYWmzaDCklRNUAZQ14TKw");
			add("AIzaSyBqNIwGT33MKIi4Vtpj2vWiGwlQWCcebGc");
			add("AIzaSyDsaA6GITCX6fjcuqj2yZ8ZoBgh9mmcqhw");
			//AmitSir
			add("AIzaSyCtSTblNn5A6j7kum8jobRQV8V9ikbxgO0");
			add("AIzaSyAZzXYqaW-KsAIEU3A-f7CcwokztxP509M");
			add("AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
		}
	};
	
	boolean changeKey = false;
	int extractCount = 1;
	int keyCount = 0;
	
	public String[] getGoogleLatLongWithKey(String[] address){
		
		String str="";
		
		String adr = address[0] + "," + address[1];
		try {
			String api = "https://maps.googleapis.com/maps/api/geocode/json?address=" + URLEncoder.encode(adr, "UTF-8")+"&sensor=true";
			String fileName = U.getCache(api);
	
			File cacheFile = new File(fileName);
			if (cacheFile.exists()){
				if(cacheFile.length() > 450){
//					U.log(fileName);
					str = FileUtil.readAllText(fileName);
				}else{
					U.delete(fileName);
				}
			}
		
			URL url = new URL(api);
//			U.log(url);
			U.log("withKey:::"+U.getCache(url.toString()));
			U.log("keyOCunt::"+keyCount+"\t"+extractCount);
			
			String key = googleKeys.get(keyCount);
		
			if(extractCount % 2500 == 0){
				keyCount++;
			}
			
			try{Thread.sleep(15);}catch(Exception e){}
			
			str = U.extractHtml(url.toString()+"&key="+ key,fileName);
			U.log(url.toString()+"&key="+ key);
			
			if (str.contains("address_components")) {
				extractCount++;
//				U.log(str);
				return readJson(fileName);
			}else{
				if(str.contains("OVER_QUERY_LIMIT"))
					keyCount = keyCount+1;
				
				U.delete(fileName);
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
				
		return null;
	}//eof getGoogleLatLong()
	
	private void insertDataInDB(List<String[]> allRows){
		String insertQuery = "insert into boundaryset(INPUT_CITY,INPUT_STATE,INPUT_ZIP,NEIGHBOURHOOD,SUBLOCALITY,CITY,STATE,ZIP,COUNTRY,NORTHEASTLAT,NORTHEASTLNG,SOUTHWESTLAT,SOUTHWESTLNG) "
				+"values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		U.log("Start inserting at database here .......");
		Connection conn = DBConnection.getConnection(DB_PATH, DB_NAME);
			
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(insertQuery);
			
			for(String[] row : allRows){
			
				for(int i = 0; i < row.length; i++){
					pstmt.setString(i+1, row[i]);
				}
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
			
			if(conn != null)
				conn.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	void writeFile(List<String[]> outputLines, String[] header, String outputFile){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(outputFile),',');
			writer.writeNext(header);
			writer.writeAll(outputLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing validate file.....Done");
	}
}
