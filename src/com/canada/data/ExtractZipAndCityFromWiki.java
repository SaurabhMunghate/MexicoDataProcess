package com.canada.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.Header;

import org.apache.commons.lang3.StringEscapeUtils;

import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractZipAndCityFromWiki {

	static boolean generateCsvFile = false;
	public static void main(String[] args) throws IOException {
		extractFromWiki();
	}
	
	static String mainUrl = "https://en.wikipedia.org";
	static String[] header = {"STATE","ZIP","RULAR_TOWNS","ZIP_PREFIX","RULAR_CITY_1","RULAR_CITY_2","RULAR_CITY_3"};
	
	static void extractFromWiki() throws IOException{
		
		String html = U.getHTML("https://en.wikipedia.org/wiki/Postal_codes_in_Canada");
		String section = U.getSectionValue(html, "title=\"Yukon\">YT</a>", "</tbody>");
		String[] zipUrls = U.getValues(section, "<a href=\"", "\" title=");
		for(String zipUrl : zipUrls){
//			U.log(zipUrl);
//			if(zipUrl.endsWith("Y"))
				extractZipAndCities(mainUrl+zipUrl);
//			break;
		}
		List<String[]> writeLines = new ArrayList<>();
		
		writeLines.add(header);
		
		for(RularData rularData : allRularList){
			String state = rularData.getState();
			String pref = rularData.getZipPref();
			List<String> cities = rularData.getRularList();
			List<RularTownData> listTownData = rularData.getRularTownDataList();
			if(listTownData != null && listTownData.size() > 1){
				for(RularTownData townData : listTownData){
					String suf = townData.getZipSuf();
					String townName = townData.getTownOfRular();
					if(cities.size() == 1)
						writeLines.add(new String[]{
								state, pref+" "+suf, townName, pref, cities.get(0) 
						});
					else if(cities.size() == 2)
						writeLines.add(new String[]{
								state, pref+" "+suf, townName, pref, cities.get(0),cities.get(1) 
						});
					else if(cities.size() == 3)
						writeLines.add(new String[]{
								state, pref+" "+suf, townName, pref, cities.get(0),cities.get(1),cities.get(2) 
						});
				}					
			}
		}
		if(generateCsvFile == true){
			U.writeCsvFile(writeLines, Path.CACHE_PATH_FOR_EXTRACTION+"WikiZipCities.csv");
		}
	}
	
	static void extractZipAndCities(String url)throws IOException{
		String html = U.getHTML(url);

		String state = U.getSectionValue(html, "\"Communications in ", "\"");
		String stateAbbr = CanadaStates.getStateFromAbbr(state); 
//		U.log(state+"\t"+stateAbbr);
		
//		urbanExtraction(html, stateAbbr);
		rularExtraction(html, stateAbbr);
	}
	static void urbanExtraction(String html, String stateAbbr){
		String urbanSection = U.getSectionValue(html, "id=\"Urban\">", "id=\"Rural\">");
		String urbanZipSections[] = {};
		if(urbanSection == null)
			urbanSection = U.getSectionValue(html, "<table rules=\"all\"", "<table class=\"multicol\"");
		
		if(urbanSection == null)
			urbanSection = U.getSectionValue(html, "id=\"Urban\">", "id=\"Rural_");
		
		if(urbanSection != null)
			urbanZipSections = U.getValues(urbanSection, "<td ", "</td>");
		
		for(String urbanZipSection : urbanZipSections){
			if(urbanZipSection.contains(">Not assigned<") ||urbanZipSection.contains(">Reserved<"))continue;
			urbanZipSection = StringEscapeUtils.unescapeHtml4(urbanZipSection);
			
			String zip = U.getSectionValue(urbanZipSection, "<b>", "</b>");
			
//			if(!zip.contains("K1Y"))continue; //L6B  L6C L7C L5E L6E L6G L9G L4H L5M L2N L2P L5P L8R  L3S
			 
//			String city = U.getSectionValue(urbanZipSection, state+"\">", "</span>");
			String city = U.getSectionValue(urbanZipSection, "<a href=", "</span>");
/*			U.log(city);
			if(city == null){
				city = U.getSectionValue(urbanZipSection, "<a href=", "</span>");
			}
			U.log(city);
*/
//			U.log(city);
			city = city.trim().replaceAll("</a>", "").replace("Station \"A\"<br />", "")
					.replaceAll("\".*?\">", "").replace("<br />", ", ")
					.replaceAll("\n|<hr />|<p>|<span style=", "")
					.replace("<b>", " (").replace("</b>", ")").trim().replaceAll(",\\s+\\(", " (")
					.replace(" <i>Canadian Forces (MARLANT)</i>", "")
					.trim().replaceAll(",$", "").replaceAll("-, |–, ", "-")
					.replace("(Includes <a href=Île Notre-Dame & <a href=Île Sainte-Hélène)", "").replace("and <a href=", "/ ").replace("Ottawa) ", "Ottawa ").replace("Nepean:", "Nepean /")
					.replaceAll("offices|\\(partly in QC\\)", "").replace(" )", ")").replace("/", " / ")
					.replace("Main (Downtown, area bounded by <a href=Princess St., Sir John A. MacDonald Blvd and Lake Ontario)", "").replace(", <a href=Reddendale", "/ <a href=Reddendale")
					.replaceAll(", <i><a href=Canadian Forces", "")
					.replaceAll("\\s{2,}", " ")
					.replace("(<a href=Army / <a href=RCAF / <a href=CANCOM)< / i>","").replace(") / ", " / ").replace("), <a href=South Gloucester", " (South Gloucester)")
					.trim();
			if(city.startsWith("Ottawa), <a href=")){
				city = city.replace("Ottawa),", "Ottawa (")+")";
			}
			if(city.trim().endsWith("/ <a href=Findlay Creek"))city = city+")";
			
			city = city.replace("( ", "(");
			Set<String> cities = new HashSet<>();
			
//			U.log(city);
			
			if(city.contains(",") && (city.contains("(") || city.contains(")"))){
				String vals [] = city.split(",");
				if(vals.length == 2){
					if(!vals[0].contains("("))
						cities.add(vals[0].trim());
					else{
						vals = city.split("\\(");
						if(vals.length == 2){
							if(vals[1].contains(")")){
								if(vals[1].contains(",")){
									vals[1] = vals[1].replace(")", "");
									String[] str = vals[1].replace("<a href=", "").split(",");
									if(str.length >= 2){
										for(String s : str)	cities.add(s.trim());
									}
								}
								cities.add(vals[0].trim());
							}
						}
					}
					if(vals[1].contains("(")){
						
						String temp = vals[1];
						
						if(!temp.contains("/"))
							temp = vals[1].replace(")", "");
						
						String data [] = temp.split("\\(");
						if(vals.length == 2){
							if(!data[1].contains("/"))
								data[1] = data[1].replaceAll("\\(|\\)|<a href=", "");
							
							cities.add(vals[0].trim()+" "+data[0].trim());
							
							if(data[1].contains("/")){
								data[1] = data[1].replace("<a href=", "");
								cities.add(vals[0].trim()+" ("+data[1].trim());
								
								String str[] = data[1].replace(")", "").split("/");
								for(String s : str)cities.add(s.trim());
							
							}else cities.add(vals[0].trim()+" "+data[1].trim());
							
							if(data[1].contains("/")){
								data[1] = data[1].replace("<a href=", "");
								cities.add(vals[0].trim()+" "+data[0].trim()+" ("+data[1].trim());
							}else cities.add(vals[0].trim()+" "+data[0].trim()+" "+data[1].trim());
						}
						vals[1] = vals[1].replace("<a href=", "");
						cities.add(vals[0].trim()+" "+vals[1].trim());
					}
				}else{
					vals = city.split("\\(");
					if(vals.length == 2){
						if(vals[1].contains(")")){
							if(vals[1].contains(",")){
								vals[1] = vals[1].replace(")", "");
								String[] str = vals[1].replace("<a href=", "").split(",");
								if(str.length >= 2){
									for(String s : str)	cities.add(s.trim());
								}
							}
							cities.add(vals[0].trim());
						}
					}
				}
				
			}else if(city.contains(",")){
				city = city.replace("<a href=", "");
				String vals [] = city.split(",");
				if(vals.length == 2){
					cities.add(vals[0].trim());
					cities.add(vals[0].trim()+" "+vals[1].trim());
				}
				if(city.contains("<sup id=[3]</sup>")){
					for(String val : vals){
						cities.add(val.replace("<sup id=[3]</sup>", "").trim());
					}
				}
				
			}else if((city.contains("(") || city.contains(")")) && !city.contains("/")){
				city = city.replace("<a href=", "");
				String temp = city;
				if(city.contains("(")){				
					if(!temp.contains("/"))
						temp = temp.replace(")", "");
					
					String vals [] = temp.split("\\(");
					if(vals.length == 2){
						cities.add(vals[0].trim());
						cities.add(vals[0].trim()+" "+vals[1].trim());
						cities.add(vals[1].trim());
					}
				}else{
					if(!temp.contains("/"))
						temp = temp.replaceAll("\\(|\\)", "").replaceAll("\\s{2,}", "").trim();
					cities.add(temp);
				}
				cities.add(city);
			}else if((city.contains("(") || city.contains(")")) && city.contains("/")){
				city = city.replace("<a href=", "");
				String temp = city;
				if(city.contains("(")){
					
					String vals [] = temp.split("\\(");
					if(vals.length == 2){
						if(!vals[0].contains("/")){
							cities.add(vals[0].trim());
						}
						if(vals[1].contains("/")){
							vals[1] = vals[1].replace(")", "");
							String [] data = vals[1].split("/");
							for(String val : data){
								if(val.contains("Central"))continue;
								cities.add(val.trim());
							}
						}
					}//eof if
					cities.add(city);
				}else{
					if(!temp.contains("/"))
						temp = temp.replaceAll("\\(|\\)", "").replaceAll("\\s{2,}", "").trim();
					cities.add(temp);
				}
				cities.add(city);
			}
			else if (city.contains("/")){
				String temp = city.replace("<a href=", "");
				String vals [] = temp.split("/");
				if(vals.length == 2){
					cities.add(vals[0].trim());
					cities.add(vals[1].trim());
					cities.add(temp.trim());
				}
			}else{
				cities.add(city);
			}
//			U.log(zip+"\t"+city+"\t"+cities);
		}
	}
	static List<RularData> allRularList = new ArrayList<>();
	static void rularExtraction(String html, String stateAbbr){
		String section = U.getSectionValue(html, "id=\"Rural\"", "</table>");
		if(section == null)
			section = U.getSectionValue(html, "<th>Rural</th>", "</table>");
		if(section == null)
			section = U.getSectionValue(html, "id=\"Rural_", "</table>");
		if(section == null)
			section = U.getSectionValue(html, "<th>Rural", "</table>");
//		U.log(section);
		
		
		if(section != null){
			String [] rularSections = U.getValues(section, "<td ", "</td>");
			
			for(String rularSection : rularSections){
				rularSection = StringEscapeUtils.unescapeHtml4(rularSection);
				
				rularSection = rularSection.replace("<p>", "</a><p>");
				RularData rularData = new RularData();
				
				//pref zip
				String pref = U.getSectionValue(rularSection, "<b>", "</b>");
				U.log("*********** pref :"+pref);
				
				//  G0L
//				if(!pref.contains("G0L"))continue;
//				U.log(rularSection);
				
				//Rular Names
				List<String> rularCityList = new ArrayList<>();
				String rular = U.getSectionValue(rularSection, "</b>", "</a>");
				U.log("Rular ::"+rular);
				if(rularSection.contains("<i><b>Reserved</b></i>"))continue;
				if(rular == null){
					if(rularSection.contains("<i>Not in use</i>"))continue;
					if(rularSection.contains("<i>Not assigned</i>"))continue;
				}
				
				String str1 = U.getSectionValue(rular, "<br />", "<a href=");
				U.log("str1 ::"+str1);
				String str2 = Util.match(rular, "(<a href=(.*?)>)(.*?)$",3);
				U.log("str2 ::"+str2);
				
				String str3 = U.getSectionValue(rularSection, "</a>", "<p>");
				if(str3 != null && !str3.trim().isEmpty()){
					str3 = str3.replaceAll("<.*?>|\n", "").trim();
					U.log("str3 :"+str3);
				}
				String str = "";
				if(str1 != null)str = str1.trim()+" "+str2.trim();
				if(str1 == null && str2 == null){
					str = rular.replaceAll("<br />|\n|<.*?>", "").trim();
					str2 = "";
				}
				U.log("str :"+str);

				rularCityList.add(str.trim());
				if(!rularCityList.contains(str2.trim())){
					if(!str2.isEmpty())
						rularCityList.add(str2.trim());
					if(str3 != null && !str3.isEmpty())
						rularCityList.add(str.trim()+" "+str3);
				}
				
				//add details
				rularData.setRularList(rularCityList);
				rularData.setZipPref(pref);
				rularData.setState(stateAbbr);
				
				List<RularTownData> listRularTownData = new ArrayList<>();
				
				//other villages details
				String villageSection = U.getSectionValue(rularSection, "<p>", "</p>");
				if(villageSection != null){
					villageSection = villageSection.replace("</span>", "").trim();
					String[] vals = villageSection.split("<br />");
					U.log("Total town ::"+vals.length);
					for(String val : vals){
						val = val.replaceAll("<.*?>", ",").replaceAll(":\\s+,", ":").trim().replaceAll(",$|\n|^,", "")
								.replace(",", "").replace("*", "");
						U.log(val);
						if(val.contains(":")){
							RularTownData townData = new RularTownData();
							String[] data = val.split(":");
//							U.log(Arrays.toString(data));
							if(data.length == 2){
								//suf zip & town
								if(data[0].trim().length() == 3){
									townData.setZipSuf(data[0].trim());
									townData.setTownOfRular(data[1].trim());
								}else if(data[1].trim().length() == 3){
									townData.setZipSuf(data[1].trim());
									townData.setTownOfRular(data[0].trim());
								}
								listRularTownData.add(townData);
							}//eof if
						}
					}
					rularData.setRularTownDataList(listRularTownData);
				}
				allRularList.add(rularData);
			}//eof for
		}
	}//eof rularExtraction()

}//eof ExtractZipAndCityFromWiki

class RularData{
	private String zipPref = null;
	private String state = null;
	private List<String> rularList = null;
	private List<RularTownData> rularTownDataList = null;
	
	public String getState() {
		return state;
	}
	public List<RularTownData> getRularTownDataList() {
		return rularTownDataList;
	}
	public List<String> getRularList() {
		return rularList;
	}
	public String getZipPref() {
		return zipPref;
	}
	
	public void setZipPref(String zipPref) {
		this.zipPref = zipPref;
	}
	public void setRularList(List<String> rularList) {
		this.rularList = rularList;
	}
	public void setRularTownDataList(List<RularTownData> rularTownDataList) {
		this.rularTownDataList = rularTownDataList;
	}
	public void setState(String state) {
		this.state = state;
	}
}

class RularTownData{
	private String zipSuf = null;
	private String townOfRular = null;
	
	public String getTownOfRular() {
		return townOfRular;
	}
	
	public String getZipSuf() {
		return zipSuf;
	}
	
	public void setZipSuf(String zipSuf) {
		this.zipSuf = zipSuf;
	}
	public void setTownOfRular(String townOfRular) {
		this.townOfRular = townOfRular;
	}
}