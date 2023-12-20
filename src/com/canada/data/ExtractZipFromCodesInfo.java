package com.canada.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;

import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractZipFromCodesInfo {

	public static void main(String[] args) throws IOException {
		extractPostalCodeInfo();
	}
	static boolean generateCsvFile = false;
	
	static String[] header = {"STATE","CITY","ZIP"};
	
	static void extractPostalCodeInfo()throws IOException{
		String html = U.getHTML("https://postalcodeinfo.ca/cities/a/");
		
		String section = U.getSectionValue(html, "<ul id=\"letter-list\"", "</ul>");
//		U.log(section);
		String[] citiesIndexUrls = U.getValues(section, "<a href=\"", "\"");
		for(String cityIndexUrl : citiesIndexUrls){
			U.log("City Index Url ::"+cityIndexUrl);
//			if(cityIndexUrl.contains("/w/"))
				extractCityInfo(cityIndexUrl);
//			break;
			cityStateSet.clear();
		}
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(header);
		Set<String> uniqueData = new HashSet<>();
		for(CanadaCitiesData data : candaCitiesList){
			if(!uniqueData.add((data.getState()+""+data.getCity()+""+data.getZip()).toLowerCase()))continue;
			writeLines.add(new String[]{
				data.getState(),data.getCity(),data.getZip()	
			});
		}
		
		if(generateCsvFile){
			U.writeCsvFile(writeLines, Path.CACHE_PATH_FOR_EXTRACTION+"PostalCodoInfo.csv");
		}
		
	}
	static List<CanadaCitiesData> candaCitiesList = new ArrayList<>();
	static Set<String> cityStateSet = new HashSet<>();
	
	static void extractCityInfo(String url)throws IOException{
		String html = U.getHTML(url);
		U.log(url);
		String section = U.getSectionValue(html, "<ul id=\"city-list\"", "</ul>");
		String[] cityUrlSections = U.getValues(section, "<li id=\"city_list\"", "</li>");
		if(cityUrlSections.length == 0){
			U.log("Not found cities for >>"+url);
		}
		for(String cityUrlSection : cityUrlSections){
			cityUrlSection = StringEscapeUtils.unescapeHtml4(cityUrlSection);
			String cityUrl = U.getSectionValue(cityUrlSection, "\" href=\"", "\"");
//			U.log("City Url :"+cityUrl);
			String cityStateName = Util.match(cityUrlSection, "/\">(.*?)</a>", 1);
			U.log("City & state ::"+cityStateName);
			
			if(cityStateSet.add(cityStateName)){
				String vals[] = cityStateName.split(",");
				
				zipExtraction(cityUrl, vals[0].trim(), vals[1].trim());
			}
//			break;
		}
		
		boolean flag = false;
		//pagination for cities
		String pagingSection = U.getSectionValue(html, "<div id=\"City_Footer_Container\">", "</div>");
		
		if(pagingSection != null && !pagingSection.isEmpty()){
			String[] pagingUrls = U.getValues(pagingSection, "<li><a href=\"", "\"");
			for(String pagingUrl : pagingUrls){
//				U.log(pagingUrl);
				if(pagingUrl.trim().equalsIgnoreCase(url.trim())){
					flag = true;
					continue;
				}
				if(flag){
					extractCityInfo(pagingUrl);
				}
			}
		}
	}//eof extractCityInfo()
	
	static void zipExtraction(String url, String city, String state)throws IOException{
		String html = U.getHTML(url);
		String section = U.getSectionValue(html, "<ul id=\"postal_code-list\"", "</ul>");
		String zipSections[] = U.getValues(section, "<li id=", "</li>");
		if(zipSections.length== 0){
			U.log("Not found zip for >>"+city+"<<>>"+state);
		}
		for(String zipSection : zipSections){
			String zip = Util.match(zipSection, "/\">(.*?)</a>", 1);
			if(zip != null && !zip.trim().isEmpty()){
				if(zip.trim().matches("[A-Z]{1}\\d[A-Z]{1}\\s?\\d[A-Z]{1}\\d")){
					addDetails(city, state, zip);
				}
			}else
				U.log("Zip is not correct for ::"+zip+"<<>>"+city+"<<>>"+state);
		}
		
		boolean flag = false;
		//pagination for zip
		String pagingSection = U.getSectionValue(html, "<div id=\"PC_Footer_Container\">", "</div>");
		
		if(pagingSection != null && !pagingSection.isEmpty()){
			String[] pagingUrls = U.getValues(pagingSection, "<li><a href=\"", "\"");
			for(String pagingUrl : pagingUrls){
				if(pagingUrl.trim().equalsIgnoreCase(url.trim())){
					flag = true;
					continue;
				}
				if(flag){
					zipExtraction(pagingUrl, city, state);
				}
			}
		}
	}//eof zipExtraction()
	
	static void addDetails(String city, String state, String zip){
		CanadaCitiesData citiesData = new CanadaCitiesData();
		citiesData.setCity(city);
		citiesData.setState(state);
		citiesData.setZip(zip);
		candaCitiesList.add(citiesData);
	}//eof addDetails()
	
}//eof ExtractZipFromCodesInfo

class CanadaCitiesData{
	private List<String> zipList = null;
	private String state = null;
	private String city = null;
	private String zip = null;
	public List<String> getZipList() {
		return zipList;
	}
	public String getState() {
		return state;
	}
	public String getCity() {
		return city;
	}
	public String getZip() {
		return zip;
	}
	public void setZipList(List<String> zipList) {
		this.zipList = zipList;
	}
	public void setState(String state) {
		this.state = state;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
}