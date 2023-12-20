package com.tequila.database.corrector;

import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.DB;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class GetDetailsFromID {
	
	
	public static void main(String[] args) {
		Connection conn=DBConnection.getConnection("/home/chinmay/Mexico/MexicoDataFiles/Db/","tequila.db");
		String fileName="/home/chinmay/Mexico/Tested/RakeshSir/emailResult_Formatted.csv";
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		List<String[]> filedata=U.readCsvFile(fileName);
		List<Integer>idList=new ArrayList<Integer>();
		HashMap<String, String>emailMap=new HashMap<>();
		for (String[] idData : filedata) {
			if (idData[0].contains("ID")) { continue;}
			idList.add(Integer.parseInt(idData[0]));
			emailMap.put(idData[0], idData[2]);
		}
		String header[]= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE","Years_In_Biz","Emp_Count_Min","Emp_Count_Max","Annual_Sales_Vol_Min","Annual_Sales_Vol_Max"};
		
		try {
			Map<Integer,String[]> idDetailsMap = DB.getIdRecordDetailsForUpdate(idList, conn); //extract details
			writer.writeNext(header);
			for (Integer id : idDetailsMap.keySet()) {
	//			String []
				String []data=idDetailsMap.get(id);
				if (data[11]==null&&data[0]=="") {
					data[11]=emailMap.get(""+id);
				}else{
					continue;
				}
				U.log(data[11]);
				writer.writeNext(data);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_WithData.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
			
//	Map<Integer,String[]> idDetailsMap = DB.getIdRecordDetails(idList, conn); //extract details
}
