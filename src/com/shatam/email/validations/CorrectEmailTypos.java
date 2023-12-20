package com.shatam.email.validations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class CorrectEmailTypos {

	public static void main(String args[]) throws IOException {

		List<String> emailData = Files.readAllLines(Paths
				.get("Data/EmailCorrectData"));

		String selectQuery = "select id,cval from (select id,email,substr(email,0,length(email)-{LENGHT})||\"{REPALCE_VALUE}\" as cval from dataset where email like \"%{SEARCH_STRING}\");";
		String updateQuery = "update dataset set email=substr(email,0,length(email)-{LENGHT})||\"{REPALCE_VALUE}\", UPDATED_DATE = datetime('now','localtime') where id in (select id from dataset where email like \"%{SEARCH_STRING}\");";
		Set<String> emailSet = new LinkedHashSet<String>(emailData);

		for (String row : emailSet) {

			String data[] = row.split("\t");
			String qupdate = updateQuery
					.replace("{LENGHT}", (data[0].length() - 1) + "")
					.replace("{SEARCH_STRING}", data[0])
					.replace("{REPALCE_VALUE}", data[1]);

			String qselect = selectQuery
					.replace("{LENGHT}", (data[0].length() - 1) + "")
					.replace("{SEARCH_STRING}", data[0])
					.replace("{REPALCE_VALUE}", data[1]);
			System.out.println(qupdate);

		}

	}
}
