package com.chinmay.test;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class MatchOtherFieldInFile {
	public static void main(String[] args) {
		String updatedRecordsFileName="/home/chinmay/Mexico/MexicoDataFiles/Db/Mexico_DB_Update_Report_30_04_2019.csv";
		String companyNameUpdatedFile="/home/chinmay/Mexico/MexicoDataFiles/FilesMayBeWrong/Update_May_02.csv";
		List<String[]> updatedRecord=U.readCsvFileWithoutHeader(updatedRecordsFileName);
		List<String[]>companyNameRecord=U.readCsvFileWithoutHeader(companyNameUpdatedFile);
		HashSet<String> IDs=new HashSet<>();
		for (String[] id : companyNameRecord) {
			IDs.add(id[0]);
		}
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		String []header= {"ID","OLD_COMPANY_NAME","UPDATED_COMPANY_NAME"};
		writer.writeNext(header);
		for (String[] updatedRecordData : updatedRecord) {
			if (IDs.contains(updatedRecordData[0])) {
				U.log("Id:\t"+updatedRecordData[0]);
				boolean flag=false;
				for (int i = 0; i < updatedRecordData.length; i++) {
					if (i<2||i==4||i==5||i>updatedRecordData.length-4)continue;
					if (updatedRecordData[i]!=null&&!updatedRecordData[i].isEmpty()) {
						flag=true;
						break;
					}
				}
				if (flag==false) {
					String out[]= {updatedRecordData[0],updatedRecordData[4],updatedRecordData[5]};
					writer.writeNext(out);
				}
			}
		}
		try {
			FileUtil.writeAllText("/home/chinmay/Mexico/MexicoDataFiles/Db/Mexico_DB_CaseChange_"+U.getTodayDateWith()+".csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
//		
	}
}
