package com.tequila.geocode;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.shatam.geoboundary.Boundary;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;


public class LatLongExtraction {
	private static final String COUNTRY = "Mexico";
	
	private String fileName = null;
	private String dbName = null;
	private String tableName = null;
	private String mapName = null;
		
	private int deleteCount = 0;
	private Connection conn = null;
	
	private int extractCount = 1;
	private int keyCount = 0;
	

	public LatLongExtraction(String fileName, String dbName, String tableName) {
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
	
	private void startExtraction(int start, int end) {
		Set<String> uniqueSet = loadUniqueTableAddress();
		
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
				
				if(!uniqueSet.add((nextLine[1].trim()+nextLine[2].trim()+nextLine[3].trim()+nextLine[4].trim()).toLowerCase()))continue;
				
				count++;
				if(count < start)continue;
				
				U.log(">>i ="+i);
				
				String[] add = { nextLine[1], nextLine[2], nextLine[3], nextLine[4] };
			
				Thread.sleep(15);
				
				String[] latLng = getGoogleLatLongWithKey(add);
				
				//validate state boundary here..
				if(!latLng[0].trim().isEmpty() && !latLng[1].trim().isEmpty()){
					if(!Boundary.boundaryCheckForState(nextLine[3].trim(), latLng[0].trim(), latLng[1].trim(), COUNTRY)){
						latLng[0] = latLng[1] = "";
					}
				}
				if(latLng[0].trim().isEmpty())continue;
				
				pstmt.setString(1, nextLine[0].trim());
				pstmt.setString(2, nextLine[1].trim());
				pstmt.setString(3, nextLine[2].trim());
				pstmt.setString(4, nextLine[3].trim());
				pstmt.setString(5, nextLine[4].trim());
				pstmt.setString(6, latLng[0].trim());
				pstmt.setString(7, latLng[1].trim());
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
	

	
	public String[] getGoogleLatLongWithKey(String[] address){
		String[] latlong = {"",""};
		String str="";
		
		String adr = address[0] + "," + address[1] + "," + address[2] + "," + address[3];
		
		try {
			String api = "https://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(adr, "UTF-8")+"&sensor=true";
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
			
			if (str.contains("<lat>") && str.contains("</lng>")) {
				extractCount++;
//				U.log(str);
				latlong[0]= U.getSectionValue(str,"<lat>", "</lat>");
				latlong[1]= U.getSectionValue(str,"<lng>", "</lng>");
				U.log(latlong[0]+"  "+latlong[1]);
				setMapName("Google Map");
			}else{
				if(str.contains("OVER_QUERY_LIMIT"))
					keyCount = keyCount+1;
				
				U.delete(fileName);
			}			
			
		}catch (Exception e) {
			e.printStackTrace();	
		}
		if(latlong[0] == null || latlong[1] == null)latlong[0] = latlong[1] = "";
		return latlong;
		
	}//eof getGoogleLatLongWithKey()
	
	public String getMapName() {
		return mapName;
	}
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	
	private Set<String> loadUniqueTableAddress(){
		Set<String> uniqueSet = new HashSet<>();
		
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from "+tableName);){
			while(rs.next()){
				String address = rs.getString("ADDRESS");
				String city = rs.getString("CITY");
				String state = rs.getString("STATE");
				String zip = rs.getString("ZIP");
				
				if(address == null) address = "";
				if(city == null) city = "";
				if(state == null) state = "";
				if(zip == null) zip ="";
				
				String uniqueKey = (address.trim() + city.trim()+ state.trim()+ zip.trim()).toLowerCase();
				uniqueSet.add(uniqueKey);
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		return uniqueSet;
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
}
