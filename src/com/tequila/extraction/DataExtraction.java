package com.tequila.extraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

import org.apache.commons.lang3.StringEscapeUtils;

import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class DataExtraction {

	static String header[] = {"INDEX","BRANCH_NAME","STREET","COLONIA","CITY","STATE","ZIP","PHONE","LATITUDE","LONGITUDE","BRANCH_URL","EMAIL"};
	public static void main(String[] args) throws IOException {
		//System.setProperty("jsse.enableSNIExtension", "false");
		//readLines.add(header);
		String homeHtml=U.getHTML("https://jeefocus.com/app/#page=home");
		String mainSec=U.getSectionValue(homeHtml, "<span>Prepare & Practice</span>", "<li class=\"nav-menu-header\">");
//		U.log(mainSec);
		String categoriesVals[]=U.getValues(mainSec, "<li>", "</li>");
		for (String categories : categoriesVals) {
			//U.log(categories);
			String catUrl=U.getSectionValue(categories, "<a href=\"", "\"");
			String catName=U.getSectionValue(categories, "<span class=\"menu-text\">", "</span>");
			U.log(catUrl);
			U.log(catName);
			String catHtml=U.getHTML(catUrl);
			String topicsListSec=U.getSectionValue(catHtml, "class=\"topic-list\"", "<div class=\"col-sm-3 web-right-section\">");
			U.log(topicsListSec);
			break;
		}
//
		String url="https://jeefocus.com/app/index.php?option=com_user&task=getQuestionSet";
		String payload="data%5BfbUserId%5D=&data%5BuserNameLong%5D=&data%5BisPaid%5D=false&data%5BmembershipStatus%5D=&data%5BsectionId%5D=3&data%5BcategoryId%5D=63&data%5BsubCatId%5D=0&data%5BsectionName%5D=Mathematics&data%5BcategoryName%5D=Sets%2C+Relations+and+Functions&data%5BpageTitle%5D=&data%5BfeedType%5D=4&data%5BcurrentQsId%5D=&data%5BnoOfQsLoaded%5D=0&data%5BnoOfQsToLoad%5D=592&data%5BtempQsCount%5D=0&data%5BnumOfTotalQsInCat%5D=592&data%5BnumOfQsAnsInPage%5D=0&data%5BnumOfQsAnsWrong%5D=0&data%5BnumOfQsAnsRight%5D=0&data%5BtotalAnsQs%5D=0&data%5BtotalFreeQs%5D=0&data%5BtopicFreeQs%5D=30&data%5BforceFlag%5D=false&data%5BforceActionShare%5D=0&data%5BforceActionInstallApp%5D=0&data%5BforceActionInviteUsers%5D=0&data%5BforcePayment%5D=false&data%5BFreeQsForcePayment%5D=false&data%5BnextBlockType%5D=question&data%5BchartLoaded%5D=true";
		//U.log(sendPostRequestAcceptJson(url, payload));
		//		extractFarmatodoPharmacia();
//		extractFarmaciaModerna();
//		extractGnc();
//		extractHarbalife();
		//extractFedel();
	}
	public static String sendPostRequestAcceptJson(String requestUrl, String payload) throws IOException {
		String fileName=U.getCache(requestUrl+payload);
		File cacheFile = new File(fileName);

		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		StringBuffer jsonString = new StringBuffer();
	    try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
	        connection.setRequestProperty("Referer", "https://jeefocus.com/app/");
	        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        connection.setRequestProperty("Cookie", "8d22a08437c5021cf0ddc7eb2111ebde=hsd7q0v1luqr4mlt3dk1pbpeb6; 8d22a08437c5021cf0ddc7eb2111ebde=hsd7q0v1luqr4mlt3dk1pbpeb6; _ga=GA1.2.1280517707.1615467512; _gid=GA1.2.1069480578.1615467512; G_ENABLED_IDPS=google");
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	            throw new RuntimeException(e.getMessage());
	    }
	    if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, jsonString.toString());
	    return jsonString.toString();
	}
	static List<String[]> readLines = new ArrayList<>();
	

	static void extractFedel() throws IOException{
		String branchUrl ="http://fedele.com.mx/2011/donde-comprar/";
		String html = U.getHTML(branchUrl);
		String section = U.getSectionValue(html, "<div id=\"wpsl-result-list\">", "</ul>");
		String [] sections = U.getValues(section, "<li data-store-id=", "</li>");
		U.log(sections.length);
		for(String sec : sections){
			String title = U.getSectionValue(sec, "<strong>", "</strong>");
			if(title != null){
				title = title.replaceAll("<.*?>", "").trim();
				U.log(title);
			}
			
		
		}
	}
	static void extractHarbalife() throws IOException{
		
		String branchUrl = "http://oportunidad.herbalife.com.mx/centros-de-venta";
		String html = U.getHTML(branchUrl);
		html = html.replace("<td valign=\"middle\"><p>&nbsp;</p>", "");
		String[] sections = U.getValues(html, "<td valign=\"middle\">", "</td>");
		U.log(sections.length);
		int i = 0;
		for(String section : sections){
			section = StringEscapeUtils.unescapeHtml4(section);
			String title = U.getSectionValue(section, "<strong>", "</strong>").replaceAll("<.*?>", "");
//			U.log(section);
//			U.log(title);
//			U.log("");
			String street = Util.match(section, "<span style=\"font-family.*?>(.*?)<a ",1);
			street = street.replaceAll("(Horario: )?L-V \\d+:\\d+ a \\d+:\\d+ y S \\d+:\\d+ - \\d+:\\d+(</br>|<br />)|L-V 09:00 a 18:00 y S 09:00 - 14:00<br/>|L-V 10:00 a 19:00 y S 09:00 - 14:00<br/>", "");
//			U.log(street);
			String zip= Util.match(street, "<br\\s?/>\\s?C\\.?P\\.?\\s?(\\d{5}),?\\.?\\s?<br\\s?/>(.*?)<br\\s?/>",1);
			if(zip == null){
				zip= Util.match(street, "<br\\s?/>(.*?)\\s?C\\.?P\\.?\\s?(\\d{5}),?\\.?\\s?<br\\s?/>(.*?)<br\\s?/>",2);
				if(zip == null)
					zip= Util.match(street, "C\\.?P\\.?\\s?(\\d{5})",1);
			}
			String city = null;
			String state = null;
			
			String[] add = null;
			if(zip != null){
				String str =  street.substring(0, street.indexOf(zip));
				str = str.replaceAll("\\s?C\\.?P\\.?", "").trim().replaceAll("<br\\s?/>$|</br>$", "").trim().replaceAll(",$", "");
//				U.log(">>"+str);
				
				add = SplitNeighborhoodFromAdd.splitColonia(str);

				String str1 = street.substring(street.indexOf(zip)+zip.length());
				str1 = str1.replaceAll("<br />|^,|^\\.|01 <br />|<br/>|</br>", "").trim().replaceAll("\\.$", "").trim();
//				U.log(">>"+str1);
				if(!str1.isEmpty()){					

					String vals[] = str1.split(",");
					if(U.isState(vals[vals.length-1])){
						state = U.matchState(vals[vals.length-1]);
//						U.log("State :::"+state);
						str1 = str1.replaceAll(state+"$", "").trim().replaceAll(",$", "");
						city = str1;
//						U.log("City :: "+city);
					}
				}
				
			}else{
//				U.log(street);
				add = SplitNeighborhoodFromAdd.splitColonia(street);
			}
			if(add != null){
				add[0] = add[0].replaceAll("<br\\s?/>$", "").trim().replaceAll(",$", "").replaceAll("<.*?>", ",").replaceAll("\\s{2}", " ")
						.replaceAll(",,|, ,", ",").replaceAll(",$", "");
				add[1] = add[1].replaceAll("<br\\s?/>$", "").trim().replaceAll(",$", "").replaceAll("<.*?>", ",").replaceAll("\\s{2}", " ")
						.replaceAll(",,|, ,", ",").replaceAll(",$", "");
//				U.log("Street ::"+add[0]);
			}
			if(zip == null)zip = "";
			
			String email = U.getSectionValue(section, "href=\"mailto:", "\">");
			if(email == null){
				email = U.getSectionValue(section, "href=\"", "\">");
			}
			U.log(email);
			
			readLines.add(new String[]{
					""+(i++), "Herbalife "+title, add[0], add[1], city, state, zip, "", "", "",branchUrl,email
			});
			
		}//eof for
		U.writeCsvFile(readLines, "/home/glady/GeoCode/Herbalife.csv");
	}
	static void extractGnc() throws IOException{
		List<String[]> readLines = new ArrayList<>();
		readLines.add(header);
		
		String branchUrl = "https://gnc.com.mx/tiendas-gnc";
		
		String html = U.getHTML(branchUrl);
		
		String section = U.getSectionValue(html, "locationList\": {", "</script>");
		
		String [] sections = U.getValues(section, "location_id", "}");
		U.log(sections.length);
		int i = 0;
		for(String sec : sections){
			sec = StringEscapeUtils.unescapeHtml4(sec);
			sec = TranslateEnglish.convertUnicodeToSpanish(sec);
			U.log(sec);
			String title = U.getSectionValue(sec, "title\":\"", "\"").trim();
			String street = U.getSectionValue(sec, "street\":\"", "\"").trim()
					.replace(" S\\/n", " S\\n");
			String zip = U.getSectionValue(sec, "zip\":\"", "\"").trim();
			U.log(zip);
			String cityState = U.getSectionValue(sec, "city\":\"", "\"").trim().replace("\\t", ",");
			U.log(cityState);
			String vals[] = {"",""};
			if(cityState.contains(",")){
				vals = cityState.split(",");
			}
			if(vals.length > 2){
				vals = street.split(",");
				street = cityState;
			}
			String data[] = SplitNeighborhoodFromAdd.splitColonia(street);
			
			street = data[0];
			String colonia = data[1];

			U.stripAll(vals);
			
			if(U.isState(vals[1]))
				vals[1] = U.findState(vals[1]);
			String phone = U.getSectionValue(sec, "phone\":\"", "\"");
			if(phone == null)phone = "";

			
			String lat = U.getSectionValue(sec, "latitude\":\"", "\"").trim();
			String lng = U.getSectionValue(sec, "longitude\":\"", "\"").trim();
			U.log(title+"\t"+street+"\t\t"+vals[0]+"\t"+vals[1]);
			readLines.add(new String[]{
					""+(i++), title, street, colonia, vals[0], vals[1], zip, U.formatNumbersAsCode(phone),lat,lng,branchUrl 	
			});
		}
		
		U.writeCsvFile(readLines, "/home/glady/GeoCode/Gnc.csv");
	}
	
	static void extractFarmaciaModerna() throws IOException{
		List<String[]> readLines = new ArrayList<>();
		readLines.add(header);
		
		String branchUrl = "http://www.farmaciamoderna.com.mx/sucursales.html";
		String html = U.getHTML(branchUrl);
		List<String> sectionList = new ArrayList<>();
		
		String[] sections = U.getValues(html, "<div class=\"col-md-3 col-sm-3 col-xs-6\"", "</div>");
		for(String sec : sections)
		sectionList.add(sec);
		U.log(sections.length);
		
		sections = U.getValues(html, "<div class=\"col-sm-3 col-md-3 col-xs-6\"", "</div>");
		for(String sec : sections)
		sectionList.add(sec);		
		U.log(sections.length);
		
		sections = U.getValues(html, "<div class=\"col-12 col-md-3\"", "</div>");
		for(String sec : sections)
		sectionList.add(sec);
		U.log(sections.length);
		
		U.log(sectionList.size());
		int i = 0;
		for(String section : sectionList){
//			U.log(section);
			String [] innerSection = U.getValues(section, "<h3 style", "/p>");
			for(String sec : innerSection){
//				U.log(sec);
				String branchName = U.getSectionValue(sec, ">", "</h3>");
				String address = U.getSectionValue(sec, "<p>", "<br>");
				if(address == null)
					address = U.getSectionValue(sec, "uppercase;\">", "<br>");
				String phone = U.getSectionValue(sec, "<br>", "<");
				
				branchName = "Farmacias Moderna "+U.toTitleCase(branchName.trim());
				address = U.toTitleCase(address.trim());
				String[] add = SplitNeighborhoodFromAdd.splitColonia(address);
				if(phone != null){
					phone = phone.replaceAll("T(E|e)(L|l)\\.?", "").trim();
				}else phone = "";
				
				U.log("BrName :"+branchName);
				U.log("Address :"+address);
				U.log("Add & Col :"+Arrays.toString(add));
				U.log("Phone :"+phone);
				String [] data ={""+(i++),
						branchName, U.toTitleCase(add[0].trim()), U.toTitleCase(add[1].trim()), "","",
						"",phone,"","",branchUrl
				};
				readLines.add(data);
			}
//			break;
		}
		U.writeCsvFile(readLines, "/home/glady/GeoCode/Farmacias Moderna.csv");
	}
	
	static void extractFarmatodoPharmacia() throws IOException{
		List<String[]> readLines = new ArrayList<>();
		readLines.add(header);
		String branchUrl = "https://www.farmatodo.com.mx/sucursales.php";
		String html = U.getHTML(branchUrl);

		String section = U.getSectionValue(html, "<div id=\"contenido_front\">", " id=\"footer\">");
//		U.log(section);
		String [] sections = U.getValues(section, "<strong class=", "</td>");
		int i = 0;
		for(String sec : sections){
//			U.log(sec);
			String branchName = U.getSectionValue(sec, ">", "</strong");
			String street = U.getSectionValue(sec, "<br />", "<br />").trim();
			String colonia = U.getSectionValue(sec, "<br />Colonia:", "<br />");
			String cityStateZip = U.getSectionValue(sec, "<br />CP:", "<br />");
			String zip = null, state = null, city = null;
			if(cityStateZip != null){
				zip  = Util.match(cityStateZip, "\\d{4,5}\\.");
				if(zip != null && !zip.trim().isEmpty()){
					cityStateZip = cityStateZip.replace(zip, "");
					zip = zip.replace(".", "").trim();
					String vals[] = cityStateZip.split(",");
					if(vals.length == 2){
						city = vals[0].trim();
						state = vals[1].trim();
					}
				}
			}
			
			if(state != null){
				state = U.toTitleCase(state);
				String correctState = U.matchState(state);
				if(correctState.length() < 3){
					correctState = MXStates.getFullNameFromAbbr(state);
					if(correctState == null){
						state = "";
					}
				}else state = correctState;
			}
			String phone = U.getSectionValue(sec, "<br />Tel:", "<br");
			U.log("phone :"+phone);
			if(phone.trim().length() < 7)phone = "";
			phone = phone.replaceAll("01 (55) 55-10-07-16 01$", "01 (55) 55-10-07-16").replaceAll("CC ", "");
			phone = phone.trim().replace("\t", ";").replaceAll("\\s{3,}", ";");
			if(!phone.contains(";") && phone.contains("/")){
				phone = phone.replace("/", ";");
			}
			phone = phone.replace("/", "");
			phone = U.formatNumbersAsCode(phone);
			if(phone.contains(";")){
				String vals[] = phone.split(";");
				for(String val : vals){
					if(val.length()<7)phone = phone.replace(val, "");
				}
			}
			phone = phone.trim().replaceAll(";$", "");

			String mapUrl = U.getSectionValue(sec, "<a href=\"", "\">");
			String mapHtml = U.getHTML(branchUrl+mapUrl);
			String latLngSection = U.getSectionValue(mapHtml, "maps.LatLng(", ");");
			String latLng[] = {"",""};
			if(latLngSection != null){
				latLng = latLngSection.split(",");
				if(latLng.length == 2){
					if(latLng[0].trim().length()< 3 || latLng[1].trim().length()< 3)latLng[0] = latLng[1] = "";
				}
			}
			if(branchName != null){
				branchName = U.toTitleCase(branchName.trim());
				if(!branchName.startsWith("Farmatodo")){
					branchName = "Farmatodo "+branchName;
				}
			}
			String [] data ={""+(i++),
					branchName, U.toTitleCase(street.trim()), U.toTitleCase(colonia.trim()), U.toTitleCase(city.trim()),U.toTitleCase(state.trim()),
					U.toTitleCase(zip.trim()),phone,latLng[0],latLng[1],branchUrl
			};
			readLines.add(data);
			U.log(Arrays.toString(data));
			
//			break;
		}//eof for
		
		U.writeCsvFile(readLines, "/home/glady/GeoCode/Farmatodo_1_New.csv");
	}
}
