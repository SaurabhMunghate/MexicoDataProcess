package com.priti.demo.extractareacode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.U;

public class ExtractAreaCodeFromCodigoSite {
	
	public static void main(String[] args) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("/home/chinmay/MexicoCache/Codigo_Postal_Mexico_All_State_City_AreaCode_17_09_2018.csv"));
		String [] header = {"Sr.No.","City","State","Area_Code"};
		writer.writeNext(header);
		String html = U.getHTML("https://codigo-postal.co/mexico/");
		html = TranslateEnglish.convertToEnglish(html);
		//U.log(U.getSectionValue(html, "<h1 itemprop=\"headline\">", "Right column"));
		String [] allState = U.getValues(html, "<li><a title=\"Codigo ", "><");
		int stateCount =0 ;
		int areacount =0 ;
		U.log(allState.length);
		ArrayList<String[]> codeList = new ArrayList<>();
		for(String state : allState){
			String stateName = U.getSectionValue(state, "Postal ", "\"");
			//U.log(state);
			if(stateName.equalsIgnoreCase("Ciudad de Mexico"))stateName = "Mexico City";
			if(stateName.equals("Estado de Mexico"))stateName = "Mexico State";
			//U.log("State Url : "+U.getSectionValue(state, "href=\"", "\""));
			String StateHtml = U.getHTML(U.getSectionValue(state, "href=\"", "\""));
			
			StateHtml =  TranslateEnglish.convertToEnglish(StateHtml);
			String [] allCities = U.getValues(StateHtml, "<li><a title=\"Codigo ", "><");
			U.log(allCities.length);
			for(String city : allCities){
				//U.log(city);
				String cityName = U.getSectionValue(city, "Postal ", "\"");
				U.log("City Url : "+U.getSectionValue(city, "href=\"", "\""));
				String cityHtml = U.getHTML(U.getSectionValue(city, "href=\"", "\""));
				cityHtml =  TranslateEnglish.convertToEnglish(cityHtml);
				String areaCode = U.getSectionValue(cityHtml, "La clave lada (codigo telefonico de area) es ", "</");
				System.out.println("Area Code : "+areaCode);
				if(areaCode.length()>0){
					areacount++;
					codeList.add(new String[] {areacount+"",cityName,stateName,areaCode});
				}
			}
			
			stateCount++;
			//break;
		}
		writer.writeAll(codeList);
		writer.close();
		U.log(areacount);
	}

}
