/**
 * @author Sawan
 * @date 24 July 2019
 */
package com.shatam.MexicoExtraction_1_15;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.tequila.address.MiCodigoPostal;

public class RestaurantGuru  extends DirectoryList{
	
	private static final String URL = "https://restaurantguru.com";
	private static final String NAME = "Restaurant Guru";
	static int start = 5000;
	static int end = 5100;
	WebDriver driver = null;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//Total Count 441575   Mar 19  22079
		Scanner scan = new Scanner(System.in);
		U.log("Start :");
//		start = scan.nextInt();
		U.log("End :");
//		end = scan.nextInt();
		scan.close();
		RestaurantGuru rg = new RestaurantGuru();
		rg.extractProcess();
		rg.printAll("Restaurant_Guru/"+NAME+"_"+start+"_"+end+".csv");
//		rg.addExtraData(Path.CACHE_PATH_FOR_EXTRACTION+"Restaurant_Guru/"+NAME+"_"+start+"_"+end+".csv");
//		Path.CACHE_PATH_FOR_EXTRACTION
//		int a=10.322;
	}
	
	@Override
	protected void extractProcess() throws Exception {
//		U.setUpGeckoPath();
//		U.setUpChromePath();
//		driver = new FirefoxDriver();
//		driver = new ChromeDriver();
		
		// TODO Auto-generated method stub
		//Total Count 441575   Mar 19
/*		String html = U.getHTML("https://restaurantguru.com/cities-Mexico-c"); //All cities
		String[] sections = U.getValues(html, "<div class=\"part_title\"", "</ul>");
//		U.log(sections.length);
		for(String section : sections){
//			U.log(section);
			String urlSec = U.getSectionValue(section, ">", "</div>");
			String allCitiesUrl = U.getSectionValue(urlSec, "<a href=\"", "\"");
//			U.log(allCitiesUrl);
			4
			if(allCitiesUrl != null)
				findAlphabeticCities(allCitiesUrl);
			
			String citiesUrls[] = U.getValues(section, "<li><a", "</li>");
			for(String cityUrl : citiesUrls){
				cityUrl = U.getSectionValue(cityUrl, " href=\"", "\"");
			}
		}
*/	
		int deletedCount = 0;
		String url = "https://restaurantguru.com/best-restaurants-Mexico-b";
//		String html = U.getHTML(url);
		String html = U.getPageSource(url);
		
		String resultCount = U.getSectionValue(html, "class=\"filter_count\">", "</span>");
		resultCount = resultCount.replaceAll(" results found|\\s", "").trim();
		
		html = null;
		U.log(resultCount);
		int count = Integer.parseInt(resultCount);
		for(int i = 1; i <= (count/20)+1; i++){
//			if(i >= 7587 && i <= 10000) //Deleted files count :641
//			if(i >= 13128 && i <= 15000)   //Deleted files count :726
//			if(i >= 17790 && i <= 20000)
//			if(i >= 20000)33
//			if(i >= 48 && i <= 100) // 0 to 100
//			if(i >= 153 && i <= 200) //100 to 200
//			if(i >= 1000 && i <= 1000) //100 to 200
//------------------------------------------------------
			if(i >= start && i <= end) 
//			if(i >= 10000 && i <= 12000) //Done
//				if(i >= 34116 && i <= 441575) //
//					if(i >= 25000 && i <= 441575) //
//						if(i >= 28000 && i <= 441575) //
//				if(i >= 25408 && i <= 441575) //
//			if(i >= start && i <= end) //100 to 200
			{
				try{Thread.sleep(100);}catch(Exception e){}
				U.log("count :"+i+ "\t" +url+"/"+i);
				String pageHtml = U.getPageSource(url+"/"+i);//getHTML(url+"/"+i);
				U.log(pageHtml.length());
				String urlSections[] = U.getValues(pageHtml, "<div class=\\\"wrapper_info\\\">", "<\\/a>");
				if(urlSections.length == 0)
					urlSections = U.getValues(pageHtml, "<div class=\"wrapper_info\">", "</a>");
//				U.log(urlSections.length);
				
				if(deleteFile(url+"/"+i)){
					deletedCount++;
					U.log("File is deleted.");
				}
				for(String urlSec : urlSections){
	//				U.log(urlSec);
					String name = U.getSectionValue(urlSec, "<a title=\\\"", "\\\"");
					if(name == null) name = U.getSectionValue(urlSec, "<a title=\"", "\"");
					
					String restaurantUrl = U.getSectionValue(urlSec, "\" href=\\\"", "\"");
					if(restaurantUrl == null) restaurantUrl = U.getSectionValue(urlSec, "\" href=\"", "\"");
					restaurantUrl = restaurantUrl.replace("\\", "");
					U.log(name+"\t"+restaurantUrl);
						extractRestaurantDetails(restaurantUrl.trim(), name, url+"/"+i);
						
					
					
					name = null;
					restaurantUrl = null;
				}
	//			break;		
				urlSections = null;
				pageHtml = null;
				
			}//eof if

		}//eof for
		
		U.log("Deleted files count :"+deletedCount);
		U.log("deletedCountRest ::"+deletedCountRest);
	}
	
/*	private void findAlphabeticCities(String url) throws IOException{
		U.log("allCityUrl :"+url);
		String html = U.getHTML(url);
		
		String pagingSection = U.getSectionValue(html, "<div class=\"pagination\">", "<div class=\"footer\">");
		if(pagingSection != null){
			U.log(pagingSection);
			
			String[] pagingUrls = U.getValues(pagingSection, "\" href=\"", "\"");
			for(String pagingUrl : pagingUrls){
				U.log("pagingUrl =="+pagingUrl);
				String pagingHtml = U.getHTML(pagingUrl);
			}
		}
	}
	
	
	private void findAllCities(String html){
		String section = U.getSectionValue(html, "<div class=\"cities scrolled-container\">", "<div class=\"footer\">");
		String[] urlSections = U.getValues(section, "<li>", "</li>");
		
		for(String urlSec : urlSections){
			String cityUrl = U.getSectionValue(urlSec, "<a href=\"", "\"");
			String cityName = U.getSectionValue(urlSec, "class=\"city_name\">", "</span>");
			String cityCount = U.getSectionValue(urlSec, "\"city-cnt\">", "</span>");
			int count = Integer.parseInt(cityCount.trim());
		}
	}
	*/
	String sicSub = "5812";
	int deletedCountRest = 0;
	int j = 0;	
	private void extractRestaurantDetails(String url, String name, String refUrl) throws Exception{
		
//		if(j >= 380 )
		{
//			Thread.sleep(5000);
			U.log("Rest. Count ="+j);
			U.log(url);
			U.log(U.getCache(url));
//			String html = U.getHTML(url);
			String html = U.getPageSource(url);
			
			if(deleteFile(url)){
				deletedCountRest++;
				U.log("File is deleted.");
				return;
			}
			
			if(html == null||html.contains("<div class=\"info404\">")) return;
			String section = U.getSectionValue(html, "type=\"application/ld+json\">", "</script>");
			
			section = TranslateEnglish.removeUnicode(section);
			name = TranslateEnglish.removeUnicode(name);
			
//			U.log(name);	
			

			String phone = ALLOW_BLANK;
			String lat = ALLOW_BLANK, lng = ALLOW_BLANK;
			String email = ALLOW_BLANK;
			String companyUrl = ALLOW_BLANK; 
			String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK, zip = ALLOW_BLANK;

			if(section != null){
				//Phone
				phone = U.getSectionValue(section, "telephone\": \"", "\"");
				
				//Lat-Lng
				lat = U.getSectionValue(section, "\"latitude\": \"", "\"");
				lng = U.getSectionValue(section, "\"longitude\": \"", "\"");
	
				//Email
				email = U.getSectionValue(section, "email\": \"", "\"");
				
				//Company URL
				companyUrl = U.getSectionValue(section, "url\": \"", "\"");
				
				//Address
				street = U.getSectionValue(section, "streetAddress\": \"", "\"");
				state = U.getSectionValue(section, "addressRegion\": \"", "\"");
				city = U.getSectionValue(section, "addressLocality\": \"", "\"");
			}else{
				//Phone
				phone = U.getSectionValue(html, "class=\"call\">", "</span>");
				
				//Lat-Lng
				String latLngSec = U.getSectionValue(html, "var _location =", "</script>");
				if(latLngSec != null){
					lat = U.getSectionValue(latLngSec, "lat:", ",");
					lng = U.getSectionValue(latLngSec, "lng:", "}");
				}
				//Company URL
				String companyUrlSec = U.getSectionValue(html, "Website</div>", "</div>");
				if(companyUrlSec == null) companyUrlSec = U.getSectionValue(html, "<div class=\"website\">", "<div class=\"wrapper_malls\">");
				if(companyUrlSec == null) companyUrlSec = ALLOW_BLANK;
				companyUrl = U.getSectionValue(companyUrlSec, "target=\"_blank\">", "</a>");
				
				//Address
				String addSec = U.getSectionValue(html, "Address</div>", "</div>");
				if(addSec == null) addSec = U.getSectionValue(html, " id=\"info_location\">", "</a>");
					addSec = addSec.replaceAll("<.*?>|Get directions| Address", "");
				
				
				String add[] = U.getAddress(addSec.trim());
				street = add[0];
				colonia = add[1];
				city = add[2];
				state = add[3];
				zip = add[4];
			}
			U.log(phone);
			if(phone == null) phone = ALLOW_BLANK;
			phone = phone.replaceAll("<span>", "").trim();
			
			if(email == null) email = ALLOW_BLANK;
			
			
			if(companyUrl == null) companyUrl = ALLOW_BLANK;
			if (zip==ALLOW_BLANK) {
				
				String tempAdd[]=U.getAddressFromLatlonHereApi(new String[] {lat,lng});
				if (tempAdd!=null) {
					zip=tempAdd[4];
					colonia=tempAdd[1];
				}
			}
			
			if(state == null)state = ALLOW_BLANK;
			if(city == null)city = ALLOW_BLANK;
			if (city!=ALLOW_BLANK&&city.contains("Mexico City")&&zip!=null) {
				state=city;
				city=MXStates.getMexicoCityMunicipalites(zip);
			}else {
				U.log(zip);
				if(zip!=null&&zip!=ALLOW_BLANK) {
					String tempcity=MiCodigoPostal.getCity(zip);
					if (tempcity!=null) {
						city=tempcity;
					}
					state=MXStates.findStateFromZip(zip);
				}
				
				if (city!=ALLOW_BLANK&&city.contains("Ciudad de México")) {
					state=city;
					city=MXStates.getMexicoCityMunicipalites(zip);
				}
			}
			if(city == null)city = ALLOW_BLANK;
			if(state == null)state = ALLOW_BLANK;
			if(street == null)street = ALLOW_BLANK;
			if (state.contains("Mexico Features"))state=ALLOW_BLANK;	
			
			//Add details
			addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, companyUrl);
			addAddress(street, colonia, city, state, zip);
//			addAddress(add[0], add[1], add[2], add[3], add[4]);
			addBoundaries(lat, lng);
			addReferenceUrl(url);
			addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email);
		}
		j++;
	}
	
	public boolean deleteFile(String url) throws Exception{
		File cacheFile = new File(U.getCache(url));
		if (cacheFile.exists() && cacheFile.isFile()){
			if(cacheFile.length() < 1001){
				return cacheFile.delete();
			}
		}
		return false;
	}
	
	public static String getHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
		// U.log(" .............."+path);
		// Thread.sleep(4000);
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);

		URL url = new URL(path);

		String html = null;

/*		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"216.56.85.100",	8080));
*/		final URLConnection urlConnection = url.openConnection();  //proxy

		// Mimic browser
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			urlConnection.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			urlConnection.addRequestProperty("Accept-Language",
					"en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
			urlConnection.setConnectTimeout(5000);
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
			inputStream.close();
			if (!cacheFile.exists())
				FileUtil.writeAllText(fileName, html);

			return html;
		} catch (Exception e) {
			U.log(e);
		}
		return html;
	}
	private void addExtraData(String filePath) {
		
		List<String[]> data=U.readCsvFile(filePath);
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			for (String[] inputArr : data) {
				String outputArrp[]=new String[inputArr.length+6];
				if (inputArr[0].contains("ID")) {
					System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
					outputArrp[inputArr.length]="HERE_ADDRESS_FORMATED";
					outputArrp[inputArr.length+1]="HERE_ADDRESS";
					outputArrp[inputArr.length+2]="HERE_COLONIA";
					outputArrp[inputArr.length+3]="HERE_CITY";
					outputArrp[inputArr.length+4]="HERE_STATE";
					outputArrp[inputArr.length+5]="HERE_POSTAL";
					writer.writeNext(outputArrp);
					continue;
				}
				
				System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
				String add1[]=getAddressFromLatlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				
				String add=getAddressFromLAtlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				if(add1==null) {add1=new String[] {"","","","",""};}
				outputArrp[inputArr.length]=add;
				System.arraycopy(add1, 0, outputArrp, inputArr.length+1, add1.length);
				writer.writeNext(outputArrp);
				U.log(Arrays.toString(outputArrp));
			}
			FileUtil.writeAllText(filePath, sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getAddressFromLAtlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":"))
				return U.getSectionValue(html, "\"Label\":\"", "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":")) {
				String add=U.getSectionValue(html, "\"Street\":\"", "\"")+" "+U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia=U.getSectionValue(html, "\"District\":\"", "\"");
				String city=U.getSectionValue(html, "\"City\":\"", "\"");
				String state=MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] {add,colonia,city,state,postal};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
