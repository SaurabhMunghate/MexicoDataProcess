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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;

public class CreateFile extends PageSourceProcess{
	private String url;
	private WebDriver driver;
	private int id;
	private BlockingQueue<Integer> bq;
	String html = null;
	String fname = null;
	boolean isValidUrl=false;
	private  String url1 =null;

	public CreateFile(String url, WebDriver driver,
			BlockingQueue<Integer> bq, Integer id) throws Exception {

		super(url, driver, bq, id);
	
		this.url = url;
		this.driver = driver;
		this.id = id;
		this.bq = bq;
	}
	public void run()  {
		try{
			System.out.println("fileurl is working"+url);
			if(url.contains("ww.santander.com.mx"))url="http://www.santander.com.mx";
			url1=testUrl(url);
			fname=getCache(url1);
			File f = new File(fname);
			if(!f.exists()){
			driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.MINUTES);
			driver.get(url1);
			if(isAlertPresents())driver.switchTo().alert().accept();
			Thread.sleep(1000);
			driver.getCurrentUrl();
			this.page = driver.getPageSource();
			html=this.page;
			System.out.println(url + "\t" + id + "\t" + "External driver");
			try {
				getHtml();
				this.bq.put(id);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
			else{
				try {
					getHtml();
					this.bq.put(id);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}			}
		}catch (Exception e) {
			driver.quit();
			e.printStackTrace();
		}
		catch (Throwable e) {
			System.out.println("Error:\t" + this.url);
			try {				
				this.bq.put(id); //If error			
			} catch (InterruptedException e1) {		
				
			}
			}	
	
	}
	
	public void getHtml() throws Exception{
		System.out.println(url1);
		fname=getCache(url1);
		System.out.println(fname);
		File f = new File(fname);
		if(f.exists())html=readAllText(fname);
		if (!f.exists()) {
			synchronized (driver) {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(f));
				Thread.sleep(2000);
				writer.append(html);
				writer.close();
			}
		}
	}
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
	public static String getCachePath(){
		String Regex="Cache";
		String Filename=System.getProperty("user.home");
		if(Filename.contains("/")){
			Regex="/MexicoCache1/";
		}
		else 
		{
			Regex="\\MexicoCache1\\";
		}
		Filename=Filename.concat(Regex);
		if(!Filename.equals(Regex))
		{
			Regex=Regex.toLowerCase();
		}
		return Filename;
	}	
	
	
	public static String readAllText(String path) throws Exception {

	    File aFile = new File(path);

	    StringBuilder contents = new StringBuilder();


	    BufferedReader input = new BufferedReader(new FileReader(aFile));
	    try {
	        String line = null; //not declared within while loop

	        while ((line = input.readLine()) != null) {
	            contents.append(line);
	            contents.append(System.getProperty("line.separator"));
	        }
	    } finally {
	        input.close();
	    }


	    return contents.toString();
	    
	}
	
	public boolean isAlertPresents() {
		try {
		driver.switchTo().alert();
		return true;
		}// try
		catch (Exception e) {
		return false;
		}// catch
	}
	
	public static String testUrl(String url){
		if(!url.contains("ht")){
		if(url.startsWith("ht") && !url.contains("https://")){
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
		}else if(!url.startsWith("http") || !url.startsWith("www") ||!url.startsWith("https") ){
			url="http://"+url;
		}}
		return url;
	}
}
