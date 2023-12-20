package com.tequila.geocode;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class GeoLocationExtraction {
	
	private String fileName = null;
	private String dbName = null;
	private String tableName = null;
	private String mapName = null;
		
	int deleteCount = 0;
	Connection conn = null;

	public GeoLocationExtraction(String fileName, String dbName, String tableName) {
		this.fileName = fileName;
		this.dbName = dbName;
		this.tableName = tableName;
	}
	
	public void start(int start,int end){
		conn = DBConnection.getConnection(Path.DB_PATH, dbName);
		if(new File(fileName).exists()){
			if(dbName != null && tableName != null){
				startExtraction(start, end);
			}
		}
		disconnect();
		
		U.log("Deleted file count::"+deleteCount);
	}
	
	private void startExtraction(int start,int end){
		String query = "INSERT INTO "+tableName+"(ID,ADDRESS,CITY,STATE,ZIP,LATITUDE,LONGITUDE,MAP_USED)"
				+ " VALUES(?,?,?,?,?,?,?,?)";
		
		PreparedStatement pstmt = null;

		int count = 0;
		int i = 0;

		CSVReader reader = null;
		try{
			conn.setAutoCommit(false);
			
			pstmt = conn.prepareStatement(query);
			reader = new CSVReader(new FileReader(fileName));

			String[] nextLine = null;
			while ((nextLine = reader.readNext()) != null) {
				count++;
				if(count < start)continue;
				
				U.log(">>i ="+i);
				
				String[] add = { nextLine[1], nextLine[2], nextLine[3], nextLine[4] };
			
				Thread.sleep(15);
				
				String[] latLng = getGoogleLatLong(add);
				
				
				pstmt.setString(1, nextLine[0].trim());
				pstmt.setString(2, nextLine[1].trim());
				pstmt.setString(3, nextLine[2].trim());
				pstmt.setString(4, nextLine[3].trim());
				pstmt.setString(5, nextLine[4].trim());
				pstmt.setString(6, latLng[0]);
				pstmt.setString(7, latLng[1]);
				
				if(latLng[0].isEmpty())	setMapName("");
				
				pstmt.setString(8, getMapName());

				pstmt.addBatch();
				if ((++i % 50) == 0) {
					System.out.println("Commit the batch");
					U.log(i);
					int count1[] = pstmt.executeBatch();
					pstmt.clearBatch();
					System.out.println("Number of rows inserted: " + count1.length + "\t Total count: " + i);
					conn.commit();

				}
				
				if(count == end)break;
				
			}
			int count1[] = pstmt.executeBatch();
			pstmt.clearBatch();
			System.out.println("Number of rows inserted: " + count1.length + "\t Total count: " + i);
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				conn.commit();
				conn.setAutoCommit(true);
				reader.close();
				pstmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
	}
	
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	
	public String[] getGoogleLatLong(String[] address){
		String[] latlong = {"",""};
		
		//"&key=" + key;
		String adr = address[0] + "," + address[1] + "," + address[2] + "," + address[3];
		try {
			String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(adr, "UTF-8")
					+ "&sensor=true";
			
			URL url = new URL(api);
//			U.log(url);
			U.log(U.getCache(url.toString()));
			String str = U.getHTML(url.toString());
			
			if (str.contains("<lat>") && str.contains("</lng>")) {
//				U.log(str);
				latlong[0]= U.getSectionValue(str,"<lat>", "</lat>");
				latlong[1]= U.getSectionValue(str,"<lng>", "</lng>");
				U.log(latlong[0]+"  "+latlong[1]);
				setMapName("Google Map");
			}else{
				String path = U.getCache(url.toString()); 
				delete(path);
				latlong = getGoogleLatLongWithKey(address);
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return latlong;
	}//eof getGoogleLatLong()
	
	
	/*
	 * List of Google keys
	 */
	private static List<String> googleKeys = new ArrayList<String>(){
		{
			add("AIzaSyBgHZxKFTDWDtBszK4OnSJi2tyd_Td2Wdw"); //sawan
			add("AIzaSyBLWy53qG4g1fH5njFVgPZvoNPFCDiZkz0"); //sawan
			add("AIzaSyAeII9qGCgPGxmYWmzaDCklRNUAZQ14TKw"); //chinmay
			add("AIzaSyBqNIwGT33MKIi4Vtpj2vWiGwlQWCcebGc"); //chinmay
			add("AIzaSyDsaA6GITCX6fjcuqj2yZ8ZoBgh9mmcqhw"); //chinmay
		}
	};
	
	boolean changeKey = false;
	int extractCount = 1;
	int keyCount = 0;
	
	public String[] getGoogleLatLongWithKey(String[] address){
		
		String[] latlong = {"",""};
		String str="";
		
		String adr = address[0] + "," + address[1] + "," + address[2] + "," + address[3];
		try {
			String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(adr, "UTF-8")+"&sensor=true";
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
			
			if(keyCount == 5){
				return getBingLatLong(address);
			}
			str = U.extractHtml(url.toString()+"&key="+ key,fileName);
			
			if (str.contains("<lat>") && str.contains("</lng>")) {
				extractCount++;
//				U.log(str);
				latlong[0]= U.getSectionValue(str,"<lat>", "</lat>");
				latlong[1]= U.getSectionValue(str,"<lng>", "</lng>");
				U.log(latlong[0]+"  "+latlong[1]);
				setMapName("Google Map");
			}else{
				delete(fileName);
				latlong = getBingLatLong(address);
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
				
		return latlong;
	}//eof getGoogleLatLong()
	

	//AIzaSyBLWy53qG4g1fH5njFVgPZvoNPFCDiZkz0
	
	public String[] getBingLatLong(String[] address) {
		
		String[] latLong = {"",""};
		String addressLine=address[0]+","+address[1]+","+address[2]+","+address[3];
		
		if (addressLine == null || addressLine.trim().length() == 0) return null;
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
			
			if(latLong[0] == null || latLong[1] == null || latLong[0].isEmpty()){
				String path = U.getCache(geocodeRequest);
				delete(path);
				//latLong[0] = latLong[1] = "";
				latLong = getMapQuestLatLong(address);
				
			}else{
				setMapName("Bing Map");
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return latLong;
	}//eof getBingLatLong()
	
	private final static String COUNTRY = "MX";  //CA   //US
	public String[] getMapQuestLatLong(String[] address) {
		String[] latLong = {"",""};

		String adr = String.join(",", address);
		try {
			String host = "http://www.mapquestapi.com/geocoding/v1/address?key=7AV661nmsttamglUwA8meTKVbngvSC2k&location="+
					URLEncoder.encode(adr, "UTF-8")+"&ignoreLatLngInput=false&callback=geocodeResult&outFormat=xml";
						
			URL url = new URL(host);
			U.log(url.toString());
//			U.log(U.getCache(url.toString()));
			String html = U.getHTML(url.toString());
			String statusCode = U.getSectionValue(html, "<statusCode>", "</statusCode>");
			if(statusCode.trim().equals("0") || !statusCode.startsWith("40") || !statusCode.trim().startsWith("50")){
				String stateSection = U.getSectionValue(html, "\'Country\'>"+COUNTRY, "<mapUrl>");
//				U.log(stateSection);
				if (stateSection != null) {
					latLong[0]= U.getSectionValue(stateSection,"<lat>", "</lat>");
					latLong[1]= U.getSectionValue(stateSection,"<lng>", "</lng>");
					setMapName("MapQuest");
				}else{
					String path = U.getCache(url.toString());
					delete(path);
					latLong[0] = latLong[1] = "";
					setMapName("");
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
		return latLong;
	}

	private void delete(String filePath){
		File file = new File(filePath);
		if (file.isFile()) {
    		if(file.length() < 400){
    			if(file.delete()){
    				U.log("delete::"+file.getName()+" \t(bytes) :" + file.length());
    				deleteCount++;
    			}
    		}
		}
	}
	
	private void disconnect(){
		try {
			if(conn != null){
				conn.close();
			}
			if(conn.isClosed())
				U.log("Disconnect from database...");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
