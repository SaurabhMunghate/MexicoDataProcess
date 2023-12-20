package com.tequila.extraction.tripadvisor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class ExtractTripAdvisorRestaurant {
	HashSet<String>uniqueKey=new HashSet<>();
	protected static final String ALLOW_BLANK = "-";
	static int totalHotel=0;
	static int duplic=0;
	public static void main(String[] args) throws Exception {
		long startTime=System.currentTimeMillis();
		new ExtractTripAdvisorRestaurant().innerProcess();
		U.log("Total Time Taken "+(System.currentTimeMillis()-startTime));
		U.log("Total No. of Dulipcate: "+duplic);
		U.log("Total No. of Hotels: "+totalHotel);
	}
	private static String sicCode="5812";
	List<String[]> data=null;
	
	String sicdetails[];
	int i=0;
	public  void innerProcess() throws Exception {
		// TODO Auto-generated method stub
		//U.serializedSicData();
		data=new ArrayList<>();
		int count=0;
		CSVWriter writer;
		StringWriter sw;
		sw=new StringWriter();
		writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		sicdetails=Sic.sicInfo(sicCode);
		String mainHtml=U.getHTML("https://www.tripadvisor.in/Restaurants-g150768-Mexico.html#LOCATION_LIST");
		String nextButtonSection=U.getSectionValue(mainHtml, "Previous\n</span>", "</a>");
//		U.log("===="+nextButtonSection);
		String hotelRegion=U.getSectionValue(mainHtml, "<div class=\"geos_grid\">", "<div class=\"unified pagination \">");
		//U.log(hotelRegion);
		//String hotelRegion2="";
		while (U.getSectionValue(nextButtonSection, "href=\"", "\"")!=null) {
			String nextUrl=U.getSectionValue(nextButtonSection, "href=\"", "\"");
			U.log(count+"_"+nextUrl);
			String paginationHtml=U.getHTML("https://www.tripadvisor.com"+nextUrl);
			nextButtonSection=U.getSectionValue(paginationHtml, "<span class=\"paging pageDisplay\">", "Next");
			//count++;
			hotelRegion+=U.getSectionValue(paginationHtml, "<ul class=\"geoList\">", "<div class=\"pagination\">");
		}
		//U.log(hotelRegion);
		String []RegionUrls=U.getValues(hotelRegion, "href=\"", "\"");
		//U.log(RegionUrls.length);
		for (String RegionUrl : RegionUrls) {
			U.log(RegionUrl);
		/*	if (data.size()>10000) {
				break;
			}*/
			getHotelsList(RegionUrl);
//			break;//Only for one region
		}
//		RegionUrls=U.getValues(hotelRegion, "<li class=\"ui_column is-12-mobile is-4-tablet is-3-desktop\">", "/a>");
//		//U.log(RegionUrls.length);
//		for (String RegionUrl : RegionUrls) {
//			U.log(i);
////			if (data.size()>1000) {
////				break;
////			}
//			//getHotelsList(RegionUrl);
//		}
		U.log(data.size());
		writer.writeAll(data);
		FileUtil.writeAllText(U.getCachePath()+"Tripadvisor_Restaurant10000.csv", sw.toString());
		sw.close();
		writer.close();
	}

	private void getHotelsList(String RegionUrlSec) throws Exception {
		U.log(RegionUrlSec);
		String geono=U.getSectionValue(RegionUrlSec, "Restaurants-g", "-");
		String RegionUrl="https://www.tripadvisor.in/RestaurantSearch?Action=FILTER&geo="+geono+"&ajax=1&itags=10591&sortOrder=relevance&availSearchEnabled=true";
		U.log(RegionUrl);
		//U.log(U.getCacheFileName(RegionUrl));
		String html=U.getHTML(RegionUrl);
		//U.log(html);
		String hoteUrlSec=U.getSectionValue(html, "<div id=\"EATERY_SEARCH_RESULTS\">", "<div class=\"deckTools btm\">");
		if (html.contains("e couldn't find a restaurant with everything you ask")||html.contains("are no restaurants in this area")) {
			return;
		}
		int oa=0;
		while (true) {
			if (!html.contains("unified pagination")) {
				break;
			}
			oa+=30;
			String nextUrl="https://www.tripadvisor.in/RestaurantSearch?Action=PAGE&geo="+geono+"&ajax=1&itags=10591&sortOrder=relevance&o=a"+oa+"&availSearchEnabled=true";
//			U.log("!@@!@_"+nextUrl);
			String paginationHtml=U.getHTML(nextUrl);
//			nextButtonSection=U.getSectionValue(paginationHtml, "'previous'", "Next");
			//count++;
			hoteUrlSec+=U.getSectionValue(paginationHtml, "<div id=\"EATERY_SEARCH_RESULTS\">", "<div class=\"deckTools btm\">");
			//U.log("==============="+nextButtonSection);
			if (paginationHtml.contains("nav next disabled\">")) {
				break;
			}
		}
		String hotelsUrl[]=U.getValues(hoteUrlSec, "<div class=\"title\">", "<div class=\"rating");
		totalHotel+=hotelsUrl.length;
		//U.log(hotelsUrl.length);
		for (String hotelSec : hotelsUrl) {
			//U.log(hotelSec);
			String hotelUrl="https://www.tripadvisor.com"+U.getSectionValue(hotelSec, "href=\"", "\"");
			//U.log(hotelUrl);
			try {
				/*if (data.size()>10000) {
					break;
				}*/
				addHotelDetails(hotelSec,hotelUrl);
			} catch (Exception e) {
				U.log(e.getMessage());
			}
		}
	}

	private void addHotelDetails(String hotelSec, String hotelUrl) throws Exception {
	//if (!hotelUrl.contains("https://www.tripadvisor.com/Hotel_Review-g154966-d10816629-Reviews-The_Floating_B_B-Yellowknife_Northwest_Territories.html"))return;//for single Hotel
		
		U.log(i+" =- "+hotelUrl+"\n"+U.getCache(hotelUrl));
//		i++;
		String hotelHtml=U.getHTML(hotelUrl);
		
		//------------------------------------------=====Company Name=======-----------------------------------------------------
		String hotelName=U.getSectionValue(hotelHtml, "\"name\" : \"", "\"");
		if (hotelName==null) {
			hotelName=U.getSectionValue(hotelHtml, "\"name\":\"", "\"");
		}
		//------------------------------------------=====Address Sec=======-----------------------------------------------------
		String cityLatlon="";
		String address=(U.getSectionValue(hotelHtml, "span class=\"street-address\">", "</span>")!=null)?U.getSectionValue(hotelHtml, "span class=\"street-address\">", "</span>"):ALLOW_BLANK;
		U.log("--"+address);
		String neighborhood=(U.getSectionValue(hotelHtml, "span class=\"extended-address\">", "</span>")!=null)?U.getSectionValue(hotelHtml, "span class=\"extended-address\">", "</span>"):ALLOW_BLANK;
		String state=(U.getSectionValue(hotelHtml, "\"addressRegion\":\"", "\"")!=null)?U.getSectionValue(hotelHtml, "\"addressRegion\":\"", "\""):ALLOW_BLANK;
		String city=(U.getSectionValue(hotelHtml, "<span class=\"locality\">", "</span>")!=null)?U.getSectionValue(hotelHtml, "<span class=\"locality\">", "</span>"):ALLOW_BLANK;
		String zip=ALLOW_BLANK;
		if (city!=null) {
			zip=Util.match(city, "\\d+");
			city=city.replaceAll("\\d+|,", "");
			U.log(city);
		}
		if (state!=null) {
//			U.log(zip);
			state=U.findStateFromZip(zip);
			if (state.equals("Mexico City")) {
				city=MXStates.getMexicoCityMunicipalites(zip);
			}
		}
		
		//--if----------------------------------------=====Latlon Sec=======-----------------------------------------------------
		//U.log(latLonSec);
		String hotelId=U.getSectionValue(hotelUrl, "-d", "-Reviews");
		String latLonSec=U.getSectionValue(hotelHtml, "\"value\":"+hotelId+",\"coords\":\"", "\"");
		String latLon[]={ALLOW_BLANK,ALLOW_BLANK};
		if (latLonSec!=null) {
			latLon=U.findLatLng(latLonSec);
//			String add[]=JsonGoogleApiLatlonExtraction.testJsonCode(add)
			
		}
		if (city!=null) {
			String latlonAdd[]=U.getAddressGoogleApi(latLon);
			if (FuzzySearch.tokenSortRatio(city, latlonAdd[2])<90) {
				cityLatlon=latlonAdd[2];
			}
		}
		//------------------------------------------=====ContactSec=======-----------------------------------------------------
		String email=ALLOW_BLANK;
		String contactNo=U.getSectionValue(hotelHtml, "ui_icon phone\">", "</div>");
		//U.log("==="+contactNo);
		if (contactNo!=null) {
			contactNo=U.removeHtml(contactNo);
		}else {
			contactNo=null;
		} 
		//------------------------------------------=====EmailSec=======-----------------------------------------------------
//		if (hotelHtml.contains("<span>E-mail")) {
			email="https://www.tripadvisor.com/EmailHotel?detail="+hotelId+"&guests=2&isOfferEmail=false&rooms=1";
			U.log(U.getCache(email));
			try {
				email=getEmail(email);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
//		}
		//------------------------------------------=====WebSec=======-----------------------------------------------------
		String webUrl=ALLOW_BLANK;
		if (hotelHtml.contains("website</span>")) {
			String webUrl1="https://www.tripadvisor.com/ShowUrl?&excludeFromVS=false&odc=BusinessListingsUrl&d="+hotelId+"&url=";
//			U.log("===="+webUrl);
			try {
				webUrl=getRedirectedUrl(webUrl1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			U.log("===="+webUrl);
		}
		U.log("=-=|"+data.size());
		try {
			String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(hotelName),U.toTitleCase(address),U.toTitleCase(neighborhood),U.toTitleCase(city),U.toTitleCase(state),zip,U.formatNumbersAsCode(contactNo),null,webUrl.trim(),email,null,null,null,null,null,latLon[0],latLon[1],hotelUrl,U.getTodayDate(),cityLatlon};
			if (uniqueKey.add((hotelName.trim()+address.trim()).toLowerCase())) {
				data.add(out);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		U.log("=-="+data.size());
//		data.addSicCode("7011");
//		data.addToDirectory(webUrl, hotelName);
//		data.addContactDetails(ALLOW_BLANK,ALLOW_BLANK, ALLOW_BLANK, email, contactNo);
//		data.addCompanyDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
//		data.addReferenceUrl(hotelUrl);
//		data.addAddress(address,city, state, zip);
//		data.addLatitudeLongitude(latLon[0], latLon[1]);
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time","CITY_LATLON"
	};
	public static String getRedirectedURL(String url) throws IOException {
		URL baseURl=new URL(url);
	    HttpURLConnection con = (HttpURLConnection) baseURl.openConnection();
	    con.setReadTimeout(5000);
	    con.setInstanceFollowRedirects(false);
	    int responseCode=0;
	    try {
	    	con.connect();
	    	con.getInputStream();
	    	responseCode=con.getResponseCode();
	    	U.log("response code : "+con.getResponseCode()+"\t"+url);
		} catch (Exception e) {
			// TODO: handle exception
		}
	    
	    
	    if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
	        String redirectUrl = con.getHeaderField("Location");
	        if (!redirectUrl.startsWith("http")) {
	        	redirectUrl=baseURl.getProtocol()+"://"+baseURl.getHost()+redirectUrl;
			}
	        if (redirectUrl.equals(url)) {
				return url;
			}
	        return getRedirectedURL(redirectUrl);
	    }
	    return url;
	}
	private String getEmail(String email) throws Exception {
		String html=U.getHTML(email);
		if (html!=null) {
			String emailSec=U.getSectionValue(html, "class=\"ui_input_text disabled\" value=\"", "\"");
			if (emailSec!=null) {
				return emailSec;
			}
		}
		return null;
	}
	public String getRedirectedUrl(String url) throws Exception{
		url = url.replaceAll(" ", "%20");
		String fileName = U.getCache(url);
		U.log("File Name "+fileName);
		File cacheFile = new File(fileName);
		if (cacheFile.exists()){
			return FileUtil.readAllText(fileName).replaceAll("http://reservation.worldweb.com|https://clk.tradedoubler.com", "").trim();
		}
		String location ="";
		location=getRedirectedURL(url);
	    U.log("!@#$$$ "+location);
	    if (!cacheFile.exists())
	    	FileUtil.writeAllText(fileName, location);
		return location.trim();
	}
	private String crawl(String url){
		try {
			//if (!url.startsWith("http"))return;
			Response response = Jsoup.connect(url).followRedirects(false).execute();
			//System.out.println(response.statusCode() + " : " + url);
			if (response.hasHeader("location")) {
			
				String redirectUrl = response.header("location");
				//U.log(redirectUrl);
				if (redirectUrl!=null) {
					if (redirectUrl.startsWith("http")||redirectUrl.startsWith("www")||redirectUrl.startsWith("m.")) {
						if (!redirectUrl.replaceAll("http://www.|http://m.", "").contains(url.replaceAll("http://www.|http://m.", ""))) {
							return crawl(redirectUrl);
						}
					}
				}
			}else {
				return url;
			}
		} catch (Exception e) {
//			/return url;
		}
		return url;
		
	}
}
