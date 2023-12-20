package com.tequila.extraction;

import java.io.StringWriter;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractPanasonic {
	public static void main(String[] args) {
		extractData();
	}

	private static void extractData() {	
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String postUrl="https://www.panasonic.com/wtb/php/wtb_off_search_address.php";
			HashSet<String> stateSet=MXStates.getAllStates();
			String header[]= {"Category","Name","Address","Neighbourdood","City","State","Zip","Phone","Website","Email","Latitude","Longitude","ADDRESS"};
			writer.writeNext(header);
			HashSet<String> uniqueKey=new HashSet<>();
			for (String state : stateSet) {
				U.log(state);
				String formData="wtb_type=0&country_code=mx&language_code=es&area_name=Mexico&category1=0&category2=0&distance_value=50&distance_type=0&unit_label=km&address="+state+"&suggest_flag=0&link_flag=0&suggest_select_lat=&suggest_select_lng=&suggest_select_city=";
				String dataJson=U.sendPostRequest(postUrl, formData);
				U.log(dataJson);
				String datavalues[]=U.getValues(dataJson, "\"wtb_type\"", "}");
				U.log(datavalues.length);
				
				for (String dataSec : datavalues) {
					U.log(dataSec);
					String zip=U.getSectionValue(dataSec, "\"zipcode\":\"", "\"");
					U.log(zip);
					
					String lat=U.getSectionValue(dataSec, "\"lat\":\"", "\"");
					String lng=U.getSectionValue(dataSec, "\"lng\":\"", "\"");
					String name=StringEscapeUtils.unescapeJson(U.getSectionValue(dataSec, "\"name\":\"", "\","));
					String address=StringEscapeUtils.unescapeJava(U.getSectionValue(dataSec, "\"adress\":\"", "\","));
					if (!uniqueKey.add((name+address).toLowerCase())) {
						continue;
					}
					String website=U.getSectionValue(dataSec, "\"website\":\"", "\",");
					String email=U.getSectionValue(dataSec, "\"email\":\"", "\"");
					String phone=U.getSectionValue(dataSec, "\"phone\":\"", "\"");
					U.log(email);
					U.log(phone);
					U.log(name);
					if (email==null) email="";
					if (phone==null) phone="";
					if (website==null) website=""; else website=StringEscapeUtils.unescapeJson(website);
					String category=U.getSectionValue(dataSec, "\"shopType\":\"", "\",");
					category=StringEscapeUtils.unescapeJava(category);
					U.log(category);
					U.log(address);
					U.log(website);
					String add[]=U.getAddress(U.toTitleCase(address));
					
					String out[]= {category,name,add[0],add[1],add[2],add[3],zip,phone,website,email,lat,lng,address};
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Panasonic_Distributor.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
