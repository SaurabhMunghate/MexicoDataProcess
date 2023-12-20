package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.text.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractFresko {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5411";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		String mainUrl="https://www.fresko.com.mx";//5411
		HashSet<String> dupliData=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String srcUrl="https://www.fresko.com.mx/hazlofresko/sucursales";
		String srcUrl1="https://www.fresko.com.mx/hazlofresko/scripts/estados.php";
		int i=0;
		try {
			String allStateHtml=U.getHTML(srcUrl1);
//			U.log(allStateHtml);
			String states[]=U.getValues(allStateHtml.replace("=\"0\">", ""), "value=", ">");
			for (String state : states) {
				String listingsHtml=U.getHTML("https://www.fresko.com.mx/hazlofresko/scripts/sucursales.php?id="+state);
//				U.log(listingsHtml);
				String listings[]=U.getValues(listingsHtml.replace("ue=\"0\"", ""), "value=", ">");
				for (String listing : listings) {
					String storesDatahtml=U.getHTML("https://www.fresko.com.mx/hazlofresko/scripts/sucursal.php?id="+listing);
//					U.log(storesDatahtml);
					String stores[]=U.getValues(storesDatahtml, "{\"nombre\"", "}");
					for (String store : stores) {
						store=StringEscapeUtils.unescapeJson(store);
						String name=U.getSectionValue(store, ":\"", "\"");
						U.log(name);
						String addSec=U.getSectionValue(store, "direccion\":\"", "\",");
						addSec=addSec!=null?addSec.replaceAll("<br><br>.*", ""):"";
						String phone=U.getSectionValue(store, "\"telefono\":\"", "\"");
						phone=phone!=null?phone.replace("|", ";"):"";
						U.log(addSec);
						U.log(phone);
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(addSec),null,null,null,null,U.formatNumbersAsCode(phone),null,mainUrl,null,null,null,null,null,null,null,null,srcUrl,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Fresko_Stores.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
