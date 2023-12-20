package com.tequila.database.corrector;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.lf5.util.ResourceUtils;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.tequila.database.TestPath;

public class FindRecordsUsingUrl {
	public static String fileName="/home/chinmay/Mexico/Tested/RakeshSir/emailResult_Formatted.csv";
	MultiValueMap<String, String> wrongUrls=new MultiValueMap<>();
//	MultiValueMap<String, String> correctURLWithEmails=new MultiValueMap<>();
//	HashMap<String, String[]> correctURLWithEmails=new HashMap<>();
	private static Connection conn=null;
	public static void main(String[] args) {
		FindRecordsUsingUrl url=new FindRecordsUsingUrl();
		url.startProcess();
		url.close();
	}
	private void close() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FindRecordsUsingUrl() {
		conn=DBConnection.getConnection("/home/chinmay/Mexico/MexicoDataFiles/Db/", "tequila.db");
	}
	public void startProcess() {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		HashMap<String, String[]> correctURLWithEmails=getDataFromTabFile(fileName);
		MultiValueMap<String , String> DbRecords=getDbRecords(new ArrayList<String>(correctURLWithEmails.keySet()));
		U.log(DbRecords.size());
		Set<String> keySet=DbRecords.keySet();
		writer.writeNext(new String[] {"ID","URL","EMAIL"});
		for (String keys : keySet) {
			Collection<String> value=DbRecords.get(keys);
			for (String val : value) {
				String email="";
				for (String string : correctURLWithEmails.get(keys))
					email+=string+";";
				email=email.replaceAll("\\[|\\]|;$", "");
				if (email.trim().length()>0) {
					String out[]=new String[] {val,keys,email};
					writer.writeNext(out);
				}
			}
		}
		try {
			FileUtil.writeAllText("/home/chinmay/Mexico/Tested/RakeshSir/URLAndEmailsformated_Formatted1_Sep_9.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	private MultiValueMap<String, String> getDbRecords(ArrayList<String> urlList) {
		try {
			int i=0,count=0;
			MultiValueMap<String, String> dataUrlMap=new MultiValueMap<>();
			Statement stmt= conn.createStatement();
			U.log("Reading Data from db..."+urlList.size());
			for (String obj : urlList) {
				
				ResultSet rs=stmt.executeQuery("SELECt ID FROM dataset WHERE EMAIL is null AND URL like \"%"+obj.replaceAll("http://|/$", "")+"%\"");
				U.log((count++)+" -> "+obj+" -> "+i);
//				if (count==500)
//					break;
				while (rs.next()) {
					i++;
					U.log(i);
					//if (i>6000)
						dataUrlMap.put(obj, rs.getString(1));
				}
				rs.close();
				if (i>=7000)
					return dataUrlMap;
			}
			U.log(i);
//			if (i==6000)
				
			return dataUrlMap;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	private HashMap<String, String[]> getDataFromTabFile(String fileName) {
		try {
			List<String> readLine=Files.readAllLines(java.nio.file.Paths.get(fileName));
			HashMap<String, String[]> correctURLWithEmails=new HashMap<>();
			for (String readData : readLine) {
//				U.log(readData);
				String []tempData=readData.split("\t");
//				if (tempData[1].contains("[")&&!tempData[1].contains("[]")) {
//					if (tempData[1].contains(",")) {
//					U.log(tempData[0]);
						correctURLWithEmails.put(tempData[0].replace("\"", ""), tempData[1].replaceAll("8b4e078a51d04e0e9efdf470027f0ec1@sentry.wixpress.com[,]?||\\[|\\]", "").split(","));
//					}else {
//						correctURLWithEmails.put(tempData[0], tempData[1].replaceAll("\\[|\\]", "").split(","));
//					}
//				}else if(tempData[1].contains("Email Not Found or Site is Inactive")) {
//					wrongUrls.put(tempData[0], tempData[1]);
//				}
//				break;
			}
			return correctURLWithEmails;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
