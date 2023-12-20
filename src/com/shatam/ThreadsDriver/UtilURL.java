package com.shatam.ThreadsDriver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

public class UtilURL {
	public static final String MY_CHROME_PATH = System.getProperty("user.home")+File.separator+"chromedriver";
	public static String getCache(String path) throws MalformedURLException {
		String Dname = null;
		String host = new URL(path).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;

		File folder = new File(getCachePath() + Dname);
		if (!folder.exists())
			folder.mkdirs();
		String fileName = getCacheFileName(path);
		fileName = getCachePath() + Dname + "/" + fileName;
		return fileName;
	}
	public static void setUpChromePath() {
        if (org.apache.commons.lang3.SystemUtils.IS_OS_LINUX) {
            System.setProperty("webdriver.chrome.driver", MY_CHROME_PATH);
        }
        if (org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS) {
            System.setProperty("webdriver.chrome.driver", MY_CHROME_PATH+".exe");
        }
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

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str + ".txt";
	}

	public static String getCachePath() {
		String Regex = "Cache";
		String Filename = System.getProperty("user.home");
		if (Filename.contains("/")) {
			Regex = "/MexicoCache1/";
		} else {
			Regex = "\\MexicoCache1\\";
		}
		Filename = Filename.concat(Regex);
		if (!Filename.equals(Regex)) {
			Regex = Regex.toLowerCase();
		}
		return Filename;
	}

	public static String readAllText(String path) throws Exception {

		File aFile = new File(path);

		StringBuilder contents = new StringBuilder();

		BufferedReader input = new BufferedReader(new FileReader(aFile));
		try {
			String line = null; // not declared within while loop

			while ((line = input.readLine()) != null) {
				contents.append(line);
				contents.append(System.getProperty("line.separator"));
			}
		} finally {
			input.close();
		}

		return contents.toString();

	}// readAllText()


	public static String getHtml(String url,WebDriver driver) throws Exception {

		String html = null;
		String Dname = null;
		url=formatURL(url);
		String host = new URL(url).getHost();
		host = host.replace("www.", "");
		int dot = host.indexOf("/");
		Dname = (dot != -1) ? host.substring(0, dot) : host;
		File folder = null;

		folder = new File(getCachePath() + Dname);
		if (!folder.exists())
			folder.mkdirs();
	
		String fileName =getCacheFileName(url);

		fileName = getCachePath()+ Dname + "/" + fileName;

		File f = new File(fileName);
		if (f.exists()) {
			System.out.println("Reading done");
			return html = readAllText(fileName);
		}

		

		// if(respCode==200)
		{

			if (!f.exists()) {
				synchronized (driver) {

					BufferedWriter writer = new BufferedWriter(
							new FileWriter(f));
				
					driver.get(url);
					if(isAlertPresents(driver))driver.switchTo().alert().accept();
					System.out.println("Current URL:::" + driver.getCurrentUrl());
					html = driver.getPageSource();
					writer.append(html);
					writer.close();

				}
			} else {
				if (f.exists()) {
					html = readAllText(fileName);
					System.out.println("Reading done");
				}
			}
			return html;
		}
		// else{
		// return null;
		// }
	}
	
	// formatting Urls
	public static String formatURL(String url){	
		
		if(url.startsWith("ht") && !url.contains("https")){
			Pattern p=Pattern.compile("ht.*?/");
			Matcher m = p.matcher(url);
		      StringBuffer sb = new StringBuffer();
		      while(m.find()) {
		         m.appendReplacement(sb, "http:/");
		      }
		      m.appendTail(sb);
		      System.out.println(sb.toString());
			
			url=sb.toString();
		}
		else if(url.startsWith("//")){
			url=url.replace("//", "www.");
		}
		else if(url.startsWith(":")){
			url=url.replace(":", "");
		}else if(!url.startsWith("http") || !url.startsWith("https") || url.startsWith("www.")){
			url="http://"+url;
		}
		else if(url.contains("https//:")){
			url=url.replace("https//:", "https://");
		}
		System.out.println(url+"hello");
		return url;
	}
	public static boolean isAlertPresents(WebDriver driver) {
		try {
		driver.switchTo().alert();
		return true;
		}// try
		catch (Exception e) {
		return false;
		}// catch
		}

}
