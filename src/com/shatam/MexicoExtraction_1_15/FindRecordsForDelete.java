package com.shatam.MexicoExtraction_1_15;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.U;

public class FindRecordsForDelete extends Connect{
	///home/chinmay/Mexico/MexicoDataFiles/Data/Apr/Updated/Chinmay_Update_30_Apr_2020.csv
	
	public static void main(String[] args) {
		new FindRecordsForDelete().processData();
	}
	private void processData() {
		try {
			List<String[]> nextLine=U.readCsvFile("/home/chinmay/Mexico/MexicoDataFiles/Data/Apr/Updated/Chinmay_Update_30_Apr_2020.csv");
			//"SELECT SIC_SUB,COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON,ID,_SCORE from dataset";
			
			Statement stmt=conn.createStatement();
			List<String[]> outLine=new ArrayList<String[]>();
			for (String[] next : nextLine) {
				
				if(next[0].contains("ID"))continue;
				String query="SELECT SIC_SUB,COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON,ID,_SCORE FROM dataset WHERE ID in ("+next[0]+","+next[1]+")";	
				ResultSet rs=stmt.executeQuery(query);
				 String mainArr[]= new String[7];
				while (rs.next()) {
					String contactPerson = rs.getString(6);
					if(contactPerson == null)contactPerson = "";
					String sicSub = rs.getString(1).trim();
					if(sicSub.length() == 3)sicSub="0"+sicSub;
					String uniqueyKey=U.toTitleCase(sicSub)+ U.toTitleCase(rs.getString(2).trim())+ U.toTitleCase(rs.getString(3).trim())+ U.toTitleCase(rs.getString(4).trim())
					+U.toTitleCase(rs.getString(5).trim())+ U.toTitleCase(contactPerson.trim());
					String afteruniqueyKey=U.trim(TranslateEnglish.convertToEnglish(uniqueyKey));
					U.log(next[0]+"|"+rs.getString("ID"));
					String dupId="";
					if (rs.getString("ID").equals(next[0])) {
						dupId=next[1];
					}
					if (rs.getString("ID").equals(next[1])) {
						dupId=next[0];
					}
					
					//=rs.getString("ID")==next[0]?next[1]:next[0];
					if (!uniqueyKey.equals(afteruniqueyKey)) {
						mainArr[0]=rs.getString("ID");
						mainArr[1]=U.toTitleCase(U.toTitleCase(rs.getString("COMPANY_NAME")));
						mainArr[2]=U.toTitleCase(U.toTitleCase(rs.getString("ADDRESS")));
						mainArr[3]=U.toTitleCase(U.toTitleCase(rs.getString("CITY")));
						mainArr[4]=U.toTitleCase(U.toTitleCase(rs.getString("STATE")));
						mainArr[5]=U.toTitleCase(U.toTitleCase(rs.getString("CONTACT_PERSON")));
						mainArr[6]=dupId;
						//outLine.add(out);
					}
				}
				
				outLine.add(mainArr);
			}
			U.writeCsvFile(outLine, "/home/chinmay/Mexico/MexicoDataFiles/Data/Apr/Updated/Chinmay_Update_30_Apr_2020_1.csv");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
