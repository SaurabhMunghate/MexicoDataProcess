package com.chinmay.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class SearchGoogleRecord {
	public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		setUpChromePath();
		int status = 0;
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--start-maximized");
		options.addArguments("user-agent=Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
		//Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36
		WebDriver driver = new ChromeDriver(options);
		String fileName="/home/chinmay/Mexico/TestGoogle.csv";
//		Scanner sc=new Scanner(System.in);
////		U.log("Enter FileName");
////		fileName= sc.nextLine();
//		sc.close();
//		U.trim("")
		List<String[]>fileRecords=U.readCsvFile(fileName);
		int i=0;
		
		StringWriter sw=new StringWriter();
		
		
		CSVWriter writer=new CSVWriter(sw);
		for (String[] records : fileRecords) {
			++i;
			//if(i<1200) {continue;}
//			U.log(i);
			if (records[0].contains("ID")||records[0].contains("SrNo")) {
				for (int j = 0; j < records.length; j++)
					if(records[j].equals("Fetching Time"))
						status=j;
				String out[]=new String[records.length+10];
				System.arraycopy(records, 0, out, 0, records.length);
				out[records.length]="GOOGLE_ADD"; 
				out[records.length+1]="GOOGLE_COL";
				out[records.length+2]="GOOGLE_CITY";
				out[records.length+3]="GOOGLE_STATE";
				out[records.length+4]="GOOGLE_ZIP";
				out[records.length+5]="GOOGLE_PHONE";
				out[records.length+6]="GOOGLE_WEBSITE";
				out[records.length+7]="GOOGLE_ADDRESS";
				out[records.length+8]="GOOGLE_NAME";
				out[records.length+9]="GOOGLE_CATEGORY";
				writer.writeNext(out);
				continue;
			}
//			if (i<60000 || i>65000)	continue;
//			if (i<10000 || i>15000) {
//				continue;
//			}
			String searchedData[]=searchRecordGoogle(records[7],records[8]+" "+records[10],driver);
//			String searchedData[]=searchRecordGoogle(records[7],records[8],driver);
			String out[]=new String[records.length+searchedData.length-1];
//			U.log(inputFileRecord.length);
//			U.log(add.length);
			U.log(i);
			System.arraycopy(records, 0, out, 0, records.length);
			System.arraycopy(searchedData, 0, out, records.length, searchedData.length-1);
			if (searchedData[searchedData.length-1].equals("PERMANETLY_CLOSED")) {
				out[status]="PERMANETLY_CLOSED";
			}
			U.log(Arrays.toString(out));
			writer.writeNext(out);
			if (i%50==0) {
				Thread.sleep(15000);
			}
//			break;
		}
		
		FileUtil.writeAllText(fileName.replace(".csv", "GOOGLESearch.csv"), sw.toString());
//		FileUtil.writeAllText(fileName.replace(".csv", "_GOOGLESearch.csv"), sw.toString());
		writer.close();
		sw.close();
		driver.close();
	}
	public static String[] searchRecordGoogle(String companyName,String address,WebDriver driver){
		String url;
		try {
			//https://www.google.com/search?q=%22el+Abuelo%22+Calle+117+316+Merida&oq=%22el+Abuelo%22+Calle+117+316+Merida&aqs=chrome..69i57.897j0j7&client=ubuntu&sourceid=chrome&ie=UTF-8
			url = "https://www.google.com/search?q="+URLEncoder.encode((companyName+" "+address), StandardCharsets.UTF_8.toString())+"&oq="+URLEncoder.encode((companyName+" "+address), StandardCharsets.UTF_8.toString())+"&client=ubuntu&sourceid=chrome&ie=UTF-8";
			//url = "https://www.google.com/search?client=ubuntu&hs=2fq&biw=1853&bih=953&ei=XcQxXZGzFOG4vgTK5qiICQ&q="+URLEncoder.encode((companyName+" restaurant "+address), StandardCharsets.UTF_8.toString());
			U.log(companyName+" "+address);
			
			String googeHtml=getHtmlWithChromeBrowser(url,driver);
//			U.log(googeHtml);
//			String rightHandSec=U.getSectionValue(googeHtml, "<div class=\"g rhsvw>", "class=\"rhstc3 rhstc4 rhstc5\"");
			String rightHandSec=U.getSectionValue(googeHtml, "id=\"rhs\"", "class=\"rhstc3 rhstc4 rhstc5\"");
//			U.log(rightHandSec);
			String addressSec=U.getSectionValue(rightHandSec, "Address</a>: </span>", "</span>");
//			U.log(addressSec);
			String add[]=getAddress(U.removeHtml(addressSec));
			String website=U.getSectionValue(googeHtml, "<a class=\"ab_button\"", ">Website</a>");
			website=website!=null?U.getSectionValue(website, "href=\"", "\""):"";
			String phone=U.getSectionValue(googeHtml, "Phone</a>: </span>", "</span>");
			phone=phone!=null?U.removeHtml(phone):"";
			String status=rightHandSec.contains("<span>Permanently closed</span>")?"PERMANETLY_CLOSED":"";
			String category=U.getSectionValue(rightHandSec, "<span class=\"YhemCb\">", "</span>");
//			U.log(category);
			String name=U.removeHtml(U.getSectionValue(rightHandSec, "data-attrid=\"title\"", "</span>").replaceAll(" data-ved=\".*\">", ""));
//			U.log(name);
//			U.log(website);
			return new String[] {add[0],add[1],add[2],add[3],add[4],phone,website,U.removeHtml(addressSec),name,category,status};
			
		} catch (Exception e) {
//			e.printStackTrace();
			return new String[] {"","","","","","","","","","",""};
		}
	}
	public static String[] getAddress(String addressSec) {
		String tempAdd[]=addressSec.replaceAll(", Mexico$", "").replace("<br/>", ",").split(",");
		String add[]= {"","","","",""};
		if (tempAdd.length==4) {
//			U.log(Arrays.toString(tempAdd));
			add[0]=tempAdd[0];
			add[1]=tempAdd[1].trim();
//			U.log(Util.match(tempAdd[2], "\\d{4,5}"));
			add[4]=Util.match(tempAdd[2], "\\d{4,5}");
			add[2]=add[4]!=null?tempAdd[2].replaceAll("\\d{4,5}", "").trim():tempAdd[2].trim();
			add[3]=U.matchState(tempAdd[3]);
			if (add[3].equals("Mexico City")&&(add[4]!=null||add[4].length()<2)) {
				add[2]=MXStates.getMexicoCityMunicipalites(add[4]);
			}
		}else if (tempAdd.length==5) {
//			U.log(Arrays.toString(tempAdd));
			add[0]=tempAdd[0];
			add[1]=tempAdd[2].trim();
//			U.log(Util.match(tempAdd[2], "\\d{4,5}"));
			add[4]=Util.match(tempAdd[3], "\\d{4,5}");
			add[2]=add[4]!=null?tempAdd[3].replaceAll("\\d{4,5}", "").trim():tempAdd[3].trim();
			add[3]=U.matchState(tempAdd[4]);
			if (add[3].equals("Mexico City")&&(add[4]!=null||add[4].length()<2)) {
				add[2]=MXStates.getMexicoCityMunicipalites(add[4]);
			}
		}
		
		return add;
	}
	public static String getHtmlWithChromeBrowser(String url, WebDriver driver) throws Exception {
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
			html = FileUtil.readAllText(fileName);
			if(!html.contains("This page appears when Google automatically detects requests coming from"))
				return html;
		}
		{
			if (!f.exists()) {
				synchronized (driver) {
					BufferedWriter writer = new BufferedWriter(
							new FileWriter(f));
//					Cookie cookie = new Cookie.Builder("name", "value").build();
//					driver.manage().addCookie(cookie);
					driver.get(url);
					U.log("Current URL:::" + driver.getCurrentUrl());
					Thread.sleep(2000);
					html = driver.getPageSource();
					if (html.contains("This page appears when Google automatically detects requests coming from")) {
						Thread.sleep(90000);
						html = driver.getPageSource();
					}
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
	public static final String MY_GECKO_PATH = System.getProperty("user.home")+File.separator+"geckodriver";
	private static void setUpGeckoPath() {
        System.setProperty("webdriver.gecko.driver", MY_GECKO_PATH);
       // clearFirefoxConsoleLogs();
    }
	public static final String MY_CHROME_PATH = System.getProperty("user.home")+File.separator+"chromedriver";
	public static void setUpChromePath() {
            System.setProperty("webdriver.chrome.driver", MY_CHROME_PATH);
    }
	private static void clearFirefoxConsoleLogs()
	{
		System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
		System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,System.getProperty("user.home")+File.separator+"Selenium_logs.txt");
		U.log("[::: Clear Firefox Console Logs ::::]");
	}
}
