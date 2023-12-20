package com.shatam.ThreadsDriver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ThreadDriver {

	private boolean isLoadedFirst = false;
	private Class<?> worker;

	private int breakAfter = -1;
	private volatile int counter = 0;
	private boolean isbreak;

	public static void main(String args[]) throws InterruptedException,
			IOException {

		String path = "D:/sqlite/sqliteSpatial/MexicoDataAnalysis/ForCheckingcompaniesHavingCounterGT100.tab";
		List<String> l = Files.readAllLines(Paths.get(path));
		System.setProperty("webdriver.chrome.driver",
				"E:/chromedriver_win32/chromedriver_win32 (2)/chromedriver.exe");

	}

	public ThreadDriver(Class<?> c) {
		this.worker = c;
	}

	public ThreadDriver(Class<?> c, Integer breakAfter) {
		this.worker = c;
		this.breakAfter = breakAfter;
		this.isbreak = true;
	}

	Map<String, WebDriver> webIdMap;

	private void initDriver(int no) {

		if (webIdMap == null) {
			webIdMap = new HashMap<String, WebDriver>();
			for (int i = 1; i <= no; i++) {
				webIdMap.put(i + "", new ChromeDriver());
			}
		}
	}

	private void _loadFirstTime(ExecutorService es, Iterator<String> itr,
			BlockingQueue<Integer> bq, int count) throws InterruptedException {

		for (int i = 1; i <= count; i++) {

			// es.execute(new PageSourceProcess(getValidName(itr.next()),
			// webIdMap
			// .get(i + ""), bq, i));

			try {
				es.execute(getInstance(getValidName(itr.next()), i, bq));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {

				e.printStackTrace();
			}
			this.counter++;
			// System.out.println("Counter:\t" + this.counter);
		}
		isLoadedFirst = true;
	}

	private PageSourceProcess getInstance(String name, int driverId,
			BlockingQueue<Integer> bq) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		return worker
				.asSubclass(PageSourceProcess.class)
				.getConstructor(String.class, WebDriver.class,
						BlockingQueue.class, Integer.class)
				.newInstance(name, webIdMap.get(driverId + ""), bq, driverId);

	}

	public void process(List<String> listOfUrls) throws InterruptedException,
			IOException {

		BlockingQueue<Integer> bq = new LinkedBlockingQueue<Integer>(2);
		ExecutorService es = Executors.newFixedThreadPool(2);
		long start = System.currentTimeMillis();
		Iterator<String> itr = listOfUrls.iterator();

		initDriver(2);
		System.out.println("Counter:\t" + this.counter);
		while (itr.hasNext()) {
			if (!isLoadedFirst)
				_loadFirstTime(es, itr, bq, 2);
			int d = bq.take();
			this.counter++;
			try {
				es.execute(getInstance(getValidName(itr.next()), d, bq));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}

			if (isbreak) {
				if (counter == breakAfter) {
					System.out.println("Breaking");
					break;
				}
			}

		}

		// es.awaitTermination(10, TimeUnit.DAYS);
		//
		// for (Entry<String, WebDriver> entry : webIdMap.entrySet()) {
		// entry.getValue().close();
		// }

		long end = System.currentTimeMillis();
		es.shutdown();
		// System.out.println("Total Time Latency:\t" + (end - start));
	}

	private String getValidName(String url) {
		// System.out.println(url + "\t URL");
		if (url.startsWith("www")) {
			return "http://" + url;
		}
		return url;
	}
}

class CacheMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private final int capacity;

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return (size() > this.capacity);
	}

	public CacheMap(int capacity) {
		super(capacity + 1, 1.0f, true);
		this.capacity = capacity;
	}

	public V find(K key) {
		return super.get(key);
	}

	public void set(K key, V value) {
		super.put(key, value);
	}

}