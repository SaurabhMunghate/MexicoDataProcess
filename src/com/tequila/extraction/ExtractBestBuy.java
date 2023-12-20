package com.tequila.extraction;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractBestBuy {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5731";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			List<String[]> zipCsv=U.readCsvFile("/home/mypremserver/DatabasesTequila/DatabaseCSv/zipdataset.csv");
			for (String[] zip : zipCsv) {
				if(zip[1].contains("LATITUDE")) continue;
				String jsonHtml=null;
				String jsonUrl="https://www.bestbuy.com.mx/storelocator/api/"+zip[0];
				try {
					String jsonHtml1=U.getHTML(jsonUrl);
					jsonHtml=jsonHtml1;
				} catch (Exception e) {
				}
				
				U.log(jsonHtml);
				if (jsonHtml==null) {
					continue;
				}
				String listings[]=U.getValues(jsonHtml, "\"id\"", "\"addressLine\"");
				for (String listing : listings) {
					U.log(listing);
					String storeURL="https://www.bestbuy.com.mx/tienda/"+U.getSectionValue(listing, ":", ",\"");
					U.log(storeURL);
//					String storeHtml=U.getHTML(storeURL);
					String latLon[]=U.getSectionValue(listing, "\"geoCoordinate\":\"", "\"").split(",");
					String name=U.getSectionValue(listing, "\"name\":\"", "\"");
					String address=U.getSectionValue(listing, "\"addr1\":\"", "\"");
					if (!dupliData.add((name+address).toLowerCase())||storeURL.contains("2022")) {
						continue;
					}
					String neigh=U.getSectionValue(listing, "\"addr2\":\"", "\"");
					if (neigh==null) {
						neigh=U.getSectionValue(listing, "\"district\":\"", "\"");
					}
					String city=U.getSectionValue(listing, "\"city\":\"", "\"");
					String state=U.getSectionValue(listing, "\"state\":\"", "\"");
					String postalCode=U.getSectionValue(listing, "\"postalCode\":\"", "\"");
					String phone=U.getSectionValue(listing, "\"phone\":\"", "\",");
					String email=U.getSectionValue(listing, "\"emailAddress\":\"", "\"");
					U.log(email);
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(address),U.toTitleCase(neigh),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(phone),"",storeURL,email,null,null,null,null,null,latLon[0],latLon[1],"https://www.bestbuy.com.mx/storelocator/",U.getTodayDate()};
					writer.writeNext(out);
//					if (i==20) {
//						break;
//					}
//					
				}
//				if (i==20) {
//				break;
//				}
			}
			FileUtil.writeAllText(U.getCachePath()+"Best_Buy_Store.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
