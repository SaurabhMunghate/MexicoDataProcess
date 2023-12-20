package com.tequila.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.shatam.geoboundary.Distance;
import com.shatam.geoboundary.DistanceCalculator;
import com.shatam.utils.SawanUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractMexicoData {
	
	private static final String INPUT_FILE = 
			"/home/glady/MexicoCache/Tequila_DATA/files/This_Week_Data/Tiendeo-Pharmacy_Main_Split_2_Correct_A_Gnc_ADD.csv";
	
	private static final String HEADER[] = {"INDEX","SEARCH_KEY","ADDRESS","STREET","COLONIA","CITY","STATE","ZIP","PHONE","URL","LATITUDE","LONGITUDE","CALCULATE_DISTANCE_KM","REF_URL","FETCH_TIME"};
	
	private static final String INPUT_GOOGLE_DATA_FILE = INPUT_FILE.replace(".csv", "_Google.csv");

	private static String googleSearchUrl = "https://www.google.com/";
	
	public static void main(String[] args) {
		
		System.setProperty("webdriver.chrome.driver", "/home/glady/chromedriver");
		WebDriver driver = new ChromeDriver();

		extractAddressFromGoogle(driver);
		
/*		String url = 
"https://www.google.co.in/search?biw=1686&bih=900&tbm=lcl&ei=H1RhW5ccy9q-BPz1r-AI&q=GNC+Mexico+in+mexico+state&oq=GNC+Mexico+in+mexico+state&gs_l=psy-ab.3..33i21k1j33i160k1.5578.8912.0.9168.16.16.0.0.0.0.284.1739.0j11j1.12.0....0...1c.1.64.psy-ab..4.11.1618...35i39k1j0i203k1j0i20i263k1j0i22i30k1j33i22i29i30k1.0.a0L_5vS8As4#rlfi=hd:;si:;mv:!1m3!1d105024.79500891909!2d-99.1784004!3d19.4520173!2m3!1f0!2f0!3f0!3m2!1i313!2i335!4f13.1;tbs:lrf:!2m1!1e3!3sIAE,lf:1,lf_ui:4";
		extractCompanyDetailListingData(url, driver);
*/		
		
		driver.close();
		driver.quit();
	}
	
	static List<String[]> loadGoogleData(){
		List<String[]> writeLines = null;
		if(new File(INPUT_GOOGLE_DATA_FILE).exists()){
			writeLines =  U.readCsvFile(INPUT_GOOGLE_DATA_FILE);
		}else{
			writeLines = new ArrayList<>();
		}
		return writeLines;
	}
	
	static Set<String> uniqueReferenceUrl(String inputGoogleDataUrl){
		Set<String> uniqueSet = new HashSet<>();
		List<String[]> readLines = U.readCsvFileWithoutHeader(inputGoogleDataUrl);
		for(String lines[] : readLines){
			uniqueSet.add(lines[13].toLowerCase().trim()); //ref_url
		}
		return uniqueSet;
	}
	
	static void extractCompanyDetailListingData(String url, WebDriver driver){
		driver.get(url);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String address = null;
		String latLngSection = null;
		String companyUrl = null;
		String latLng[] = {"",""};
		String phoneNumber = null;
		String[] add = {"","","","",""};
		
//		String html = driver.getPageSource();
		WebElement element =  driver.findElement(By.xpath("//*[@id=\"rl_ist0\"]/div[1]/div[4]"));
		String text = element.getText();
		ArrayList<String> vals = Util.matchAll(text, "GNC[\\s\\n\\w\\W\\d\\+#()⋅,](.*)?DIRECTIONS\n", 0);
		U.log(element.getText());
		U.log(vals.size());
		for(String val : vals){
			U.log(">>"+val);
		}
		
		List<WebElement> list = driver.findElements(By.className("rllt__details lqhpac"));
		for(WebElement el : list){
			el.click();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			U.log(el.getText());
		}
	}
	

	
	static void extractAddressFromGoogle(WebDriver driver){
//		boolean isExist = false;
		/**
		 * Google unique dataset
		 */
		Set<String> uniqueSet = new HashSet<>();

		List<String[]> writeLines = loadGoogleData();
		
		
		if(writeLines.size() == 0){
			writeLines.add(HEADER);
		}else if(writeLines.size() > 0){
//			isExist = true;
			uniqueSet.addAll(uniqueReferenceUrl(INPUT_GOOGLE_DATA_FILE));
		}
		
		List<String[]> readLines = U.readCsvFile(INPUT_FILE);
		
		String[] lines = null;
		int x = 0;

		Iterator<String[]> it = readLines.iterator();
		
		while (it.hasNext()) {
			lines=it.next();
			
			if (x++ == 0) continue;
			if(x > 289 && x < 320){
//			if(x > 273 && x < 275){
				if(lines[9].trim().isEmpty()){ //colonia
					
					if(!uniqueSet.add(lines[24].toLowerCase().trim()))continue; //ref_url
					U.log(">>> Row :"+x);
					String[] vals = getDetailsFromGoogle(lines, driver);
					if(vals == null)break;
					String distance = "";
					if(!vals[0].isEmpty()){
						if(!vals[8].isEmpty() && !vals[9].isEmpty() && !lines[22].isEmpty() && !lines[23].isEmpty()){
							try{
								distance = DistanceCalculator.haversineDistance(lines[22].trim(), lines[23].trim(), vals[8].trim(), vals[9].trim(), Distance.KILOMETER);
							}catch(Exception e){
								distance = "";
							}	
						}
						writeLines.add(new String[]{
							lines[0], vals[10], vals[0], vals[1], vals[2], vals[3], vals[4], vals[5],
							vals[6], vals[7], vals[8], vals[9], distance, lines[24], U.getTodayDate()
						});
					}
				}
			}
		}
		
		U.writeCsvFile(writeLines, INPUT_GOOGLE_DATA_FILE);			

	}
	
	static String[] getDetailsFromGoogle(String []lines, WebDriver driver){

        
		driver.get(googleSearchUrl);
		
		WebElement element = driver.findElement(By.id("lst-ib"));
		//company name + street
		String key = lines[7]+" "+lines[8];
		U.log("Key = "+key);
		
		element.sendKeys(key);
		element.submit();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String address = null;
		String latLngSection = null;
		String companyUrl = null;
		String latLng[] = {"",""};
		String phoneNumber = null;
		String[] add = {"","","","",""};
		boolean IsOnlyAddress = false;
		String[] onlyAddressDetails = null;
		
		String html = driver.getPageSource();
		
		if(html.contains("No results|did not match any documents"))return null;
		if(html.contains("Our systems have detected unusual traffic")) {
			return null;
		}
		
		String addressSection = U.getSectionValue(html, "Address</a>", "</div>");
		if(addressSection != null){
			address = Util.match(addressSection, "<span class=\"(.*?)\">(.*?)</span>", 2);
		}else if(addressSection == null){
			onlyAddressDetails = getDetailsFromDesktop(lines, driver, driver.getCurrentUrl());
			if(!onlyAddressDetails[0].isEmpty() || !onlyAddressDetails[1].isEmpty()){
				IsOnlyAddress = true;
			}
		}
		if(addressSection == null && IsOnlyAddress == false){
			String tempHtml = getDetailsFromCompany(lines, driver, driver.getCurrentUrl());
			if(tempHtml != null){
				addressSection = U.getSectionValue(tempHtml, "Address</a>", "</div>");
				if(addressSection != null){
					address = Util.match(addressSection, "<span class=\"(.*?)\">(.*?)</span>", 2);
					IsOnlyAddress = false;
					html = tempHtml;
					key = lines[7];
				}
			}
		}
		
		if(!IsOnlyAddress){
			companyUrl = U.getSectionValue(html, "<a class=\"ab_button\" href=\"", "\" role");
			
			String phoneSection = U.getSectionValue(html, "Phone</a>", "</div>");
			if(phoneSection != null){
				phoneNumber = Util.match(phoneSection, "<span>([\\d\\s+]+)</span>",1);
			}
			
			latLngSection = U.getSectionValue(html, "<div class=\"rhsg4 rhsmap5col\"", "<img height");
		}else{
			address = onlyAddressDetails[0];
			latLngSection = onlyAddressDetails[1];
			key = onlyAddressDetails[2];
		}
//		U.log("latLngSec =="+latLngSection);
		if(latLngSection != null)latLng = U.findLatLng(latLngSection);

		if(companyUrl == null) companyUrl = "";
		if(companyUrl.trim().length() > 120) companyUrl = "";
		
		if(address == null) address = "";
		if(phoneNumber == null) phoneNumber = "";
		
		if(!address.isEmpty()){
			add = U.getAddressGoogle(address);
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("CompanyUrl : ").append(companyUrl).append("\n")
		.append("Full Address : ").append(address).append("\n")
		.append("Street : "+add[0]+"\n").append("Colonia : "+add[1]+"\n").append("City : "+add[2]+"\n")
		.append("State : "+add[3]+"\n").append("Zip : "+add[4]+"\n")
		.append("Phone : ").append(phoneNumber).append("\n")
		.append("Latitude : ").append(latLng[0]).append("\t")
		.append("Longitude : ").append(latLng[1]);
		
		U.log(sb.toString());
/*		try{
			U.log("Lat :"+driver.findElement(By.xpath("//*[@id=\"media_result_group\"]/div/div/div[1]/div[2]")).getText());
			element = driver.findElement(By.xpath("//*[@id=\"rhs_block\"]/div/div[1]/div"));
			String text = element.getText();
			if(text != null){
				U.log(text);
				address = Util.match(text, "Address:(.*?)\n", 1);
				U.log(address);
			}
			String url = driver.findElement(By.xpath("//*[@id=\"rhs_block\"]/div/div[1]/div/div[1]/div[2]/div[1]/div/div[2]/div[1]/div/div/div[2]/div[1]/a")).getAttribute("href");
		}catch(NoSuchElementException  e){
			U.log("Address Not found");
		}
*/		
		return new String[]{address, add[0], add[1], add[2], add[3], add[4], phoneNumber, companyUrl, latLng[0], latLng[1], key};
	}
	
	static String getDetailsFromCompany(String[] lines, WebDriver driver, String url){
		driver.get(googleSearchUrl);
		
		WebElement element = driver.findElement(By.id("lst-ib"));
		
		//company name
		String key = lines[7];
		U.log("Key = "+key);
		
		element.sendKeys(key);
		element.submit();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
				
		String html = driver.getPageSource();
		if(html.contains("Address</a>")){
			return html;
		}
		return null;
	}
	
	static String[] getDetailsFromDesktop(String[] lines, WebDriver driver, String url){
		driver.get(googleSearchUrl);
		
		WebElement element = driver.findElement(By.id("lst-ib"));
		
		if(lines[12].trim().length() == 4)lines[12] = "0"+lines[12].trim();
		//street + city + state + zip
		String key = lines[8]+" "+lines[10]+" "+lines[11]+" "+lines[12];
		U.log("Key = "+key);
		
		element.sendKeys(key);
		element.submit();
		try {
			Thread.sleep(8000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String address = null;
		
		String html = driver.getPageSource();
		
		address = U.getSectionValue(html, "<div class=\"desktop-title-content\">", "</span>");
		if(address != null){
			address = address.replace("</div>", ",").replaceAll("\\s?<span.*>", "");
		}
		if(address == null) address = "";
		
		String latLngSection = U.getSectionValue(html, "id=\"lu_map\"", "style=\"background-image");
		
		if(latLngSection == null)latLngSection = "";
		
		if(latLngSection != null && !latLngSection.contains("/@")){
			latLngSection = U.getSectionValue(html, "class=\"lu_map_section\">", "<img src=");
			if(latLngSection != null){
				if(latLngSection.contains("href")){
					String mapUrl = U.getSectionValue(latLngSection, "<a href=\"", "\"");
					U.log(mapUrl);
					if(!mapUrl.trim().startsWith("http")){
						mapUrl = "https://www.google.co.in"+mapUrl;
					}
					driver.get(mapUrl);
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					html = driver.getPageSource();
					latLngSection = U.getSectionValue(html, "itemprop=\"name\"", "itemprop=");
				}
			}
		}
		if(latLngSection == null)latLngSection = "";
		return new String[]{address.trim(),latLngSection, key};
	}
	
}
