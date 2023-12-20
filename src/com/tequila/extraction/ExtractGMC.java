package com.tequila.extraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractGMC {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			HashSet<String>stateSet=MXStates.getAllStates();
			int s=0,i=0;
			String baseURl="https://www.gmc.com.mx/localiza-distribuidor-gmc";
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			for (String state : stateSet) {
				U.log((++s)+" "+state);
				String googleUrl="https://maps.googleapis.com/maps/api/geocode/json?address="+URLEncoder.encode(state, StandardCharsets.UTF_8.toString())+"&key=AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s";
				String latllonHtml=U.getHTML(googleUrl);
//				U.log(latllonHtml);
				String latLonSec=U.getSectionValue(latllonHtml, "\"location\"", "\"location_type\"");
//				U.log(latLonSec);
				String lat=U.getSectionValue(latLonSec, "\"lat\" : ", ",").trim();
				String lng=U.getSectionValue(latLonSec, "\"lng\" : ", "}").trim();
				U.log(lat+" "+lng);
				String jsonDataURl="https://www.gmc.com.mx/OCRestServices/dealer/latlong/v1/GMC/"+lat+"/"+lng+"/?distance=10000&maxResults=1000";
				String jsonHtml=getHTML(jsonDataURl);
//				U.log(jsonHtml);
				String dataValues[]=U.getValues(jsonHtml, "\"id\":", "\"makeCodes\":");
				for (String dataSec : dataValues) {
					U.log(dataSec);
					String name=U.getSectionValue(dataSec, "\"dealerName\":\"", "\"");
					String phone=U.getSectionValue(dataSec, "\"phone1\":\"", "\"");
					String phone2=U.getSectionValue(dataSec, "\"phone2\":\"", "\"");
					if (phone2!=null&&!phone2.isEmpty()) {
						phone+=";"+phone2;
					}
					
					String companylat=U.getSectionValue(dataSec, "\"latitude\":", ",");
					String companylng=U.getSectionValue(dataSec, "\"longitude\":", "}");
					String streetAdd=U.getSectionValue(dataSec, "\"addressLine1\":\"", "\"");
					if (!dupliData.add((name+streetAdd).toLowerCase())) {
						continue;
					}
					String neighAdd=U.getSectionValue(dataSec, "\"addressLine2\":\"", "\"");
					neighAdd=neighAdd==null?"":neighAdd;
					String city=U.getSectionValue(dataSec, "\"cityName\":\"", "\"");
					String zip=U.getSectionValue(dataSec, "\"postalCode\":\"", "\"");
					
					String companyState=U.getSectionValue(dataSec, "\"countrySubdivisionCode\":\"", "\"");
					String website=U.getSectionValue(dataSec, "\"dealerUrl\":\"", "\"");
					if (companyState.toLowerCase().equals("Cdmx".toLowerCase())) {
						companyState="Mexico City";
						city=MXStates.getMexicoCityMunicipalites(zip);
					}else if (companyState.toLowerCase().contains("Estado De México".toLowerCase())) {
						companyState="Mexico State";
					}
					if (sicCode.contains("5511")) {
						sicdetails[6]="Nuevos Y Usados Concesionarios De Coches Y Camiones";
					}
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(neighAdd),U.toTitleCase(city),U.toTitleCase(companyState),zip.trim(),U.formatNumbersAsCode(phone),null,website,null,null,null,null,null,null,companylat,companylng,baseURl,U.getTodayDate()};
					U.log(Arrays.toString(out));
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"GMC_Distributor.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

		// chk responce code

		int respCode = U.CheckUrlForHTML(path);
		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"216.56.85.100",	8080));
		final URLConnection urlConnection = url.openConnection();  //proxy

		// Mimic browser
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language",
					"en-us,en;q=0.5");
			urlConnection.addRequestProperty("clientapplicationid", "OCNATIVEAPP");
			urlConnection.addRequestProperty("locale", "es_MX");
			urlConnection.addRequestProperty("loginid", "mytest016@outlook.com");
			urlConnection.addRequestProperty("Host", "api-us.renault.com");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.setConnectTimeout(5000);
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
			// final String html = toString(inputStream);
			inputStream.close();

			if (!cacheFile.exists())
				FileUtil.writeAllText(fileName, html);

			return html;
		} catch (Exception e) {
			U.log(e);
		}
		return html;
	}
}
