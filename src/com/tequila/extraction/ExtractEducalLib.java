package com.tequila.extraction;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractEducalLib {
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		try {
			int i=0;
			ArrayList<String>allData=new ArrayList<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String name="Librerias Educal";
			String sicCode="5942";
			String sicdetails[]=Sic.sicInfo(sicCode);
			String mainUrl="https://www.educal.com.mx";
			String extracturl="https://www.educal.com.mx/librerias";
			String baseHtml=U.getPageSource(extracturl);
			String mainSec=U.getSectionValue(baseHtml, "<div class=\"whole-line dsk-locations-column\">", "<div class=\"col-md-6 dsk-map-column\">");
			String listings[]=U.getValues(mainSec, "<a", "</a>");
			for (String s : listings) {
				allData.add(s);
			}
			String optionSec=U.getSectionValue(baseHtml, "<div class=\"whole-line dsk-column-topic\">", "</select>");
			String datas[]=U.getValues(optionSec, "<option value=\"", "\"");
			U.log(datas.length);
			HashSet<String> branchNameSet=new HashSet<>();
			for (int j = 0; j < datas.length; j++) {
				baseHtml=U.getPageSource(extracturl+"/"+datas[j]);
				U.log(extracturl+"/"+datas[j]);
				mainSec=U.getSectionValue(baseHtml, "<div class=\"whole-line dsk-locations-column\">", "<div class=\"col-md-6 dsk-map-column\">");
				listings=U.getValues(mainSec, "<a", "</a>");
				for (String listing : listings) {
					//U.log(listing);
					String branchName=U.getSectionValue(listing, "<span class=\"mayus\">", "</span>").trim();
					if (!branchNameSet.add(branchName)) {
						continue;
					}
					String email=U.getSectionValue(listing, "Correo:", "<br/>").trim();
					String telephone=U.getSectionValue(listing, "Teléfono:", "<br/>");
					String mapUrl=mainUrl+U.getSectionValue(listing, "href=\"", "\"");
					String mapHtml=U.getPageSource(mapUrl);
					U.log(mapUrl);
					String latLonsec=U.getSectionValue(mapHtml, "new google.maps.LatLng(", "),");
					String latlon[]= {"",""};
					String add[]= {"","","","",""};
					if (latLonsec!=null) {
						latlon=latLonsec.split(",");
						add=U.getGoogleAddress(latlon);
						U.log(Arrays.toString(add));
					}
					add[0]=U.getSectionValue(listing, "</div>", "<br/>").trim();
					add[3]=datas[j].replaceAll("\\d+|-", "").trim();
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],U.formatNumbersAsCode(telephone),"",mainUrl,email,"","","","","",latlon[0],latlon[1],extracturl,U.getTodayDate()};
					writer.writeNext(out);
					//break;
				}
			}
			FileUtil.writeAllText(U.getCachePath()+name+".csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
