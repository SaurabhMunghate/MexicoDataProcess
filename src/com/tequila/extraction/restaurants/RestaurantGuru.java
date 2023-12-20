/**
 * @author Sawan
 * @date 24 July 2019
 */
package com.tequila.extraction.restaurants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class RestaurantGuru extends DirectoryList {

	private static final String URL = "https://restaurantguru.com";
	private static final String NAME = "Restaurant Guru";
	static int start = 0;
	static int end = 0;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		Scanner scan = new Scanner(System.in);
		U.log("Start :");
		start = scan.nextInt();
		U.log("End :");
		end = scan.nextInt();
		scan.close();

		RestaurantGuru rg = new RestaurantGuru();
		rg.extractProcess();
		rg.printAll(NAME + "_" + start + "_" + end + ".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub

		/*
		 * String html = U.getHTML("https://restaurantguru.com/cities-Mexico-c"); //All
		 * cities String[] sections = U.getValues(html, "<div class=\"part_title\"",
		 * "</ul>"); // U.log(sections.length); for(String section : sections){ //
		 * U.log(section); String urlSec = U.getSectionValue(section, ">", "</div>");
		 * String allCitiesUrl = U.getSectionValue(urlSec, "<a href=\"", "\""); //
		 * U.log(allCitiesUrl);
		 * 
		 * if(allCitiesUrl != null) findAlphabeticCities(allCitiesUrl);
		 * 
		 * String citiesUrls[] = U.getValues(section, "<li><a", "</li>"); for(String
		 * cityUrl : citiesUrls){ cityUrl = U.getSectionValue(cityUrl, " href=\"",
		 * "\""); } }
		 */
		int deletedCount = 0;
		String url = "https://restaurantguru.com/best-restaurants-Mexico-b";
		// String html = U.getHTML(url);
		String html = U.getPageSource(url);

		String resultCount = U.getSectionValue(html, "class=\"filter_count\">", "</span>");
		resultCount = resultCount.replaceAll(" results found|\\s", "").trim();

		html = null;
		U.log(resultCount);
		int count = Integer.parseInt(resultCount);
		for (int i = 1; i <= (count / 20) + 1; i++) {
			// if(i >= 7587 && i <= 10000) //Deleted files count :641

			// if(i >= 13128 && i <= 15000) //Deleted files count :726
			// if(i >= 17790 && i <= 20000)
			// if(i >= 20000)
			// if(i == 10000)
			// if(i >= 48 && i <= 100) // 0 to 100
			// if(i >= 153 && i <= 200) //100 to 200
			// if(i >= 1000 && i <= 1000) //100 to 200

			if (i >= start && i <= end) // 100 to 200
			{
//				try {
//					Thread.sleep(100);
//				} catch (Exception e) {
//				}
				U.log("count :" + i + "\t" + url + "/" + i);
				String pageHtml = U.getPageSource(url + "/" + i);// getHTML(url+"/"+i);
				// U.log(pageHtml);
				String urlSections[] = U.getValues(pageHtml, "<div class=\\\"wrapper_info\\\">", "<\\/a>");
				if (urlSections.length == 0)
					urlSections = U.getValues(pageHtml, "<div class=\"wrapper_info\">", "</a>");
				U.log(urlSections.length);

				if (deleteFile(url + "/" + i)) {
					deletedCount++;
					U.log("File is deleted.");
				}
				for (String urlSec : urlSections) {
					// U.log(urlSec);
					String name = U.getSectionValue(urlSec, "<a title=\\\"", "\\\"");
					if (name == null)
						name = U.getSectionValue(urlSec, "<a title=\"", "\"");

					String restaurantUrl = U.getSectionValue(urlSec, "\" href=\\\"", "\"");
					if (restaurantUrl == null)
						restaurantUrl = U.getSectionValue(urlSec, "\" href=\"", "\"");
					restaurantUrl = restaurantUrl.replace("\\", "");
					// U.log(name+"\t"+restaurantUrl);
					extractRestaurantDetails(restaurantUrl.trim(), name, url + "/" + i);

					name = null;
					restaurantUrl = null;
				//	break;
				}
				//
				urlSections = null;
				pageHtml = null;
				// break;
			} // eof if
			//break;
		} // eof for

		U.log("Deleted files count :" + deletedCount);
		U.log("deletedCountRest ::" + deletedCountRest);
	}

	/*
	 * private void findAlphabeticCities(String url) throws IOException{
	 * U.log("allCityUrl :"+url); String html = U.getHTML(url);
	 * 
	 * String pagingSection = U.getSectionValue(html, "<div class=\"pagination\">",
	 * "<div class=\"footer\">"); if(pagingSection != null){ U.log(pagingSection);
	 * 
	 * String[] pagingUrls = U.getValues(pagingSection, "\" href=\"", "\"");
	 * for(String pagingUrl : pagingUrls){ U.log("pagingUrl =="+pagingUrl); String
	 * pagingHtml = U.getHTML(pagingUrl); } } }
	 * 
	 * 
	 * private void findAllCities(String html){ String section =
	 * U.getSectionValue(html, "<div class=\"cities scrolled-container\">",
	 * "<div class=\"footer\">"); String[] urlSections = U.getValues(section,
	 * "<li>", "</li>");
	 * 
	 * for(String urlSec : urlSections){ String cityUrl = U.getSectionValue(urlSec,
	 * "<a href=\"", "\""); String cityName = U.getSectionValue(urlSec,
	 * "class=\"city_name\">", "</span>"); String cityCount =
	 * U.getSectionValue(urlSec, "\"city-cnt\">", "</span>"); int count =
	 * Integer.parseInt(cityCount.trim()); } }
	 */
	String sicSub = "5812";
	int deletedCountRest = 0;
	int j = 0;

	private void extractRestaurantDetails(String url, String name, String refUrl) throws Exception {
		// if(j == 0 )
		{
			// if(!url.contains("https://restaurantguru.com/Sedona-Grill-Cancun-2"))return;
			U.log("Rest. Count =" + j);
			U.log(url);
			// String html = U.getHTML(url);
			String html = U.getPageSource(url);

			if (deleteFile(url)) {
				deletedCountRest++;
				U.log("File is deleted.");
				return;
			}

			if (html == null || html.contains("<div class=\"info404\">"))
				return;
			String section = U.getSectionValue(html, "type=\"application/ld+json\">", "</script>");

			section = TranslateEnglish.removeUnicode(section);
			name = TranslateEnglish.removeUnicode(name);

			// U.log(name);

			String hoursOfOperation = ALLOW_BLANK;
			String phone = ALLOW_BLANK;
			String lat = ALLOW_BLANK, lng = ALLOW_BLANK;
			String email = ALLOW_BLANK;
			String companyUrl = ALLOW_BLANK;
			String street = ALLOW_BLANK, colonia = ALLOW_BLANK, city = ALLOW_BLANK, state = ALLOW_BLANK,
					zip = ALLOW_BLANK;

			if (section != null) {
				// Phone
				phone = U.getSectionValue(section, "telephone\": \"", "\"");

				// Lat-Lng
				lat = U.getSectionValue(section, "\"latitude\": \"", "\"");
				lng = U.getSectionValue(section, "\"longitude\": \"", "\"");

				// Email
				email = U.getSectionValue(section, "email\": \"", "\"");

				// Company URL
				companyUrl = U.getSectionValue(section, "url\": \"", "\"");

				// Address
				street = U.getSectionValue(section, "streetAddress\": \"", "\"");
				state = U.getSectionValue(section, "addressRegion\": \"", "\"");
				city = U.getSectionValue(section, "addressLocality\": \"", "\"");
			} else {
				// Phone
				phone = U.getSectionValue(html, "class=\"call\">", "</span>");

				// Lat-Lng
				String latLngSec = U.getSectionValue(html, "var _location =", "</script>");
				if (latLngSec != null) {
					lat = U.getSectionValue(latLngSec, "lat:", ",");
					lng = U.getSectionValue(latLngSec, "lng:", "}");
					// string geocodeRequest = "http://dev.virtualearth.net/REST/v1/Locations/"
					// + addressLine
					// + "?o=xml&key="+ Path.BINGKEY;
				}
				// Company URL
				String companyUrlSec = U.getSectionValue(html, "Website</div>", "</div>");
				if (companyUrlSec == null)
					companyUrlSec = U.getSectionValue(html, "<div class=\"website\">", "<div class=\"wrapper_malls\">");
				if (companyUrlSec == null)
					companyUrlSec = ALLOW_BLANK;
				companyUrl = U.getSectionValue(companyUrlSec, "target=\"_blank\">", "</a>");

				// Address
				String addSec = U.getSectionValue(html, "Address</div>", "</div>");
				if (addSec == null)
					addSec = U.getSectionValue(html, " id=\"info_location\">", "</a>");
				addSec = addSec.replaceAll("<.*?>|Get directions| Address", "");

				String add[] = U.getAddress(addSec.trim());
				street = add[0];
				colonia = add[1];
				city = add[2];
				state = add[3];
				zip = add[4];
			}
			String addr[] = getBingAddress(lat, lng);
			if (addr != null && addr.length == 5) {
				street = addr[0];
				colonia = addr[1];
				city = addr[2];
				state = addr[3];
				zip = addr[4];
			}
			// U.log(phone);
			String scheduleSec = U.getSectionValue(html, "<table class=\"schedule-table\">", "</table>");
			// U.log("----> "+scheduleSec+" < ----");
			if (scheduleSec != null) {
				hoursOfOperation = getFormatedHoursOfOperation(scheduleSec);
			}
			if (phone == null)
				phone = ALLOW_BLANK;
			phone = phone.replaceAll("<span>", "").trim();

			if (email == null)
				email = ALLOW_BLANK;

			if (companyUrl == null)
				companyUrl = ALLOW_BLANK;

			if (state == null)
				state = ALLOW_BLANK;
			if (city == null)
				city = ALLOW_BLANK;
			if (street == null)
				street = ALLOW_BLANK;

			// Add details
			addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, companyUrl);
			addAddress(street, colonia, city, state, zip);
			// addAddress(add[0], add[1], add[2], add[3], add[4]);
			addBoundaries(lat, lng);
			addReferenceUrl(url);
			addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email);
			addCompanyHoursOfOperation(hoursOfOperation);
		}
		j++;
	}

	public String[] getBingAddress(String lat, String lon) throws Exception {
		String[] addr = null;
		String htm = U.getHTML("http://dev.virtualearth.net/REST/v1/Locations/" + lat + "," + lon
				+ "?o=json&jsonp=GeocodeCallback&key=Ak8RoKwrZE-IbLkkXRFae9UTXw2UhpuET1mWY9z_ZkzTYR_-TCG8pMcNIUEtiqo5");
//		U.log(U.getCache("http://dev.virtualearth.net/REST/v1/Locations/" + lat + "," + lon
//				+ "?o=json&jsonp=GeocodeCallback&key=Ak8RoKwrZE-IbLkkXRFae9UTXw2UhpuET1mWY9z_ZkzTYR_-TCG8pMcNIUEtiqo5"));
//		U.log(htm);
		String[] adds = U.getValues(htm, "formattedAddress\":\"", "\"");
		for (String item : adds) {
			addr = U.getAddress(item);
			if (addr == null || addr[0] == "-")
				continue;
			else {
				U.log("Bing Address =>  Street:" + addr[0] + " Neighb :" + addr[1] + " city :" + addr[2] + " State :"
						+ addr[3]);
				return addr;
			}

		}
		return addr;
	}

	private String getFormatedHoursOfOperation(String scheduleSec) {
		String weekdayTime[] = U.getValues(scheduleSec, "<span class=\"short-day\">", "</tr>");
		// U.log(Arrays.toString(weekdayTime));
		JSONObject obj = new JSONObject();
		for (String daytime : weekdayTime) {
			// U.log(daytime);
			String timerange = U.getSectionValue(daytime, "<td", "</td>").replaceAll(">|class=\"current-day\"", "")
					.trim();

			String day = daytime.replaceAll(timerange
					+ "|</span>|</td>|<td|>|class=\"current-day\"|\\d+(A|P)M-\\d+(P|A)M|\\d+(P|A)M-\\d+:\\d+(P|A)M|<br/|\\d+:",
					"").trim();
			// U.log(" --> " + day);
			if (!(timerange.contains("Closed") || timerange.contains("Open 24 hours"))) {
				if (timerange.contains("<br")) {
					String multipleTime[] = timerange.replace("/", "").split("<br");
					// U.log(Arrays.toString(multipleTime));
					JSONArray arr = new JSONArray();
					for (String times : multipleTime) {
						// U.log(times);
						String startEnd[] = times.trim().split("-");

						String start = parseTime(startEnd[0]);
						String end = parseTime(startEnd[1]);

						JSONObject startJson = new JSONObject();
						startJson.put("start", start);
						startJson.put("end", end);
						arr.add(startJson);

					}
					// U.log(arr);
					obj.put(day.toLowerCase(), arr);
				} else {
					String startEnd[] = timerange.split("-");

					String start = parseTime(startEnd[0]);
					String end = parseTime(startEnd[1]);

					JSONArray arr = new JSONArray();
					JSONObject startJson = new JSONObject();
					startJson.put("start", start);
					startJson.put("end", end);
					arr.add(startJson);
					// 0:00 23:59
					obj.put(day.toLowerCase(), arr);
				}
			} else if (timerange.contains("Open 24 hours")) {
				JSONArray arr = new JSONArray();
				JSONObject startJson = new JSONObject();
				startJson.put("start", "00:00");
				startJson.put("end", "23:59");
				arr.add(startJson);

				obj.put(day.toLowerCase(), arr);
			} else {
				JSONArray arr = new JSONArray();
				JSONObject startJson = new JSONObject();
				// startJson.put("start", "");
				// startJson.put("end", "");
				// arr.add(startJson);

				obj.put(day.toLowerCase(), arr);
			}
		}
		if (obj.size() != 7)
			System.err.println("-->>>>>>>>>>>>>>> " + obj.size());

		return obj.toJSONString();
	}

	public String parseTime(String time) {
		if (time.length() == 7) {
			return LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mma", Locale.US))
					.format(DateTimeFormatter.ofPattern("HH:mm"));
		} else if (time.length() == 6) {
			return LocalTime.parse(time, DateTimeFormatter.ofPattern("h:mma", Locale.US))
					.format(DateTimeFormatter.ofPattern("HH:mm"));
		} else {
			// U.log(time);
			return LocalTime.parse(time, DateTimeFormatter.ofPattern("ha", Locale.US))
					.format(DateTimeFormatter.ofPattern("HH:mm"));
		}
	}

	// {mon: [{start: "10:00",end: "22:00"}],tue: [{start: "10:00",end: "22:00"}],
	// wed: [{start: "10:00",end: "22:00"}],thu: [{start: "10:00",end: "22:00"}],
	// fri: [{start: "10:00",end: "22:00"}],sat: [{start: "10:00",end: "22:00"}],
	// sun: [{start: "10:00",end: "22:00"}]}
	public boolean deleteFile(String url) throws Exception {
		File cacheFile = new File(U.getCache(url));
		if (cacheFile.exists() && cacheFile.isFile()) {
			if (cacheFile.length() < 1001) {
				return cacheFile.delete();
			}
		}
		return false;
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

		/*
		 * Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
		 * "216.56.85.100", 8080));
		 */ final URLConnection urlConnection = url.openConnection(); // proxy

		// Mimic browser
		try {
			urlConnection.addRequestProperty("User-Agent",
					"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
			urlConnection.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			urlConnection.addRequestProperty("Accept-Language", "en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			// urlConnection.addRequestProperty("Referer",
			// "https://restaurantguru.com/"+city);
			urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
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
