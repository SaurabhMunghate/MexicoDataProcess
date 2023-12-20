package com.shatam.maps;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;

import com.shatam.utils.FileUtil;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class YahooMap implements Map,TextFormat {

	public static void main(String[] args) throws IOException {
		YahooMap map = new YahooMap();
	/*	String[] add = {"Rio San Lorenzo Ote 25","Culiacan","Sinaloa","80000"};
		String[] latLng = map.getLatLong(add);
		U.log("latlng"+Arrays.toString(latLng));*/
		
		Scanner scaner = new Scanner(System.in);
		System.out.print("Enter start index ::");
		int start = scaner.nextInt();
		System.out.print("Enter end index ::");
		int end = scaner.nextInt();
		
		scaner.close();
		
		U.log("start::"+start+" end::"+end);
		//map.getHtml();
	}
	
	public void getHtml() throws IOException{
		String path = "http://yboss.yahooapis.com/geo/placefinder?line1=Market+St+at+Montgomery+St&line2=San+Francisco,+CA";
		URI uri;
		try {
			uri = new URI ("http://yboss.yahooapis.com/geo/placefinder?line1=Market+St+at+Montgomery+St&line2=San+Francisco,+CA");
			String html = getHTML(path);
			U.log(html);
			U.log ("Authority = " +uri.getAuthority ());
			U.log ("Fragment = " +uri.getFragment ());
			U.log ("Host = " +uri.getHost ());
			U.log ("Path = " +uri.getPath ());
			U.log ("Port = " +uri.getPort ());
			U.log ("Query = " +uri.getQuery ());
			U.log ("Scheme = " +uri.getScheme ());
			U.log ("Scheme-specific part = " +uri.getSchemeSpecificPart ());
			U.log ("User Info = " +uri.getUserInfo ());
			U.log ("URI is absolute: " +uri.isAbsolute ());
			U.log ("URI is opaque: " +uri.isOpaque ());
			
			URL url = uri.toURL();
			URLConnection urlConnection = url.openConnection();
/*			String userpass = "" + ":" + "";
			String basicAuth = "Basic " + new String(new Base64().encode(userpass.getBytes()));
			uc.setRequestProperty ("Authorization", basicAuth);
			InputStream in = uc.getInputStream();
			String contents = IOUtils.toString(url.openStream());
*/		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public String[] getLatLong(String[] address) {
		
		String[] latLong = {ALLOW_BLANK,ALLOW_BLANK};

		String adr = String.join(",", address);
		try {
			String host = "http://yboss.yahooapis.com/geo/placefinder?location="+URLEncoder.encode(adr, "UTF-8")+
					"&gflags=ACL&locale=MX";
			
/*			String api = "http://maps.googleapis.com/maps/api/geocode/xml?address=" + URLEncoder.encode(adr, "UTF-8")
					+ "&sensor=true";
*/			
			URL url = new URL(host);
			U.log(url);
			U.log(U.getCache(url.toString()));
			String str = U.getHTML(url.toString());
			
			if (str.contains("<lat>") && str.contains("</lng>")) {
				U.log(str);
				latLong[0]= U.getSectionValue(str,"<lat>", "</lat>");
				latLong[1]= U.getSectionValue(str,"<lng>", "</lng>");
				U.log(latLong[0]+"  "+latLong[1]);
		}else if(str.contains(""))
		{}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
		return null;
	}

	@Override
	public String[] getAddress(String[] latLong) {
		
		String[] address = {ALLOW_BLANK,ALLOW_BLANK,ALLOW_BLANK,ALLOW_BLANK};
		String url = "http://yboss.yahooapis.com/geo/placefinder?";
		/*
		 * Client ID (Consumer Key)::
dj0yJmk9TVJLTjAzanRRT1JMJmQ9WVdrOVlXZDVSMFJOTm04bWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD1kZQ--
Client Secret (Consumer Secret)::
e2c186ffd9924cc7b043129295ca83706b7a5d31
		 */
		
		String htm = null;
		try {
			htm = U.getHTML("http://dev.virtualearth.net/REST/v1/Locations/"
							+ latLong[0]
							+ ","
							+ latLong[1]
							+ "?o=json&jsonp=GeocodeCallback&key=Anqg-XzYo-sBPlzOWFHIcjC3F8s17P_O7L4RrevsHVg4fJk6g_eEmUBphtSn4ySg");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] adds = U.getValues(htm, "formattedAddress\":\"", "\"");
		return null;
	}
	
	public static String getHTML(String path) throws IOException, URISyntaxException {

		path = path.replaceAll(" ", "%20");
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);

		URI uri = new URI(path);
		
		URL url = uri.toURL();
		
		String html = null;

		// chk responce code

		int respCode = U.CheckUrlForHTML(path);
		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"70.169.70.88",	80));
		final URLConnection urlConnection = url.openConnection();  //proxy

		String username = "johncenayoo123@yahoo.com";
		String password = "123@johnrock";
		// Mimic browser
		try {
			String userpass = username + ":" + password;
			String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
			urlConnection.setRequestProperty ("Authorization", basicAuth);
		
			
			urlConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language","en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
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
