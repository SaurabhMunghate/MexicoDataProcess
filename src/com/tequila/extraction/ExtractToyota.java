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
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class ExtractToyota {
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
			List<String[]> zipCsv=U.readCsvFile("/home/mypremserver/DatabasesTequila/DatabaseCSv/zipdataset.csv");
			int s=0,i=0;
			HashSet<String> uniqueKey=new HashSet<>();
			String baseURl="https://www.gmc.com.mx/localiza-distribuidor-gmc";
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int id=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			for (String[] zip : zipCsv) {
				if(zip[1].contains("LATITUDE")) continue;
				U.log((s++)+""+zip);
				String lat=zip[1];
				String lng=zip[2];
				U.log(lat+" "+lng);
				String jsonUrl="https://www.toyota.mx/api/geo?latitud="+lat+"&longitud="+lng+"&tipo=maps";
				U.log(jsonUrl);
				String jsonHtml=U.getPageSource(jsonUrl);
				
				jsonHtml=StringEscapeUtils.unescapeJson(jsonHtml);
//				U.log(jsonHtml);
				if (jsonHtml.length()>5) {
					String comDatas[]=U.getValues(jsonHtml, "{\"tid\"", "}");
					for (String comData : comDatas) {
						String distriId=U.getSectionValue(comData, ":\"", "\"");
						String dataUrl="https://www.toyota.mx/api/geo?distribuidor="+distriId+"&latitud=null&longitud=null&tipo=distribuidor";
						String moreInfo=U.getPageSource(dataUrl);
						U.log(moreInfo);
						String name=U.getSectionValue(comData, "\"name\":\"", "\"");
						String cmpLat=U.getSectionValue(comData, "latitud\":\"", "\"").replaceAll("asd", "");
						String cmpLon=U.getSectionValue(comData, "longitud\":\"", "\"");
						String phone=U.getSectionValue(comData, "\"telefono\":\"", "\"");
						String addSec=U.getSectionValue(comData, "\"description\":\"", "\",");
						String add[]= {"","","","",""};
						if (addSec!=null) {
							addSec=addSec.replaceAll("<p>|</p>", "");
							String tempadd[]=addSec.split(",");
							add[0]=tempadd[0];
							add[1]=tempadd[1];
						}
						U.log(addSec);
						String latLon[]= {cmpLat,cmpLon};
						String latlonAdd[]=U.getGoogleAddress(latLon);
						U.log(Arrays.toString(latlonAdd));
						String webSite=U.getSectionValue(moreInfo, "\"sitio\":\"", "\",\"");
						U.log(webSite);
						String email=U.getSectionValue(moreInfo, "\"email\":\"", "\"");
						U.log(email);
						U.log(phone);
						if(!phone.isEmpty()){
							phone = TextFormat.getUniqueNumber(phone);
						}
						U.log(phone);
						if(uniqueKey.add((name+add[0]).toLowerCase())){
							String out[] = {""+(id++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase("Nuevos Y Usados Concesionarios De Coches Y Camiones"),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(latlonAdd[2]),U.toTitleCase(latlonAdd[3]),latlonAdd[4],U.formatNumbersAsCode(phone),null,webSite,email.toLowerCase(),null,null,null,null,null,latLon[0],latLon[1],"https://www.toyota.mx/distribuidores/"+zip,U.getTodayDate()};
							writer.writeNext(out);
						}
						
					}
				}
			}
			FileUtil.writeAllText(U.getCachePath()+"Toyota_Distributor.csv", sw.toString());
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
		U.disableSslVerification();
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
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/65.0.3325.181 Chrome/65.0.3325.181 Safari/537.36");
			urlConnection.addRequestProperty("Referer", "https://www.toyota.mx/distribuidores/54720");
			urlConnection.addRequestProperty("x-csrf-token", "ZO6j2_Tn-S56m-2nSegTesHyogee-betYtvMQZmQfBM");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
			urlConnection.setConnectTimeout(5000);
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream,StandardCharsets.UTF_8.toString());
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
