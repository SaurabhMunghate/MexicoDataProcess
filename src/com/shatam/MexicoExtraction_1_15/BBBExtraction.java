package com.shatam.MexicoExtraction_1_15;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringEscapeUtils;

import com.shatam.utils.U;
import com.shatam.utils.Util;

public class BBBExtraction {

	public static void main(String[] args) {
		if(args.length ==2){
			startIndex = args[0].trim();
			stopIndex = args[1].trim();
		}else if(args.length ==1){
			startIndex = args[0].trim();			
		}else{
			U.log("Catgeory is not given");
			start(false);
		}
		start(true);
	}
	
	private static final String COUNTRY = "MEX"; //CAN

	private static final String MAIN_URL = "https://www.bbb.org";
	private static final String CATEGARY_URL = "https://www.bbb.org/es/mx/categories";
	
	private static final String HEADER[] ={
			"Srnoid","Industry_sector=industry_sector","Spanish_industry_sector=sector De La Industria","Major_sic_category=principales_categoría_sic",
			"Sub_sic_category=categoría Del Sic","Product_desc=product_description","Sp_product_description=productos_y_descripción_de_los_servicios",
			"Company_Name=Nombre De La Empresa","Address=address_dirección","Neighbourhood","City=city_ciudad","State=state_estado","Zipcode=códigopostal","PHONE=Telephone_Teléfono","FAX=Fax",
			"URL=Web Address","EMAIL = Email_Correo Electrónico","Contact Person=contacto_Ejecutivo","Title=Title_título","Annual_sales_volume=volumen_anual_de_ventas",
			"Number_of_employees=número_de_empleados","Years_in_biz","LATUTUDE","LONGITUDE","UniqueUrl","Fetching Time"
	};
/*		{ "SrNo=id", "INDUSTRY_SECTOR","SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR","SIC_SUB", "PRODUCT_DESC","SPANISH_PRODUCT_DESC", 
				"COMPANY_NAME","ADDRESS","NEIGHBORHOOD" ,"CITY", "STATE", "ZIPCODE", "PHONE", "FAX","URL", "EMAIL", "CONTACT_PERSON","TITLE", "ANNUAL_SALES_VOL","EMP_COUNT", "YEARS_IN_BIZ", "LATITUDE", "LONGITUDE","REFERENCE_URL", "Fetching Time" };
*/
	
	
	private static String startIndex = null;
	private static String stopIndex = null;
	
	private static Scanner sc = new Scanner(System.in);
	
	private static List<String[]> writeLines = null;
	private static void start(boolean flag){
		U.log("Start BBB Extraction");
		if(!flag)
			displayMenu();
		else{
			try {
				extraction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void extraction() throws Exception{
		U.log("StartIndex :"+startIndex);
		U.log("StopIndex :"+stopIndex);
		
		if(startIndex != null && startIndex.trim().length() == 1){
			startIndex = "/"+startIndex.trim().toLowerCase();
		}
		if(stopIndex != null && stopIndex.trim().length() == 1){
			stopIndex = "/"+stopIndex.trim().toLowerCase();
		}
		
		if(startIndex != null && stopIndex == null)
			startExtraction(startIndex);
		else if(startIndex != null && stopIndex != null){
			int start = categoryList.indexOf(startIndex);
			int end  = categoryList.indexOf(stopIndex);
//			U.log(start+"\t"+end);
			if(start < end){
				for(int i = start; i <= end; i++){
//					U.log(categoryList.get(i));
					startExtraction(categoryList.get(i));
				}
			}else{
				for(int i = end; i <= start; i++){
//					U.log(categoryList.get(i));
					startExtraction(categoryList.get(i));
				}
			}
		}
			

	}
	
	private static void startExtraction(String catgeory) throws Exception{
		
		writeLines = new ArrayList<>();
		
		String categoryListHtml = U.getHTML(CATEGARY_URL + catgeory);
		String categorySection = U.getSectionValue(categoryListHtml, "categoryGroups", "</script>");

		HashMap<String,String> uniqueListing = new HashMap<>();
		
		HashMap<String,String> categoryListing = new HashMap<>();
		
		String[] categorySec = U.getValues(categorySection, "\"title\":", "}");
		
		for(String sec : categorySec){

			if (sec.contains("All Categories - Mexico") || sec.contains("All Categories - Canada") || U.getSectionValue(sec, "url\":\"", "\"")==null) {
				continue;
			}
/*			String categoryUrl = U.getSectionValue(sec, "url\":\"", "\"");
			U.log("categoryUrl :::"+MAIN_URL+categoryUrl);
			String html = U.getHTML(MAIN_URL+categoryUrl);
			
			String totalResult = U.getSectionValue(html, "totalResults\":", ",\"");
			if(Integer.parseInt(totalResult.trim()) == 0)continue;			
			U.log(totalResult);

			
			String totalPage = U.getSectionValue(html, "totalPages\":", ",\"");
			if(Integer.parseInt(totalPage.trim()) > 1){
				U.log("total Paging :"+totalPage);
				extractPaging(MAIN_URL+categoryUrl, Integer.parseInt(totalPage.trim()));
			}
*/
			String categoryUrl = U.getSectionValue(sec, "url\":\"", "\"");
			U.log("categoryUrl :::"+MAIN_URL+categoryUrl);
			
			String searchText = U.getSectionValue(sec, "\"", "\"");
			U.log("Category ::"+searchText);
			String html = getListingsHtml(searchText.trim(), 10000);
			if(html == null){
				U.log("Result : 0");
				continue;
			}
			String lisitngSec = U.getSectionValue(html, "\"results\"", "\"totalResults\"");
			String lsitings[] = U.getValues(lisitngSec, "{\"id\":", "}");
			U.log("Found ::"+lsitings.length);
			for (String lisitng : lsitings) {;
				String lisitingUrl=U.getSectionValue(lisitng, "eportUrl\":\"", "\"");
				if(lisitingUrl != null){
					lisitingUrl = lisitingUrl.replace("http://www", "https://www");
					uniqueListing.put(lisitingUrl,lisitng);
					categoryListing.put(lisitingUrl,searchText);
				}
			}

		}//eof for
		

		//To extract Listing details
		findListingDetails(uniqueListing,categoryListing);
		
		U.log("Total No. of Records :"+writeLines.size());
		//write file here
		if(writeLines.size() > 0){
			U.writeCsvFile(HEADER, getMoreCompanyRecord(writeLines), U.getCachePath()+"BBB-Category-"+catgeory.replace("/", "").toUpperCase()+"-All"+".csv");
		}
		else U.log("File is not written.");
	}
	
	private static void findListingDetails(HashMap<String, String> uniqueList, HashMap<String, String> categoryListing) throws IOException {
		int index = 0;
		for(String keyUrl : uniqueList.keySet()){
			try{
			U.log("url ::"+keyUrl);
			String listingHtml = U.getHTML(keyUrl);
			String listingData = uniqueList.get(keyUrl);
//			U.log("uniqueListing : "+listingData);
			
/*			listingHtml = StringEscapeUtils.unescapeHtml4(listingHtml);
			listingData = StringEscapeUtils.unescapeHtml4(listingData);
*/			listingData = listingData.replace("\\\"", "'");
			
			String companyName = U.getSectionValue(listingData, "businessName\":\"", "\"");
			
			//U.log("companyName : "+companyName);
			
			
			String alternateBizName = U.getSectionValue(listingHtml, "Alternate Business Names", "</ul>");
			if(alternateBizName != null){
				String alternateNames[] = U.getValues(alternateBizName, "company-info__list-item\">", "</li>");
				
				for(String alternateName : alternateNames){
					if(!companyName.toLowerCase().contains(alternateName.trim().toLowerCase())){
						companyName +="####"+alternateName.trim();
					}
				}
			}

			//Address
			String address = U.getSectionValue(listingData, "address\":\"", "\"");
			//U.log("address : "+address);
			
			//city
			String city = U.getSectionValue(listingData, "city\":\"", "\"");
//			U.log("city : "+city);
			
			//state
			String state = U.getSectionValue(listingData, "state\":\"", "\"");
			//U.log("state : "+state);
			
			//zip
			String zip = U.getSectionValue(listingData, "postalcode\":\"", "\"");
			//U.log("zip : "+zip);
			
			//lat-lng
			String[] latLng = {"",""};
			String latLngSec=U.getSectionValue(listingData, "location\":\"", "\",\"businessId");
			if(latLngSec != null){
				latLng = latLngSec.split(",");
			}
			//U.log("LatLng is "+Arrays.toString(latLng));
			
			//phone
			String phone = "";
			phone = Util.match(listingHtml, "phone-number-link\" href=\"tel:(.*?)\">(.*?)<",2);
			String phoneSec = U.getSectionValue(listingHtml, "Additional Phone Numbers", "</ul>");
			if(phoneSec != null){
				int i = 0;
				String[] additionalPhone = U.getValues(phoneSec, "company-info__list-item\">", "<");
				//phone = U.formatNumbersAsCode(phone);
				for(String addPhone : additionalPhone){
					if(phone != null)phone += " ; " +addPhone.trim();
					else phone = addPhone.trim();
					i++;
					if(i==3)break;
				}
			}
			if(phone == null)phone = "";
			//U.log("phone : "+phone);
			
			//fax
			String fax = "";
			String faxSec = U.getSectionValue(listingHtml, "Fax Numbers", "</ul>");
			if(faxSec != null){
				int j =0;
				String[] additionalFax = U.getValues(faxSec, "company-info__list-item\">", "<");
				//phone = U.formatNumbersAsCode(phone);
				for(String addFax : additionalFax){
					if(fax != "")fax += " ; " +addFax.trim();
					else fax = addFax.trim();
					j++;
					if(j==3)break;
				}
			}
			if(fax == null)fax = "";
			//U.log("fax : "+fax);
			
		
			
			//Bussiness Category
			String businessCat = U.getSectionValue(listingData, "\"tobText\":\"", "\"");
			if(businessCat.trim().isEmpty())
				businessCat = categoryListing.get(keyUrl);
//			U.log("businessCat : "+businessCat);
			
			//-----------Website Url-----------
			String webSiteUrl = U.getSectionValue(listingHtml, "business-buttons__button\" rel=\"nofollow\" href=\"", "\"");
			//U.log("webSiteUrl : "+webSiteUrl);
			if(webSiteUrl == null)webSiteUrl = "";
			
			//--------Email-----------
			String email = "";
			email = U.getSectionValue(listingHtml, "business-buttons__button\" href=\"mailto:", "\"");
			//U.log("email : "+email);
			if(email == null)email = "";
			else email = email.trim();
			
			String additionalEmailSection = U.getSectionValue(listingHtml, "Additional Email Addresses", "</div>");
			String additionalEmailID[] = {};			
			if(additionalEmailSection != null){
				additionalEmailID = U.getValues(additionalEmailSection, "company-info__list-item\">", "</li>"); 
				if(additionalEmailID.length != 0){
					for(String addemail : additionalEmailID){
						addemail = addemail.trim();
						if(!email.contains(addemail)){
							if(email.trim().length()>4){
								email += " ; "+addemail;
							}
							else{
								if(email.length()==0) email = addemail;
							}
						}
					}
				}
			}
			//U.log("email : "+email);
			
			//openedInYear
			String openedInYear = "";
			String businessStarted = U.getSectionValue(listingHtml, "Business Started:&nbsp;<span>", "</span>"); //Business Started
			String businessIncorporated = U.getSectionValue(listingHtml, "Business Incorporated:&nbsp;<span>", "</span>"); //Business Incorporated
			if(businessStarted != null){
				openedInYear = businessStarted;
			}
			else if (businessIncorporated != null){
				openedInYear = businessIncorporated;
			}
			else{
				openedInYear = "";
			}	
			if(openedInYear.length()>0){
				openedInYear = openedInYear.replace(" in ", "").replaceAll("[A-Z]{2}", "");
			}
//			U.log("openedInYear : "+openedInYear);
			
			//Contact Person	
			String contactData = null;
			
			String contactInform = U.getSectionValue(listingHtml, "Contact Information", "</ul>"); //contact information
			String businessManagement = U.getSectionValue(listingHtml, "Business Management", "</ul>"); // Business management
			
			if(contactInform !=null ){
				contactData  = contactInform;
			}else if(businessManagement !=null){
				contactData =  businessManagement;
			}
			
			if(contactData != null){
				String [] dataValues = U.getValues(contactData, "company-info__list-item\">", "</li>");
				
				for(String data : dataValues){				
					if(data.contains(",")){ 
						//for multiple contact person and title
						data = data.replace(",,", ",").replaceAll("Principal: |Customer Service: ", "");
						String person = Util.match(data, "(.*?),",1);
						String title = Util.match(data, ",\\s*(.*)",1); //,\\s*([a-z|A-Z]*)
						//U.log(person+"\t"+title);
/*						String[] out = {""+index++ ,"","","","",businessCat,"",companyName,address,"",city,state,zip,phone,fax,webSiteUrl,email,person,title,"","",openedInYear,latLng[1],latLng[0],keyUrl};
						writer.writeNext(out);
*/
						add(""+index++, categoryListing.get(keyUrl), businessCat, companyName, address, "", city, state, zip, phone, fax, webSiteUrl, email, person, title, openedInYear, latLng[0], latLng[1], keyUrl);
					}
					else{
						//contact person with no title
						data = data.replace(",,", ",").replaceAll("Principal: |Customer Service: ", "");
						String person = data.trim();
/*						String[] out = {""+index++,"","","","",businessCat,"",companyName,address,"",city,state,zip,phone,fax,webSiteUrl,email,person,"","","",openedInYear,latLng[1],latLng[0],keyUrl};
						writer.writeNext(out);
*/						add(""+index++, categoryListing.get(keyUrl), businessCat, companyName, address, "", city, state, zip, phone, fax, webSiteUrl, email, person, "", openedInYear, latLng[0], latLng[1], keyUrl);
					}
				}
			}
			else{
/*				String[] out = {""+index++,"","","","",businessCat,"",companyName,address,"",city,state,zip,phone,fax,webSiteUrl,email,"","","","",openedInYear,latLng[1],latLng[0],keyUrl};
				writer.writeNext(out);
*/	
				add(""+index++, categoryListing.get(keyUrl), businessCat, companyName, address, "", city, state, zip, phone, fax, webSiteUrl, email, "", "", openedInYear, latLng[0], latLng[1], keyUrl);	
			}
			}catch(Exception e){}
			
		}
	}
	
	private static void add(String index,String industrySector, String catgeory, String companyName, String address, String neighbourhood, String city, String state,
			String zip, String phone,String fax, String url, String email,String contactPerson,String title,String yearInBiz, String lat, String lng, String refUrl){
		
		writeLines.add(new String[]{
				index,industrySector,"","","",catgeory,"",companyName,address,neighbourhood,city,state,zip,phone,fax,url,email,
				contactPerson,title,"","",yearInBiz,lat,lng,refUrl,U.getTodayDate()
		});
	}
	
	private static List<String[]> getMoreCompanyRecord(List<String[]> readLines){
		List<String[]> newDataset = new ArrayList<>();
		String lines[] = null;
		int index = 0;
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			//company name
			if(lines[7].contains("####")){
				String[] companyNames = lines[7].split("####");
				for(String name : companyNames){
					List<String> dataset = Arrays.asList(lines);
					dataset.set(7, name);
					newDataset.add(dataset.toArray(new String[dataset.size()]));
				}
			}else{
//				lines[0] = ""+(index++);
				newDataset.add(lines);				
			}
			//index++;
		}//eof while
		return newDataset;
	}
	
/*	private static void extractPaging(String categoryUrl, int totalPaging) throws IOException{
		
		for(int i = 2; i <= totalPaging; i++){
			categoryUrl = categoryUrl+"?page="+i+"&sort=Rating";
			U.log("Paging category url ::"+categoryUrl);
			String html = U.getHTML(categoryUrl);
			html = StringEscapeUtils.unescapeHtml4("");
		}
	}
*/	
	private static String getListingsHtml(String searchText,int i) throws IOException{
		String strUrl="https://www.bbb.org/api/search?find_text="+URLEncoder.encode(searchText,java.nio.charset.StandardCharsets.UTF_8.toString())+"&find_type=Category&page=1&pageSize="+i+"&country="+COUNTRY;
		U.log("strUrl : "+strUrl);
		String categoryHtml = U.getHTML(strUrl);
		if (categoryHtml.contains("totalPages\":0")) {
			return null;
		}else if (!categoryHtml.contains("totalPages\":1")) {
			i += 10000;
			categoryHtml = getListingsHtml(searchText,i);
		}
		return categoryHtml;
	}
	
	private static List<String> categoryList = new ArrayList<String>(){
		{
			add("/a");
			add("/b");
			add("/c");
			add("/d");
			add("/e");
			add("/f");
			add("/g");
			add("/h");
			add("/i");
			add("/j");
			add("/k");
			add("/l");
			add("/m");
			add("/n");
			add("/o");
			add("/p");
			add("/q");
			add("/r");
			add("/s");
			add("/t");
			add("/u");
			add("/v");
			add("/w");
			add("/x");
			add("/y");
			add("/z");
		}
	};
	private static void displayMenu(){
		U.log("****** Menu *******");
		U.log("1] Execute For Single Category");
		U.log("2] Execute For Multiple Category");
		U.log("3] Exit");
		U.log("Choose any one option ..... ");
		try {
			choose();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void choose() throws Exception{
		int num = sc.nextInt();
		switch(num){
		case 1 :
			U.log("");
			System.out.print("Enter Category : ");
			startIndex = sc.next();
			extraction();
			break;
		case 2 :
			U.log("");
			System.out.print("Enter Catgetory Range 1 : ");
			startIndex = sc.next();
			U.log("");
			System.out.print("Enter Catgetory Range 2 : ");
			stopIndex = sc.next();
			extraction();
			break;
		case 3 :
			U.log("Exit");
			System.exit(0);
			break;
		default :
			U.log("Wrong option, choose again..");
			choose();
			break;
		}
	}
}
