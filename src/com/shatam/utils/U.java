package com.shatam.utils;

import static com.shatam.utils.TextFormat.BLANK;
import static com.shatam.utils.TextFormat.ALLOW_BLANK;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.interactions.Actions;

//import org.apache.commons.lang3.text.WordUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.collection.MultiMap;
import com.shatam.email.validations.MailHostsLookup;
import com.shatam.scrapper.SplitNeighborhoodFromAdd;



public class U implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String ignoredState = "Col. Valle (d|D)el Mezquital|Miguel Hidalgo";
	public static String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":")) {
//				log(html);
				String add=U.getSectionValue(html, "\"Street\":\"", "\"")+" "+U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia=U.getSectionValue(html, "\"District\":\"", "\"");
				String city=U.getSectionValue(html, "\"City\":\"", "\"");
				String state=MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] {add,colonia,city,state,postal};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String sendPostRequestAcceptJson(String requestUrl, String payload) throws IOException {
		String fileName=U.getCache(requestUrl+payload);
		//File cacheFile = new File(fileName);
//		log(fileName);
//		if (cacheFile.exists())
//			return FileUtil.readAllText(fileName);
		StringBuffer jsonString = new StringBuffer();
	    try {
	        URL url = new URL(requestUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.setDoOutput(true);
	        connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/json");
	        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
	        connection.setRequestProperty("referer", "https://www.shatam.com/");
	        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
	        writer.write(payload);
	        writer.close();
	        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String line;
	        while ((line = br.readLine()) != null) {
	                jsonString.append(line);
	        }
	        br.close();
	        connection.disconnect();
	    } catch (Exception e) {
	            throw new RuntimeException(e.getMessage());
	    }
//	    if (!cacheFile.exists())
//			FileU
	    return jsonString.toString();
	}
	public static String sendPostRequest(String path, String parameter,String storeUrl) throws Exception {
		// firstname=&lastname=&extension=&department=21%3A%3AAccess+Services
		String fileName=U.getCache(path+parameter);
		File cacheFile = new File(fileName);
		//log(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		String html="";
		String urlParameters = parameter;
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		URL url = new URL(path);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("67.205.153.192", 3128));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("authority", "www.justdial.com");
		conn.setRequestProperty("path", "/functions/maps.php");
		conn.setRequestProperty("accept", "application/json, text/javascript, */*; q=0.01");
//		conn.setRequestProperty("accept-encoding", "gzip, deflate, br");
		conn.setRequestProperty("accept-language", "en-GB,en-US;q=0.9,en;q=0.8");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		conn.setRequestProperty("referer", storeUrl);
		conn.setRequestProperty("user-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/73.0.3683.103 Chrome/73.0.3683.103 Safari/537.36");
		
		conn.setRequestProperty("cookie", "ppc=; attn_user=logout; TKY=5c9bb5acec348dcfacda71a9e62af0219edb98d33fee8dab4fe06bf4441a4ff1; _ctk=ee49a56c954e40bed72cc4084e4a35eb51cd0a3d8d05e54662807fd7e085368a; _ga=GA1.2.1199651319.1563882384; _fbp=fb.1.1563882384755.897357050; area_suffix=At; dealBackCity=Nagpur; scity=Nagpur; profbd=0; tab=toprs; view=lst_v; inweb_city=Nagpur; akcty=Nagpur; bdcheck=1; vfdisp=%7B%220712PX712.X712.190502124144.M3G4%22%3A%221024%22%7D; AKA_A2=A; PHPSESSID=531bd551dd39ea173eccc550f66a53fa; bm_mi=A8DF6E0EE7291050BB69D733C251208B~VawbJLZkh5FeHHcevhWN++HEidXJulJs+a0wbfxiHeudAN7YQezcVYpGOk9K/egS1y0hpoeNpAqjUTEWgSpyfBgos+pFUDT5TThMZ6ymgLM4ksMeXEMVx3CMLKZ6nc6PxAmK7Embt2SjeMNg+e6UNI1PDc3yM+bJMbyoLEBNLT2ubIbYHkcKCa6qozpqHF9CqQJZyd1II5AoJ8ET1Yhmcz6I6RIvyoLqEm/0sTeLqQN4pU67qFrOUcfWVPsXCtSxa6jynADXd9urlyQHEG94sDGBZGhUNXE7I+/EyRa65HQDl50FC9SLHcHxycjNc0rB; _gid=GA1.2.1734980621.1567672849; ak_bmsc=AECF2654FFDA355CD6D2468AD0AD31E075EFBD7A860B000010CA705D3570D820~plYEZAN2KKca5GP1lkv59ummsGoDlCSDB/4X6mbnzVr+56kcfPWYD5u8XifpTY3ppY5YK/smkgQ7zhbf8h8+UvVa0FgTuhtynZ0QuGAeBPsx+k4PauItvSwpeLq/f+LjOXtlFYISFhp+oVLmqbT8t8h36dZOw0KA6S01fn41g8BTDWfPBonLg0v9C/EcMnwXc78mAGJfQK0cb53RCroG0VqwCEVCqFQAQgkmZUt+7ZmFLmUvxsS40CgfVcDc7hxxps; ppc=; main_city=Nagpur; BDprofile=1; sarea=Hudkeswar Road; detailmodule=0712PX712.X712.170526163144.L2Y5; showrtp=2%232019-09-05; docidarray=%7B%220712PX712.X712.190502124144.M3G4%22%3A%222019-08-21%22%2C%220712PX712.X712.130510130005.G3C4%22%3A%222019-09-05%22%2C%220712PX712.X712.161226181206.W9R9%22%3A%222019-09-05%22%2C%220712PX712.X712.170526163144.L2Y5%22%3A%222019-09-05%22%7D; bdapop=0712PX712.X712.170526163144.L2Y5; usrcity=Nagpur; bm_sv=8856DFEF35C3AFFF24B0A14DEF46DF9E~IVAsYqb567L7wx+ZnOt4wMGoyuRDmRoL/oAzqSwcPoBWUbCri2hMw8tTKypqQflVfg++9jOU7i4Fiu1uv6lZFrk55YKlmaFKY2I14xbpzWtd+o3BsSYvj1c+4wTyEKRzJwmPQC1QNqiM78PFFo7yzPJSQ6j/jnpAdeVLN1v7UK4=; RT=\"z=1&dm=justdial.com&si=aa540c99-4760-4eac-a126-5f2e58183316&ss=k06fxr1i&sl=15&tt=15kt&bcn=%2F%2F60062f0e.akstat.io%2F&obo=3&nu=ed8553192c8d124be55949cc1b2e99dc&cl=1hbht\"");
		conn.setRequestProperty("sec-fetch-mode", "cors");
		conn.setRequestProperty("sec-fetch-site", "same-origin");
		conn.setRequestProperty("x-frsc-token", "5c9bb5acec348dcfacda71a9e62af0219edb98d33fee8dab4fe06bf4441a4ff1");
//		conn.setRequestProperty("origin", "https://www.justdial.com");
		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
		conn.setRequestProperty("x-requested-with","XMLHttpRequest");
		conn.setUseCaches(false);
		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			wr.write(postData);
		}
		final InputStream inputStream = conn.getInputStream();
		html=IOUtils.toString(inputStream,StandardCharsets.UTF_8.toString());
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, html);
		return html;
	}
	
	private static HashMap<String, String> mexicoStateList = new HashMap<String, String>() {

		{
			put("^AG|AGS|Ags\\.|Aguascalientes|\\bAgauscalientes\\b",		"Aguascalientes");
			put("^BC|B\\.C\\.|Baja California|Baja Calif.",			"Baja California");
			put("^BS|B\\.C\\.S\\.|Baja California Sur",	"Baja California Sur");
			put("^CM|Camp\\.|Campeche",					"Campeche");
			put("^CS|Chis\\.|Chiapas|Chaipas",					"Chiapas");
			put("^CH|Chih\\.|Chihuahua",				"Chihuahua");
			put("^DF|D\\.(F|f)\\.?|México D\\.F\\.|Ciudad De Mexico|Ciudad De Mexico|Ciudad de México|CDMX|Mexico City|Federal District|Distrito Federal|Mexico City",	"Mexico City");
			put("^CO|Coah\\.|Coahuila",					"Coahuila");
			put("^CL|Col\\.$|Colima",					"Colima");
			put("^DG|Dgo\\.|Durango",					"Durango");
			put("^EM|Edomex\\.|M�x\\.|Estado de Mexico|M�xico|Mexico State|México State|Esatdo De Mexico|Estado De México|Estado de México|Edo De Méx|^México|Edomex",
					"Mexico State");
			put("^GT|Gto\\.|Guanajuato|Guajajuato",				"Guanajuato");
			put("^GR|Gro\\.|Guerrero",					"Guerrero");
			put("^HG|Hgo\\.|Hidalgo",					"Hidalgo");
			put("^JA|Jal\\.|Jalisco|jalisco|Jalicso",			"Jalisco");
			put("^MI|Mich\\.|Michoac�n|Michoacan|Michoacán",				"Michoacan");
			put("^MO|Mor\\.|Morelos|Morleos",					"Morelos");
			put("^NA|Nay\\.|Nayarit",					"Nayarit");
			put("^NL|N\\.L\\.|Nuevo Le�n|Nueva Leon|Nuevo Leon|Nuevo León",				"Nuevo Leon");
			put("^OA|Oax\\.|Oaxaca",					"Oaxaca");
			put("^PU|Pue\\.|Puebla",					"Puebla");
			put("^QT|Qro\\.|Querétaro|Queretaro|Quer�taro|Queteraro|Querataro",		"Queretaro");
			put("^QR|Q\\.R\\.|Q\\. Roo\\.|Quintana Roo|Qunitana Roo|Qinntana Roo",	"Quintana Roo");
			put("^SL|S\\.L\\.P\\.|San Luis Potos�|San Luis Potosí|San Luis Potosi|San Luís Potosí",			"San Luis Potosi");
			put("^SI|Sin\\.|Sinaloa|Sinaola",					"Sinaloa");
			put("^SO|Son\\.|Sonora",					"Sonora");
			put("^TB|Tab\\.|Tabasco",					"Tabasco");
			put("^TM|Tamps\\.|Tamaulipas|Tampaulipas",				"Tamaulipas");
			put("^TL|Tlax\\.|Tlaxcala",					"Tlaxcala");
			put("^VE|Ver\\.|Veracruz",					"Veracruz");
			put("^YU|Yuc\\.|Yucat�n|Yucatan|Yucatán",					"Yucatan");
			put("^ZA|Zac\\.|Zacatecas",					"Zacatecas");
//			put("Mexico",					"Mexico");
		}
	};
	
	public static void log(Object o){
		System.out.println(o);
	}
	public static void errLog(Object o){
		System.err.println(o);
	}
	
	public static boolean matches(String input, String regexp){
		return Pattern.compile(regexp).matcher(input).matches();
	}

	public static String getCachePath(){
		return Path.CACHE_PATH_FOR_EXTRACTION;
	}	

	public static String findState(String stateSec){
		//HashSet<String> type = new HashSet<String>();
		String match, list = "", value;
		Iterator<?> i = mexicoStateList.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			value = me.getValue().toString();
			match = Util.match(stateSec, me.getKey().toString(), 0);
			if (match != null) {
				if (!list.contains(value)) {
					list = value; 
				}
			}
		}
		list = (list.length() < 2) ? "-" : list;
		return list;
	}
	
	public static boolean isEmpty(String val){
		
		if(val.trim().length() == 0 || val.isEmpty())
			return true;
		
		return false;
	}
	/**
	 * This method is return the correct format of state of Mexico country.
	 * @param state
	 * @return
	 */
	public static String matchState(String state){
		
		state = state.replaceAll(ignoredState, "");
//		U.log(ignoredState);
//		U.log(state);
		String match, list = "", value;
		Iterator<?> i = mexicoStateList.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			value = me.getValue().toString();
			match = Util.match(state, me.getKey().toString(), 0);
			U.log(state+" ----=-=- "+match);
			if (match != null) {
				U.log(me.getKey().toString());
				U.log(value);
				list = value;
//				break;		
			}
		}
		list = (list.length() < 2) ? "-" : list;
		U.log(state+"-----"+list);
		return list;
	}
	/**
	 * This method is used to check given state is Maxico state or not.
	 * @param StateSec
	 * 
	 * @return
	 */
	public static boolean isState(String state){
		String match, value;
		Iterator<?> i = mexicoStateList.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry me = (Map.Entry) i.next();
			value = me.getValue().toString();
			match = Util.match(state, me.getKey().toString(), 0);
			if (match != null) {
				return true;
			}
		}
		return false;
	}
	
	
	private static Map<String, String> validator = new HashMap<String,String>(){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("Mexico City",	"00,01,02,03,04,05,06,07,08,09,10,11,12,13,14,15,16");
			put("Aguascalientes",	"20");
			put("Baja California",	"21,22");
			put("Baja California Sur",	"23");
			put("Campeche",	"24");
			put("Chiapas",	"29,30");
			put("Chihuahua",	"31,32,33");
			put("Coahuila",	"25,26,27");
			put("Colima",	"28");
			put("Durango",	"34,35");
			put("Guanajuato",	"36,37,38");
			put("Guerrero",	"39,40,41");
			put("Hidalgo","42,43");
			put("Jalisco","44,45,46,47,48,49");
			put("Mexico State",	"50,51,52,53,54,55,56,57");
			put("Michoacan","58,59,60,61");
			put("Morelos","62");
			put("Nayarit","63");
			put("Nuevo Leon","64,65,66,67");
			put("Oaxaca","68,69,70,71");
			put("Puebla","72,73,74,75");
			put("Queretaro","76");
			put("Quintana Roo","77");
			put("San Luis Potosi","78,79");
			put("Sinaloa","80,81,82");
			put("Sonora","83,84,85");
			put("Tabasco","86");
			put("Tamaulipas","87,88,89");
			put("Tlaxcala","90");
			put("Veracruz","91,92,93,94,95,96");
			put("Yucatan","97");
			put("Zacatecas","98,99");
		}
	};
	

	/**
	 * This method is return state from given zip values of Mexico.
	 * @param zip
	 * @return
	 */
	public static String findStateFromZip(String zip){
		
		if(!zip.matches("\\d{4,5}")){
			return null; 
		}
		if(zip.trim().length() == 4)
			zip = "0"+zip;
		
		for(Entry<String, String> it : validator.entrySet()){
		
			if(it.getValue().contains(zip.substring(0, 2))){
				return it.getKey().toString();
			}
		}		
		return null;
	}

	public static String[] getValues(String code, String From, String To) {

		ArrayList<String> al = new ArrayList<String>();
		int n = 0;
		String value = null;
		while (n != -1) {
			int start = code.indexOf(From, n);

			if (start != -1) {
				int end = code.indexOf(To, start + From.length());

				try {
					if (end != -1 && start < end && end < code.length())
						value = code.substring(start + From.length(), end);
				} catch (StringIndexOutOfBoundsException ex) {
					n = end;
					continue;
				}

				al.add(value);
				n = end;
			} else
				break;
		}
		Object ia[] = al.toArray();
		String[] values = new String[ia.length];

		for (int i = 0; i < values.length; i++)
			values[i] = ia[i].toString();

		return values;

	}

	public static String[] getValues(String code, String begin, String From,
			String To) {

		ArrayList<String> al = new ArrayList<String>();
		int n = 0;
		n = code.indexOf(begin, n);
		String value = null;
		while (n != -1) {
			int start = code.indexOf(From, n);

			if (start != -1) {
				int end = code.indexOf(To, start + From.length());

				try {
					if (end != -1 && start < end && end < code.length())
						value = code.substring(start + From.length(), end);
				} catch (StringIndexOutOfBoundsException ex) {
					n = end;
					continue;
				}

				al.add(value);
				n = end;
			} else
				break;
		}

		Object ia[] = al.toArray();
		String[] values = new String[ia.length];

		for (int i = 0; i < values.length; i++)
			values[i] = ia[i].toString();

		return values;

	}

	public static String getSectionValue(String code, String From, String To) {

		String section = null;
		int start, end;
		start = code.indexOf(From);
		// U.log(start);
		if (start != -1) {
			end = code.indexOf(To, start + From.length());
			if (start < end)
				section = code.substring(start + From.length(), end);

		}
		return section;
	}
/*	
	private static HashMap<String, String[]> cityState=U.deserializedSicData("/CityStateList.ser");
	
	public static String findStateFromCity(String city){
		
		Set<String>keys=cityState.keySet();
		
		for (String key : keys) {
			//U.log(Arrays.toString(cityState.get(key)));
			String temp=Util.match(key, city);
			if (temp!=null) {
				return cityState.get(key)[2];
			}
		}
		return "-";
	}*/
	
	private static final HashMap<String, String[]> sicMap = U.deserializedSicData(Path.RESOURCES + "updateSicCodeData.ser");
	
	/**
	 * This method is used to extract SIC code with its information by passing Sub-SIC code. This mehtod is only for Tequila.
	 * @param subSIC :- Pass sub SIC code.
	 * @return
	 * It return Array of String with size of 8.<br>
	 * data[0] = 'Industry Sector'<br>
	 * data[1] = 'Spanish_Industry_Sector'<br>
	 * data[3] = 'Major Sic Category / Principales Categoría Sic'<br>
	 * data[4] = 'SubSicCategory/CategoríaDelSic'<br>
	 * data[5] = 'Product Description'<br>
	 * data[6] = 'Spanish_Product_Description'<br>
	 */
	public final static String[] extractSICInfo(String subSIC){
		
		if(subSIC.isEmpty()) return null;
		if(subSIC.trim().length() != 4) return null;
		String data[] = sicMap.get(subSIC);
		if(data != null){
			data[5] = data[5].replace("’", "'");
		}
		return data;
/*		List<String> list = new ArrayList<>(Arrays.asList(data));
		list.remove(2);
		list.remove(6);
		return list.toArray(new String[list.size()]);
*/	}
	
	
	public static HashMap<String,String[]> deserializedSicData(String fileName){
		HashMap<String, String[]> e=null;
		try {
	        FileInputStream fileIn = new FileInputStream(fileName);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        e = (HashMap<String, String[]>) in.readObject();
	        in.close();
	        fileIn.close();
	     } catch (IOException i) {
	        i.printStackTrace();
	      
	     } catch (ClassNotFoundException c) {
	        System.out.println("no data found");
	        c.printStackTrace();
	     }
		return e;
	}
	
	/**
	 * This method is used to trim all elements in the array of string.
	 * @param data
	 */
	public static final void stripAll(String[] data){
		Arrays.stream(data).map(String::trim).toArray(unused -> data);
	}
	
	/**
	 * This method is used to trim all elements in the list of string.
	 * @param data
	 * @return
	 */
	public static final List<String> stripAll(List<String> data){
		return data.stream().map(String::trim).collect(Collectors.toList());
	}

	public static void deleteFile(String FileName) {
		File file = new File(FileName);
		if (file.delete()) {
			log("Successfully Deleted");
		}
	}
	
	public static String[] findLatLng(String latLngSec) {
		String latLng[] = { "", "" };
		Matcher mat = Pattern.compile("\\d{2}\\.\\d{3,},\\s{0,2}?-\\d{2,3}\\.\\d{3,}", Pattern.CASE_INSENSITIVE)
				.matcher(latLngSec);
		while (mat.find()) {
			latLng = mat.group().split(",");
		}
		return latLng;
	}
	
	
/*	public static ArrayList<String[]> readCSV(String path) throws Exception{
		ArrayList<String[]> nextlline=new ArrayList<>();
		CSVReader reader=new CSVReader(new FileReader(path));
		String[] record = null;
		while ((record = reader.readNext()) != null) {
			if (record[0].contains("SrNo=id")) {
				continue;
			}
			nextlline.add(record);
		}
		U.log(nextlline.size());
		reader.close();
		return nextlline;
	}*/

	public static String[] toArray(ArrayList<String> list) {

		Object ia[] = list.toArray();
		String[] strValues = new String[ia.length];
		for (int i = 0; i < strValues.length; i++)
			strValues[i] = ia[i].toString();
		return strValues;
	}
	// ==================================================================================================================

	public static String toTitleCase(String givenString) {
		if (givenString==null) {
			return "";
		}
		char[] c = { '\'', '-', ',', ' ', ';', '/', '(', ')', '!', '@', '#', ':', '|', '*', '&', '+', '$','.' };
//		char[] c = { '-', ',', ' ', ';', '/', '(', ')', '!', '@', '#', ':', '|', '*', '&', '+', '$','.' };
		//givenString = WordUtils.capitalizeFully(givenString, c).replaceAll("[$&+:=?@#|'<>^*()%!]", "");
		givenString = WordUtils.capitalizeFully(givenString, c);
		givenString = givenString.replaceAll("\\s{2,}", " ").replaceAll("\\-{2,}", "-").trim();
		// givenString=givenString.replaceAll("[$&+,:=?@|'<>^*()%!]", "");
		if (givenString.length() > 1) {
			if ("{[$&+,:=?@|'<>^*%!-]}".contains(givenString.substring(0, 1))) { //()
				// log("hello");
				givenString = givenString.substring(1, givenString.length()).trim();
			}
			if ("{[$&+,:=?@|'<>^*%!-]}".contains(givenString.substring(givenString.length() - 1, givenString.length()))) { //()
				givenString = givenString.substring(0, givenString.length() - 1).trim();
			}
		}
		return givenString.replace(" ;", ";").replace(" -", "-").replace("- ", "-")
				.replace("-", " - ").replace("'S", "'s").trim();
//				.replaceAll(" Cv$", " CV").replace(" Sa ", " SA ").replace(" De ", " DE ");
	}
	
	
	public static String toCapitalizeCase(String givenString){
		char[] c= {'\'','-',',',' ',';','/','(',')','!','@','#',':','|','*','&','+','$'};
		String str=WordUtils.capitalizeFully(givenString.toUpperCase(), c);
		return str.replaceAll("\\s+", " ");
	}
	
	// =================================================================================================================
	public static String formatNumbersAsCode(String s) {
		if(s==null)
			return "";
		String finalNumber = "";
		s = s.replace(",", ";").replaceAll("ext|Ext|Ext\\.|EXT",";ext");
/*		U.log(s);
		if(s.contains("/")){
			String[] vals = s.split("/");
			stripAll(vals);
			U.log(vals.length);
			U.log(Arrays.toString(vals));
		}*/
		if (s.contains(";")) {
			s = s.replaceAll(";{2,}", ";");

			String[] ss = s.split(";");
			for (String string : ss) {
				
				boolean startWithPlus = false;
				if(string.startsWith("+")) startWithPlus = true;
				
				string = string.replaceAll("\\s+| ", "");
				string = string.toString().replace("-", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replaceAll("\\[|\\]|•", "").trim()
						.replace("+", "");
				if(string.contains("ext")){

				}else if (string.length() < 10) {
//					string =  string;
					if(string.length() == 7) string = String.format("%s-%s", string.subSequence(0, 3), string.subSequence(3, 7));
					else if(string.length() == 8) string = String.format("%s-%s", string.subSequence(0, 4), string.subSequence(4, 8));
					else if(string.length() == 9) string = String.format("%s-%s-%s", string.subSequence(0, 2), string.subSequence(2, 5), string.subSequence(5, 9));
					
				} else if (string.length() == 10) {
					string = String.format("%s-%s-%s", string.subSequence(0, 3), string.subSequence(3, 6), string.subSequence(6, 10));
				} else if (string.length() == 11) {
					string = String.format("%s-%s-%s-%s", string.subSequence(0, 1),
							string.subSequence(1, 4), string.subSequence(4, 7), string.subSequence(7, 11));
				} else if (string.length() == 12) {
					string = String.format("%s-%s-%s-%s", string.subSequence(0, 2),
							string.subSequence(2, 5), string.subSequence(5, 8), string.subSequence(8, 12));
					
				} else if (string.length() == 13) {
					string = String.format("%s-%s-%s-%s", string.subSequence(0, 3),
							string.subSequence(3, 6), string.subSequence(6, 9), string.subSequence(9, 13));
				} else if (string.length() == 14) {
					string = String.format("%s-%s-%s-%s-%s", string.subSequence(0, 1), string.subSequence(1, 4),
									string.subSequence(4, 7), string.subSequence(7, 10), string.subSequence(10, 14));
				} else if (string.length() == 15) {
					string = String.format("%s-%s-%s-%s-%s", string.subSequence(0, 2), string.subSequence(2, 5),
									string.subSequence(5, 8), string.subSequence(8, 11), string.subSequence(11, 15));
				} else if (string.length() == 16) {
					string = String.format("%s-%s-%s-%s-%s", string.subSequence(0, 3), string.subSequence(3, 6),
									string.subSequence(6, 9), string.subSequence(9, 12), string.subSequence(12, 16));

				} else {
					string = string.toString();
				}
				
				if(startWithPlus) string ="+"+string;

				finalNumber += ";" + string;
				
			}//eof for
			if (finalNumber.length() < 1) {
				return finalNumber;
			}
			finalNumber = finalNumber.substring(1);
		} else {
			s = s.replaceAll("\\s+| ", "");
			
			boolean startWithPlus = false;
			
			if(s.startsWith("+")) startWithPlus = true;
				
			s = s.toString().replace("-", "").replace(".", "").replace("(", "").replace(")", "").replace("+", "").replaceAll("\\[|\\]", "").trim()
					.replace("+", "");
			
			if(s.contains("ext")){
				
			}if (s.length() < 10) {
				s = s.toString();
				if(s.length() == 7) s = String.format("%s-%s", s.subSequence(0, 3), s.subSequence(3, 7));
				else if(s.length() == 8) s = String.format("%s-%s", s.subSequence(0, 4), s.subSequence(4, 8));
				else if(s.length() == 9) s = String.format("%s-%s-%s", s.subSequence(0, 2), s.subSequence(2, 5), s.subSequence(5, 9));
			} else if (s.length() == 10) {
				s = String.format("%s-%s-%s", s.subSequence(0, 3), s.subSequence(3, 6), s.subSequence(6, 10));
			} else if (s.length() == 11) {
				s = String.format("%s-%s-%s-%s", s.subSequence(0, 1), s.subSequence(1, 4), s.subSequence(4, 7),
						s.subSequence(7, 11));
			} else if (s.length() == 12) {
				s = String.format("%s-%s-%s-%s", s.subSequence(0, 2), s.subSequence(2, 5), s.subSequence(5, 8),
						s.subSequence(8, 12));
			} else if (s.length() == 13) {
				s = String.format("%s-%s-%s-%s", s.subSequence(0, 3), s.subSequence(3, 6), s.subSequence(6, 9),
						s.subSequence(9, 13));
			} else if (s.length() == 14) {
				s = String.format("%s-%s-%s-%s-%s", s.subSequence(0, 1), s.subSequence(1, 4), s.subSequence(4, 7),
						s.subSequence(7, 10), s.subSequence(10, 14));
			} else if (s.length() == 15) {
				s = String.format("%s-%s-%s-%s-%s", s.subSequence(0, 2), s.subSequence(2, 5), s.subSequence(5, 8),
						s.subSequence(8, 11), s.subSequence(11, 15));
			} else if (s.length() == 16) {
				s = String.format("%s-%s-%s-%s-%s", s.subSequence(0, 3), s.subSequence(3, 6), s.subSequence(6, 9),
						s.subSequence(9, 12), s.subSequence(12, 16));
			} else {
				s = s.toString();
			}
			if(startWithPlus) s ="+"+s;
			return s;
		
		}//eof else
		
		if (finalNumber.substring(0, 1).contains(";")) {
			finalNumber = finalNumber.substring(1);
		}
		//return finalNumber.replace(";ext"," ext");
		return finalNumber.replace(";ext"," Ext. ");
	}

	// ===========================================================================================================================

	public static String formatLongitude(String longitude) {
		if (!longitude.contains("-") && longitude.length() > 3) {
			return "-" + longitude;
		}
		return longitude.trim();
	}

	// ===========================================================================================================================
	public static String zipFormat(String zip) {
		int len = zip.length();
		if (len < 4 || len > 5) {
			return zip = "";
		} else if (len == 4) {
			return "0" + zip.trim();
		}
		return zip.trim();
	}
	
	public static void delete(String filePath){
		File file = new File(filePath);
		if (file.isFile()) {
    		if(file.length() < 400){
    			if(file.delete()){
    				U.log("delete::"+file.getName()+" \t(bytes) :" + file.length());
    			}
    		}
		}
	}
	
	
    public static final boolean isValidEmailAddress(String email) {
//           String ePattern = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
           String ePattern ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

           Pattern pattern = Pattern.compile(ePattern);
           Matcher matcher = pattern.matcher(email);
           return matcher.matches();
    }
    
    public static final boolean isValidUrl(String url) {
    	UrlValidator urlValidator = new UrlValidator();
    	if (urlValidator.isValid(url)) {
    		return true;
    	}
    	return false;
    }
    
    
    public static final String formatEmail(String email){
    	if(email.trim().length() > 1){
    		email = email.replaceAll("\\s+", "").replaceAll(";$", "");
    	}
    	return email.trim().toLowerCase();
    }
    
	public static Object deserialized(String path){
		Object obj = null;
		try{
			FileInputStream fis = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
			fis.close();
		}catch(IOException | ClassNotFoundException e){
			e.printStackTrace();
		}
		return obj;
	}
	

	public static void writeSerializedFile(Object obj, String fileName){
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);		
	}
	
	public static void writeSerializedFileWithStringArray(MultiMap<String, String[]> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}
	
	public static void writeSerializedFileWithString(Map<String, String> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}
	
	public static void writeSerializedFileWithStringArray(Map<String, String[]> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}
	

	public static void writeSerializedFileWithList(Map<String, List<String>> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}
	public static void writeSerializedFileWithString(Set<String> obj, String fileName){
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.close();
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
		U.log("Writing is done of file ::"+fileName);
	}

	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final String getTodayDate(){
		return dateFormat.format(new Date());
	}
	/**
	 * 
	 * @return it only return the current date with this format 'dd_MMM_yyyy'<br>
	 * for Eg.: 07_Sep_2018
	 */
	public static final String getTodayDateWith(){
		LocalDate date = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MMM_yyyy");
		return date.format(formatter);
	}
	
	private static final Set<String> cityStateZipSet = (Set<String>) deserialized(Path.RESOURCES +"CityStateZipSet.ser");
	
	public static boolean isValidCityStateZip(String zip, String city, String state){
		if(zip.trim().length() == 4){
			zip = "0"+zip.trim();
		}
		if(zip.trim().length() == 5){
			return cityStateZipSet.contains((zip.trim()+city.trim()+state.trim()).toLowerCase());				
		}
		return false;
	}
	
	public static String[] getAddressGoogleApi(String latlng[])
			throws IOException {
		String st = latlng[0].trim() + "," + latlng[1].trim();
		String addr = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ st;
		U.log(addr);

		U.log(U.getCache(addr));
		String html = U.getPageSource(addr);

		String txt = U.getSectionValue(html, "formatted_address\" : \"", "\"");
		U.log("txt:: " + txt);
		if (txt != null)
			txt = txt.trim();
		txt = txt.replaceAll("The Arden, |TPC Sugarloaf Country Club, ", "").replace("50 Biscayne, 50", "50");
		txt = txt.replaceAll("110 Neuse Harbour Boulevard, ", "");
		txt = txt
				.replaceAll(
						"Waterview Tower, |Liberty Towers, |THE DYLAN, |Cornerstone, |Roosevelt Towers Apartments, |Zenith, |The George Washington University,|Annapolis Towne Centre, |Waugh Chapel Towne Centre,|Brookstone, |Rockville Town Square Plaza, |University of Baltimore,|The Galleria at White Plains,|Reston Town Center,",
						"");
		String[] add = txt.split(",");
		add[3] = Util.match(add[2], "\\d+");
		add[2] = add[2].replaceAll("\\d+", "").trim();

		// U.log("Priti"+add[2]);
		return add;
	}

	public static String[] getAddressGoogleApiProxy(String latlng[])
			throws IOException {
		String st = latlng[0].trim() + "," + latlng[1].trim();
		String proxy = "http://75.119.204.81:3302/gproxy?to=";

		String addr = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
				+ st;
		proxy = proxy + addr;
		U.log(proxy);

		U.log(U.getCache(proxy));
		String html = U.getPageSource(proxy);

		String txt = U.getSectionValue(html, "formatted_address\" : \"", "\"");
		U.log("txt:: " + txt);
		if (txt != null)
			txt = txt.trim();
		txt = txt.replaceAll("The Arden, |TPC Sugarloaf Country Club, ", "");
		txt = txt.replaceAll("110 Neuse Harbour Boulevard, ", "");
		txt = txt
				.replaceAll(
						"Waterview Tower, |Liberty Towers, |THE DYLAN, |Cornerstone, |Roosevelt Towers Apartments, |Zenith, |The George Washington University,|Annapolis Towne Centre, |Waugh Chapel Towne Centre,|Brookstone, |Rockville Town Square Plaza, |University of Baltimore,|The Galleria at White Plains,|Reston Town Center,",
						"");
		String[] add = txt.split(",");
		add[3] = Util.match(add[2], "\\d+");
		add[2] = add[2].replaceAll("\\d+", "").trim();

		// U.log(lat);
		return add;
	}

	public static String[] getlatlongGoogleApi(String add[]) throws IOException {
		String addr = add[0] + "," + add[1] + "," + add[2];
		addr = "https://maps.googleapis.com/maps/api/geocode/json?address="
				+ URLEncoder.encode(addr, "UTF-8");
		U.log(addr);
		U.log(U.getCache(addr));
		String html = U.getHTML(addr);

		String sec = U.getSectionValue(html, "location", "status");
		/*
		 * Below condition modified by Sawan. On date 26 Sept 2017
		 */
//		if(sec != null){
			String lat = U.getSectionValue(sec, "\"lat\" :", ",");
			if (lat != null)
				lat = lat.trim();
			String lng = U.getSectionValue(sec, "\"lng\" :", "}");
			if (lng != null)
				lng = lng.trim();
			 String latlng[] = { lat, lng };
			 return latlng;
//		}
		// U.log(lat);
//		return null;
	}

	public static String[] getlatlongGoogleApiProxy(String add[])
			throws IOException {
		String proxy = "http://75.119.204.81:3301/gproxy?to=";
		String addr = add[0] + "," + add[1] + "," + add[2];
		addr = "https://maps.googleapis.com/maps/api/geocode/json?address="// 1138
																			// Waterlyn
																			// Drive","Clayton","NC
				+ URLEncoder.encode(addr, "UTF-8");
		proxy = proxy + addr;
		U.log(proxy);
		U.log(U.getCache(proxy));
		String html = U.getHTML(proxy);
		String sec = U.getSectionValue(html, "location", "status");
		String lat = U.getSectionValue(sec, "\"lat\" :", ",");
		if (lat != null)
			lat = lat.trim();
		String lng = U.getSectionValue(sec, "\"lng\" :", "}");
		if (lng != null)
			lng = lng.trim();
		String latlng[] = { lat, lng };
		// U.log(lat);
		return latlng;
	}
	
	public static String getCacheFileName(String url) {

		String str = url.replaceAll("http://", "");
		str = str.replaceAll("www.", "");
		str = str.replaceAll("[^\\w]", "");
		if (str.length() > 200) {
			str = str.substring(0, 100) + str.substring(170, 190)
					+ str.length() + "-" + str.hashCode();

		}

		try {
			str = URLEncoder.encode(str, "UTF-8");
			// U.log(str);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

		}
		return str + ".txt";
	}



	/**
	 * @param args
	 * @throws IOException
	 */
	// New function that mimics browser

	public static String getHtml(String url, WebDriver driver, Boolean flag)
			throws Exception {
		// WebDriver driver = new FirefoxDriver();
		String html = null;
		String Dname = null;
		String host = new URL(url).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;
		File folder = null;
		if (flag == true)
			folder = new File(U.getCachePath()+ Dname + "Quickkk");
		else
			folder = new File(U.getCachePath() + Dname);
		if (!folder.exists())
			folder.mkdirs();
		String fileName = U.getCacheFileName(url);

		// fileName = "C:/cache/" + Dname + "/" + fileName;
		if (flag == true)
			fileName = U.getCachePath() + Dname + "Quickkk" + "/"
					+ fileName;
		else {
			fileName = U.getCachePath()+ Dname + "/" + fileName;
		}

		File f = new File(fileName);
		if (f.exists())
			return html = FileUtil.readAllText(fileName);
		// if(f.exists()){
		// f.delete();
		// }
		if (!f.exists()) {

			BufferedWriter writer = new BufferedWriter(new FileWriter(f));

			driver.get(url);
			// driver.get(url);
			// for (int second = 0;; second++) {
			//
			// if (second >= 6) {
			// break;
			// }
			((JavascriptExecutor) driver).executeScript(
					"window.scrollBy(0,3000)", ""); // y value '400' can be
			// ((Object)
			// driver).execute_script("arguments[0].style.border = '1px solid red';")
			// ((JavascriptExecutor)driver).executeScript("document.getElementById('cboxOverlay').style.opacity =1;");
			// ((JavascriptExecutor)driver).executeScript("document.getElementById('cboxClose').click()");
			// cboxClose
			// driver.execute_script("document.getElementById('cboxOverlay').style.opacity = 1;")
			// altered
			// Thread.sleep(3000);
			// Lennar code
			U.log("sahil hello");
			try {

				U.log("sahil hello");
				// WebElement option =
				// driver.findElement(By.cssSelector("#main-content > div.floorplan-rollup-wrap > div.floorplan-loadmore > a"));//cntQMI//available-homes

				// WebElement option = driver.findElement(By.id("b1"));
				// option = driver.findElement(By.id("b2"));
				// option =driver.findElement(By.id("btn"));

				// driver.manage().timeouts().implicitlyWait(10,
				// TimeUnit.SECONDS);
				WebElement option = null;

				option = driver.findElement(By.id("cntQMI"));
				option.click();
				// WebElement
				// option=driver.findElement(By.xpath("//*[@id=\"cntQMI\"]"));//*[@id="cntQMI"]

				Thread.sleep(2000);
				// U.log("click succusfull");
				// / option.click();
				// option.click();
				// option.click();
				// option.click();
				U.log("click succusfull1");

			} catch (Exception e) {
				U.log(e.toString());
				// System.exit(1);
			}
			// }
			// Thread.sleep(2000);
			// Thread.sleep(3*1000);
			U.log("Current URL:::" + driver.getCurrentUrl());
			html = driver.getPageSource();
			Thread.sleep(2 * 1000);

			writer.append(html);
			writer.close();

		} else {
			if (f.exists())
				html = FileUtil.readAllText(fileName);
		}

		return html;

	}
	
	public static String getHtmlWithChromeBrowser(String url, WebDriver driver) throws Exception {
		// WebDriver driver = new ChromeDriver();

		String html = null;
		String Dname = null;

		String host = new URL(url).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;
		File folder = null;

		folder = new File(U.getCachePath() + Dname);
		if (!folder.exists())

			folder.mkdirs();
		String fileName = U.getCacheFileName(url);

		fileName = U.getCachePath() + Dname + "/" + fileName;

		File f = new File(fileName);
		if (f.exists()) {
			return html = FileUtil.readAllText(fileName);
			// U.log("Reading done");
		}

		// int respCode = CheckUrlForHTML(url);

		// if(respCode==200)
		{

			if (!f.exists()) {
				synchronized (driver) {

					BufferedWriter writer = new BufferedWriter(
							new FileWriter(f));
	/*				driver.manage()
							.addCookie(
									new Cookie("visid_incap_612201",
											"gt5WFScsSRy46ozKP+BwUyrx4FcAAAAAQUIPAAAAAADA5A7HU2IYoId7VKl8vCPR"));
*/					driver.get(url);
					Thread.sleep(2000);
					((JavascriptExecutor) driver).executeScript(
							"window.scrollBy(0,400)", ""); // y value '400' can
					// be

					// WebElement click =
					// driver.findElement(By.xpath("//*[@id=\"reset-available-homes\"]"));
					// click.click();

					Thread.sleep(8000);
					U.log("Current URL:::" + driver.getCurrentUrl());
					html = driver.getPageSource();
					//Thread.sleep(5000);
					writer.append(html);
					writer.close();

				}
			} else {
				if (f.exists()) {
					html = FileUtil.readAllText(fileName);
					U.log("Reading done");
				}
			}
			return html;
		}
		// else{
		// return null;
		// }
	}


	public static String getHtml(String url, WebDriver driver) throws Exception {
		// WebDriver driver = new FirefoxDriver();

		String html = null;
		String Dname = null;

		String host = new URL(url).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;
		File folder = null;

		folder = new File(U.getCachePath() + Dname);
		if (!folder.exists())

			folder.mkdirs();
		String fileName = U.getCacheFileName(url);

		fileName = U.getCachePath()+ Dname + "/" + fileName;

		File f = new File(fileName);
		if (f.exists()) {
			return html = FileUtil.readAllText(fileName);
			// U.log("Reading done");
		}

		// int respCode = CheckUrlForHTML(url);

		// if(respCode==200)
		{

			if (!f.exists()) {
				synchronized (driver) {

					BufferedWriter writer = new BufferedWriter(
							new FileWriter(f));
				driver.manage()
							.addCookie(
									new Cookie("visid_incap_612201",
											"gt5WFScsSRy46ozKP+BwUyrx4FcAAAAAQUIPAAAAAADA5A7HU2IYoId7VKl8vCPR"));
					driver.get(url);
				//	Thread.sleep(2000);
					((JavascriptExecutor) driver).executeScript(
							"window.scrollBy(0,400)", ""); // y value '400' can
					// be

					// WebElement click =
					// driver.findElement(By.xpath("//*[@id=\"reset-available-homes\"]"));
					// click.click();

					Thread.sleep(6000L);
					U.log("Current URL:::" + driver.getCurrentUrl());
					html = driver.getPageSource();
					Thread.sleep(5000);
					
//					Thread.sleep(000);
					writer.append(html);
					writer.close();

				}
			} else {
				if (f.exists()) {
					html = FileUtil.readAllText(fileName);
					U.log("Reading done");
				}
			}
			return html;
		}
		// else{
		// return null;
		// }
	}

	public static String getHtml(String url, WebDriver driver, String id)
			throws Exception {

		String html = null;
		String Dname = null;
		String host = new URL(url).getHost();
		
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;
		File folder = null;
		folder = new File(U.getCachePath() + Dname);
		if (!folder.exists())
			folder.mkdirs();
		String fileName = U.getCacheFileName(url);
		fileName = U.getCachePath() + Dname + "/" + fileName;
		File f = new File(fileName);
		if (f.exists())
			return html = FileUtil.readAllText(fileName);
		if (!f.exists()) {
			synchronized (driver) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(f));
				U.log("in gethtml==" + url);
				driver.get(url);
				Actions dragger = new Actions(driver);
				WebElement draggablePartOfScrollbar = driver.findElement(By
						.id(id));
				int numberOfPixelsToDragTheScrollbarDown = 50;
				for (int i = 10; i < 500; i = i	+ numberOfPixelsToDragTheScrollbarDown) {
					try {
						dragger.moveToElement(draggablePartOfScrollbar).click().moveByOffset(0,	numberOfPixelsToDragTheScrollbarDown)	.release().perform();
						Thread.sleep(1000L);
					} catch (Exception e1) {
					}
				}
				U.log("Current URL:::" + driver.getCurrentUrl());
				html = driver.getPageSource();
				writer.append(html);
				writer.close();
			}
		} else {
			if (f.exists())
				html = FileUtil.readAllText(fileName);
		}
		return html;

	}
	
	public static int CheckUrlForHTML(String path) {
		// TODO Auto-generated method stub
		int respCode = 0;
		try {

			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost request1 = new HttpPost(path);
			HttpResponse response1 = httpclient.execute(request1);
			respCode = response1.getStatusLine().getStatusCode();
		} catch (Exception ex) {

			U.log(ex);
			return respCode;
		}
		return respCode;
	}

	public static String getHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
//		 U.log(" .............."+path);
		 
		String fileName = getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
			//return null;
		URL url = new URL(path);
		String html = null;
		// chk responce code
		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {
		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"3.213.139.74",8888));
		final URLConnection urlConnection = url.openConnection();  //proxy
		// Mimic browser
		try {
			//Thread.sleep(10000);
			urlConnection
			.addRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36");
	urlConnection.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
	urlConnection.addRequestProperty("Accept-Language",	"en-us,en;q=0.5");
	urlConnection.addRequestProperty("Cache-Control", "max-age=0");
	urlConnection.addRequestProperty("referer", "https://restaurantguru.com/Licantropo-Mexico-City");
//	urlConnection.addRequestProperty("x-frsc-token", "5c9bb5acec348dcfacda71a9e62af0219edb98d33fee8dab4fe06bf4441a4ff1");
	urlConnection.addRequestProperty("cookie", "PHPSESSID=48f7aeb29ab166d444f40924af452ccf; _gcl_au=1.1.64598125.1626435819; _ga=GA1.2.772670950.1626435821; __gads=ID=b05638a964d930c7-2227b41f5aca00a0:T=1626435821:RT=1626435821:S=ALNI_MYk1VH7FL8bvC0qCceRQriPnRhKlQ; _gid=GA1.2.1598576255.1627561074; currentCountry=9; currentCity=140856; for_br8072=8072; rg_check=1; closebnr=1; dc_location=ci180353; for_br8078=8078; client_time_hour=2021-07-30%2010:41:30; _gat=1");
	urlConnection.addRequestProperty("Connection", "keep-alive");
	urlConnection.addRequestProperty("upgrade-insecure-requests", "same-origin");
	urlConnection.addRequestProperty("sec-fetch-mode", "same-origin");
	urlConnection.addRequestProperty("sec-fetch-dest", "empty");
	urlConnection.addRequestProperty("sec-ch-ua", "\"Chromium\";v=\"91\", \" Not;A Brand\";v=\"99\"");
	urlConnection.addRequestProperty("sec-fetch-site", "1");
	urlConnection.setConnectTimeout(15000);
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
	
	public static String getHTML(String path, String ip, int port) throws IOException {

		path = path.replaceAll(" ", "%20");
		// U.log(" .............."+path);
		// Thread.sleep(4000);
		String fileName = getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);

		URL url = new URL(path);

		String html = null;

		// chk responce code

		int respCode = CheckUrlForHTML(path);
		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
		final URLConnection urlConnection = url.openConnection(proxy);  //proxy

		// Mimic browser
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language",
					"en-us,en;q=0.5");
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

	private static String toString(InputStream inputStream) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream));
		String line;

		StringBuilder output = new StringBuilder();
		while ((line = in.readLine()) != null) {
			output.append(line).append("\n");
		}
		in.close();
		return output.toString();
	}
	
	public static void printFile(String[] lines, String fileName) {

		Writer output = null;

		try {
			output = new BufferedWriter(new FileWriter(fileName, true));
			for (int i = 0; i < lines.length; i++)
				output.write(lines[i] + "\n");

			output.close();

		} catch (IOException ex) {
		} finally {
		}

	}

	public static void printFile(ArrayList<String> list, String fileName) {

		Writer output = null;

		try {
			output = new BufferedWriter(new FileWriter(fileName, true));

			for (String item : list)
				output.write(item + "\n");

			output.close();

		} catch (IOException ex) {
		} finally {
		}

	}

	public static String degToDecConverter(String str) {

		double s1, s3, s4;
		double val;
		String s2;
		s1 = Integer.parseInt(str.substring(0, str.indexOf("&deg;")));
		s2 = str.substring(str.indexOf(";"));
		s3 = Integer.parseInt(s2.substring(2, s2.indexOf("'")));
		s4 = Integer
				.parseInt(str.substring(str.length() - 3, str.indexOf("\"")));

		val = s1 + (s3 / 60) + (s4 / 3600);
		return Double.toString(val);
	}

	public static String[] getMapQuestLatLong(String html) {

		String lat = null, lng = null;
		String reg = "\"latLng\":\\{\"lng\":(-\\d+.\\d+),\"lat\":(\\d+.\\d+)";
		String reg2 = "\"latLng\":\\{\"lat\":(\\d+.\\d+),\"lng\":(-\\d+.\\d+)";
		String match = Util.match(html, reg, 0);
		if (match == null) {
			lat = Util.match(html, reg2, 1);
			lng = Util.match(html, reg2, 2);
		} else {
			lat = Util.match(html, reg, 2);
			lng = Util.match(html, reg, 1);
		}
		return new String[] { lat, lng };
	}
	
	public static String getPageSource(String url) throws IOException {

		String html = null;
		String fileName = U.getCache(url);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		
		WebDriver driver = new HtmlUnitDriver();

		driver.get(url);
		html = driver.getPageSource();
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, html);
		return html;
	}// //
	
	public static ArrayList<String> removeDuplicates(ArrayList<String> list) {

		HashSet<String> hs = new HashSet<String>();
		hs.addAll(list);
		list.clear();
		list.addAll(hs);
		hs = null;
		return list;
	}// ///


	public static String getNoHtml(String html) {  
		String noHtml=null;  
		noHtml = html.toString().replaceAll("\\<.*?>","");  
		return noHtml; 
	}




	public static String removeComments(String html) {
		String[] values = U.getValues(html, "<!--", "-->");
		for (String item : values)
			html = html.replace(item, "");
		return html;

	}
	
	/**
	 * @author Sawan
	 * 
	 */
	 
	public static String getProjectPath(){
		return System.getProperty("user.home") + File.separator+ Path.PROJECT_NAME + File.separator; 
	}
	
	public static String getCache(String path) throws MalformedURLException {

		String Dname = getDemain(path);
		File folder = new File(getCachePath() + Dname);
		if (!folder.exists())
			folder.mkdirs();
		String fileName = getCacheFileName(path);
		fileName = getCachePath() + Dname + "/" + fileName;
		return fileName;
	}
	
	
	private static String getDemain(String path) throws MalformedURLException{
		String host = new URL(path).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		return ((dot != -1) ? host.substring(0, dot) : host);
	}
	
	public String [] getGoogleLatLong(String add[]) throws IOException{
		String addr = add[0] + "," + add[1] + "," + add[2];
		addr = "https://maps.googleapis.com/maps/api/geocode/json?address="
				+ URLEncoder.encode(addr, "UTF-8");
	//	log(addr);
	//	log(U.getCache(addr));
		String html = getGoogleHTML(addr);

		String sec = getSectionValue(html, "location", "status");

		String lat = getSectionValue(sec, "\"lat\" :", ",");
		if (lat != null)
			lat = lat.trim();
		
		String lng = getSectionValue(sec, "\"lng\" :", "}");
		if (lng != null)
			lng = lng.trim();
		
		String latlng[] = {"", ""};
		String status = getSectionValue(html, "status\" : \"", "\"");
		if(status.trim().equals("OK")){
			latlng[0] = lat;
			latlng[1] = lng;
			return latlng;
		}else
			return latlng;
	}
	
	public static String[] getGoogleAddress(String latLng[]) throws IOException{
		
		String st = latLng[0].trim() + "," + latLng[1].trim();
		String addr = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+ st;

		String html = getGoogleHTML(addr);
		log(html);
		String status = getSectionValue(html, "status\" : \"", "\"");
		
		if(status.trim().equals("OK")){
			String txt = getSectionValue(html, "formatted_address\" : \"", "\"");
			log("txt:: " + txt);
			if (txt != null)
				txt = txt.trim();
			txt = txt.replaceAll("The Arden, |TPC Sugarloaf Country Club, ", "").replace("50 Biscayne, 50", "50");
			txt = txt.replaceAll("110 Neuse Harbour Boulevard, ", "");
			txt = txt
					.replaceAll(
							"Waterview Tower, |Liberty Towers, |THE DYLAN, |Cornerstone, |Roosevelt Towers Apartments, |Zenith, |The George Washington University,|Annapolis Towne Centre, |Waugh Chapel Towne Centre,|Brookstone, |Rockville Town Square Plaza, |University of Baltimore,|The Galleria at White Plains,|Reston Town Center,",
							"");
			String[] add = txt.split(",");
			add[3] = Util.match(add[2], "\\d+");
			add[2] = add[2].replaceAll("\\d+", "").trim();
			
			return add;
		}else{
			return new String[]{"","","",""};
		}
	}
		
	public static boolean isValidFileSize(String path) throws MalformedURLException{
		
		File cacheFile = new File(getCache(path));
		if (cacheFile.exists()){
			if(cacheFile.length() > 400)
				return true;
		}
		return false;
	}
	
	private static String readFile(String fileName) throws IOException {
		return FileUtil.readAllText(fileName);
	}
	
	/*
	 * List of Google keys
	 */
	private static List<String> googleKeys = new ArrayList<String>(){
		{
			add("AIzaSyBgHZxKFTDWDtBszK4OnSJi2tyd_Td2Wdw"); //sawan
			add("AIzaSyBLWy53qG4g1fH5njFVgPZvoNPFCDiZkz0"); //sawan
		}
	};
	
	private static String checkGoogleKeys(String path) throws IOException{
		String pathWithKey = null;	
		int i = 0;
		//check if cache is exist or not
		for(String key : googleKeys){
			i++;
			String tempPath = path + "&key=" + key;
			String temFileName = getCache(tempPath);
			File tempCacheFile = new File(temFileName);
			if (tempCacheFile.exists()){
				if(i != googleKeys.size()){
					if(tempCacheFile.length() < 400)continue;
				}
				pathWithKey = tempPath;
				break;
			}
		}
		
		return pathWithKey;
	}
	
	public static String getGoogleHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
		
		String fileName = getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists()){
			if(cacheFile.length() > 400){
				log(fileName);
				return FileUtil.readAllText(fileName);
			}
		}
		
		//without google key
		String html = extractHtml(path, fileName);
		
		boolean validSize = isValidFileSize(path);
		if(validSize){
			log(fileName);
			return html;
		}
		
		//with google key
		String tempPath = checkGoogleKeys(path);
		
		validSize = isValidFileSize(tempPath);
		if(validSize){
			log(getCache(tempPath));
			return readFile(getCache(tempPath));
		}
		
		//extract with google key 
		for(String key : googleKeys){
			tempPath = path + "&key=" + key;
			String temFileName = getCache(tempPath);
			html = extractHtml(tempPath, temFileName);
			
			validSize = isValidFileSize(tempPath);
			
			if(validSize){
				log(temFileName);
				return readFile(getCache(tempPath));
			}
		}
		
		return null;
	}
	
	public static String extractHtml(String path,String fileName) throws IOException{
		path = path.replaceAll(" ", "%20");
		URL url = new URL(path);
		String html = null;

//		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("70.169.70.88",	80));
		
		final URLConnection urlConnection = url.openConnection();  //proxy

		// Mimic browser
		try {
			urlConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language",	"en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
			// final String html = toString(inputStream);
			inputStream.close();

			FileUtil.writeAllText(fileName, html);
			
		} catch (ConnectException | SocketTimeoutException |UnknownHostException e) {
			log(e);
/*			log("Sleeping for 5 mins");
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			extractHtml(path,fileName);
*/		}
		return html;
	}
	
	public static String extractHtml(String path,String fileName, String ip, int port) throws IOException{
		path = path.replaceAll(" ", "%20");
		URL url = new URL(path);
		String html = null;

		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip,	port));
		
		final URLConnection urlConnection = url.openConnection(proxy);  //proxy

		// Mimic browser
		try {
			urlConnection.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language",	"en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
			// final String html = toString(inputStream);
			inputStream.close();

			FileUtil.writeAllText(fileName, html);
			
		} catch (ConnectException | SocketTimeoutException |UnknownHostException e) {
			log(e);
/*			log("Sleeping for 5 mins");
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			extractHtml(path,fileName);
*/		}
		return html;
	}
	
	public static String[] getMapQeustAddress(String[] latLong) {
		String[] add= {"","","",""};
		
		String location = String.join(",", latLong);
		try {
			String url = "http://www.mapquestapi.com/geocoding/v1/reverse?key=7AV661nmsttamglUwA8meTKVbngvSC2k&location="+location
		+"&includeNearestIntersection=true&callback=reverseGeocodeResult&outFormat=xml";

			U.log(url);
			String html= U.getHTML(url);
//			U.log(html);
//			U.log(U.getCache(url));
			String statusCode = U.getSectionValue(html, "<statusCode>", "</statusCode>");
			if(statusCode.trim().equals("0") || !statusCode.startsWith("40") || !statusCode.trim().startsWith("50")){
				add[0] = U.getSectionValue(html, "<street>", "</street>");
				add[1] = U.getSectionValue(html, "type=\'City\'>", "</adminArea5>");
				if(add[1] == null || add[1].isEmpty())
					add[1] = U.getSectionValue(html, "type=\'County\'>", "</adminArea4>");
				
				add[2] = U.getSectionValue(html, "type=\'State\'>", "</adminArea3>");
				add[3] = U.getSectionValue(html, "<postalCode>", "</postalCode>");
			}else{
				U.delete(U.getCache(url));
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log(Arrays.toString(add));
		return add;
	}
	
	/**
	 * @author Sawan
	 */
	public static String[] getAddressGoogle(String addSec) {
		String add[] = {BLANK, BLANK, BLANK, BLANK, BLANK };

		if(addSec.isEmpty() || addSec == null)return add;
		addSec = addSec.replace("(Cto. Com. Sor.,", "(Cto. Com. Sor.),");
//		U.log(addSec);
		addSec = StringEscapeUtils.unescapeHtml4(addSec);
		String zipCityState = Util.match(addSec, "\\d{5}(.*?)$");
		U.log(zipCityState);
		
		//In case if zip is present
		if(zipCityState != null){
			add[4] = Util.match(zipCityState, "\\d{5}"); //zip
			if(add[4] != null){
				addSec = addSec.replaceAll(zipCityState, "").trim().replaceAll(",$", "");
				zipCityState = zipCityState.replace(add[4], "");
				
				String vals[] = zipCityState.trim().split(",");
				if(vals.length == 3 || vals.length == 2){
					add[2] = vals[0].trim(); //city
					add[3] = vals[1].trim(); //state
					
					if(vals[1].trim().equals("Mexico")){
						add[2] = null; //city
						add[3] = vals[0].trim(); //state						
					}
				}
				
				vals = addSec.split(",");
				if(vals.length > 1){
					if(add[2] == null){
						if(vals.length > 2){
							add[2] = vals[vals.length-1].trim(); //city
							add[1] = vals[vals.length-2].trim(); //colonia
						}else{
							add[2] = vals[vals.length-1].trim(); //city
							add[1] = "";//colonia
						}
						addSec = addSec.replaceAll(add[2]+"$", "").trim().replaceAll(",$", ""); //street
					}
					else add[1] = vals[vals.length-1].trim(); //colonia
				
					if(add[1] != null && !add[1].isEmpty())
						add[0] = addSec.replaceAll(add[1]+"$", "").trim().replaceAll(",$", ""); //street
				}else{
					add[0] = addSec.trim().replaceAll(",$", ""); //street
				}			
			}
		}else{
			//In case if zip is absent
			String vals[] = addSec.split(",");
			if(vals.length >= 5){
				if(vals[vals.length-1].trim().equals("Mexico")){
					add[3] = vals[vals.length-2]; //state
					add[2] = vals[vals.length-3]; //city
					add[1] = vals[vals.length-4]; //colonia
					add[0] = vals[vals.length-5]; //street
				}				
			}else{
				if(vals.length == 4){
					if(vals[vals.length-1].trim().equals("Mexico")){
						add[3] = vals[vals.length-2]; //state
						add[2] = vals[vals.length-3]; //city
						add[0] = vals[vals.length-4]; //street
					}				
				}
			}
		}
		if(add[3].length()<=5){
			add[3] = MXStates.getFullNameFromAbbr(add[3].trim()); //state Full Abbr
		}
		if(add[3] == null)add[3] = "";
		return add;
	}

	public static List<String[]>  readCsvFile(String filePath){
		List<String[]>  readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(filePath));)
		{
			readLines = reader.readAll();
		}catch (Exception e) {
			U.log("Came here in error block");
			e.printStackTrace();
		}
		U.log("Load input file...... Done: "+ filePath);
		U.log("Size of record is ::"+readLines.size());
		return readLines;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public static List<String[]> readCsvFileWithoutHeader(String fileName){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName),',','"',1);){
			readLines = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readLines;
	}
	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 */
	public static void writeCsvFile(List<String[]> writeLines, String filePath){
		try(CSVWriter writer = new CSVWriter(new FileWriter(filePath),',');){
			writer.writeAll(writeLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();			
		}
		U.log("Done writing here.. at "+filePath);
	}
	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 */
	public static void writeCsvFile(ArrayList<String[]> writeLines, String filePath){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(lines, filePath);
	}
	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 */
	public static void writeCsvFile(Collection<String[]> writeLines, String filePath){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(lines, filePath);
	}

	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 * @param append
	 */
	public static void writeCsvFile(List<String[]> writeLines, String filePath, boolean append){
		try(CSVWriter writer = new CSVWriter(new FileWriter(filePath, append),',');){
			writer.writeAll(writeLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();			
		}
		U.log("Done writing here..");
	}
	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 * @param append
	 */
	public static void writeCsvFile(ArrayList<String[]> writeLines, String filePath, boolean append){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(lines, filePath, append);
	}
	/**
	 * 
	 * @param writeLines
	 * @param filePath
	 * @param append
	 */
	public static void writeCsvFile(Collection<String[]> writeLines, String filePath, boolean append){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(lines, filePath, append);
	}
	
	public static void writeCsvFile(String[] header, List<String[]> writeLines, String filePath){
		try(CSVWriter writer = new CSVWriter(new FileWriter(filePath),',');){
			writer.writeNext(header);
			writer.writeAll(writeLines);
			writer.flush();
		}catch(IOException e){
			e.printStackTrace();			
		}
		U.log("Done writing here..");
	}
	/**
	 * 
	 * @param header
	 * @param writeLines
	 * @param filePath
	 */
	public static void writeCsvFile(String[] header, ArrayList<String[]> writeLines, String filePath){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(header, lines, filePath);
	}
	
	public static void writeCsvFile(String[] header, Collection<String[]> writeLines, String filePath){
		List<String[]> lines = new ArrayList<>(writeLines);
		writeCsvFile(header, lines, filePath);
	}

	public static final String trim(String val){
		val = val.trim().replace(" ", " ").replaceAll("\\s{1,}", " ").replaceAll(" $|^ ", "").trim(); //.replaceAll("  *", "")
		return val;
	}
	
	public static void deleteZeroBytesFiles(String dirPath){
		File[] files = new File(dirPath).listFiles();
		int count = 0;
		for(File file :files){
			if (file.isFile()) {
	    		if(file.length() < 460){
//					U.log(file.getName());

	    			if(file.delete()){
	    				U.log("delete::"+file.getName()+" \t(bytes) :" + file.length());
	    				count++;
	    			}
	    		}
			}
		}
		U.log("Total Files :"+files.length);
		U.log("Deleted Count :"+count);
	}//eof deleteZeroBytesFiles()

	/**
	 * This method is used to find the list of invalid email. If size of list is zero then it consider as the given email does not contain invalid email.
	 * @param email
	 * @return list of string that contain invalid email.<br>
	 *  If it return null then it means that input email is either null or empty.  
	 */
	public static List<String> invalidEmailList(String email){
		if(email == null) return null;
		if(email.isEmpty()) return null;
		
		List<String> invalidEmail = new ArrayList<>();
		if(email.contains(";")){
			String emails[] = email.split(";");
			boolean validEmail [] = new boolean[emails.length];
			for(int i = 0; i < emails.length; i++){
				if(!emails[i].contains("@"))
					validEmail[i] = false;
				else{
					String[] vals = emails[i].split("@");
					if(vals.length != 2)
						validEmail[i] = false;
					else if (vals.length == 2){
						validEmail[i] = MailHostsLookup.isEmailExist(vals[1].trim());
					}
				}
			}
			for(int i = 0; i < validEmail.length; i++){
				if(validEmail[i] == false)
					invalidEmail.add(emails[i]);
			}
		}else{
			String[] vals = email.split("@");
			if(vals.length != 2)
				invalidEmail.add(email);
			else if (vals.length == 2){
				if(!MailHostsLookup.isEmailExist(vals[1].trim())){
					invalidEmail.add(email);
				}				
			}
		}
		return invalidEmail;
	}
	
	
	/**
	 * This method is used to split address into street, neighborhood, city, state and zip.
	 * @param addSec
	 * @return Arrays size of 5, includes {@code [street, neighborhood, city, state, zip]}
	 */
	public static String[] getAddress(String addSec){
		
		final String ALLOW_BLANK = "-";
		String[] add = {"","","","",""};
		
		if(addSec.isEmpty())return add;
		U.log("Input Test ::"+addSec);
		addSec = addSec.trim().replaceAll("\\.$|C\\.P\\.", "").replace(";", ",")
				.replaceAll("  +| +", " ").trim();
		
		String extraInfo = Util.match(addSec, "\\(.*?\\)");
		if(extraInfo != null) addSec = addSec.replace(extraInfo, "");
		
		if(addSec.length() < 10 && !addSec.contains(",")){
			add[0] = addSec;
			return add;
		}
		/*
		 * To Find Zip		 
		 */
		ArrayList<String> zipList = Util.matchAll(addSec, "\\d{5}",0); //exp. to find postal code in address section

		if(zipList.size() > 0)
			add[4] = zipList.get(zipList.size()-1); //found zip
		if(add[4].isEmpty()){
			if(Util.match(addSec, "C\\.?P\\.? \\d{4}") != null){
				add[4] = Util.match(addSec, "C\\.?P\\.? (\\d{4})", 1);
			}
		}
		
		//State 
		if(!add[4].isEmpty()){ //if postal code is found
			addSec= addSec.replaceAll("\\.$", "");
			if(addSec.endsWith(add[4])){ //If zip is present at address end 
				log(add[4]);
				log(addSec.indexOf(add[4]));
				int index = addSec.lastIndexOf(",", addSec.indexOf(add[4])-4);
				if(index == -1)	index = addSec.lastIndexOf(",", addSec.lastIndexOf(add[4]));
				log(addSec);
//				if(index == -1)	index = addSec.lastIndexOf(",", addSec.indexOf(add[4]));

				String val = addSec.substring(index);

				if(val != null)val = val.replaceAll(add[4]+"|,", "").trim();
				
				if(val.length()>4){
					if(val.length() > 5) val = val.replaceAll(",$|\\.?", "");

					if(!U.matchState(val).equals(ALLOW_BLANK)) add[3] = val; //found state
				}
				if(add[3].equals("-")) add[3] = "";
								
			}else{
				//If zip is present at middle in the address 

				String val = addSec.substring(addSec.indexOf(add[4])+add[4].length()); //remaining section onward postal code
				val = val.trim().replaceAll("^,|,$", "");
				
				String v[] = val.split(",");
				
				if(v.length >1){
					if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country

						if(!U.matchState(v[v.length-2]).equals(ALLOW_BLANK)){
							add[3] = v[v.length-2]; //found state
						}
					}
					if(v.length > 2 && add[3].length() > 1)
						add[2] = v[v.length-3]; //found city
				}	
								
				if(add[3].length() > 1){ // && add[2].length() > 1){
					if(addSec.indexOf(add[4]) > 1)
						addSec = addSec.replace(addSec.substring(addSec.indexOf(add[4])), "").trim().replaceAll(",$", "");  //replace zip, if it is present in between address
					
					if(addSec.indexOf(add[4]) == 0){
						addSec = addSec.trim().replaceAll("^"+add[4], "");  //replace zip, if it is present at beginning of address
						addSec = addSec.trim().replaceAll("^,", "").trim();
						
						if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){
							addSec = addSec.replaceAll(v[v.length-1]+"$", "").trim();
							for(int i = v.length-1; i >= 0 && i >= v.length-3; i--){
								addSec = addSec.replaceAll(v[i]+"$", "").trim();
								addSec = addSec.replaceAll(",$", "").trim();
							}
						}
					}
					if(v.length == 4) addSec = addSec.trim()+", "+v[0].trim();
				}
			}
		}
			
		/*
		 * To find Address, Neighborhood and city
		 */
		String[] vals = SplitNeighborhoodFromAdd.splitColonia(addSec);

		if(vals.length == 2 && add[2].isEmpty()){
			if(!add[3].isEmpty()) vals[1] = vals[1].replaceAll(add[3], "");
			if(!add[4].isEmpty()) vals[1] = vals[1].replaceAll(add[4], "");
			add[0] = vals[0];
			String val = addSec;

			if(!add[0].isEmpty())val = val.replace(add[0], "");
			if(!add[4].isEmpty())val = val.replace(add[4], "");
			if(!add[3].isEmpty())val = val.replace(add[3], "");

			if(!val.isEmpty()){ //if val is not empty
				val = val.trim().replaceAll("^,|\\.$|, ,|,$", "").trim().replaceAll(",$", "").replace(",,", ",");

				if(val.contains(",")){
					String v[] = val.split(",");
					
					if(v.length == 2){
						add[1] = v[0].trim(); //found neighborhood
						add[2] = v[1].trim(); //Found city
					}
					if(v.length == 3){
						if(v[2].length()>4){

							if(!U.matchState(v[2].trim()).equals(ALLOW_BLANK)) add[3] = v[2].trim();  //found state
						}
						add[1] = v[0].trim(); //found neighborhood
						add[2] = v[1].trim(); //Found city
					}
					if(v.length == 4){
						if(v[3].length()>4){

							if(!U.matchState(v[3].trim()).equals(ALLOW_BLANK)) add[3] = v[3].trim();  //found state
						}
						add[1] = v[0].trim()+ ", " + v[1].trim(); //found neighborhood
						add[2] = v[2].trim(); //Found city
					}
				}

			}else{
				//when address section and street both are same
				if(addSec.trim().equals(add[0].trim())){
					//TODO :
					if(add[4].length() == 5) addSec = addSec.replace(add[4], "");
					addSec = addSec.trim().replaceAll(",$|^,", "").trim();

					if(add[3].length() > 1 && !addSec.equals(add[0].trim())){
						addSec = addSec.replace(add[3], "");
					}
					addSec = addSec.trim().replaceAll(",$|^,", "").trim();
					if(!addSec.equals(add[0].trim())) add[0] = addSec;
					
					String v[] = addSec.split(",");

					
					if(add[3].length() < 2){ // To Find state
						if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country

							if(!U.matchState(v[v.length-2]).equals(ALLOW_BLANK)){
								add[3] = v[v.length-2]; //found state
							}
						}
						if(add[3].length() > 1){
							add[0] = add[0].trim().replaceAll(v[v.length-1]+"$", "");
							add[0] = add[0].trim().replaceAll(",$", "").replaceAll(v[v.length-2]+"$", "");
							add[0] = add[0].trim().replaceAll(",$", "");
						}

						v = add[0].split(",");
					}
					
					if(add[2].length() < 2 && add[3].length() > 1){ //To Find City, and Neighborhood
						
						if(v.length > 1){
							add[2] = v[v.length-1].trim(); //Found city
						}
						if(v.length > 2){
							add[1] = v[v.length-2].trim(); //found neighborhood
						}
					}
					add[0] = add[0].trim().replaceAll(add[2]+"$", "");
					add[0] = add[0].trim().replaceAll(",$", "").replaceAll(add[1]+"$", "");
				}
			}
			
			if(add[1].length() < 2 && !vals[1].trim().isEmpty()){
				vals[1] = vals[1].trim().replaceAll(",$|^,|\\.$", "")
						.trim().replaceAll(",$|^,|\\.$", "");

				add[1] = vals[1]; //found neighborhood
			}
		}//eof if
		else if(vals.length == 2 && add[2].length() > 1){
			if(add[3].length() > 1 && add[4].length() > 1){
				if(vals[1].isEmpty()){ //Check if neighborhood is empty
					String[] v = addSec.split(",");
					if(v.length >1){
						add[1] = v[v.length-1].trim(); //found neighborhood
						String street = "";
						for(int i = 0; i < v.length-1; i++){
							street = street+ v[i].trim()+ ", ";
						}
						add[0] = street;
					}
					
				}else{
					add[1] = vals[1]; //found neighborhood
					add[0] = vals[0]; //found street
				}
			}
		}
		
		//if zip & colonia are not find at above algo.
		if(add[2].isEmpty() && add[3].isEmpty()){
			String v[] = null;
			if(!add[0].isEmpty()){
				v = add[0].split(",");
				if(v.length == 3){
					if(v[v.length-1].length() > 4){

						if(!U.matchState(v[v.length-1].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-1].trim();  //found state
					}
					add[2] = v[1]; //found city
					add[0] = v[0]; //found street
				}

				if(v.length > 3){
					if(v[v.length-1].length() > 4){

						if(!U.matchState(v[v.length-1].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-1].trim();  //found state
					}
					if(add[3].equals("-")){
						if(v[v.length-2].length() > 4){

							if(!U.matchState(v[v.length-2].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-2].trim();  //found state
						}
						add[2] = v[v.length-1].trim(); //found city
					}else{
						add[2] = v[v.length-2].trim(); //found city
					}
				}
				if(!add[2].isEmpty())add[0] = add[0].replace(add[2], "");
				if(!add[3].isEmpty())add[0] = add[0].replace(add[3], "");
			}
		}
		if(add[3].equals("-")) add[3] = "";
		add[0] = add[0].replace(" ,", ",").replace(",", ", ").replaceAll("\\s{2,}", " ").trim().replace(", ,", ",").replaceAll(",$", "").trim();
		if(extraInfo != null) add[0] += " "+extraInfo;
		
		add[0] = U.trim(add[0]);
		add[1] = U.trim(add[1]);
		add[2] = U.trim(add[2]);

		if(add[1].matches("\\d+")){
			add[0] += ", "+add[1];
			add[1] = "";
		}
		if(add[2].matches("\\d+")) add[2] = "";
		if(add[2].length() > 1){

			if(add[0].contains(add[1])) add[0] = add[0].replace(add[1], "");
			
			add[1] = add[1].replaceAll(add[2]+"$", "").trim().replaceAll("\\.$|,$|^,", "");
		}
		add[0] = U.trim(add[0]);
		add[1] = U.trim(add[1]);

		add[3] = U.trim(add[3]); //.trim().replaceAll(" $|^ ", "");

		if(!add[3].equals(ALLOW_BLANK) && U.matchState(add[3]).equals(ALLOW_BLANK)) add[3] = "";
		add[4] = U.trim(add[4]); //.trim().replaceAll(" $|^ ", "");;
		if(add[0].contains(",")&&add[1].trim().length()==0) {
			String streetCol[]=add[0].split(",");
			if(streetCol.length==2) {
			add[0]=streetCol[0].trim();
			add[1]=streetCol[1].trim();
			}else if(streetCol.length==3) {
				add[0]=streetCol[0].trim()+", "+streetCol[1].trim();
				add[1]=streetCol[2].trim();
			}
		}
		
		U.log("**************** Result ******************");
		U.log("Street : "+add[0]);
		U.log("Colonia : "+add[1]);
		U.log("City : "+add[2]);
		U.log("State : "+add[3]);
		U.log("Zip : "+add[4]);
		U.log("**********************************");

		return add;
	}
	
	/**
	 * This method is used to give list of dates from starting date to end of date.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public final static List<String> dateRange(String startDate, String endDate){
		LocalDate _startDate = LocalDate.parse(startDate);
		LocalDate _endDate = LocalDate.parse(endDate);
		
		List<String> dateRange = new ArrayList<>();
		while(!_startDate.isAfter(_endDate)){
			dateRange.add(_startDate.toString());
			if(_startDate.isEqual(_endDate))break;
			_startDate = _startDate.plusDays(1);
		}
		return dateRange;
	}
	/**
	 * This method is used to give list of dates from starting date to end of date except it exclude the SUNDAY.
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public final static List<String>  dateRangeIgnoreSunday(String startDate, String endDate){
		LocalDate _startDate = LocalDate.parse(startDate);	
		LocalDate _endDate = LocalDate.parse(endDate);	
		List<String> dateRange = new ArrayList<>();
		
		while(!_startDate.isAfter(_endDate)){
			if(!_startDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
				dateRange.add(_startDate.toString());
			}
			if(_startDate.isEqual(_endDate))break;
			_startDate = _startDate.plusDays(1);
		}
		return dateRange;
	}
	
	static {
	    disableSslVerification();
	}
	public static void disableSslVerification() {
	    try
	    {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
				}
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType)
						throws CertificateException {
					// TODO Auto-generated method stub
				}
	        }
	        };
	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
	public static ArrayList<String[]> readTabCsv(String path) {
		ArrayList<String[]> nextlline=new ArrayList<>();
		FileInputStream inputStream = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream(path);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				String[] aData = line.split("\t");
				nextlline.add(aData);
			}
			sc.close();
			inputStream.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return nextlline;
	}
	
	public static final String INSERTIONHEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
			};
	
	
	public static String findCityFromZip(String zip){
		Map<String,String> cityZipMap = (Map<String, String>) U.deserialized("/home/chinmay/eclipse-workspace/MexicoDataProcess/resources/MexicoAddress/cityZip.ser");

		String city = "";
		//U.log(cityZipMap);
		city = cityZipMap.get(zip);
		if(city==null)city = "";
		return city;
		
	}
	
	public static String getHTMLForGoogleApiWithKey(String path,String key) throws Exception {
		path = path.replaceAll(" ", "%20");
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists()) {
			if (cacheFile.length()> 400) {
				return FileUtil.readAllText(fileName);
			}else if (cacheFile.length()<400) {
				cacheFile.delete();
			}
		}
		URL url = new URL(path+"&key="+key);
		String html = null;
		//Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("45.77.146.9", 55555));
		final URLConnection urlConnection = url.openConnection(); // proxy
		// Mimic browser
		try {
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
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
	public static String removeHtml(String html) {
	    return Jsoup.parse(StringEscapeUtils.unescapeHtml3(html)).text();
	}
	public static String getRedirectedURL( String mainDomain,String url) throws IOException {
		//if(!url.contains("http"))url = mainDomain+url;
		String fileName=U.getCache(mainDomain);
		File cacheFile = new File(fileName);
		//log(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("207.144.111.230",8080));
		HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
	    con.setInstanceFollowRedirects(false);
	    con.setRequestProperty("cookie", "PHPSESSID=48f7aeb29ab166d444f40924af452ccf; country=20; _gcl_au=1.1.64598125.1626435819; _ga=GA1.2.772670950.1626435821; _gid=GA1.2.1771367959.1626435821; __gads=ID=b05638a964d930c7-2227b41f5aca00a0:T=1626435821:RT=1626435821:S=ALNI_MYk1VH7FL8bvC0qCceRQriPnRhKlQ; rg_check=1; currentCountry=9; currentCity=140856; closebnr=1; for_br8072=done; dc_location=ci109130; client_time_hour=2021-07-17%2012:21:28");
	    try {
	    con.connect();
	    con.getInputStream();
	    
	    U.log("response code : "+con.getResponseCode());
	    if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
	        String redirectUrl = con.getHeaderField("Location");
	        return getRedirectedURL(mainDomain,redirectUrl);
	    }
	    }catch(Exception e) {e.printStackTrace();}
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, url);
	    return url;
	}
	public static String sendPostRequest(String path, String parameter) throws Exception {
		// firstname=&lastname=&extension=&department=21%3A%3AAccess+Services
		String fileName=U.getCache(path+parameter);
		File cacheFile = new File(fileName);
		//log(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		String html="";
		String urlParameters = parameter;
		byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
		int postDataLength = postData.length;
		URL url = new URL(path);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("54.89.151.62", 5836));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setInstanceFollowRedirects(false);
		conn.setRequestMethod("POST");
		
		conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json, text/plain, */*");
//		conn.setRequestProperty("Referer", "https://www.strategis.mx/Glocator/");
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/73.0.3683.103 Chrome/73.0.3683.103 Safari/537.36");
//			conn.setRequestProperty("Cookie", "usu_tiendeo=89898989454857456868324849585352585356465748535756514957485349; lastcssversion=MTYwMTAyNjE2NA==; _ga=GA1.2.277350916.1601362500; _gid=GA1.2.1424906007.1601362500; __gads=ID=80734b7f64dfd093:T=1601362533:S=ALNI_MbK1yq_dKTblRbUKhMcd9B9WLmVgg; city_tiendeo2=QWNhcHVsY28gZGUgSnXDoXJleg==; openingDate=2020-9-29 12:30:31.195; _gat_UA-36734402-1=1");
//		conn.setRequestProperty("apollographql-client-version", "v91_1310_3_aj_9ff98190ce6");
//		CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
		conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
//		conn.setRequestProperty("x-trv-cst","1443703219,1459869632,27291,32046,35446,39578,40402,42320,43759,42164,42280,42673,44964,38217,45171,45433,39875,45349,45749,46136,46138,46164,46378,46411,46534,46587,46363,46876,46906,43806,46535,47123,47121,44769,47159,47036,47357,47185,47005,45124,46296,47737,45039,47748,46927,46928,47250,47252,47251,47367,46364,46145,47225,47306,48040,47832,47405,48237,48011,45613,47772,46860,44051,48020,48395,48329,48491,48013,47828,47908,48700,48796,48546,47739,48871,48227,48440,48458,48994,48943,48292,48956,48910,49162,48911,48848,48531,48672,49108,48681,48998,46734,43316,47340");
		conn.setUseCaches(false);
		try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
			wr.write(postData);
		}
		final InputStream inputStream = conn.getInputStream();
		html=IOUtils.toString(inputStream,StandardCharsets.UTF_8);
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, html);
		return html;
	}
	public static final String MY_GECKO_PATH = System.getProperty("user.home") + File.separator + "geckodriver";
	public static void setUpGeckoPath() {
		if (SystemUtils.IS_OS_LINUX) {
			System.setProperty("webdriver.gecko.driver", MY_GECKO_PATH);
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("webdriver.gecko.driver", MY_GECKO_PATH + ".exe");
		}
		clearFirefoxConsoleLogs();
	}
	// Clear selenium log from console
	public static void clearFirefoxConsoleLogs() {
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE, "true");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,
				System.getProperty("user.home") + File.separator + "Selenium_logs.txt");
		U.log("[::: Clear Firefox Console Logs ::::]");
	}
	public static final String MY_CHROME_PATH = System.getProperty("user.home") + File.separator + "chromedriver";

	public static void setUpChromePath() {
		if (SystemUtils.IS_OS_LINUX) {
			System.setProperty("webdriver.chrome.driver", MY_CHROME_PATH);
		}
		if (SystemUtils.IS_OS_WINDOWS) {
			System.setProperty("webdriver.chrome.driver", MY_CHROME_PATH + ".exe");
		}
	}

}
