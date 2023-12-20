package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLEncoder;

import org.apache.commons.lang3.StringEscapeUtils;
import org.sqlite.util.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractToksRestaturant {
	public static void main(String[] args) {
		extractData();
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			String sicCode="5812";
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="https://www.toks.com.mx/restaurantes";
			String baseHtml=U.getHTML(baseUrl);
			String stateListSec=U.getSectionValue(baseHtml, "<li><em>SELECCIONA TU ESTADO</em>", "<button class=\"get-geo\"");
//			U.log(stateListSec);
			int counter=0;
			String stateLists[]=U.getValues(stateListSec, "<a href=\"", "\"");
			for (String state : stateLists) {
				U.log(URLEncoder.encode(state,"UTF-8"));
				String jsonStateWiseUrl="https://www.toks.com.mx/restaurantes/cargar_sucursales/"+URLEncoder.encode(state,"UTF-8").replace("+", "%20");
				U.log(jsonStateWiseUrl);
				String jsonSateHtml=U.getHTML(jsonStateWiseUrl);
				U.log(jsonSateHtml);
				String restSecs[]=U.getValues(jsonSateHtml, "{\"name\":", "}");
				for (int i = 0; i < restSecs.length; i++) {
					restSecs[i]=StringEscapeUtils.unescapeJava(restSecs[i]);
					String restName=U.getSectionValue(restSecs[i], "\"", "\"");
					String add[]= {"","","","",""};
					add[0]=U.getSectionValue(restSecs[i], "address\":\"", "\"");
					add[1]="";
					add[2]=U.getSectionValue(restSecs[i], "city\":\"", "\"");
					add[3]=U.getSectionValue(restSecs[i], "\"state\":\"", "\"");
					add[4]=U.getSectionValue(restSecs[i], "\"zip\":\"", "\"");
					String telphone=U.getSectionValue(restSecs[i], "\"tel1\":\"", "\"")+";"+U.getSectionValue(restSecs[i], "\"tel2\":\"", "\"");
					String lat=U.getSectionValue(restSecs[i], "\"lat\":\"", "\"");
					String lng=U.getSectionValue(restSecs[i], "\"lng\":\"", "\"");
					String website="https://www.toks.com.mx";
					String out[]= {""+(counter++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase("Restaurantes Toks, S.A. De C.V."),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],U.formatNumbersAsCode(telphone),"",website,"","","","","","",lat,lng,baseUrl,U.getTodayDate()};
					writer.writeNext(out);
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Toks Restaturant.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
