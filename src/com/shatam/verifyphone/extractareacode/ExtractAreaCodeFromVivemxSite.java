package com.shatam.verifyphone.extractareacode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractAreaCodeFromVivemxSite {

	public static void main(String[] args) throws IOException {
		String html = U.getHTML("https://www.vivemx.com/mpios/todos.htm");
		String stateUrls [] = U.getValues(html, "class=\"divFloatBig\" align=\"left\"><a href=\"", "\"><ol");
		System.out.println(stateUrls.length);
		ArrayList<String[]> records = new ArrayList<>();
		int index = 0;
		
		String [] header = {"SrNo","STATE","CITY","NEIGHBORHOOD","AREA_CODE","POSTAL_CODE","NEIGHBORHOOD_TYPE"};
		CSVWriter writer= new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/NEIGHBORHOOD_AreaCodeList_From_VIVEMX_20_09_2018.csv"));
		writer.writeNext(header);	
		
		for(String stateUrl : stateUrls){
			stateUrl = "https://www.vivemx.com"+stateUrl;
			U.log(stateUrl);
			String stateHtml = U.getHTML(stateUrl);
			stateHtml = TranslateEnglish.convertToEnglish(stateHtml);
			String StateName = U.getSectionValue(stateHtml, "selected=\"selected\">", "</option>");
			String [] cityUrls = U.getValues(stateHtml, "divFloatBig\" align=\"left\"><a href=\"", "\"><ol");
			U.log(cityUrls.length);
			for(String cityUrl : cityUrls){
				
				cityUrl = "https://www.vivemx.com"+cityUrl;
				U.log("\t"+cityUrl);
				String cityHtml = U.getHTML(cityUrl);
				cityHtml = TranslateEnglish.convertToEnglish(cityHtml);
				String cityName = U.getSectionValue(cityHtml, "<h1>", ",");
				
				String cityPostalHtml = U.getHTML(cityUrl.replace("/mpo/", "/cp/"));
				cityPostalHtml = TranslateEnglish.convertToEnglish(cityPostalHtml);
				System.out.println("City Name : "+cityName);
				
				//----------Neighborhood---------
				String [] neighborhoods = U.getValues(cityPostalHtml, "<tr><td align=\"left\" class=", "</tr>");
				U.log(neighborhoods.length);
				for(String neigh : neighborhoods){
					//System.out.println(neigh);
					String colony = U.getSectionValue(neigh, ".htm\"> &nbsp; ", "</a>");
					String colonyType = Util.match(neigh, "</a></td><td align=\"left\" class=\"cell(.*?)\"> &nbsp; (.*?)</td><",2);
					String areaCode = Util.match(neigh, "\">Lada (.*?)</td>",1);
					String postalCode = Util.match(neigh, "<a href=\"/cp/codigopostal(.*?).htm\">(.*? \\d+)</a><",2);
					System.out.println(colony+" :"+colonyType+" :"+areaCode+" : "+postalCode);
					
					records.add(new String[]{index+"",StateName,cityName,colony,areaCode,postalCode,colonyType});
					index++;
					//break;
				}
				
				//break;
			}
			//break;
		}
		System.out.println(index);
		writer.writeAll(records);	
		writer.close();
	}

}
