package com.shatam.ThreadsDriver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.WebDriver;

public class CreateCache extends PageSourceProcess{
	
	private String url;
	private WebDriver driver;
	private int id;
	private BlockingQueue<Integer> bq;
	String html = null;
	String fname = null;
	boolean isValidUrl=false;
	public CreateCache(String url, WebDriver driver,
	BlockingQueue<Integer> bq, Integer id) throws Exception {
		super(url, driver, bq, id);
		if(!url.contains("Database_Url")){
		this.url = url;
		this.driver = driver;
		this.id = id;
		this.bq = bq;
		}
		if(url.contains("ww.santander.com.mx"))url="www.santander.com.mx";

	}
	
	

	@Override
	public void run()  {
		long startTime = System.currentTimeMillis();
		url=UtilURL.formatURL(url);
		try{
		driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.MINUTES);
		driver.get(url);
		if(UtilURL.isAlertPresents(driver))driver.switchTo().alert().accept();
		Thread.sleep(1000);
		driver.getCurrentUrl();
		this.page = driver.getPageSource();
		html=this.page;
		System.out.println(url + "\t" + id + "\t" + "External driver");
		try {
			getHtml();
			this.bq.put(id);
		} catch (InterruptedException e) {
			driver.quit();
			e.printStackTrace();
		}
		}
		catch (Throwable e) {
			System.out.println("Error:\t" + this.url);
			try {				
				this.bq.put(id); //If error			
			} catch (InterruptedException e1) {		
				
			}
			}

		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime+"Time taken");
	}

	public void getHtml() throws Exception{
		fname=UtilURL.getCache(url);
		System.out.println(fname);
		File f = new File(fname);
		if(f.exists() && html.length()>20)html=UtilURL.readAllText(fname);
		if (!f.exists() && html.length()<20) {
			synchronized (driver) {
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(f));
				writer.append(html);
				writer.close();
			}
		}	
	}	
	
}
