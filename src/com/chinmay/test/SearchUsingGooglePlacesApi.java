package com.chinmay.test;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.ApiKey;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.shatam.utils.Util;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class SearchUsingGooglePlacesApi {
	final static private String fileName="/home/chinmay/MexicoCache/Cache/Restaurant_Guru/Restaurant Guru_6501_6600_Unique.csv";
//	static private String textSearch="banamex";
	final static private int radius=30;
	public static void main(String[] args) {
		new SearchUsingGooglePlacesApi().start();
	}
	static String categoriesMAin[]= {"eat-drink",
			"food-drink",
			"restaurant",
			"snacks-fast-food",
			"bar-pub",
			"coffee-tea",
			"coffee",
			"tea",
			"hotel",
			"motel"};
	public static ArrayList<String[]> nonMatchedRecords=new ArrayList<>();
	private void start() {
		try {
//			String finalStrSearch=textSearch;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			int i=0;
			List<String[]> inputFileData=U.readCsvFile(fileName);	
			for (String[] inputFileRecord : inputFileData) {
				//if (!inputFileRecord[0].contains("11390")) continue;
				U.log(i++);
//				if (i>1 && i<=20000) {
//					continue;
//				}
//				if (i==10) {
//					break;
//				}
				U.log(Arrays.toString(inputFileRecord));
				if (inputFileRecord[0].contains("ID")) {
					String out[]=new String[inputFileRecord.length+10];
					System.arraycopy(inputFileRecord, 0, out, 0, inputFileRecord.length);
					out[inputFileRecord.length]="HERE_ADD";
					out[inputFileRecord.length+1]="HERE_COL";
					out[inputFileRecord.length+2]="HERE_CITY";
					out[inputFileRecord.length+3]="HERE_STATE";
					out[inputFileRecord.length+4]="HERE_ZIP";
					out[inputFileRecord.length+5]="HERE_TYPE";
					out[inputFileRecord.length+6]="HERE_PHONE";
					out[inputFileRecord.length+7]="HERE_WEBSITE";
					out[inputFileRecord.length+8]="HERE_COMPANY_NAME";
					out[inputFileRecord.length+9]="HERE_SOURCE_URL";
					writer.writeNext(out);
					continue;
				}
				if (inputFileRecord[0].contains("ID")||inputFileRecord[22].contains("-"))continue;
				U.log(inputFileRecord[22]+"\t"+inputFileRecord[23]);
				
				U.log(inputFileRecord[7]+"  == > ");
				if (inputFileRecord[23].trim().length()==0) {
					continue;
				}
				String add[]=getAddressFromHereUsingPlaceApi(inputFileRecord[7], Double.parseDouble(inputFileRecord[22]), Double.parseDouble(inputFileRecord[23]), radius);
//				String add[]=getAddressFromGoogleUsingPlaceApi(inputFileRecord[7], Double.parseDouble(inputFileRecord[22]), Double.parseDouble(inputFileRecord[23]), radius);
				String out[]=new String[inputFileRecord.length+10];
//				U.log(inputFileRecord.length);
//				U.log(add.length);
				System.arraycopy(inputFileRecord, 0, out, 0, inputFileRecord.length);
				System.arraycopy(add, 0, out, inputFileRecord.length, add.length);
				U.log(Arrays.toString(out));
				writer.writeNext(out);
				
//				break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_PlacesSearch_HERE_.csv"), sw.toString());
			writer.close();
			sw.close();
			writeNonMatchedRecords();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void writeNonMatchedRecords() {
		try {
			String header[]= {"CATEGORY","COMPANY_NAME","ADDRESS","NEIGHBOURHOOD","CITY","STATE","ZIP","TYPE","DISTANCE","LATITUDE","LONGITUDE","PHONE","WEBSITE","EXTRACTED_URL","CATEGORIES"};
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			writer.writeAll(nonMatchedRecords);
			FileUtil.writeAllText(fileName.replace(".csv", "_Non_Matched_PlacesSearch_.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public String[] getAddressFromHereUsingPlaceApi(String textSearch,double lat,double lon,int dist) throws IOException {
		String searchUrl="https://places.cit.api.here.com/places/v1/autosuggest?at="+lat+","+lon+"&radius="+dist+"&q="+URLEncoder.encode(textSearch,StandardCharsets.UTF_8.toString())+"&&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
		String html=U.getHTML(searchUrl);
		html=TranslateEnglish.removeUnicode(html);
		String stores[]=U.getValues(html, "{\"title\":"	, "}");
//		U.log(html);
		boolean flag=true;
		String finalAddress[]=new String[] {"","","","","","",""};
		for (String store : stores) {
			//U.log(store);
//			if (store.contains("\"type\":\"urn:nlp-types:search\"")) {
//				String url=U.getSectionValue(store, "\"href\":\"", "\"");
//				//placeSearchURL(url);
//				continue;
//				
//			}
			String companyName=U.getSectionValue(store, "\"", "\"");
//			U.log(FuzzySearch.tokenSortRatio(companyName, textSearch));
			String cat=U.getSectionValue(store, "\"category\":\"", "\"");
			String dista=Util.match(store, "\"distance\":(\\d+)",1);
//			U.log(dista);
			if (cat==null||dista==null)	continue;
			if (FuzzySearch.tokenSortRatio(companyName, textSearch)>70&&(Arrays.asList(categoriesMAin).contains(cat)&&Integer.parseInt(dista)<100)&&flag) {
				U.log(store);
				String detailUrl=U.getSectionValue(store, "\"href\":\"", "\"").replace(ApiKey.HEREAPIKEYS[0][0], ApiKey.HEREAPIKEYS[1][0]).replace(ApiKey.HEREAPIKEYS[0][1], ApiKey.HEREAPIKEYS[1][1]);
				String detailHtml=U.getHTML(detailUrl);
				if (detailHtml==null) {
					continue;
				}
				String phone=detailHtml.contains("\"phone\":[{\"value\":\"")?U.getSectionValue(detailHtml, "\"phone\":[{\"value\":\"", "\""):"";
				String website=detailHtml.contains("\"website\":[{\"value\":\"")?U.getSectionValue(detailHtml, "\"website\":[{\"value\":\"", "\""):"";
				
				String formattedAddres=U.getSectionValue(store, "\"vicinity\":\"", "\"");
				String typePlace=U.getSectionValue(store, "\"categoryTitle\":\"", "\",");
				if (formattedAddres==null) continue;
//				U.log(store);
				String add[]=U.getAddress(formattedAddres.replace("<br/>", ","));
				U.log(formattedAddres.replace("<br/>", ","));
				String tempAdd[]=formattedAddres.replace("<br/>", ",").split(",");
				if (tempAdd.length==4) {
					U.log(Arrays.toString(tempAdd));
					add[0]=tempAdd[0];
					add[1]=tempAdd[1];
//					U.log(Util.match(tempAdd[2], "\\d+"));
					add[4]=Util.match(tempAdd[2], "\\d+");
					add[2]=add[4]!=null?tempAdd[2].replaceAll("\\d+", "").trim():tempAdd[2].trim();
					add[3]=U.matchState(tempAdd[3]);
				}
				
//				U.log(Arrays.toString(add));
				finalAddress= new String[] {add[0],add[1],add[2],add[3],add[4],cat+" "+dista,phone,website,companyName,detailUrl};
				flag=false;
				
			}
//				else {
//				String detailUrl=U.getSectionValue(store, "\"href\":\"", "\"").replace(ApiKey.HEREAPIKEYS[0][0], ApiKey.HEREAPIKEYS[1][0]).replace(ApiKey.HEREAPIKEYS[0][1], ApiKey.HEREAPIKEYS[1][1]);
//				String detailHtml=U.getHTML(detailUrl);
//				String phone="";
//				String website="";
//				if (!detailHtml.contains("\"countryCode\":\"MEX\"")) continue;
//				if (detailHtml.contains("\"phone\":[{\"value\":\"")) phone=U.getSectionValue(detailHtml, "\"phone\":[{\"value\":\"", "\"");
//				if (detailHtml.contains("\"website\":[{\"value\":\"")) website=U.getSectionValue(detailHtml, "\"website\":[{\"value\":\"", "\"");
//				
//				String categories=U.getSectionValue(detailHtml, "\"categories\":", "]");
//				String categ[]=U.getValues(categories, "\"id\":\"", "\"");
//				boolean catFlag=false;
//				HashSet<String> categoriesSet=new HashSet<>();
//				for (String ca : categ)
//					for (String ss : categoriesMAin) 
//						if (ca.contains(ss)) {
//							categoriesSet.add(ss);
//							catFlag=true;
//							break;
//						}
//				
//				if (!catFlag) {
//					continue;
//				}
//				
//				String formattedAddres=U.getSectionValue(store, "\"vicinity\":\"", "\"");
//				if (formattedAddres==null) formattedAddres=U.getSectionValue(detailHtml, "\"address\":{\"text\":\"", "\"");
//				String typePlace=U.getSectionValue(store, "\"categoryTitle\":\"", "\",");
//				U.log(formattedAddres);
//				String add[]=U.getAddress(formattedAddres.replace("<br/>", ","));
//				U.log(formattedAddres.replace("<br/>", ","));
//				String tempAdd[]=formattedAddres.replace("<br/>", ",").split(",");
//				if (tempAdd.length==4) {
//					U.log(Arrays.toString(tempAdd));
//					add[0]=tempAdd[0];
//					add[1]=tempAdd[1];
////					U.log(Util.match(tempAdd[2], "\\d+"));
//					add[4]=Util.match(tempAdd[2], "\\d+");
//					add[2]=add[4]!=null?tempAdd[2].replaceAll("\\d+", "").trim():tempAdd[2].trim();
//					add[3]=U.matchState(tempAdd[3]);
//				}
//				
//				U.log(Arrays.toString(add));
////				String title=U.getSectionValue(code, From, To)
//				String latLon[]=U.getSectionValue(store, "\"position\":[", "]").split(",");
//				
//				String out[]= new String[] {categoriesSet.toString(),companyName,add[0],add[1],add[2],add[3],add[4],typePlace,dista,latLon[0],latLon[1],phone,website,detailUrl,Arrays.toString(categ)};
//				nonMatchedRecords.add(out);
//			}
//				else {
//				String formattedAddres=U.getSectionValue(store, "\"vicinity\":\"", "\"");
//				U.log(formattedAddres);
//				String typePlace=U.getSectionValue(store, "\"categoryTitle\":\"", "\",");
//				String add[]=U.getAddress(formattedAddres.replace("<br/>", ","));
//				return new String[] {add[0],add[1],add[2],add[3],add[4],"Not Matched: "+companyName+" "+typePlace};
//			}
		}
		return finalAddress;
	}

	private void placeSearchURL(String url) {
		try {
			String dataHtml=U.getHTML(url);
			String stores[]=U.getValues(dataHtml, "\"position\"", ",\"id\":\"");
			for (String store : stores) {
				String detailUrl=U.getSectionValue(store, "\"href\":\"", "\"").replace(ApiKey.HEREAPIKEYS[0][0], ApiKey.HEREAPIKEYS[1][0]).replace(ApiKey.HEREAPIKEYS[0][1], ApiKey.HEREAPIKEYS[1][1]);
				String detailHtml=U.getHTML(detailUrl);
				String phone="";
				String website="";
				if (!detailHtml.contains("\"countryCode\":\"MEX\"")) continue;
				if (detailHtml.contains("\"phone\":[{\"value\":\"")) phone=U.getSectionValue(detailHtml, "\"phone\":[{\"value\":\"", "\"");
				if (detailHtml.contains("\"website\":[{\"value\":\"")) website=U.getSectionValue(detailHtml, "\"website\":[{\"value\":\"", "\"");
				
				String categories=U.getSectionValue(detailHtml, "\"categories\":", "]");
				String categ[]=U.getValues(categories, "\"id\":\"", "\"");
				boolean catFlag=false;
				HashSet<String> categoriesSet=new HashSet<>();
				for (String ca : categ)
					for (String ss : categoriesMAin) 
						if (ca.contains(ss)) {
							categoriesSet.add(ss);
							catFlag=true;
							break;
						}
				
				if (!catFlag) {
					continue;
				}
				String companyName=U.getSectionValue(detailHtml, "\"name\":\"", "\"");
				String formattedAddres=U.getSectionValue(store, "\"vicinity\":\"", "\"");
				if (formattedAddres==null) formattedAddres=U.getSectionValue(detailHtml, "\"address\":{\"text\":\"", "\"");
				String typePlace=U.getSectionValue(store, "\"categoryTitle\":\"", "\",");
				U.log(formattedAddres);
				String add[]=U.getAddress(formattedAddres.replace("<br/>", ","));
				U.log(formattedAddres.replace("<br/>", ","));
				String tempAdd[]=formattedAddres.replace("<br/>", ",").split(",");
				if (tempAdd.length==4) {
				//	U.log(Arrays.toString(tempAdd));
					add[0]=tempAdd[0];
					add[1]=tempAdd[1];
//					U.log(Util.match(tempAdd[2], "\\d+"));
					add[4]=Util.match(tempAdd[2], "\\d+");
					add[2]=add[4]!=null?tempAdd[2].replaceAll("\\d+", "").trim():tempAdd[2].trim();
					add[3]=U.matchState(tempAdd[3]);
				}
				String latLon[]=U.getSectionValue(store, ":[", "]").split(",");
				
				String out[]= new String[] {categoriesSet.toString(),companyName,add[0],add[1],add[2],add[3],add[4],typePlace,"",latLon[0],latLon[1],phone,website,detailUrl,Arrays.toString(categ)};
				nonMatchedRecords.add(out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public String[] getAddressFromGoogleUsingPlaceApi(String textSearch,double lat,double lon,int dist) throws IOException {
		
		String searchUrl="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+lat+","+lon+"&radius="+dist+"&type=department%20Store&keyword="+textSearch+"&key="+ApiKey.GOOGLEAPIKEYS[0];
		String html=U.getHTML(searchUrl);
//		U.log(html);
//		U.log(textSearch);
		if (html!=null&&!html.contains("\"ZERO_RESULTS\"")) {
			String name=U.getSectionValue(html, "\"name\" : \"", "\"");
//			U.log(name);
			if (name.toLowerCase().contains(textSearch.toLowerCase())) {
				String placeId=U.getSectionValue(html, "\"place_id\" : \"", "\"");
				String placeHtml=U.getHTML("https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeId+"&key="+ApiKey.GOOGLEAPIKEYS[6]);
//				U.log(placeHtml);
				String formattedAddres=U.getSectionValue(placeHtml, " \"formatted_address\" : \"", "\"");
				String typePlace=U.getSectionValue(html, "\"types\" : [ ", "]");
				String add[]=U.getAddress(formattedAddres);
				return new String[] {add[0],add[1],add[2],add[3],add[4],typePlace};
			}
		}
		return new String[] {"","","","","",""};
	}
}
