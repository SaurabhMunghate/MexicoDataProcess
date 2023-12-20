package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractCemexMexico {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5032";//3273 Planta Concreto//3241 Planta Cemento//Of. Comerciales//Change sic code 5032 Distribuidores  Construrama//1442 PLANTA AGREGADOS//7999 Centro Comunitario
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String rawFileName="/home/chinmay/MexicoCache/Cache/cemexMexico/locations_brands_JS.js";
			String rawFile=FileUtil.readAllText(rawFileName);
			String dataSec=U.getSectionValue(rawFile, "\"localsalescontact\": [", "]");
			String listings[]=U.getValues(dataSec, "{", "}");
			U.log(listings.length);
			int i=0;
			for (String listing : listings) {
				String type=U.getSectionValue(listing, "\"LocationType\": \"", "\"");
				if (!type.contains("Distribuidores")) {
					continue;
				}
				String srcUrl="https://www.cemexmexico.com/acerca-de-cemex/donde-estamos-ubicados";
				String mainUrl="https://www.cemexmexico.com";
				U.log(listing);
				String name=U.getSectionValue(listing, "\"Name\": \"", "\"");
				U.log(name);
				String add[]= {"","","","",""};
				add[0]=U.getSectionValue(listing, "\"Address\": \"", "\"");
				add[1]="";
				add[2]=U.getSectionValue(listing, "\"City\": \"", "\"");
				add[3]=U.getSectionValue(listing, "\"States\": \"", "\"");
				add[4]=U.getSectionValue(listing, "\"PostalCode\": \"", "\"");
				String phone=U.getSectionValue(listing, "\"Phone\": \"", "\"");
				String lat=U.getSectionValue(listing, "\"lat\": \"", "\"");
				String lng=U.getSectionValue(listing, "\"lng\": \"", "\"");
				if (dupliData.add((name+add[0]).toLowerCase())) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,"","",null,null,null,null,null,lat,lng,srcUrl,U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"CemexCsv/Cemex_Distribuidores.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
