package com.shatam.MexicoExtraction_1_15;

import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractbBenavides {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5912";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String companyName="Farmacia Benavides, S.A. De C.V.";
			String websiteUrl="https://www.benavides.com.mx";
			String baseUrl="https://www.benavides.com.mx/api/v1/branches/Get";
			String baseHtml=U.getHTML(baseUrl);
			JSONArray jsonarr=(JSONArray) new JSONParser().parse(baseHtml);
			for (int j = 0; j < jsonarr.size(); j++) {
				JSONObject jsonObj=(JSONObject) jsonarr.get(j);
				String addressSec=(String) jsonObj.get("BranchAddress");
				String lat=(String) jsonObj.get("BranchLatitude");
				String lng=(String) jsonObj.get("BranchLongitude");
				JSONObject stateOBj=(JSONObject)jsonObj.get("State");
				String state=(String) stateOBj.get("Name");
				JSONObject cityOBj=(JSONObject)jsonObj.get("City");
				String city=(String) cityOBj.get("Name");
				U.log(addressSec);
				
				String phone=Util.match(addressSec, "TEL:.*");
				U.log(phone);
				addressSec=phone!=null?addressSec.replaceAll(phone+"|#N/A", "").trim():addressSec.replaceAll("#N/A", "");
//				addressSec=addressSec;
				phone=phone!=null?phone.replaceAll("TEL:|N/A", "").trim():"";
				
				String neigh="";
				String zip="";
				U.log(addressSec);
//				String distanceHtml=U.getGoogleHTML("https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins="
//						+ URLEncoder.encode((addressSec+city+state).toLowerCase(), "UTF-8")+"&destinations="+lat+","+lng);
//				U.log(distanceHtml);
//				String distance=U.getSectionValue(distanceHtml, "\"value\" : ", "}");
//				U.log(distance);
				
//				String googleHtml=U.getGoogleHTML("https://maps.googleapis.com/maps/api/geocode/json?address="
//						+ URLEncoder.encode((addressSec+city+state).toLowerCase(), "UTF-8"));
				//U.log(latloN);
				
//				JSONObject jsonAddarr=(JSONObject) new JSONParser().parse(googleHtml);
//				JSONArray results=(JSONArray) jsonAddarr.get("results");
//				if (results.size()!=0) {
//					JSONObject addResult=(JSONObject) results.get(0) ;
//					JSONObject latlonSecarr2=(JSONObject)((JSONObject) addResult.get("geometry")).get("location");
//					String googlelng=latlonSecarr2.get("lng").toString();
//					String googlelat=latlonSecarr2.get("lat").toString();
//					JSONArray addressComSec=(JSONArray) addResult.get("address_components");
//					zip=((JSONObject) addressComSec.get(addressComSec.size()-1)).get("long_name").toString();
//					U.log(phone);
//					U.log(lat);
//					U.log(lng);
//					if (distance!=null&&Integer.parseInt(distance.trim())>500) {
//						lat=googlelat;
//						lng=googlelng;
//					}else if (distance==null) {
//						lat="";
//						lng="";
//					}
//					U.log(googlelat);
//					U.log(googlelng);
//				}
				
				U.log(state);
				U.log(city);
				String out[]= {""+(j),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(addressSec),neigh,U.toTitleCase(city),U.toTitleCase(state),zip,U.formatNumbersAsCode(phone),null,websiteUrl,"igonzalez@benavides.com.mx",null,null,"100000000 to 500000000","25 to 50","1917",lat,lng,"http://www.benavides.com.mx/Sucursales/",U.getTodayDate()};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Farmacia_Benavides_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private String getLatLonAddressUsingGoogle() {
		
		return null;
	}
}
