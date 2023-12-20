package com.shatam.ThreadsDriver;


import java.util.concurrent.BlockingQueue;

import org.openqa.selenium.WebDriver;

public class ExternalDriver extends PageSourceProcess {

	private String url;
	private WebDriver driver;
	private int id;
	private BlockingQueue<Integer> bq;

	public ExternalDriver(String url, WebDriver driver,
			BlockingQueue<Integer> bq, Integer id) throws InterruptedException {

		super(url, driver, bq, id);
		this.url = url;
		this.driver = driver;
		this.id = id;
		this.bq = bq;
	}

	@Override
	public void run() {

		driver.get(url);
		this.page = driver.getPageSource();
		System.out.println(url + "\t" + id + "\t" + "External driver");
		try {
			this.bq.put(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
	}
}