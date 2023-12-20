package com.canada.data;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.shatam.utils.U;


public class JustDialExtract {
	public static void main(String[] args) {
		String city="Nagpur";
		String searchTerm="Schools";
		int maxPages=20;
		try {
			startprocess(city,searchTerm,maxPages);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static HashMap<String, String> nosMap=new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
		put("\\9d001", "0");
		put("\\9d002", "1");
		put("\\9d003", "2");
		put("\\9d004", "3");
		put("\\9d005", "4");
		put("\\9d006", "5");
		put("\\9d007", "6");
		put("\\9d008", "7");
		put("\\9d009", "8");
		put("\\9d010", "9");
		put("\\9d011", "+");
		//put("\\9d006", "6");
	}};	
	private static HashMap<String, String> mobIconMap=new HashMap<String,String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 2803752892907698264L;

		{
			put("icon-dc",		"+");
			put("icon-fe",		"(");
			put("icon-ji",		"9");
			put("icon-yz",		"1");
			put("icon-hg",		")");
			put("icon-ba",		"-");
			put("icon-nm",		"7");
			
			put("icon-wx",		"2");
			put("icon-po",		"6");
			put("icon-vu",		"3");
			
			put("icon-ts",		"4");
			put("icon-acb",		"0");
			put("icon-lk",		"8");
			put("icon-rq",		"5");
		}
	};
	static String mapUrl="https://www.justdial.com/functions/maps.php";
	static ArrayList<String[]> outData=new ArrayList<>();
	private static void startprocess(String city, String searchTerm, int maxPages) throws Exception {
		String testPagerUrl="https://www.justdial.com/"+city+"/"+searchTerm;
		try {
			U.log(testPagerUrl);
			String testPageHtml=U.getHTML(testPagerUrl);
			String lat=U.getSectionValue(testPageHtml, "id=\"median_latitude\" value=\"", "\"");
			String lon=U.getSectionValue(testPageHtml, "id=\"median_longitude\" value=\"", "\"");
//			U.log(lat);
//			U.log(lon);
//			String startPage=U.getSectionValue(testPageHtml, "", "");
			String stopPage=U.getSectionValue(testPageHtml, "var paginationLastPageNum = ", ";");
			String catId=U.getSectionValue(testPageHtml, "var nationalCategoryId = ", ";");
			String currentPage=U.getSectionValue(testPageHtml, "var paginationPageNum = ", ";");
			if(lat!=null&&catId!=null&stopPage!=null&currentPage!=null) {
				maxPages=Integer.parseInt(stopPage);
				int j=0;
				for (int i = 1; i <= maxPages; i++) {
					String nextUrl="https://www.justdial.com/functions/ajxsearch.php?national_search=0&act=pagination_new&city="+city+"&search="+searchTerm+"&where=&catid=0&psearch=&prid=&page="+i+"&SID=&mntypgrp=0&toknbkt=&bookDate=&jdsrc=&median_latitude="+lat+"&median_longitude="+lon+"&ncatid="+catId+"&mncatname="+searchTerm+"&dcity="+city+"&pncode=999999&htlis=0";
					String html=U.getHTML(nextUrl);
					String vals[]=U.getValues(html, "\"closedown_flag", "<\\/section>");
					if(vals!=null)
						for (String listing : vals) {
//							U.log(val);
							String dataUrl=U.getSectionValue(listing, "data-href=\\\"", "\\\"");
							U.log((j++)+" "+dataUrl);
							String mobileNos[]=U.getValues(listing, "<span class=\\\"mobilesv ", "\\\"");
							U.log(Arrays.toString(mobileNos));
							StringBuilder mobNo=new StringBuilder();
							for (String string : mobileNos) {
								System.out.print(mobIconMap.get(string));
								mobNo.append(mobIconMap.get(string));
							}
							String storeURl=U.getSectionValue(listing, " href=\\\"", "\\\"");
							
//							if (storeURl==null) {
//								storeURl=U.getSectionValue(listing, "href=\"", "\"");
//							}
//							U.log("==-=-"+storeURl.replace("\\", "").replace("&tab=gallery", ""));
//							String storehtml=U.getHTML(storeURl.replace("\\", "").replace("&tab=gallery", ""));
							String completeAddress=U.getSectionValue(listing, "<span class=\\\"cont_fl_addr\\\">", "<\\/span>");
//							if (completeAddress==null) {
//								completeAddress=U.getSectionValue(storehtml, "<span class=\"adrstxtr\" id=\"fulladdress\">", "</span>");
//							}
//							U.log(completeAddress);
//							String addressLoc=U.getSectionValue(storehtml, "\"addressLocality\": \"", "\"");
//							String postalCode=U.getSectionValue(storehtml, "\"postalCode\": \"", "\"");
//							String addressRegion=U.getSectionValue(storehtml, "\"addressRegion\": \"", "\"");
//							String ratings=U.getSectionValue(storehtml, "\"ratingValue\": \"", "\"");
//							String website=U.getSectionValue(storehtml, "<span class=\"ic_web\"></span>", "</p>");
//							String openedIn=U.getSectionValue(storehtml, "<p class=\"mreinfp lng_commn\">Year Established</p>", "</ul>");
//							if (website!=null)website=U.removeHtml(website);
//							if (openedIn!=null)openedIn=U.removeHtml(openedIn);
//							String mapKey="doc="+U.getSectionValue(storehtml, "onclick=\"view_map('", "',");
////							U.log(mapKey);
////							U.log(mapData);
//							String mapData=U.sendPostRequest(mapUrl, mapKey,storeURl);
//							String lat2=U.getSectionValue(mapData, "\"lil\":\"", "\"");
//							String lon2=U.getSectionValue(mapData, "\"lon\":\"", "\"");
//							String openingHours[]=U.getValues(storehtml, "\"dayOfWeek\": \"http://schema.org/", "}");
////							String timingData[]=new String[openingHours.length];
////							for (int i = 0; i < openingHours.length; i++) {
////								String openingTime=U.getSectionValue(openingHours[i], "\"opens\": [\"", "\"");
////								String closeingTime=U.getSectionValue(openingHours[i], "\"closes\": [\"", "\"");
////								if (openingTime.contains("Open 24 Hrs"))
////									timingData[i]=openingTime.trim();
////								else
////									timingData[i]=openingTime.trim()+"-"+closeingTime.trim();
////							}
//							String phoneNoSec=U.getSectionValue(storehtml, "-moz-osx-font-smoothing:grayscale}", ".mobilesv{");
////							U.log(phoneNoSec);
//							String noVals[]=U.getValues(phoneNoSec, ".icon", "}");
//							String phoneSec=U.getSectionValue(storehtml, "<span class=\"telnowpr\">", "</a>");
//							U.log(phoneSec);
//							HashMap<String, String >noMapLocal=new HashMap<>();
//							for (String nos : noVals) {
//								String key=U.getSectionValue(nos, "-", ":");
//								String val=U.getSectionValue(nos, "{content:\"", "\"");
//								if (nosMap.containsKey(val))
//									noMapLocal.put(key, nosMap.get(val));
//								else
//									U.log("--|"+val);
//							}
//							
//							String phone="";
//							if (phoneSec!=null) {
//								String nos[]=U.getValues(phoneSec, "mobilesv icon-", "\"");
//								for (int k = 0; k < nos.length; k++) {
//									if(noMapLocal.containsKey(nos[k]))
//										phone+=noMapLocal.get(nos[k]);
//								}
//							}
//							completeAddress=completeAddress!=null?U.removeHtml(completeAddress):"";
							String companyName=U.getSectionValue(listing, "title='", "'>");
//							U.log("--==--"+phone);
							//{"SrNo","CATEGORY","COMPAN_NAME","ADDRESS","PHONE","WEBSITE","ESTABLISHED_YEAR","HOURS_OF_OPERATION","JD_RATINGS"};
							U.log(listing);
							String out[]= {"",U.removeHtml(companyName),completeAddress,mobNo.toString(),storeURl.replace("\\", "").replace("&tab=gallery", "")};
							outData.add(out);
//							break;
						}
//					U.log(nextUrl);
//					break;
				}
			}
			String []header= {"SrNo","CompanyName","Address","MobileNo","StoreUrl"};
			StringWriter sw=new StringWriter();
			com.opencsv.CSVWriter writer=new com.opencsv.CSVWriter(sw);
			writer.writeNext(header);
			for (String[] out : outData) {
				writer.writeNext(out);
			}
			com.shatam.utils.FileUtil.writeAllText(U.getCachePath()+"NagpurSchools.csv", sw.toString());
			sw.close();
			writer.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
