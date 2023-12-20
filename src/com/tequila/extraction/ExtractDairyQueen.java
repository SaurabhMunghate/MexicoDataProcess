package com.tequila.extraction;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractDairyQueen {
	public static void main(String[] args) {
		new ExtractDairyQueen().extractData();
		//formatData();
	}
	private void extractLatLon(){
		//   \[\d{2,}.\d+,-\d{2,3}.\d+\]
		
		try {
			String fileName="";
			//ArrayList<String[]> fileRows=U.readCsvFile("")
			String dataUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayCities";
			String dataHtml=U.sendPostRequest(dataUrl, "country=9&lang=%2Fes");
			ArrayList<String[]> outputData=new ArrayList<>();
//			U.log(dataHtml);
			String cityList[]=U.getValues(dataHtml, "<option value=\"", "\"");
			
			for (String city : cityList) {
				if (city.equals("0")) continue;
				String displayStoreUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayStores";
				String displayStoreHtml=U.sendPostRequest(displayStoreUrl, "country=9&city="+city+"&lang=%2Fes");
				String displayInfoData[]=U.getValues(displayStoreHtml, "<option value=\"", "\"");
				for (String displayInfo : displayInfoData) {
					if (displayInfo.equals("0")) continue;
					String displayInfoUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayInformation";
					U.log(displayInfo);
					String displayInfoHtml=U.sendPostRequest(displayInfoUrl, "country=9&store="+displayInfo+"&lang=%2Fes");
//					U.log(displayInfoHtml);
					String latLonsec=Util.match(displayInfoHtml, "\\[\\d{2,}.\\d+,-\\d{2,3}.\\d+\\]");
					U.log(latLonsec);
					if (latLonsec==null) {
						latLonsec=" , ";
					}
					
					String latLon[]=latLonsec.split(",");
					String shopName=U.getSectionValue(displayInfoHtml, "<p class=\"store-name\">", "</p>");
					String phone=U.getSectionValue(displayInfoHtml, "Teléfono:", "<");
					String mail=U.getSectionValue(displayInfoHtml, "Mail:", "<");
					String addSec=U.getSectionValue(displayInfoHtml, "<div class=\"store-location\">", "</p>");
					addSec=addSec==null?"":addSec;
					phone=phone==null?"":phone;
					mail=mail==null?"":mail;
					String out[]= {"",shopName.trim(),addSec.trim(),"","","","",phone.trim(),mail.trim(),"http://www.dairyqueen.com.mx",latLon[0],latLon[1],"http://www.dairyqueen.com.mx/mx/stores"};
					outputData.add(out);
//					break;
				}
//				break;
			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeAll(outputData);
			FileUtil.writeAllText(U.getCachePath()+"withLatlon.csv", sw.toString());
			writer.close();
			sw.close();
			U.log(outputData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void formatData() {
		try {
			String fileName="/home/mypremserver/MexicoCache/Cache/DairyQueen.csv";
			List<String[]> input=U.readCsvFile(fileName);
			int counter=0;
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			for (String[] record : input) {
				if (record[0].contains("SIC_SUB"))continue;
				String []sicDetails=Sic.sicInfo(record[0]);
				U.log(record[1]);
				String out[]= 
					{
						""+counter++,//id
						U.toCapitalizeCase(sicDetails[0]),
						U.toCapitalizeCase(sicDetails[1]),
						U.toCapitalizeCase(sicDetails[3]),
						U.toCapitalizeCase(sicDetails[4]),
						U.toCapitalizeCase(sicDetails[5]),
						U.toCapitalizeCase(sicDetails[6]),
						U.toCapitalizeCase(record[1]),
						U.toCapitalizeCase(record[2]),
						U.toCapitalizeCase(record[3]),
						U.toCapitalizeCase(record[4]),
						U.toCapitalizeCase(record[5]),
						U.toCapitalizeCase(record[6]),
						U.formatNumbersAsCode(record[7]),
						null,
						record[9].toLowerCase(),
						record[8].toLowerCase(),
						null,
						null,
						null,
						null,
						null,
						record[10],
						record[11],
						record[12],
						U.getTodayDate()
					};
				writer.writeNext(out);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "INSERT.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void extractData() {
		try {
			String dataUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayCities";
			String dataHtml=U.sendPostRequest(dataUrl, "country=9&lang=%2Fes");
			ArrayList<String[]> outputData=new ArrayList<>();
//			U.log(dataHtml);
			String cityList[]=U.getValues(dataHtml, "<option value=\"", "\"");
			String header[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBOURHOOD","CITY","STATE","ZIP","PHONE","MAIL","URL","LATITUDE","LONGITUDE","SOURCE_URL"};
			outputData.add(header);
			for (String city : cityList) {
				if (city.equals("0")) continue;
				String displayStoreUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayStores";
				String displayStoreHtml=U.sendPostRequest(displayStoreUrl, "country=9&city="+city+"&lang=%2Fes");
				String displayInfoData[]=U.getValues(displayStoreHtml, "<option value=\"", "\"");
				for (String displayInfo : displayInfoData) {
					if (displayInfo.equals("0")) continue;
					String displayInfoUrl="http://fbapps.creativebdp.com/aroundtheworld_web/index/displayInformation";
					U.log(displayInfo);
					String displayInfoHtml=U.sendPostRequest(displayInfoUrl, "country=9&store="+displayInfo+"&lang=%2Fes");
//					U.log(displayInfoHtml);
					String latLonsec=Util.match(displayInfoHtml, "\\d{2,}.\\d+,-\\d{2,}.\\d+");
//					U.log(latLonsec);
				
					if (latLonsec==null) {
						String tempLatlonSec=U.getSectionValue(displayInfoHtml, "<iframe src=\"", "\"");
						if (tempLatlonSec!=null) {
							String latLonHtml=U.getHTML(tempLatlonSec);
							latLonsec=Util.match(latLonHtml, "\\[\\d{2,}.\\d+,-\\d{2,3}.\\d+\\]");
						}
						if (latLonsec!=null) {
							latLonsec=latLonsec.replaceAll("\\[|\\]", "");
						}else
							latLonsec=" , ";
					}
					
					String latLon[]=latLonsec.split(",");
					String shopName=U.getSectionValue(displayInfoHtml, "<p class=\"store-name\">", "</p>");
					String phone=U.getSectionValue(displayInfoHtml, "Teléfono:", "<");
					String mail=U.getSectionValue(displayInfoHtml, "Mail:", "<");
					String addSec=U.getSectionValue(displayInfoHtml, "<div class=\"store-location\">", "</p>");
					addSec=addSec==null?"":addSec;
					phone=phone==null?"":phone;
					mail=mail==null?"":mail;
					String out[]= {"",shopName.trim(),addSec.trim(),"","","","",phone.trim(),mail.trim(),"http://www.dairyqueen.com.mx",latLon[0],latLon[1],"http://www.dairyqueen.com.mx/mx/stores"};
					outputData.add(out);
//					break;
				}
//				break;
			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeAll(outputData);
			FileUtil.writeAllText(U.getCachePath()+"DairyQueentemp_Latlon.csv", sw.toString());
			writer.close();
			sw.close();
			U.log(outputData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
