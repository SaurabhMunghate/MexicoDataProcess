package com.shatam.email.validations;

import java.io.IOException;

public class PingEmail {

	private static String DIRPATH = System.getProperty("user.dir");

	private static String DOMAINLISTPATH = "Data/EmailDomainList.txt";

	static {
		DOMAINLISTPATH = DIRPATH + "/" + DOMAINLISTPATH;
	}

	public static void main(String args[]) throws IOException,
			InterruptedException {
		ping(DOMAINLISTPATH);
	}

	/**
	 * 
	 * @param path
	 *            Email file path
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public static void ping(String path) throws IOException,
			InterruptedException {

		String commandPath = DIRPATH + "/" + "resources/automateping.cmd "
				+ DOMAINLISTPATH;
		Runtime run = Runtime.getRuntime();
		Process process = run.exec("cmd /C start cmd.exe /K " + commandPath);
		System.out.println("Results will be saved to file:\t" + DIRPATH
				+ "/PingResults.txt");
		int code = process.waitFor();

	}
}
