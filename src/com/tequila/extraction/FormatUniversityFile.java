package com.tequila.extraction;

import java.io.StringWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class FormatUniversityFile {
	private static String sicCode="8211";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		try {
			String fileName="/home/mypremserver/MexicoCache/Cache/UniversityData/Colegio Euro Texcoco.csv";
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			List<String[]> records=U.readCsvFile(fileName);
			int i=0;
			for (String[] record : records) {
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(record[0]),U.toTitleCase(record[1]),U.toTitleCase(record[2]),U.toTitleCase(record[3]),U.toTitleCase(record[4]),record[5],U.formatNumbersAsCode(record[6]),U.formatNumbersAsCode(record[7]),record[8],record[9],U.toTitleCase(record[10]),U.toTitleCase(record[11]),"",record[12],record[13],record[14],record[15],record[16],U.getTodayDate()};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_INSERT.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
