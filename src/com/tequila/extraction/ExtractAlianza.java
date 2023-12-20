package com.tequila.extraction;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractAlianza {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="6061";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			int i=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="https://cajasalianza.coop/sucursales/";
			String baseHtml=U.getHTML(baseUrl);
			String dataSec=U.getSectionValue(baseHtml, "var maplistScriptParamsKo", "</script>").replace("categories\":[{\"title\":\"", "categories\":[\"title\":\"");
			String listings[]=U.getValues(dataSec, "{\"title\"", "\"pinColor");
			U.log(listings.length);
			for (String listing : listings) {
				U.log("----------------------------------------");
				
				U.log(listing);
				String addressSec=U.getSectionValue(listing, "<div class='address'>", "<\\/div>");
				U.log(addressSec);
				String streetNeigh=U.getSectionValue(addressSec, "<p>", "\\t");
				String zip=U.getSectionValue(addressSec, "\\t", "<br");
				if (streetNeigh==null) {
					String tempSec=U.getSectionValue(addressSec, "<p>", "<br");
					streetNeigh=tempSec==null?U.getSectionValue(addressSec, "<p>", "<\\/p>"):U.getSectionValue(addressSec, "<p>", "<br");
					zip=Util.match(addressSec, "\\d{4,5}");
					
				}
				streetNeigh=StringEscapeUtils.unescapeJava(streetNeigh.replaceAll(", S.L.P.", "").replace("\\nCol.", "\\n,Col.").replace(" B Col. ", " B, Col. ").replace(", Locales", "; Locales").replace("s/n Centro", "s/n,Centro").replace("#25 Col", "#25,Col").replace("102 Col", "102,Col").replace("Mina col", "Mina,col"));
				U.log(streetNeigh);
				String streetAdd[]=streetNeigh.split(",");
				U.log(streetAdd.length);
				U.log(zip);
				String companyLat=U.getSectionValue(listing, "\"latitude\":\"", "\"");
				String companyLng=U.getSectionValue(listing, "\"longitude\":\"", "\"");
				String cityStateSec=U.getSectionValue(listing, "<br \\/>", "<br \\/>");
				U.log(cityStateSec);
				if (cityStateSec==null) {
					cityStateSec="";
				}
				String cityState[]=cityStateSec.replace("\\n", "").replace("\\t", ",").split(",");
				if (cityState.length!=2) {
					cityState=new String[] {StringEscapeUtils.unescapeJava(cityStateSec),StringEscapeUtils.unescapeJava(cityStateSec)};
				}
				if (streetAdd.length<2) {
					streetAdd=new String[] {StringEscapeUtils.unescapeJava(addressSec),StringEscapeUtils.unescapeJava(addressSec)};
				}
				U.log("-=-=--="+cityState.length);
			U.log(addressSec);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase("Federación de Cajas Populares Alianza SC de RL de CV"),U.toTitleCase(StringEscapeUtils.unescapeJava(streetAdd[0])),U.toTitleCase(StringEscapeUtils.unescapeJava(streetAdd[1])),U.toTitleCase(cityState[0]),U.toTitleCase(cityState[1]),zip,null,null,"https://cajasalianza.coop","atencionasocios@cajasalianza.coop",null,null,null,null,null,companyLat,companyLng,baseUrl,U.getTodayDate(),StringEscapeUtils.unescapeJava(addressSec).trim()};
				writer.writeNext(out);
//				if (streetAdd.length==0) {
//					break;
//				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Alianza_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
