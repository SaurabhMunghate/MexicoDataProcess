package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractCinemex {
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		String baseUrl="https://cinemex.com/cines/";
		try {
			String sicSub="7832";
			int counter=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			HashSet<String>compnayName=new HashSet<>();
			String baseHtml=U.getHTML(baseUrl);
			String mainSec=U.getSectionValue(baseHtml, "<select id=\"cinemas-select-city\">", "</select>");
			String cityDatas[]=U.getValues(mainSec, "<option value=\"", "\"");
			String []sicDetails=Sic.sicInfo(sicSub);
			for (String cityData : cityDatas) {
				U.log(baseUrl+cityData);
				String cityHtml=U.getHTML(baseUrl+cityData);
				String listings[]=U.getValues(cityHtml, "<li id=\"cinema-item", "</li>");
				for (String lisiting : listings) {
					String address=StringEscapeUtils.unescapeHtml3(U.getSectionValue(lisiting, "data-address=\"", "\""));
					String latLonSec=U.getSectionValue(lisiting, "&query=", "&query_");
					String latlon[]=latLonSec.split(",");
					String Name=StringEscapeUtils.unescapeHtml3(U.removeHtml("<a href="+U.getSectionValue(lisiting, "<a href=", "</a>")));
					if (!compnayName.add(address)) {
						continue;
					}
					String detailUrl=U.getSectionValue(lisiting, "<a href=\"", "\"");
					U.log("https://cinemex.com"+detailUrl);
					String detailHtml=U.getHTML("https://cinemex.com"+detailUrl);
					//U.log(detailHtml);
					String telePhone=U.getSectionValue(detailHtml, "<span itemprop=\"telephone\">", "</span>");
					U.log(telePhone);
					U.log(Name);
					U.log(address);
					U.log(counter);
					U.log(Arrays.toString(latlon));
					//break;
					String out[]= 
						{
							""+counter++,//id
							U.toCapitalizeCase(sicDetails[0]),
							U.toCapitalizeCase(sicDetails[1]),
							U.toCapitalizeCase(sicDetails[3]),
							U.toCapitalizeCase(sicDetails[4]),
							U.toCapitalizeCase(sicDetails[5]),
							U.toCapitalizeCase(sicDetails[6]),
							U.toCapitalizeCase(Name),
							U.toCapitalizeCase(address),
							U.toCapitalizeCase(""),
							U.toCapitalizeCase(""),
							U.toCapitalizeCase(""),
							U.toCapitalizeCase(""),
							U.formatNumbersAsCode(telePhone),
							null,
							"https://cinemex.com",
							null,
							null,
							null,
							null,
							null,
							null,
							latlon[0],
							latlon[1],
							baseUrl,
							U.getTodayDate()
						};
					writer.writeNext(out);
				}
				//break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Cinemex.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
