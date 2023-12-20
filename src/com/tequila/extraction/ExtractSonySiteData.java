package com.tequila.extraction;

import java.io.StringWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractSonySiteData {
	public static void main(String[] args) {
		new ExtractSonySiteData().formatData();
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	void formatData(){
		try {
			String fileName="/home/mypremserver/MexicoCache/Cache/Mahle.csv";
			List<String[]>inputData=U.readCsvFile(fileName);
			
			int counter=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			for (String[] input : inputData) {
				if (input[0].equals("ID")) {
					continue;
				}
				String []sicDetails=Sic.sicInfo(input[4]);
				String out[]= 
					{
						""+counter++,//id
						U.toCapitalizeCase(sicDetails[0]),
						U.toCapitalizeCase(sicDetails[1]),
						U.toCapitalizeCase(sicDetails[3]),
						U.toCapitalizeCase(sicDetails[4]),
						U.toCapitalizeCase(sicDetails[5]),
						U.toCapitalizeCase(sicDetails[6]),
						U.toCapitalizeCase(input[7]),
						U.toCapitalizeCase(input[8]),
						U.toCapitalizeCase(input[9]),
						U.toCapitalizeCase(input[10]),
						U.toCapitalizeCase(input[11]),
						U.toCapitalizeCase(input[12]),
						U.formatNumbersAsCode(input[13]),
						input[14],
						input[15].toLowerCase(),
						input[16].toLowerCase(),
						input[17],
						input[18],
						input[19],
						input[20],
						input[21],
						input[22],
						input[23],
						input[24],
						U.getTodayDate()
					};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "INSERT.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
