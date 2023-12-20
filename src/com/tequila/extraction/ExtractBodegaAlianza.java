package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractBodegaAlianza {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5921";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	
	private static void extractData() {
		HashSet<String> dupliData=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		try {
			String mainUrl="https://www.bodegasalianza.com";
			String srcUrl="https://www.bodegasalianza.com/descubre-bodegas/sucursales";
			String jsonUrl="https://bodegasalianza.vteximg.com.br/arquivos/storesData.js?v=9996";
			String jsonHtml=U.getHTML(jsonUrl);
			String storesData[]=U.getValues(jsonHtml, "{\"storeID\":", "}}");
			U.log(storesData.length);
			int i=0;
			for (String store : storesData) {
				U.log(store);
				String lat=U.getSectionValue(store, "\"latitude\":", ",");
				String lon=U.getSectionValue(store, "\"longitude\":", ",");
				String addSec=U.getSectionValue(store, "\"address\":\"", "\"");
				String phone=U.getSectionValue(store, "\"phone\":\"", "\"").replace(",", ";");
				String name="Vinos y Licores Bodegas Alianza "+U.getSectionValue(store, "\"name\":\"", "\"");
				U.log(name);
				U.log(addSec);
				String add[]= {"","","","",""};
				if (addSec.contains(",")) {
					add=U.getAddress(addSec);
				}else
					add[0]=addSec;
				
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,mainUrl,null,null,null,null,null,null,lat,lon,srcUrl,U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
//				break;
				
			}
			FileUtil.writeAllText(U.getCachePath()+"Bodegas_Alianza.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
