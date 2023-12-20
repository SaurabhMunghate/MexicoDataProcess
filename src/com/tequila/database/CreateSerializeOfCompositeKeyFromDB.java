package com.tequila.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.database.connection.DBConnection;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CreateSerializeOfCompositeKeyFromDB implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7600913282414351342L;
	
//	private static final String DB_PATH = "/home/shatam-10/mexico_files/";
	private static final String DB_PATH = "/home/shatam-100/CODE Repository/Maxico/tequila.db_latest/";
	
	private static final String DB_NAME ="tequila.db";
	
	private static final String compositeKeyQuery = "SELECT SIC_SUB,COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON,ID,_SCORE from dataset";
	
	private static final String COMPOSITE_SER = "uniqueKeyTequila.ser";
	
	private static final String COMPOSITE_SER_WITH_SCORE = "uniqueKeyTequilaWithIdScore.ser";
//	
	private static final String COMPOSITE_SER_WITH_ID = "uniqueKeyTequilaWithId.ser";
	
	Connection conn=null;
	
	public CreateSerializeOfCompositeKeyFromDB(String path,String database) {
		conn = DBConnection.getConnection(path,database);
		U.log("Database Connected...");
	}
	
	public static void main(String[] args) throws Exception {
		CreateSerializeOfCompositeKeyFromDB compositekeyDb=new CreateSerializeOfCompositeKeyFromDB(DB_PATH, DB_NAME);
		compositekeyDb.readCompositKeyDatafromDB();
//		compositekeyDb.createCompositKeyWithScoreFromDB();
		compositekeyDb.createCompositKeyWithIdFromDB();
		compositekeyDb.disconnect();
	}

	private void readCompositKeyDatafromDB() throws Exception {
		HashSet<String>compositeKey=new HashSet<>();
		Statement stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(compositeKeyQuery);
		while (rs.next()) {
			String contactPerson = rs.getString(6);
			if(contactPerson == null)contactPerson = "";
			
/*			if (contactPerson.trim().length()!=0 && contactPerson.endsWith(";")) {
				contactPerson=contactPerson.toLowerCase().substring(0, contactPerson.length()-1); //CONTACT_PERSON
			}*/
			
			String sicSub = rs.getString(1).trim();
			if(sicSub.length() == 3)sicSub="0"+sicSub;
				
			
			String uniqueyKey=U.toTitleCase(sicSub)+ U.toTitleCase(rs.getString(2).trim())+ U.toTitleCase(rs.getString(3).trim())+ U.toTitleCase(rs.getString(4).trim())
			+U.toTitleCase(rs.getString(5).trim())+ U.toTitleCase(contactPerson.trim());
			uniqueyKey=U.trim(TranslateEnglish.convertToEnglish(uniqueyKey));
			if(!compositeKey.add(uniqueyKey.toLowerCase())){
				//U.errLog(rs.getString(7)+"::"+sicSub+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+contactPerson);
			}

		}
		U.log("Size::"+compositeKey.size());
		writeSerializeUniqueKey(compositeKey);
		
		rs.close();
		stmt.close();
		
	}
	/**
	 * compositeKey
	 * String is contain unique key
	 * Integer[] is size 2. index 0 contain ID and index 1 contain _SCORE
	 * @throws Exception
	 */
	private void createCompositKeyWithScoreFromDB() throws Exception {
		/**
		 * compositeKey
		 * String is contain unique key
		 * Integer[] is size 2. index 0 contain ID and index 1 contain _SCORE
		 */
		Map<String,Integer[]> compositeKeyaMap = new HashMap<>();
		Statement stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(compositeKeyQuery);
		while (rs.next()) {
			String contactPerson = rs.getString(6);
			if(contactPerson == null)contactPerson = "";
			
/*			if (contactPerson.trim().length()!=0 && contactPerson.endsWith(";")) {
				contactPerson=contactPerson.toLowerCase().substring(0, contactPerson.length()-1); //CONTACT_PERSON
			}*/
			
			String sicSub = rs.getString(1).trim();
			if(sicSub.length() == 3)sicSub="0"+sicSub;
				
			
			String uniqueyKey=U.toTitleCase(sicSub)+ U.toTitleCase(rs.getString(2).trim())+ U.toTitleCase(rs.getString(3).trim())+ U.toTitleCase(rs.getString(4).trim())
			+U.toTitleCase(rs.getString(5).trim())+ U.toTitleCase(contactPerson.trim());
			
			compositeKeyaMap.put(uniqueyKey.toLowerCase(), new Integer[]{rs.getInt(7),rs.getInt(8)});
			
			if(!compositeKeyaMap.containsKey(uniqueyKey.toLowerCase())){
				U.errLog(rs.getString(7)+"::"+sicSub+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+contactPerson);
			}

		}
		U.log("Size::"+compositeKeyaMap.size());
		U.writeSerializedFile(compositeKeyaMap, DB_PATH+ COMPOSITE_SER_WITH_SCORE);
		
		rs.close();
		stmt.close();
		
	}
	
	private void createCompositKeyWithIdFromDB() throws Exception {
		/**
		 * compositeKey
		 * String is contain unique key
		 * Integer[] is size 2. index 0 contain ID and index 1 contain _SCORE
		 */
		Map<String,Integer> compositeKeyaMap = new HashMap<>();
		Statement stmt=conn.createStatement();
		ResultSet rs = stmt.executeQuery(compositeKeyQuery);
		while (rs.next()) {
			String contactPerson = rs.getString(6);
			if(contactPerson == null)contactPerson = "";
			
/*			if (contactPerson.trim().length()!=0 && contactPerson.endsWith(";")) {
				contactPerson=contactPerson.toLowerCase().substring(0, contactPerson.length()-1); //CONTACT_PERSON
			}*/
			
			String sicSub = rs.getString(1).trim();
			if(sicSub.length() == 3)sicSub="0"+sicSub;
			
			String uniqueyKey=U.toTitleCase(sicSub)+ U.toTitleCase(rs.getString(2).trim())+ U.toTitleCase(rs.getString(3).trim())+ U.toTitleCase(rs.getString(4).trim())
			+U.toTitleCase(rs.getString(5).trim())+ U.toTitleCase(contactPerson.trim());
			uniqueyKey=U.trim(TranslateEnglish.convertToEnglish(uniqueyKey));
			
			
			if(compositeKeyaMap.containsKey(uniqueyKey.toLowerCase())){
//				U.errLog(rs.getString(7)+"::"+sicSub+"\t"+rs.getString(2)+"\t"+rs.getString(3)+"\t"+rs.getString(4)+"\t"+rs.getString(5)+"\t"+contactPerson+"\t"+compositeKeyaMap.get(uniqueyKey.toLowerCase()));
				U.errLog(rs.getString(7)+"\t"+compositeKeyaMap.get(uniqueyKey.toLowerCase()));
			}else {
				compositeKeyaMap.put(uniqueyKey.toLowerCase(), rs.getInt(7));
			}

		}
		U.log("Size::"+compositeKeyaMap.size());
		U.writeSerializedFile(compositeKeyaMap, DB_PATH+ COMPOSITE_SER_WITH_ID);
		
		rs.close();
		stmt.close();
		
	}
	
	
	private void writeSerializeUniqueKey(HashSet<String> uniqueKeys) throws IOException {
		
//		String filename="/home/mypremserver/RyeProject/DatabaseSerializeFile/uniquekeyRYEJan12.ser";
		try {
	        FileOutputStream fileOut =new FileOutputStream(DB_PATH + COMPOSITE_SER);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(uniqueKeys);
	        out.close();
	        fileOut.close();
	     } catch (IOException i) {
	        i.printStackTrace();
	     }
		U.log("File Created  "+COMPOSITE_SER);
	}

	private void disconnect(){
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		conn = null;
		U.log("Connection is closed.");
	}
}
