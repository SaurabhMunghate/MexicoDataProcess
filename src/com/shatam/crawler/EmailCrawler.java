package com.shatam.crawler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;


public class EmailCrawler {
	private static HashSet<String>urls=null;
	private static HashMap<String,HashSet<String>>cacheUrl=null;
	private static HashSet<String>emails=null;
	//private static String baseUrl="http://chai.com.mx/";
	private static String baseUrl="https://www.buffalowildwings.com.mx/";
	
	//private static String filePath="/home/chinmay/Mexico/MexicoDataFiles/Data/Jul/INSERTED/Jul_31/JULY_31_4File_CORRECT_NW_REC_formatted_Unique_CORRECT_NW_REC_CORRECT_NW_REC.csv";
	private static String filePath="/home/shatam-10/MexicoCache/Cache/BuffaloWildWings.csv";
	
	public static void main(String[] args) throws IOException {
		try {
			startCrawling();
//			U.log(getEmailFromWebpage("http://tacocitycafe.com"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		U.log(emails.size());
		U.log(emails);
	}
	private static void startCrawling() throws IOException {
		cacheUrl=new HashMap<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		int emailCol=0;
		int websiteCol=0;
		int j=0;
		List<String[]> dataRows=U.readCsvFile(filePath);
		for (String[] data : dataRows) {
			U.log(j);
			if (data[0].contains("ID")) {
				for (int i = 0; i < data.length; i++)
					if (data[i].equalsIgnoreCase("EMAIL"))
						emailCol=i;
					else if (data[i].equalsIgnoreCase("URL")) 
						websiteCol=i;
				writer.writeNext(data);
				continue;
			}
			if (data[websiteCol].trim().length()==0) {
				U.log(Arrays.toString(data));
				writer.writeNext(data);
				continue;
			}
			if (!cacheUrl.containsKey(data[websiteCol])) {
				try {
					cacheUrl.put(data[websiteCol], getEmailFromWebpage(data[websiteCol]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (cacheUrl.containsKey(data[websiteCol])) {
				data[emailCol]=cacheUrl.get(data[websiteCol]).toString();
			}
			U.log(Arrays.toString(data));
			writer.writeNext(data);
		}
		FileUtil.writeAllText(filePath.replace(".csv", "_withEmail_Fb.csv"), sw.toString());
		writer.close();
		sw.close();
		
	}
	public static HashSet<String> getEmailFromWebpage(String url) throws Exception {
		baseUrl=url;
		urls=new HashSet<>();
		emails=new HashSet<>();
		
		checkUrlStatus(baseUrl);
		return emails;
	}
	private static void checkUrlStatus(String baseURl) throws Exception {
//		if (!baseURl.contains("facebook.com")) return;
		URL url = new URL(baseURl);
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
		int statusCode = http.getResponseCode();
		if (statusCode<400 && !baseURl.contains("facebook.com")) {
			U.log(statusCode);
			crawler(baseURl);
		}else if (statusCode<400) {
			baseUrl=baseUrl.replaceAll("https://facebook|http://facebook|https://m.facebook", "https://www.facebook");
			crawlerFB(baseURl);
		}else {
			throw new Exception("Error with Url Returned "+statusCode);
		}
	}
	private static void crawler(String inputUrl) throws IOException, InterruptedException {
		//U.log(inputUrl);
		String html=getHTML(inputUrl.trim());
		ArrayList<String>em=getEmail(Jsoup.parse(html).text());
		
		if (em!=null) {
			emails.addAll(em);
		}
		Document doc=Jsoup.parse(html,"UTF-8");
		Elements links = doc.select("a[href]");
//		U.log(html);
		for (Element page : links) {//contact-uscontacto
//			U.log(page.attr("href"));
            if (page.attr("href").contains("contact-us")||page.attr("href").contains("contact")||page.attr("href").contains("location")||page.attr("href").contains("Locations")) {
            	String url=page.attr("href");
            	U.log(url);
            	if (!url.startsWith("http")) {
					url=baseUrl+url;
				}
            	if (url.contains(baseUrl.replaceAll("http:/|https:/|", ""))) {
            		if (urls.add(url)) {
            			crawler(url);
            		}
            	}
			}
        }
	}
	private static void crawlerFB(String inputUrl) throws IOException, InterruptedException {
		U.log(inputUrl);
		
		String html=getHTML(inputUrl.trim().replaceAll("https://facebook|http://facebook", "https://www.facebook"));
//		U.log(html);
		ArrayList<String>em=getEmail(Jsoup.parse(html).text());
		if (em!=null) {
			emails.addAll(em);
		}
		Document doc=Jsoup.parse(html,"UTF-8");
		Elements links = doc.select("a[href]");
		for (Element page : links) {//contact-us
//			U.log(page.attr("href"));
            if (page.attr("href").contains("/about/")) {
            	String url=page.attr("href");
            	if (!url.startsWith("http")) {
					url="https://www.facebook.com"+url;
				}
            	U.log(url);
            	if (url.contains(baseUrl.replaceAll("http:/|https:/", ""))) {
            		if (urls.add(url)) {
            			crawlerFB(url);
            		}
            	}
			}
        }
	}
	
	private static String getHTML(String path) throws IOException, InterruptedException {
		String html = null;
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		path=getRedirectedUrl(path);
		U.log(path+"--");
		
		HtmlUnitDriver driver = new HtmlUnitDriver();
//		org.openqa.selenium.Proxy proxy = new org.openqa.selenium.Proxy();
//		proxy.setHttpProxy("66.7.113.39:3128"); 
//		driver.setProxySettings(proxy);
		driver.get(path);
		
		driver.setJavascriptEnabled(true);
		Thread.sleep(2000);
		html = driver.getPageSource();
		if (!cacheFile.exists())
			FileUtil.writeAllText(fileName, html);
		return html;
	}
	private static String getRedirectedUrl(String url) throws MalformedURLException, IOException {
//		U.log(url);
		boolean redirect = false;
		HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
		int status = conn.getResponseCode();
//		U.log(status);
		if (status != HttpURLConnection.HTTP_OK) {
			if (status == HttpURLConnection.HTTP_MOVED_TEMP
				|| status == HttpURLConnection.HTTP_MOVED_PERM
					|| status == HttpURLConnection.HTTP_SEE_OTHER)
			redirect = true;
		}
		if (redirect) {
			String newUrl = conn.getHeaderField("Location");
			return getRedirectedUrl(newUrl);
		}
		return url;
	}
	private static ArrayList<String> getEmail(String html) throws IOException {
		if (html.contains("@")) {
			return Util.matchAll(html, "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", 0);
		}
		return null;
	}
}
