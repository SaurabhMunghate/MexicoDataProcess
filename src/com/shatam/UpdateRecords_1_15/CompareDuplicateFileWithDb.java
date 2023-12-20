package com.shatam.UpdateRecords_1_15;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class CompareDuplicateFileWithDb extends Connect{
	public static void main(String[] args) throws SQLException {
		String folderPath="/home/chinmay/MexicoCache/Cache/Restaurant_Guru/Duplicated";
		new CompareDuplicateFileWithDb().startProcess(folderPath);
	}
	private List<String> _getListOfFiles(String folderPath) {
		try (Stream<Path> walk = Files.walk(Paths.get(folderPath))){
			return walk.filter(Files::isRegularFile).map(x -> x.toString()).collect(Collectors.toList());
		}catch (IOException e) {
			e.printStackTrace();
		}
		return new ArrayList<String>();
	}
	String header[]= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE","YEARS_IN_BIZ","EMP_COUNT_MIN","EMP_COUNT_MAX","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX"};
	private void startProcess(String folderPath) throws SQLException {
		File f = new File(folderPath);
		String[] pathnames = f.list();
		Statement stmt=conn.createStatement();
		List<String[]> writeLines=new ArrayList<>();
		writeLines.add(header);
        for (String pathname : pathnames) {
        	if (pathname.endsWith(".csv")) {
        		String fileName=folderPath+"/"+pathname;
        		U.log(fileName);
        		List<String[]> fileData=U.readCsvFile(fileName);
        		for (String[] nextLine : fileData) {
        			if (nextLine[0].contains("ID"))continue;
//        			U.log(nextLine[ID_MATCHED]);
        			ResultSet rs=stmt.executeQuery("SELECT * from dataset where ID="+nextLine[ID_MATCHED]);
            		while(rs.next()) {
            			String out[]=new String[header.length];
            			out[Out_ID]=nextLine[ID_MATCHED];
            			out[Out_SIC_SUB]=compareString(nextLine[SIC_SUB], rs.getString("SIC_SUB"));
            			out[Out_COMPANY_NAME]=compareString(nextLine[COMPANY_NAME], rs.getString("COMPANY_NAME"));
            			out[Out_ADDRESS]=compareString(nextLine[ADDRESS], rs.getString("ADDRESS"));
            			out[Out_NEIGHBORHOOD]=compareString(nextLine[NEIGHBORHOOD], rs.getString("NEIGHBORHOOD"));
            			out[Out_CITY]=compareString(nextLine[CITY], rs.getString("CITY"));
            			out[Out_STATE]=compareString(nextLine[STATE], rs.getString("STATE"));
            			out[Out_ZIP]=compareString(nextLine[ZIP], rs.getString("ZIP"));
            			out[Out_PHONE]=compareString(nextLine[PHONE], rs.getString("PHONE"));
            			out[Out_FAX]=compareString(nextLine[FAX], rs.getString("FAX"));
            			out[Out_URL]=compareString(nextLine[URL], rs.getString("URL"));
            			out[Out_EMAIL]=compareString(nextLine[EMAIL], rs.getString("EMAIL"));
            			boolean flag=false;
            			for (int i = 1; i < out.length; i++)
							if (out[i]!=null) {
								flag=true;
								break;
							}
						if(flag)writeLines.add(out);
            			
            		}
            		rs.close();
				}
        		
        		
			}
        }
        U.writeCsvFile(writeLines, "/home/chinmay/Mexico/MexicoDataFiles/Data/Apr/Updated/Chinmay_Update_25_Apr_2020.csv");
        stmt.close();
	}
	private String compareString(String fileString,String dbString) {
		if (dbString==null&&fileString==null)return null;
		if (fileString.trim().length()==0)return null;
		if (dbString==null&&fileString!=null) return fileString;
		if (fileString==null&&dbString!=null) return null;
		if (!dbString.equals(fileString))return fileString+";;"+dbString;
		return null;
	}
	int ID=0;
	int SIC_SUB=4;
	int SPANISH_PRODUCT_DESC=6;
	int COMPANY_NAME=7;
	int ADDRESS=8;
	int NEIGHBORHOOD=9;
	int CITY=10;
	int STATE=11;
	int ZIP=12;
	int PHONE=13;
	int FAX=14;
	int URL=15;
	int EMAIL=16;
	int CONTACT_PERSON=17;
	int TITLE=18;
	int ANNUAL_SALES=19;
	int EMP_COUNT=20;
	int YEARS_IN_BIZ=21;
	int LATITUDE=22;
	int LONGITUDE=23;
	int SOURCE_URL=24;
	int ID_MATCHED=26;
	
	int Out_ID=0;
	int Out_SIC_SUB=1;
	int Out_COMPANY_NAME=2;
	int Out_ADDRESS=3;
	int Out_NEIGHBORHOOD=4;
	int Out_CITY=5;
	int Out_STATE=6;
	int Out_ZIP=7;
	int Out_PHONE=8;
	int Out_FAX=9;
	int Out_URL=10;
	int Out_EMAIL=11;
	int Out_STATUS=12;
	int Out_CONTACT_PERSON=13;
	int Out_TITLE=14;
	int Out_LATITUDE=15;
	int Out_LONGITUDE=16;
	int Out_YEARS_IN_BIZ=17;
	int Out_EMP_COUNT_MIN=18;
	int Out_EMP_COUNT_MAX=19;
	int Out_ANNUAL_SALES_VOL_MIN=20;
	int Out_ANNUAL_SALES_VOL_MAX=21;
	
	
	
}
