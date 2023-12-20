package com.shatam.email.validations;

//Print out a sorted list of mail exchange servers for a network domain name
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class MailHostsLookup {
	public static void main(String args[]) throws IOException {

		System.out.println(isEmailExist("toyotacopa.com.mx"));
		
	}

	private static Record[] lookupMxRecords(final String domainPart)
			throws TextParseException {
		final Lookup dnsLookup = new Lookup(domainPart, Type.MX);
		return dnsLookup.run();
	}

	/**
	 * 
	 * @param domainPart
	 *            domain name of a email address
	 * @return Return True if email domain exist otherwise false
	 */
	public static boolean isEmailExist(final String domainPart) {		
		try {			
			return lookupMxRecords(domainPart) != null ? true : false;			
		} catch (TextParseException e) {
			
		}
		return false;
		
	}

	private static void bulkRunEmailList() throws IOException {
		List<String> list = Files.readAllLines(Paths
				.get("Data/EmailDomainList.txt"));

		System.setOut(new PrintStream(new File(
				"Data/EmailDomailList_OUTPUT.txt")));
		for (int i = 0; i < list.size(); i++) {
			try {
				for (Record mailHost : lookupMxRecords(list.get(i))) {
				}
				System.out.println("Host " + list.get(i) + " reachable OK");
			} catch (Exception e) {
				System.out.println("Host " + list.get(i) + " not reachable KO");
			}
		}
	}

}