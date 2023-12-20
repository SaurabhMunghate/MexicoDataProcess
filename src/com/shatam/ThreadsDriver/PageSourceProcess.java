package com.shatam.ThreadsDriver;

import java.util.concurrent.BlockingQueue;

import org.openqa.selenium.WebDriver;

public class PageSourceProcess implements Runnable {

	private String url;
	private WebDriver driver;
	private BlockingQueue<Integer> bq;
	private int id;
	protected String page;

	public PageSourceProcess(String url, WebDriver driver,
			BlockingQueue<Integer> bq, Integer id) throws InterruptedException {
		this.url = url;
		this.driver = driver;
		this.bq = bq;
		this.id = id;
	}

	@Override
	public void run() {

		driver.get(url);
		this.page = driver.getPageSource();
		try {
			bq.put(id);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(url + "\t" + id + "\t" + "External driver internal");
	}

}
