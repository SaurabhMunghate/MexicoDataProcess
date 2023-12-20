package com.tequila.extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.html.HTML;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.U;

public class ExtractRestrauntDataMexicoCity {
	static int cnt=1;
	static CSVWriter writer = null;
	static CSVWriter returnedUrlwriter = null;

//	static WebDriver driver = null;
//	public static final String MY_GECKO_PATH = System.getProperty("user.home")+File.separator+"geckodriver";

	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	static List<String[]> addFetchedDataList = new ArrayList<String[]>();
	public static void main(String args[]) throws Exception {
//		System.setProperty("webdriver.gecko.driver", MY_GECKO_PATH);
//		driver = new FirefoxDriver();
//		returnedUrlwriter = new CSVWriter(new FileWriter("/home/shatam-17/Mexico_Data_JUN_2019/ReturnedUrls-1.csv"));
//		writer = new CSVWriter(new FileWriter("/home/shatam-17/Mexico_Data_JUN_2019/Restraunt_Mexico_City-1.csv"));
//		writer.writeNext(HEADER);
		String sicDetails[] = Sic.sicInfo("5812");
		String sourceUrl = "https://www.yelp.com/search?find_desc=&find_loc=Mexico&start=0";
		String mainListingHtm ="";
		for(int i = 0;i<=1000;i=i+10) {
			mainListingHtm += U.getHTML("https://www.yelp.com/search?find_desc=&find_loc=Mexico&start="+i);
		}
		String restruaunSec []= U.getValues(mainListingHtm, "<h3 class=\"lemon--h3__373c0__sQmiG heading--h3__373c0__1n4Of alternate__373c0__1uacp\">", "</p></div></div></div></div></div>");
		for(String rest:restruaunSec) {
			U.log(rest);
			String yelpurl= "https://www.yelp.com"+U.getSectionValue(rest, "href=\"", "\"");
			String yelprestHtml = U.getHTML(yelpurl);

//			try {
//				String restaurant_name = U.getSectionValue(rest, "rel=\"\">", "</a></h3>");
//				U.log("Restaurant_Name::::::::::::::::"+restaurant_name);
//				String yearInBiz = "";
//				String empCount = "";
//				String minAnnualSales = "";
//				String email = "";
//				String url = "";
//				String fax = "";
//				String phone = "";
//				String add[] = {"","","","",""};
//				String latLng[] = {};
//				phone = U.getSectionValue(yelprestHtml,"<span itemprop=\"telephone\">","</span>");
//				String addressSec = U.getSectionValue(yelprestHtml, "<address itemprop=\"address\"", "itemprop=\"addressCountry\">");
//				if(addressSec!=null){
//					add[0]=U.getSectionValue(addressSec, "<span itemprop=\"streetAddress\">", "<");
//					add[1]=U.getSectionValue(addressSec, "br>", "<");
//					add[2]=U.getSectionValue(addressSec, "span itemprop=\"addressRegion\">", "<");
//					add[3]=U.getSectionValue(addressSec, "span itemprop=\"addressLocality\">", "<");
//					add[4]=U.getSectionValue(addressSec, "<span itemprop=\"postalCode\">", "<");
//				}
//				String latlngSec = U.getSectionValue(yelprestHtml, "<use xlink:href=\"#18x18_directions\" />", " </div>");
//				if(latlngSec!=null) {
//					String mapHtml = U.getHTML("https://www.yelp.com"+U.getSectionValue(latlngSec, "<a href=\"", "\""));
//					latLng[0]=U.getSectionValue(mapHtml, "{\"latitude\":", ",");
//					latLng[1]=U.getSectionValue(mapHtml,"\"longitude\":", "}");
//				}
//				url = U.getSectionValue(yelprestHtml, "<a href=\"biz_redir?url=http%3A%2F%2F", "&amp");
//				U.log(Arrays.toString(add) + ":::::::::::::::::::;" + Arrays.toString(latLng));
//				
//				String[] data  = {cnt+"",U.toTitleCase(sicDetails[0]),U.toTitleCase(sicDetails[1]),U.toTitleCase(sicDetails[3]),U.toTitleCase(sicDetails[4]),U.toTitleCase(sicDetails[5]),U.toTitleCase(sicDetails[6]),
//						U.toTitleCase(restaurant_name.trim()),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4],
//						phone.trim(),fax,url,email,"","",minAnnualSales,empCount,yearInBiz,
//						latLng[0].trim(),latLng[1].trim(),sourceUrl,U.getTodayDate()};
//				addFetchedDataList.add(data);
//				cnt++;
//			}
//			catch (Exception e) {
//				// TODO: handle exception
//				returnedUrlwriter.writeNext(new String[] {yelpurl});
//			}
		}
		returnedUrlwriter.close();
		writer.writeAll(addFetchedDataList);
		writer.close();
	}
}
