package com.shatam.ThreadsDriver;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.print.attribute.HashAttributeSet;

public class ThreadDriverTest {
	
	public static final String MY_CHROME_PATH = System.getProperty("user.home")+File.separator+"chromedriver";

	static Class<ExternalDriver> c = ExternalDriver.class;

	public static void main(String args[]) throws IOException,
			InterruptedException, InstantiationException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		String path = "/home/shatam-22/PallaviWorkspace/MexicoUrlCorrection/validAndInvalidUrls/"+"Dt:24sept2018_SIR_above9500.csv";
		List<String> l = Files.readAllLines(Paths.get(path));
		long startTime = System.currentTimeMillis();

		l = l.stream().map(new Function<String, String>() {
			@Override
			public String apply(String t) {
				return t.split(",")[1];
			}
		}).collect(Collectors.toList());
		UtilURL.setUpChromePath();
		new ThreadDriver(CreateCache.class).process(l);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println(totalTime+" Is The Time Taken");
	}
	
}
