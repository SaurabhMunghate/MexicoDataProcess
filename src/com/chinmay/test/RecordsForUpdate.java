package com.chinmay.test;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.scrapper.CSVCorrectorInValidFormat;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class RecordsForUpdate extends Connect{
	private static String header[]={"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE","Years_In_Biz","Emp_Count_Min","Emp_Count_Max","Annual_Sales_Vol_Min","Annual_Sales_Vol_Max","CREATED_DATE"};
	private static String query="select ID,SIC_SUB,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,_STATUS as STATUS,CONTACT_PERSON,TITLE,LATITUDE,LONGITUDE,Years_In_Biz,Emp_Count_Min,Emp_Count_Max,Annual_Sales_Vol_Min,Annual_Sales_Vol_Max,CREATED_DATE from dataset where ZIP like \"72000\" AND CITY!=\"Heroica Puebla De Zaragoza\"";
	public static void main(String[] args) {
		new RecordsForUpdate().startProcess();
	}
	public RecordsForUpdate() {
		super();
	}
	private void startProcess() {
		try {
			List<String[]>outData=new ArrayList<String[]>();
			outData.add(header);
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(query);
			while (rs.next()) {
				String state=rs.getString("STATE");
				String zip=rs.getString("ZIP");
				String city=rs.getString("CITY");
				if(state!=null&&zip!=null) {
					if (!state.equals(MXStates.findStateFromZip(zip))) {
						String out[]=new String[header.length];
						for (int i = 0; i < header.length; i++)
							out[i]=rs.getString(header[i]);
						outData.add(out);
					}
				}else if(state!=null&&city!=null) {
					if (!CSVCorrectorInValidFormat.isExistCityWithinState(city, state)) {
						String out[]=new String[header.length];
						for (int i = 0; i < header.length; i++)
							out[i]=rs.getString(header[i]);
						outData.add(out);
					}
				}
			}
			U.writeCsvFile(outData, "/home/chinmay/Mexico/MexicoDataFiles/FilesMayBeWrong/Chinmay_Update_Pue_2_"+U.getTodayDateWith()+".csv");
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
