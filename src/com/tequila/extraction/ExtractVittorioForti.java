package com.tequila.extraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.core.sym.Name;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractVittorioForti {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="2311";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			int i=0;
			HashSet<String> uniqueVal=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> unique=new HashSet<>();
			String baseURl="https://storelocator.w3apps.co/map.aspx?shop=vittorio-forti-3&container=false";
			String baseHtml=U.getHTML(baseURl);
//			U.log(baseHtml);
			String dataSec=U.getSectionValue(baseHtml, " var sucursales = [", "function");
			U.log(dataSec);
			//https://storelocator.w3apps.co/get_stores2.aspx?shop=vittorio-forti-3&all=1&lat=25.148703736237&lng=-102.3354185803
			String areas[]=U.getValues(dataSec, "[", "],");
			for (String area : areas) {
				String areaVal[]=area.replaceAll("\".*\",", "").split(",");
				U.log(Arrays.toString(areaVal));
				String areaUrls="https://storelocator.w3apps.co/get_stores2.aspx?shop=vittorio-forti-3&all=1&lat="+areaVal[0].trim()+"&lng="+areaVal[1].trim();
				String arehtml=U.sendPostRequest(areaUrls,"");
//				U.log(areaUrls);
				String locationSecs=U.getSectionValue(arehtml, "[", "]");
				String locations[]=U.getValues(locationSecs, "{", "}");
				for (String location : locations) {
					U.log(location);
					U.log("https://storelocator.w3apps.co/get_store_info.aspx?id="+U.getSectionValue(location, "\"id\": ", ","));
					String detailsHtml=U.getHTML("https://storelocator.w3apps.co/get_store_info.aspx?id="+U.getSectionValue(location, "\"id\": ", ","));
					String shopName="Vittorio Forti "+U.getSectionValue(detailsHtml, "\"name\": \"", "\"");
					String address=U.getSectionValue(detailsHtml, "\"address\": \"", "\"");
					if (!unique.add((shopName+address).toLowerCase())) {
						continue;
					}
					String neighbourhood=U.getSectionValue(detailsHtml, "\"address2\": \"", "\"");
					String city=U.getSectionValue(detailsHtml, "\"city\": \"", "\"");
					String state=U.getSectionValue(detailsHtml, "\"state\": \"", "\"");
					String postalCode=U.getSectionValue(detailsHtml, "\"zip\": \"", "\"");
					String phone=U.getSectionValue(detailsHtml, "\"phone\": \"", "\"");
					String fax=U.getSectionValue(detailsHtml, "\"fax\": \"", "\"");
					String website="https://www.vittorioforti.com.mx";
					String email=U.getSectionValue(detailsHtml, "\"email\": \"", "\"");
					String lat=U.getSectionValue(detailsHtml, "\"lat\": \"", "\"");
					String lng=U.getSectionValue(detailsHtml, "\"long\": \"", "\"");
					//U.log(detailsHtml);
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(shopName),U.toTitleCase(address),U.toTitleCase(neighbourhood),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(phone),U.formatNumbersAsCode(fax),website,email,null,null,null,null,null,lat,lng,"https://www.vittorioforti.com.mx/apps/sucursales",U.getTodayDate()};
					writer.writeNext(out);
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"VittorioForti_Stores.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
		// U.log(" .............."+path);s
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
			urlConnection.addRequestProperty("Cookie", "ASP.NET_SessionId=wp1gvat2ygvq0cupllalwnn1");
			urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
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
