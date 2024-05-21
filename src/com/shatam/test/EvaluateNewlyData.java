package com.shatam.test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.Field;
import com.shatam.utils.U;
import com.shatam.utils.Validator;

public class EvaluateNewlyData {

	private static final String FILE_NAME = "/home/shatam-100/MexicoCache/Cache/Inegi_Information_0_224000_160001_170000_S_F1_4500_7000_CORRECT_NW_REC.csv";

	public static void main(String[] args) throws Exception {
		evaluateNewlyData();
	}

	static void evaluateNewlyData() throws Exception {
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines = it.next(); 
			if (x++ == 0)
				continue;
			Validator.industrySectorEnglishAndSpanish(Field.INDUSTRY_SECTOR, lines[1]);
			Validator.industrySectorEnglishAndSpanish(Field.SPANISH_INDUSTRY_SECTOR, lines[2]);
			Validator.productDescEnglishAndSpanish(Field.PRODUCT_DESC, lines[5]);
			Validator.productDescEnglishAndSpanish(Field.SPANISH_PRODUCT_DESC, lines[6]);
			Validator.companyName(Field.COMPANY_NAME, lines[7]);
			Validator.addressAndNeighbourhood(Field.ADDRESS, lines[8]);
			Validator.addressAndNeighbourhood(Field.NEIGHBORHOOD, lines[9]);
			Validator.city(Field.CITY, lines[10]);
			Validator.state(Field.STATE, lines[11]);
			Validator.zip(Field.ZIP, lines[12]);
			Validator.phoneAndFax(Field.PHONE, lines[13]);
			Validator.phoneAndFax(Field.FAX, lines[14]);
			Validator.url(Field.URL, lines[15]);
			Validator.email(Field.EMAIL, lines[16]);
			Validator.contactPerson(Field.CONTACT_PERSON, lines[17]);
			Validator.title(Field.TITLE, lines[18]);
			Validator.latlon(lines[23], lines[24]);
//			List<String> invalidEmail = U.invalidEmailList(lines[16]);
//			if (invalidEmail != null) {
//				if (!invalidEmail.isEmpty()) {
//					System.err.println("Invalid email at row :" + x + "\tInvalid emails are " + invalidEmail);
//				}
//			}
		}
		U.log("Validate file is done.");
	}
}
