/**
 * @author Sawan
 * @date 18 Sept 2018
 */
package com.tequila.database.corrector;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.scrapper.CSVCorrectorInValidFormat;
import com.shatam.utils.DB;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.shatam.utils.Util;
import com.tequila.database.validator.CorrectorAtValidateTable;

public class CorrectorAllFields extends CorrectNonUniqueFields {

	public static final String ALLOW_BLANK = TextFormat.ALLOW_BLANK;

	private static String fileName = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/UpdateSheet_18_December.csv";
	///home/chinmay/Mexico/stateUpdate_1.csv
	///home/chinmay/Mexico/stateUpdate_2.csv
	
//  /home/chinmay/Mexico/MexicoDataFiles/AddressCorrection/a/Update-0_100000.csv
//	/home/chinmay/Mexico/MexicoDataFiles/AddressCorrection/a/Update-100000_200000.csv
	private String deletedRecordFileName = Path.CORRECTORS_DIR + U.getTodayDateWith() + "_1_deleted.csv";

	private static int indexStatus = 12; // 10; //12;

	// private static final String COMPOSITE_KEY_TEQUILA_SER =
	// "/home/glady/MexicoCache/database/uniqueKeyTequila.ser";

	private static final String COMPOSITE_KEY_TEQUILA_SER_WITH_ID = Path.TEQUILA_DB_PATH + "uniqueKeyTequilaWithId.ser";
	/*
	 * load composite key from serialize file																																																							
	 */
	/*
	 * CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
	 * HashSet<String> uniqueKeyHashSet =
	 * chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
	 */
	Map<String, Integer> uniqueKeyWithId = (Map<String, Integer>) U.deserialized(COMPOSITE_KEY_TEQUILA_SER_WITH_ID);

	public static void main(String[] args) {
		U.log("File Name ::" + fileName);
		CorrectorAllFields correct = new CorrectorAllFields();

		// correct.startUpdatingCompanyAddressCityState(0, 1, 2, 3, 5, 6, 15, 16);
		// correct.startUpdatingCompanyAddressCityState(0, 14, 1, 2, 4, 5, 15,16);
		// correct.startUpdatingCompanyAddressCityState(0, 9, 1, 4, 6, 7);

		// correct.updatedAndDeleteUrl(0, 10);
		// correct.updatedAndDeleteUrl(0, 11);

		correct.updatedAndDeleteUrl(0, 10);
		correct.updatedAndDeleteEmail(0, 11);

		correct.updatedAndDeletePhone(0, 8);
		correct.updatedAndDeleteFax(0, 9);

		// correct.startUpdatePhone(0, 8, fileName);
		// correct.startUpdatePhone(2, 3, fileName);

		correct.updatedAndDeleteNeighborhood(0, 4);	
		// correct.startUpdateNeighborhood(0, 4, fileName);
		// correct.startUpdateNeighborhood(0, 2, fileName);

		correct.startUpdateZip(0, 7, fileName);
		// correct.startUpdateZip(0, 2, fileName);

		// correct.startUpdateURL(0, 10, fileName);
		// correct.startUpdateURL(0, 1, fileName);
		correct.startUpdateYearInBiz(0, 17, fileName);
		correct.startUpdateHoursOFOperation(0, 24, fileName);
		
		correct.startUpdateLocationSource(0,25,fileName);
		correct.startUpdateQualityScore(0,26,fileName);
		correct.startUpdateGeoDistance(0,27,fileName);
		correct.startUpdateGeoAccuracyCode(0,28,fileName);
		correct.startUpdateAddressVerificationCode(0,29,fileName);
		correct.startUpdateEmpCount(0, 18, 19, fileName);
		correct.startUpdateAnnualSales(0, 20, 21, fileName);
		// correct.varifiedStatusUpdate(0, 1, 2, 3, 4, 5, 6, 7, 8, 10);
		correct.closedStatusUpdate(0);

		// correct.startUpdateEmail(0, 10, fileName);
		// correct.startUpdateEmail(0, 2, fileName);
		// correct.startUpdateLatLng(0,15,16,fileName);
		correct.updatedAndDeleteLatLng(0, 15, 16);
		correct.startUpdatingCompanyAddressCityState(0, 1, 2, 3, 5, 6, 13, 14);

		correct.updatePossibleDuplicate(0);
		correct.disconnect();
	}
	Map<Integer, String[]> updateOnlyUniqueFieldMap = new HashMap<>();
	Map<Integer, String[]> updateOnlyUniqueFieldModifiedOthers = new HashMap<>();
	Map<Integer, String> updateOnlyTitleFieldMap = new HashMap<>();

	// List<Integer> idForDeleteDupRecord = new ArrayList<>();

	Map<Integer, String[]> idForDeleteDupRecord = new HashMap<>();
	
	List<Integer> validateSicSub = new ArrayList<>();
	List<Integer> validateCompanyName = new ArrayList<>();
	List<Integer> validateAddress = new ArrayList<>();
	List<Integer> validateCity = new ArrayList<>();
	List<Integer> validateState = new ArrayList<>();
	List<Integer> validateContactPerson = new ArrayList<>();
	List<Integer> validateTitle = new ArrayList<>();

	int totalFailedCount = 0;
	int totalFoundDuplicate = 0; 

	/**
	 * This method is used to update sic sub, company name, address, city, state and
	 * contact person
	 * 
	 * @param indexId
	 * @param indexSicSub
	 * @param indexCompanyName
	 * @param indexAddress
	 * @param indexCity
	 * @param indexState
	 * @param indexContactPerson
	 */
	private void startUpdatingCompanyAddressCityState(int indexId, int indexSicSub, int indexCompanyName,
			int indexAddress, int indexCity, int indexState, int indexContactPerson, int indexTitle) {

		Map<Integer, String[]> updateDatasetMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String status = lines[indexStatus].trim();
			// ignore if record is not found on google or other resources
			if (!status.isEmpty() && status.equalsIgnoreCase("NOT_FOUND"))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("VERIFIED"))
				continue;
			// if(!status.isEmpty() && status.equalsIgnoreCase("CLOSED"))continue;
			if (!status.isEmpty() && (status.contains("Possible Duplicate") || status.contains("Possible_Duplicate")))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("UPDATE_URL"))
				continue;

			// id
			int id = Integer.parseInt(lines[indexId].trim());

			// sic sub
			String sicSub = lines[indexSicSub].trim();
			if (!sicSub.isEmpty()) {
				if (sicSub.length() == 3)
					sicSub = "0" + sicSub;
				if (sicSub.length() == 4) {
					String[] sicInfo = Sic.sicInfo(sicSub);
					if (sicInfo == null || sicSub.length() > 4) {
						throw new IllegalArgumentException("Wrong sic sub found, input sic sub is " + sicSub);
					}
				}
				if (sicSub.length() != 4)
					sicSub = ALLOW_BLANK;
			} else if (sicSub.isEmpty())
				sicSub = ALLOW_BLANK;

			// company name
			String companyName = lines[indexCompanyName].trim();
			if (!companyName.isEmpty()) {
				companyName = TextFormat.getCompanyNameStandardised(companyName);
				// companyName = TranslateEnglish.convertToEnglish(companyName);
			} else if (companyName.isEmpty())
				companyName = ALLOW_BLANK;

			// address
			String address = lines[indexAddress].trim();
			if (!address.isEmpty()) {
				address = TextFormat.getAddressStandardised(address.toLowerCase());
				// address = TranslateEnglish.convertToEnglish(address);
			} else if (address.isEmpty())
				address = ALLOW_BLANK;

			// city
			String city = lines[indexCity].trim();
			if (!city.isEmpty()) {
				// city = U.toTitleCase(TranslateEnglish.convertToEnglish(city)).replace(" - ",
				// "-");
				city = U.toTitleCase(city).replace(" - ", "-");
			} else if (city.isEmpty())
				city = ALLOW_BLANK;

			// state
			String state = lines[indexState].trim();
//			if (!state.isEmpty()) {
//				state = U.findState(state);
//				if (state.equals(ALLOW_BLANK))
//					throw new IllegalArgumentException(
//							"Wrong state found, state is " + lines[indexState].trim() + " for id :" + id);
//			} else
			if (state.isEmpty())
				state = ALLOW_BLANK;

			// Contact Person
			String contactPerson = lines[indexContactPerson].trim();
			if (!contactPerson.isEmpty()) {
				contactPerson = U.toTitleCase(contactPerson);
			} else if (contactPerson.isEmpty())
				contactPerson = ALLOW_BLANK;

			// Title of Contact Person
			String title = lines[indexTitle].trim();
			if (!title.isEmpty()) {
				title = U.toTitleCase(title);
			} else if (title.isEmpty())
				title = ALLOW_BLANK;

//			if (!city.equals(ALLOW_BLANK)) {
//				String tempState = state;
//				// U.log(tempState);
//				if (state.equals(ALLOW_BLANK))
//					tempState = getState("" + id);
//
//				if (city.equals("Ciudad De Mexico"))
//					throw new IllegalArgumentException("Wrong city found++, city is " + city.trim() + " for id :" + id);
//
//				if (tempState != null) {
//					if (!CSVCorrectorInValidFormat.isExistCityWithinState(city, tempState)) {
//						throw new IllegalArgumentException("Wrong city found, city is " + city.trim() + " for id :" + id
//								+ " " + tempState + state);
//					}
//				} else {
//					System.err.println("ID does not exist in the database ::" + id);
//				}
//			}

			if (sicSub.equals(ALLOW_BLANK) && companyName.equals(ALLOW_BLANK) && address.equals(ALLOW_BLANK)
					&& city.equals(ALLOW_BLANK) && state.equals(ALLOW_BLANK) && contactPerson.equals(ALLOW_BLANK)
					&& title.equals(ALLOW_BLANK))
				continue;

			// load values for updation
			updateDatasetMap.put(id, new String[] { sicSub, companyName, address, city, state, contactPerson, title });

		} // eof for

		// compare data
		compareUpdateWithDuplicateRecord(updateDatasetMap);
		
		// validate fields at validated table
		if (validateSicSub.size() > 0)
			CorrectorAtValidateTable.validateSicSubAtValidationTable(validateSicSub);
		if (validateCompanyName.size() > 0)
			CorrectorAtValidateTable.validateCompanyNameAtValidationTable(validateCompanyName);
		if (validateAddress.size() > 0)
			CorrectorAtValidateTable.validateAddressAtValidationTable(validateAddress);
		if (validateCity.size() > 0)
			CorrectorAtValidateTable.validateCityAtValidationTable(validateCity);
		if (validateState.size() > 0)
			CorrectorAtValidateTable.validateStateAtValidationTable(validateState);
		if (validateContactPerson.size() > 0)
			CorrectorAtValidateTable.validateContactPersonAtValidationTable(validateContactPerson);
		if (validateTitle.size() > 0)
			CorrectorAtValidateTable.validateTitleAtValidationTable(validateTitle);

		// Generate deleted file here
		String header[] = { "ID", "SIC_SUB", "COMPANY_NAME", "ADDRESS", "CITY", "STATE", "CONTACT_PERSON", "TITLE" };
		if (idForDeleteDupRecord.size() > 0) {
			U.writeCsvFile(header, idForDeleteDupRecord.values(), deletedRecordFileName);
		}

	}// eof startUpdatingCompanyAddressCityState()

	/**
	 * This method is used to find ID record information and update its unique
	 * values in the database.<br>
	 * Also, this method is used to delete duplicate records in the database.
	 * 
	 * @param updateDatasetMap
	 */
	private void compareUpdateWithDuplicateRecord(Map<Integer, String[]> updateDatasetMap) {

		List<Integer> idList = new ArrayList<>(updateDatasetMap.keySet());
		/*
		 * Key is ID, value is ID record
		 */
		Map<Integer, String[]> idDetailsMap = DB.getIdRecordDetails(idList, conn); // extract details

		for (Entry<Integer, String[]> entry : idDetailsMap.entrySet()) {

			int id = entry.getKey();
			String data[] = entry.getValue();
			String[] updateData = updateDatasetMap.get(id);

			if (data[0] == null)
				continue; // in case id is deleted

			// sicSub, companyName, address, city, state, contactPerson, title
			compare(data, id, updateData[0], updateData[1], updateData[2], updateData[3], updateData[4], updateData[5],
					updateData[6]);
			// compare(id, sicSub, companyName, address, city, state);

			// to check if address is getting duplicate in db, if not then added to
			// modifiedAddressId
		}
		if (updateOnlyUniqueFieldMap.size() > 0) {
			updateOnlyUniqueFieldsAtDB(updateOnlyUniqueFieldMap);
		}

		if (updateOnlyUniqueFieldModifiedOthers.size() > 0) {
			// delete id here
			U.log("Total id for delete ::" + idForDeleteDupRecord.keySet());
			List<Integer> deletedIdRecord = new ArrayList<>(idForDeleteDupRecord.keySet());
			startRecordDeleting(deletedIdRecord);

			updateOnlyUniqueFieldsWithOtherAtDB(updateOnlyUniqueFieldModifiedOthers);
		}
		if (updateOnlyTitleFieldMap.size() > 0) {
			updateTitle(updateOnlyTitleFieldMap);
		}

		U.log("*** Total count for unsuccessfully updating company name :" + totalFailedCount);
		U.log("*** Total found duplicate company records ::" + totalFoundDuplicate);

	}// eof compareUpdateWithDuplicateRecord()

	/**
	 * This method is used to compare the given id records with modified its unique
	 * values provided by user and find duplicate composite key here. <br>
	 * This method is set the map with containing key is ID and values is its fields
	 * for updates.<br>
	 * Also, it set the map of duplicate ID's.
	 * 
	 * @param data
	 * @param id
	 * @param sicSub
	 * @param companyName
	 * @param address
	 * @param city
	 * @param state
	 * @param contactPerson
	 * @param title
	 */
	private void compare(String[] data, int id, String sicSub, String companyName, String address, String city,
			String state, String contactPerson, String title) {

		if (data[4] == null) {
			U.log("Sic null found ::" + id);
		}
		if (!isDupCompositeKey(data, sicSub, companyName, address, city, state, contactPerson)) {
			if (!sicSub.equals(ALLOW_BLANK)) {
				data[4] = sicSub;
				validateSicSub.add(id);
				U.log("***** id :" + id + "\tsic code ::" + sicSub);
			}
			if (!companyName.equals(ALLOW_BLANK)) {
				data[7] = companyName;
				validateCompanyName.add(id);
			}
			if (!address.equals(ALLOW_BLANK)) {
				data[8] = address;
				validateAddress.add(id);
			}
			if (!city.equals(ALLOW_BLANK)) {
				data[10] = city;
				validateCity.add(id);
			}
			if (!state.equals(ALLOW_BLANK)) {
				data[11] = state;
				validateState.add(id);
			}
			if (!contactPerson.equals(ALLOW_BLANK)) {
				data[17] = contactPerson;
				validateContactPerson.add(id);
			}
			updateOnlyUniqueFieldMap.put(id, data); // update for unique record

			if (!title.equals(ALLOW_BLANK)) {
				validateTitle.add(id);
				updateOnlyTitleFieldMap.put(id, title);
			}

		} else {
			// Record is getting duplicated with other records.

			List<String[]> duplicatedRecord = getCompanyDuplicateRecord(data, sicSub, companyName, address, city, state,
					contactPerson);

			if (duplicatedRecord.size() > 0) {

				if (!sicSub.equals(ALLOW_BLANK)) {
					data[4] = sicSub;
					validateSicSub.add(id);
					U.log(">>>> id :" + id + "\tsic code ::" + sicSub);
				}
				if (!companyName.equals(ALLOW_BLANK)) {
					data[7] = companyName;
					validateCompanyName.add(id);
				}
				if (!address.equals(ALLOW_BLANK)) {
					data[8] = address;
					validateAddress.add(id);
				}
				if (!city.equals(ALLOW_BLANK)) {
					data[10] = city;
					validateCity.add(id);
				}
				if (!state.equals(ALLOW_BLANK)) {
					data[11] = state;
					validateState.add(id);
				}
				if (!contactPerson.equals(ALLOW_BLANK)) {
					data[17] = contactPerson;
					validateContactPerson.add(id);
				}
				if (!title.equals(ALLOW_BLANK)) {
					// data[18] = title;
					validateTitle.add(id);
					updateOnlyTitleFieldMap.put(id, title);
				}

				// we got duplicate records
				U.log("Total found duplicate records ::" + duplicatedRecord.size());
				totalFoundDuplicate += duplicatedRecord.size();

				// if we got single duplicate records
				if (duplicatedRecord.size() == 1) {
					data = TequilaCorrector.validateDataAndPerformOperation(data, duplicatedRecord.get(0));
					// idForDeleteDupRecord.add(Integer.parseInt(duplicatedRecord.get(0)[0])); //id
					// for delete record

					idForDeleteDupRecord.put(Integer.parseInt(duplicatedRecord.get(0)[0]),
							new String[] { duplicatedRecord.get(0)[0], sicSub, companyName, address, city, state,
									contactPerson, title }); // id for delete record
					U.log("##### Duplicate Records Found For Id : " + id + "\t\tDupId : " + duplicatedRecord.get(0)[0]);
					updateOnlyUniqueFieldModifiedOthers.put(id, data); // modified other details for ID record
				} else {
					// if we got more than single duplicate records
					for (String dupData[] : duplicatedRecord) {
						data = TequilaCorrector.validateDataAndPerformOperation(data, dupData);
						// idForDeleteDupRecord.add(Integer.parseInt(dupData[0])); //id for delete
						// record
						idForDeleteDupRecord.put(Integer.parseInt(dupData[0]), new String[] { dupData[0], sicSub,
								companyName, address, city, state, contactPerson, title });
						//if (!dupData[0].equals("" + id)) {
							U.log("##### Multiple Duplicate Records Found For Id : " + id + "\t\tDupId : " + dupData[0]);
							try{
							for (Map.Entry<Integer, String[]> it : updateOnlyUniqueFieldModifiedOthers.entrySet()) {
								if (it.getKey().equals(id))
									it.setValue(data); // update data
								else
									updateOnlyUniqueFieldModifiedOthers.put(id, data); // modified other details for ID
																						// record
							} // eof for inner
							}catch(Exception ex) {}
						//}

					} // eof for outer

				} // eof else

			} else {
				// if we does not get any duplicate records for id still it getting duplicate
				// unique key
				U.log("We haven't found duplicate records for id :" + id);
				totalFailedCount++;
				gotDuplicateCompareRecord(data, id, sicSub, companyName, address, city, state, contactPerson, title);
			}
		} // eof else outer

	}// eof compare()

	/**
	 * This method is used to find those records that getting duplicated more than
	 * one while find duplicate composite keys.
	 * 
	 * @param dupData
	 * @param id
	 * @param sicSub
	 * @param companyName
	 * @param address
	 * @param city
	 * @param state
	 * @param contactPerson
	 * @param title
	 */
	private void gotDuplicateCompareRecord(String[] dupData, int id, String sicSub, String companyName, String address,
			String city, String state, String contactPerson, String title) {

		if (!sicSub.equals(ALLOW_BLANK)) {
			dupData[4] = sicSub;
		}
		if (!companyName.equals(ALLOW_BLANK)) {
			dupData[7] = companyName;
		}
		if (!address.equals(ALLOW_BLANK)) {
			dupData[8] = address;
		}
		if (!city.equals(ALLOW_BLANK)) {
			dupData[10] = city;
		}
		if (!state.equals(ALLOW_BLANK)) {
			dupData[11] = state;
		}
		if (!contactPerson.equals(ALLOW_BLANK)) {
			dupData[17] = contactPerson;
		}
		String gotId = null;
		boolean foundUniqueRecordForDuplication = false;

		// find unique record from updatedMap that only contains unique fields for
		// update.
		for (Map.Entry<Integer, String[]> entry : updateOnlyUniqueFieldMap.entrySet()) {
			String[] updateData = entry.getValue();

			if (updateData[4].trim().length() == 3)
				updateData[4] = "0" + updateData[4].trim();
			if (dupData[4].trim().length() == 3)
				dupData[4] = "0" + dupData[4].trim();

			if (updateData[17] == null)
				updateData[17] = "";
			if (dupData[17] == null)
				dupData[17] = "";

			/**
			 * SIC_SUB && COMPANY_NAME && ADDERSS && CITY && STATE && CONTACT_PERSON
			 */
			if (updateData[4].trim().equals(dupData[4].trim()) && // SIC_SUB
					updateData[7].trim().equals(dupData[7].trim()) && // COMPANY_NAME
					updateData[8].trim().equals(dupData[8].trim()) && // ADDERSS
					updateData[10].trim().equals(dupData[10].trim()) && // CITY
					updateData[11].trim().equals(dupData[11].trim()) && // STATE
					updateData[17].trim().equals(dupData[17].trim())) // CONTACT_PERSON
			{
				if (updateData[0].equals(dupData[0]))
					continue;

				System.err.println(
						"### Found Duplicate At Unique Map, Id : " + updateData[0] + "\t\tDupId : " + dupData[0]);
				updateData = TequilaCorrector.validateDataAndPerformOperation(updateData, dupData);

				if (!updateOnlyUniqueFieldModifiedOthers.containsKey(entry.getKey())) {
					updateOnlyUniqueFieldModifiedOthers.put(entry.getKey(), updateData);
				}

				entry.setValue(updateData);
				foundUniqueRecordForDuplication = true;
				gotId = updateData[0];
				break;
			} // eof if
		} // eof for

		// if we haven't got unique records for duplicate record.
		if (!foundUniqueRecordForDuplication) {

			// find unique record from updatedMap that contains unique fields and also other
			// fields for update.
			for (Map.Entry<Integer, String[]> entry : updateOnlyUniqueFieldModifiedOthers.entrySet()) {
				String[] updateData = entry.getValue();

				if (updateData[4].trim().length() == 3)
					updateData[4] = "0" + updateData[4].trim();
				if (dupData[4].trim().length() == 3)
					dupData[4] = "0" + dupData[4].trim();

				if (updateData[17] == null)
					updateData[17] = "";
				if (dupData[17] == null)
					dupData[17] = "";

				/**
				 * SIC_SUB && COMPANY_NAME && ADDERSS && CITY && STATE && CONTACT_PERSON
				 */
				if (updateData[4].trim().equals(dupData[4].trim()) && // SIC_SUB
						updateData[7].trim().equals(dupData[7].trim()) && // COMPANY_NAME
						updateData[8].trim().equals(dupData[8].trim()) && // ADDERSS
						updateData[10].trim().equals(dupData[10].trim()) && // CITY
						updateData[11].trim().equals(dupData[11].trim()) && // STATE
						updateData[17].trim().equals(dupData[17].trim())) // CONTACT_PERSON
				{
					if (updateData[0].equals(dupData[0]))
						continue;

					System.err.println("### Found Duplicate At Unique with other field Map, Id : " + updateData[0]
							+ "\t\tDupId : " + dupData[0]);
					updateData = TequilaCorrector.validateDataAndPerformOperation(updateData, dupData);
					entry.setValue(updateData);
					foundUniqueRecordForDuplication = true;
					gotId = updateData[0];
					break;
				} // eof if
			}
		}

		if (foundUniqueRecordForDuplication) {
			U.log("Duplicated Record Id ::" + id + "\tUnique Record Id ::" + gotId);

			totalFoundDuplicate += 1;

			idForDeleteDupRecord.put(id,
					new String[] { dupData[0], sicSub, companyName, address, city, state, contactPerson, title });
		}
	}// eof gotDuplicateCompareRecord()

	/**
	 * This method is used to deleted duplicated ID's in the database.
	 * 
	 * @param idForDelete
	 */
	private void startRecordDeleting(List<Integer> idForDelete) {
		if (idForDelete.size() > 0) {
			// these record must be deleted in order to update company name.
			U.log("\nStart removing immediate Duplicate ID record for not getting UNIQUE constraint failed exception");
			U.log("Size Of Id List for deletion ::" + idForDelete.size());
			Set<Integer> idSet = new HashSet<>(idForDelete);
			idForDelete.clear();
			idForDelete.addAll(idSet);
			Collections.sort(idForDelete);
			idSet.clear();
			// U.log("Size of Unique List ::"+idForDelete.size());

			DeleteReportAtTable deleteReportAtTable = new DeleteReportAtTable(Path.TEQUILA_DB_PATH,
					Path.TEQUILA_MAIN_DB_NAME);
			deleteReportAtTable.deleteRecordAtDB(idForDelete);
			deleteReportAtTable.disconnect();
		}
	}

	/**
	 * This method is used to find duplicate composite key, when we generate new
	 * unique key with specified corrected unique fields.
	 * 
	 * @param data
	 * @param correctSicSub
	 * @param correctCompanyName
	 * @param correctAddress
	 * @param correctCity
	 * @param correctState
	 * @param correctContactPerson
	 * @return :- return true if corrected values becomes duplicate unique key
	 *         constraint, false if corrected values becomes unique.
	 */
	private boolean isDupCompositeKey(String[] data, String correctSicSub, String correctCompanyName,
			String correctAddress, String correctCity, String correctState, String correctContactPerson) {

		String uniqueSicSub = "";
		String uniqueCompany = "";
		String uniqueAddress = "";
		String uniqueCity = "";
		String uniqueState = "";
		String uniqueContactPerson = "";

		if (correctSicSub == null)
			correctSicSub = "";
		if (correctCompanyName == null)
			correctCompanyName = "";
		if (correctAddress == null)
			correctAddress = "";
		if (correctCity == null)
			correctCity = "";
		if (correctState == null)
			correctState = "";
		if (correctContactPerson == null)
			correctContactPerson = "";

		if (!correctSicSub.equals(ALLOW_BLANK) && !correctSicSub.isEmpty())
			if (correctSicSub.trim().length() == 3)
				correctSicSub = "0" + correctSicSub.trim();

		String sicSub = data[4].trim();
		if (sicSub.length() == 3)
			sicSub = "0" + sicSub;

		String contactPerson = data[17];
		if (contactPerson == null)
			contactPerson = "";

		/*
		 * sic sub
		 */
		if (correctSicSub.equals(ALLOW_BLANK) || correctSicSub.isEmpty())
			uniqueSicSub = sicSub;
		else if (!correctSicSub.isEmpty())
			uniqueSicSub = correctSicSub;
		/*
		 * company name
		 */
		if (correctCompanyName.equals(ALLOW_BLANK) || correctCompanyName.isEmpty())
			uniqueCompany = data[7].trim();
		else if (!correctCompanyName.isEmpty())
			uniqueCompany = correctCompanyName;
		/*
		 * address
		 */
		if (correctAddress.equals(ALLOW_BLANK) || correctAddress.isEmpty())
			uniqueAddress = data[8];
		else if (!correctAddress.isEmpty())
			uniqueAddress = correctAddress;
		/*
		 * city
		 */
		if (correctCity.equals(ALLOW_BLANK) || correctCity.isEmpty())
			uniqueCity = data[10];
		else if (!correctCity.isEmpty())
			uniqueCity = correctCity;
		/*
		 * state
		 */
		if (correctState.equals(ALLOW_BLANK) || correctState.isEmpty())
			uniqueState = data[11];
		else if (!correctState.isEmpty())
			uniqueState = correctState;
		/*
		 * contact person
		 */
		if (correctContactPerson.equals(ALLOW_BLANK) || correctContactPerson.isEmpty())
			uniqueContactPerson = contactPerson;
		else if (!correctContactPerson.isEmpty())
			uniqueContactPerson = correctContactPerson;
		/*
		 * unique key
		 */
		String uniqueKey = U.toTitleCase(uniqueSicSub) + U.toTitleCase(uniqueCompany.trim())
				+ U.toTitleCase(uniqueAddress.trim()) + U.toTitleCase(uniqueCity.trim())
				+ U.toTitleCase(uniqueState.trim()) + U.toTitleCase(uniqueContactPerson.trim());

		/**
		 * @date : 1 Nov 2018
		 */
		int tempId = Integer.parseInt(data[0]);

		if (uniqueKeyWithId.containsKey(uniqueKey.toLowerCase())) {
			if (tempId == uniqueKeyWithId.get(uniqueKey.toLowerCase())) {
				return false;
			} else {
				return true;
			}
		} else {
			uniqueKeyWithId.put(uniqueKey.toLowerCase(), tempId);
			return false;
		}

		/*
		 * if(uniqueKeyHashSet.add(uniqueKey.toLowerCase()))return false; else return
		 * true;
		 */
	}

	/**
	 * This method is used to find duplicate records using unique constraint fields
	 * in the database.
	 * 
	 * @param data
	 * @param correctSicSub
	 * @param correctCompanyName
	 * @param correctAddress
	 * @param correctCity
	 * @param correctState
	 * @param correctContactPerson
	 * @return
	 */
	private List<String[]> getCompanyDuplicateRecord(String[] data, String correctSicSub, String correctCompanyName,
			String correctAddress, String correctCity, String correctState, String correctContactPerson) {

		List<String[]> duplicatedRecord = new ArrayList<>();

		String query = "select * from dataset where sic_sub=? and company_name=? and address=? and city=? and state=? and ";

		String uniqueSicSub = "";
		String uniqueCompany = "";
		String uniqueAddress = "";
		String uniqueCity = "";
		String uniqueState = "";
		String uniqueContactPerson = "";
		// U.trim(val)
		if (correctSicSub == null)
			correctSicSub = "";
		if (correctCompanyName == null)
			correctCompanyName = "";
		if (correctAddress == null)
			correctAddress = "";
		if (correctCity == null)
			correctCity = "";
		if (correctState == null)
			correctState = "";
		if (correctContactPerson == null)
			correctContactPerson = "";

		String sicSub = data[4].trim();
		String contactPerson = data[17];
		// U.log("** contactPerson from DB ::"+contactPerson+"\tId :"+data[0]);

		/*
		 * if(contactPerson == null) query = query +" contact_person is null"; else{
		 * query = query +" contact_person=?"; }
		 */

		/*
		 * sic sub
		 */
		if (correctSicSub.equals(ALLOW_BLANK) || correctSicSub.isEmpty())
			uniqueSicSub = sicSub;
		else if (!correctSicSub.isEmpty())
			uniqueSicSub = correctSicSub;
		/*
		 * company name
		 */
		if (correctCompanyName.equals(ALLOW_BLANK) || correctCompanyName.isEmpty())
			uniqueCompany = data[7].trim();
		else if (!correctCompanyName.isEmpty())
			uniqueCompany = correctCompanyName.trim();
		/*
		 * address
		 */
		if (correctAddress.equals(ALLOW_BLANK) || correctAddress.isEmpty())
			uniqueAddress = data[8];
		else if (!correctAddress.isEmpty())
			uniqueAddress = correctAddress;
		/*
		 * city
		 */
		if (correctCity.equals(ALLOW_BLANK) || correctCity.isEmpty())
			uniqueCity = data[10];
		else if (!correctCity.isEmpty())
			uniqueCity = correctCity;
		/*
		 * state
		 */
		if (correctState.equals(ALLOW_BLANK) || correctState.isEmpty())
			uniqueState = data[11];
		else if (!correctState.isEmpty())
			uniqueState = correctState;
		/*
		 * contact person
		 */
		if (correctContactPerson.equals(ALLOW_BLANK) || correctContactPerson.isEmpty()) {
			uniqueContactPerson = contactPerson;
		} else if (!correctContactPerson.isEmpty()) {
			uniqueContactPerson = U.toTitleCase(correctContactPerson);
		}

		if (uniqueContactPerson == null)
			query = query + " contact_person is null";
		else {
			query = query + " contact_person=?";
		}

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, uniqueSicSub); // sic sub
			pstmt.setString(2, uniqueCompany); // correct company name
			pstmt.setString(3, uniqueAddress); // address
			pstmt.setString(4, uniqueCity); // city
			pstmt.setString(5, uniqueState); // state

			// U.log("*** Inside query : uniqueContactPerson ::>"+uniqueContactPerson);
			if (uniqueContactPerson != null)
				pstmt.setString(6, uniqueContactPerson); // contact person

			ResultSet rs = pstmt.executeQuery();

			duplicatedRecord.addAll(DB.resultSetToList(rs)); // add resultset result here...

			rs.close();
			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return duplicatedRecord;

	}// eof getCompanyDuplicateRecord();

	/**
	 * 
	 * @param indexId
	 */
	private void updatePossibleDuplicate(int indexId) {
		/*
		 * this map contain the possible duplicate id as key and value is contain id of
		 * the unique or more accurate record data for duplicate id record.
		 */
		Map<Integer, Integer> idDuplicateWithUniqueMap = new HashMap<>();
		int noUpdate = 0;
		// HashMap<String , String >updateTrackmap=new HashMap<>();
		HashSet<String> updatedId = new HashSet<>();
		TreeMap<String, String[]> noUpdateList = new TreeMap<>();
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		StringWriter sw = new StringWriter();
		CSVWriter noUpdateWriter = new CSVWriter(sw);
		String header[] = { "ID", "DUP_ID", "STATUS" };
		noUpdateWriter.writeNext(header);
		U.log("**** Start searching possible duplicate ID's records here ...");
		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String status = lines[indexStatus].trim();
			// ignore if record is not found on google or other resources.

			if (status.isEmpty())
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("NOT_FOUND"))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("CLOSED"))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("VERIFIED"))
				continue;

			if (!status.isEmpty() && (status.contains("Possible Duplicate") || status.contains("Possible_Duplicate"))) {
				// id
				int dupId = Integer.parseInt(lines[indexId].trim());

				String uniqueId = Util.match(status, "\\d+");
				U.log("dup :" + dupId + "\tUqe ::" + uniqueId);
				idDuplicateWithUniqueMap.put(dupId, Integer.parseInt(uniqueId.trim()));
			} // eof if
		} // eof for

		U.log("Size of Duplicate Record ID's ::" + idDuplicateWithUniqueMap.size());
		if (idDuplicateWithUniqueMap.size() == 0)
			return;

		// List of ID's containing duplicate records id.
		List<Integer> duplicateId = new ArrayList<>(idDuplicateWithUniqueMap.keySet());

		// List of ID's containing unique records id.
		List<Integer> uniqueId = new ArrayList<>(idDuplicateWithUniqueMap.values());

		// Map is contain duplicate id as key, and id record details as value.
		Map<Integer, String[]> duplicateIdDataset = DB.getIdRecordDetails(duplicateId, conn);

		// Map is contain unique id as key, and id record details as value.
		Map<Integer, String[]> uniqueIdDataset = DB.getIdRecordDetails(uniqueId, conn);

		/*
		 * List is contains updated non unique key fields of unique id records.
		 */
		List<String[]> updatedIdDataset = new ArrayList<>();
		int mainCounter = 0;
		/*
		 * compare duplicate data with unique data record.
		 */
		for (Map.Entry<Integer, String[]> entry : duplicateIdDataset.entrySet()) {

			String duplicateData[] = entry.getValue();
			int uId = idDuplicateWithUniqueMap.get(entry.getKey()); // unique record id
			String uniqueData[] = uniqueIdDataset.get(uId);
			if (uniqueData == null)
				continue;
			String preCheck[] = new String[uniqueData.length];
			System.arraycopy(uniqueData, 0, preCheck, 0, uniqueData.length);
			uniqueData = TequilaCorrector.validateDataAndPerformOperation(uniqueData, duplicateData);
			// if (Arrays.equals(Arrays.copyOfRange(preCheck, 9, 10),
			// Arrays.copyOfRange(uniqueData, 9, 10))&&
			// Arrays.equals(Arrays.copyOfRange(preCheck, 13, 17),
			// Arrays.copyOfRange(uniqueData, 13, 17))&&
			// Arrays.equals(Arrays.copyOfRange(preCheck, 18, 26),
			// Arrays.copyOfRange(uniqueData, 18, 26))) {
			// String out[]= {uniqueData[0],duplicateData[0],"NO UPDATE"};
			// if (!updatedId.contains(uniqueData[0]))
			// noUpdateList.put(uniqueData[0], out);
			// }else {
			// Arrays.equa
			boolean flag = false;
			int counter = 0;
			for (int i = 9; i < uniqueData.length - 8; i++) {
				if (i == 10 || i == 11 || i == 12 || i == 17)
					continue;
				if (!(uniqueData[i] == null && preCheck[i] == null) && !uniqueData[i].equals(preCheck[i])) {
					flag = true;
					// U.log(uniqueData[0]+"\t>>\t"+preCheck[0]);
					counter++;
				} else if (uniqueData[i] != null && preCheck[i] == null) {
					flag = true;
				}
			}
			if (counter == 0) {
				noUpdate++;
			}
			if (flag) {
				updatedId.add(uniqueData[0]);
				updatedIdDataset.add(uniqueData); // set List for update unique id.
			} else {
				String out[] = { uniqueData[0], duplicateData[0], "NO UPDATE" };
				if (!updatedId.contains(uniqueData[0]))
					noUpdateList.put(uniqueData[0], out);
			}

			// }
		}
		/*
		 * Update unique ID's records here ..
		 */
		if (updatedIdDataset.size() > 0) {
			updateNonUniqueKeyFields(updatedIdDataset);
		}
		if (noUpdateList.size() > 0) {
			for (Iterator<String> iterator = noUpdateList.keySet().iterator(); iterator.hasNext();) {
				String id = iterator.next();
				if (updatedId.contains(id)) {
					iterator.remove();
				} else {
					noUpdateWriter.writeNext(noUpdateList.get(id));
				}
			}
		}
		try {
			FileUtil.writeAllText(fileName.replace(".csv", "_No_Update.csv"), sw.toString());
			noUpdateWriter.close();
			sw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * Start deleting duplicate records here ..
		 */
		if (duplicateId.size() > 0) {
			startRecordDeleting(duplicateId);
		}
		U.log("------" + noUpdate);
	}// eof updatePossibleDuplicate()

	/**
	 * This method is used to set flag for varified ID record. <br>
	 * Flag will set for sic sub, company name, address, neighborhood, city, state,
	 * zip, phone, url, etc.
	 * 
	 * @param indexId
	 * @param indexSicSub
	 * @param indexCompanyName
	 * @param indexAddress
	 * @param neighborhood
	 * @param indexCity
	 * @param indexState
	 * @param indexZip
	 * @param indexPhone
	 * @param indexUrl
	 */
	private void varifiedStatusUpdate(int indexId, int indexSicSub, int indexCompanyName, int indexAddress,
			int neighborhood, int indexCity, int indexState, int indexZip, int indexPhone, int indexUrl) {
		// sic company name ,address neighborhood city,state ,zip,phone,url

		List<Integer> idList = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String status = lines[indexStatus].trim();
			// ignore if record is not found on google or other resources

			// --- if(status.isEmpty())continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("NOT_FOUND"))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("CLOSED"))
				continue;
			if (!status.isEmpty() && (status.contains("Possible Duplicate") || status.contains("Possible_Duplicate")))
				continue;

			if ((!status.isEmpty() && status.equalsIgnoreCase("VERIFIED")) || status.isEmpty()) {
				// id
				int id = Integer.parseInt(lines[indexId].trim());
				idList.add(id); // ID
			} // eof if

		} // eof for

		// if list is empty
		if (idList.isEmpty())
			return;
		U.log("*** Total verified ID's ::" + idList.size());
		Map<Integer, String[]> varifiedDataset = DB.getIdRecordDetails(idList, conn);

		List<Integer> validateNeighborhood = new ArrayList<>();
		List<Integer> validateZip = new ArrayList<>();
		List<Integer> validatePhone = new ArrayList<>();
		List<Integer> validateUrl = new ArrayList<>();

		for (Entry<Integer, String[]> entry : varifiedDataset.entrySet()) {
			String[] data = entry.getValue();
			if (data[4] != null && !data[4].isEmpty())
				validateSicSub.add(entry.getKey()); // sic sub
			if (data[7] != null && !data[7].isEmpty())
				validateCompanyName.add(entry.getKey()); // company name
			if (data[8] != null && !data[8].isEmpty())
				validateAddress.add(entry.getKey()); // address
			if (data[9] != null && !data[9].isEmpty())
				validateNeighborhood.add(entry.getKey()); // neighbourhood
			if (data[10] != null && !data[10].isEmpty())
				validateCity.add(entry.getKey()); // city
			if (data[11] != null && !data[11].isEmpty())
				validateState.add(entry.getKey()); // state

			if (data[12] != null && !data[12].isEmpty())
				validateZip.add(entry.getKey()); // zip
			if (data[13] != null && !data[13].isEmpty())
				validatePhone.add(entry.getKey()); // phone
			if (data[15] != null && !data[15].isEmpty())
				validateUrl.add(entry.getKey()); // Url
		}

		// validate fields at validated table
		if (validateSicSub.size() > 0)
			CorrectorAtValidateTable.validateSicSubAtValidationTable(validateSicSub);
		if (validateCompanyName.size() > 0)
			CorrectorAtValidateTable.validateCompanyNameAtValidationTable(validateCompanyName);
		if (validateAddress.size() > 0)
			CorrectorAtValidateTable.validateAddressAtValidationTable(validateAddress);
		if (validateCity.size() > 0)
			CorrectorAtValidateTable.validateCityAtValidationTable(validateCity);
		if (validateState.size() > 0)
			CorrectorAtValidateTable.validateStateAtValidationTable(validateState);
		if (validateNeighborhood.size() > 0)
			CorrectorAtValidateTable.validateNeighborhoodAtValidationTable(validateNeighborhood);
		if (validateZip.size() > 0)
			CorrectorAtValidateTable.validateZipAtValidationTable(validateZip);
		if (validatePhone.size() > 0)
			CorrectorAtValidateTable.validatePhoneAtValidationTable(validatePhone);
		if (validateUrl.size() > 0)
			CorrectorAtValidateTable.validateUrlAtValidationTable(validateUrl);

	}// eof varifiedStatusUpdate()

	/**
	 * This method is used to find the ID's record that has permanently closed and
	 * update its status in the database.
	 * 
	 * @param indexId
	 *            :- Index of ID in the file
	 */
	private void closedStatusUpdate(int indexId) {

		List<Integer> idList = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String status = lines[indexStatus].trim();
			// ignore if record is not found on google or other resources

			if (status.isEmpty())
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("NOT_FOUND"))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("VERIFIED"))
				continue;
			if (!status.isEmpty() && (status.contains("Possible Duplicate") || status.contains("Possible_Duplicate")))
				continue;
			if (!status.isEmpty() && status.equalsIgnoreCase("UPDATE_URL"))
				continue;

			if (!status.isEmpty() && status.equalsIgnoreCase("CLOSED")) {
				// id
				int id = Integer.parseInt(lines[indexId].trim());
				idList.add(id); // ID

			} // eof if

		} // eof for
		U.log("id ::" + idList);
		if (idList.size() > 0) {
			updateClosedStatus(idList); // update closed status
		}
	}// eof closedStatusUpdate()

	/**
	 * This method is used to update URL that having wrong in the database. Also, It
	 * remove URL from database if they does not exist anymore.
	 * 
	 * @param indexId
	 * @param indexUpdatedUrl
	 */
	private void updatedAndDeleteUrl(int indexId, int indexUpdatedUrl) {

		Map<Integer, String> updateUrlMap = new HashMap<>();
		List<Integer> deletedUrlId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String url = lines[indexUpdatedUrl].trim();
			if (url.isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());

			if (url.equalsIgnoreCase("DELETE_URL") || url.contains("DELETE_URL")) {
				deletedUrlId.add(id);
			} else {
				updateUrlMap.put(id, url.toLowerCase());
			}
		}
		if (updateUrlMap.size() > 0) {
			updateURL(updateUrlMap, false); // start updating url
		}
		if (deletedUrlId.size() > 0) {
			deletedURL(deletedUrlId); // start updating url to null
		}

	}// eof updatedAndDeleteUrl();

	/**
	 * This method is used to update Email that having wrong in the database. Also,
	 * It remove Email from database if they does not exist anymore.
	 * 
	 * @param indexId
	 * @param indexUpdatedEmail
	 */
	private void updatedAndDeleteEmail(int indexId, int indexUpdatedEmail) {

		Map<Integer, String> updateEmailMap = new HashMap<>();
		List<Integer> deletedEmailId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String email = lines[indexUpdatedEmail].trim();
			if (email.isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());

			if (email.equalsIgnoreCase("DELETE_EMAIL") || email.contains("DELETE_EMAIL")) {
				deletedEmailId.add(id);
			} else {
				updateEmailMap.put(id, email.toLowerCase());
			}
		}
		if (updateEmailMap.size() > 0) {
			updateEmail(updateEmailMap, false); // start updating url
		}
		if (deletedEmailId.size() > 0) {
			deletedEmail(deletedEmailId); // start updating url to null
		}
	}// eof updatedAndDeleteEmail()

	/**
	 * This method is used to update Phone that having wrong in the database. Also,
	 * It remove Phone from database if they does not exist anymore.
	 * 
	 * @param indexId
	 * @param indexUpdatedPhone
	 */
	private void updatedAndDeletePhone(int indexId, int indexUpdatedPhone) {

		Map<Integer, String> updatePhoneMap = new HashMap<>();
		List<Integer> deletedPhoneId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String phone = lines[indexUpdatedPhone].trim();
			if (phone.isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());

			if (phone.equalsIgnoreCase("DELETE_PHONE") || phone.contains("DELETE_PHONE")) {
				deletedPhoneId.add(id);
			} else {
				phone = U.formatNumbersAsCode(phone);
				updatePhoneMap.put(id, phone);
			}
		}
		if (updatePhoneMap.size() > 0) {
			updatePhone(updatePhoneMap); // start updating phone
		}
		if (deletedPhoneId.size() > 0) {
			deletedPhone(deletedPhoneId); // start updating phone to null
		}
	}// eof updatedAndDeletePhone()

	/**
	 * This method is used to update Fax that having wrong in the database. Also, It
	 * remove Fax from database if they does not exist anymore.
	 * 
	 * @param indexId
	 * @param indexUpdatedFax
	 */
	private void updatedAndDeleteFax(int indexId, int indexUpdatedFax) {

		Map<Integer, String> updateFaxMap = new HashMap<>();
		List<Integer> deletedFaxId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;

			String fax = lines[indexUpdatedFax].trim();
			if (fax.isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());

			if (fax.equalsIgnoreCase("DELETE_FAX") || fax.contains("DELETE_FAX")) {
				deletedFaxId.add(id);
			} else {
				fax = U.formatNumbersAsCode(fax);
				updateFaxMap.put(id, fax);
			}
		}
		if (updateFaxMap.size() > 0) {
			updateFax(updateFaxMap); // start updating Fax
		}
		if (deletedFaxId.size() > 0) {
			deletedFax(deletedFaxId); // start updating Fax to null
		}
	}// EOF updatedAndDeleteFax()

	/**
	 * Added By : Priti - 17Jun2019 This method is used to update Neighborhood that
	 * having wrong in the database. Also, It remove Neighborhood from database if
	 * they does not exist anymore.
	 * 
	 * @param indexId
	 * @param indexUpdatedNeighborhood
	 */
	private void updatedAndDeleteNeighborhood(int indexId, int indexUpdatedNeighborhood) {

		Map<Integer, String> updateNeighborhoodMap = new HashMap<>();
		List<Integer> deletedNeighId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedNeighborhood].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());
			String neighborhood = U.toTitleCase(lines[indexUpdatedNeighborhood].trim());
			if (neighborhood.equalsIgnoreCase("DELETE_NEIGH") || neighborhood.contains("DELETE_NEIGH")) {
				deletedNeighId.add(id);
			} else {
				updateNeighborhoodMap.put(id, neighborhood);
			}
		}
		if (updateNeighborhoodMap.size() > 0) {
			updateNeighborhood(updateNeighborhoodMap); // start updating Neighborhood
		}
		if (deletedNeighId.size() > 0) {
			deletedNeighborhood(deletedNeighId); // start updating Neighborhood to null
		}
	}

	/**
	 * Added By : Priti - 17Jun2019 This method is used to update LatLng that having
	 * wrong in the database. Also, It remove LatLng from database.
	 * 
	 * @param indexId
	 * @param indexLat
	 * @param indexLng
	 */
	private void updatedAndDeleteLatLng(int indexId, int indexLat, int indexLng) {

		Map<Integer, String[]> updateLatLngMap = new HashMap<>();
		List<Integer> deletedLatLngId = new ArrayList<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {
			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexLat].trim().isEmpty())
				continue;
			if (lines[indexLng].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim());
			String lat = lines[indexLat].trim();
			String lng = lines[indexLng].trim();

			if (lat.equalsIgnoreCase("DELETE_LAT_LON") || lat.contains("DELETE_LAT_LON")
					|| lng.equalsIgnoreCase("DELETE_LAT_LON") || lng.contains("DELETE_LAT_LON")) {
				deletedLatLngId.add(id);
			} else {
				updateLatLngMap.put(id, new String[] { lat, lng });
			}
		}
		if (updateLatLngMap.size() > 0) {
			updateLatLng(updateLatLngMap); // start updating latlng
		}
		if (deletedLatLngId.size() > 0) {
			deletedLatLng(deletedLatLngId); // start updating latlng to null
		}
	}// eof updatedAndDeleteLatLng()

	/**
	 * This method is used to find State for specified ID value in the database.
	 * 
	 * @param id
	 * @return :- return state if able to find state for specified ID, if not then
	 *         return null for not found state.
	 */
	public String getState(String id) {
		String state = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select state from dataset where id=" + id);
			while (rs.next()) {
				state = rs.getString("state");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return state;
	}

	/**
	 * This method is used to find Sic Sub for specified ID value in the database.
	 * 
	 * @param id
	 * @return :- return SicSub if able to find SicSub for specified ID, if not then
	 *         return null for not found Sic Sub.
	 */
	public String getSicSub(String id) {
		String sicSub = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select sic_sib from dataset where id=" + id);
			while (rs.next()) {
				sicSub = rs.getString("sic_sib");
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sicSub;
	}
}