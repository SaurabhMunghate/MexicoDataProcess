/**
 * @author Sawan
 * @date 18 Sept 2018
 */
package com.tequila.database.corrector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.database.connection.Connect;
import com.shatam.conversion.Employee;
import com.shatam.conversion.Sic;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.shatam.utils.Util;
import com.tequila.database.validator.CorrectorAtValidateTable;

import me.xdrop.fuzzywuzzy.StringProcessor;

public class CorrectNonUniqueFields extends Connect {

	/**
	 * This method is used to read Phone from file with its id and updated at
	 * database.
	 * 
	 * @param indexID
	 *            :- Index of ID in the file.
	 * @param indexUpdatedPhone
	 *            :- Index of Phone for updated in the file.
	 * @param fileName
	 *            :- Input file with path containing phones with its ID's.
	 */
	public void startUpdatePhone(int indexId, int indexUpdatedPhone, String fileName) {

		Map<Integer, String> idPhoneMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedPhone].trim().isEmpty())
				continue;
			String phone = U.formatNumbersAsCode(lines[indexUpdatedPhone].trim());
			// U.log(lines[indexUpdatedPhone]+"\t\t"+phone);
			int id = Integer.parseInt(lines[indexId].trim());

			idPhoneMap.put(id, phone);
		}
		U.log("Total phones for update ::" + idPhoneMap.size());
		/*
		 * Start updating here
		 */
		updatePhone(idPhoneMap);

	}// eof startUpdatePhoneNumber()

	/**
	 * This method is used to read Zip from file with its id and updated at
	 * database.
	 * 
	 * @param indexId
	 *            :- Index of ID in the file.
	 * @param indexUpdatedZip
	 *            :- Index of Zip for updated in the file.
	 * @param fileName
	 *            :- Input file with path containing Zip with its ID's.
	 */
	public void startUpdateZip(int indexId, int indexUpdatedZip, String fileName) {

		Map<Integer, String> idZipMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedZip].trim().isEmpty())
				continue;
			String zip = lines[indexUpdatedZip].trim();
			if (zip.length() == 4) {
				zip = "0" + zip;
			}
			if (zip.length() != 5)
				continue;

			int id = Integer.parseInt(lines[indexId].trim());

			idZipMap.put(id, zip);
		}
		U.log("Total zip for update ::" + idZipMap.size());
		/*
		 * Start updating here
		 */
		updateZip(idZipMap);
	}// eof startUpdateZip()

	/**
	 * This method is used to read Neighborhood from file with its id and updated at
	 * database.
	 * 
	 * @param indexId
	 *            :- Index of ID in the file.
	 * @param indexUpdatedNeighborhood
	 *            :- Index of Neighborhood for updated in the file.
	 * @param fileName
	 *            :- Input file with path containing Neighborhood with its ID's.
	 */
	public void startUpdateNeighborhood(int indexId, int indexUpdatedNeighborhood, String fileName) {

		Map<Integer, String> idNeighborhoodMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedNeighborhood].trim().isEmpty())
				continue;
			String neighborhood = U.toTitleCase(lines[indexUpdatedNeighborhood].trim());

			int id = Integer.parseInt(lines[indexId].trim());

			idNeighborhoodMap.put(id, neighborhood);
		}
		U.log("Total neighborhood for update ::" + idNeighborhoodMap.size());
		/*
		 * Start updating here
		 */
		updateNeighborhood(idNeighborhoodMap);
	}// eof startUpdateNeighborhood()

	/**
	 * This method is used to read URL file with its id and updated at database.
	 * 
	 * @param indexID
	 *            :- Index of ID in the file.
	 * @param indexUpdatedUrl
	 *            :- Index of URL for updated in the file.
	 * @param fileName
	 *            :- Input file with path containing URL with its ID's.
	 */
	public void startUpdateURL(int indexId, int indexUpdatedUrl, String fileName) {

		Map<Integer, String> idUrlMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedUrl].trim().isEmpty())
				continue;
			String url = lines[indexUpdatedUrl].trim().toLowerCase();
			int id = Integer.parseInt(lines[indexId].trim());

			idUrlMap.put(id, url);
		}
		U.log("Total URL's for update ::" + idUrlMap.size());
		/*
		 * Start updating here
		 */
		updateURL(idUrlMap, true);
	}// eof startUpdateURL()

	/**
	 * This method is used to read Email file with its id and updated at database.
	 * 
	 * @param indexId
	 *            :- Index of ID in the file.
	 * @param indexUpdatedEmail
	 *            :- Index of Email for updated in the file.
	 * @param fileName
	 *            :- Input file with path containing Email with its ID's.
	 */
	public void startUpdateEmail(int indexId, int indexUpdatedEmail, String fileName) {

		Map<Integer, String> idEmailMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedEmail].trim().isEmpty())
				continue;
			String email = lines[indexUpdatedEmail].trim().toLowerCase();
			int id = Integer.parseInt(lines[indexId].trim());

			idEmailMap.put(id, email);
		}
		U.log("Total Email's for update ::" + idEmailMap.size());
		/*
		 * Start updating here
		 */
		updateEmail(idEmailMap, false);
	}// eof startUpdateEmail()

	/**
	 * This method is used to update year of establishment value for particular the
	 * company records in the database.
	 * 
	 * @param indexId
	 * @param indexUpdatedHoursOfOperation
	 * @param fileName
	 */
	public void startUpdateLocationSource(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
		//	U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateLocationSource(idHoursOfOperation);
	}// eof startUpdateURL()

	public void startUpdateQualityScore(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
			//U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateQualityScore(idHoursOfOperation);
	}// eof startUpdateURL()

	public void startUpdateGeoDistance(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
			//U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateGeoDistance(idHoursOfOperation);
	}// eof startUpdateURL()
	public void startUpdateGeoAccuracyCode(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
		//	U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateGeoAccuracyCode(idHoursOfOperation);
	}// eof startUpdateURL()
	
	public void startUpdateAddressVerificationCode(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
			//U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateAddressVerficationCode(idHoursOfOperation);
	}// eof startUpdateURL()
	/**
	 * This method is used to update year of establishment value for particular the
	 * company records in the database.
	 * 
	 * @param indexId
	 * @param indexUpdatedHoursOfOperation
	 * @param fileName
	 */
	public void startUpdateHoursOFOperation(int indexId, int indexUpdatedHoursOfOperation, String fileName) {

		Map<Integer, String> idHoursOfOperation = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedHoursOfOperation].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String hourOfOperation = lines[indexUpdatedHoursOfOperation].trim();
			U.log(lines[indexUpdatedHoursOfOperation]);
			if (hourOfOperation != null)
				idHoursOfOperation.put(id, hourOfOperation);

		}
		U.log("Total Hours Of Operation for update ::" + idHoursOfOperation.size());
		/*
		 * Start updating here
		 */
		updateHoursOfOperation(idHoursOfOperation);
	}// eof startUpdateURL()

	/**
	 * This method is used to update year of establishment value for particular the
	 * company records in the database.
	 * 
	 * @param indexId
	 * @param indexUpdatedYearInBiz
	 * @param fileName
	 */
	public void startUpdateYearInBiz(int indexId, int indexUpdatedYearInBiz, String fileName) {

		Map<Integer, String> idYearInBizMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexUpdatedYearInBiz].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String yearInBiz = lines[indexUpdatedYearInBiz].trim();
			yearInBiz = Util.match(yearInBiz, "\\d{4}"); // idYearInBizMap

			if (yearInBiz != null)
				idYearInBizMap.put(id, yearInBiz);

		}
		U.log("Total Year In Biz for update ::" + idYearInBizMap.size());
		/*
		 * Start updating here
		 */
		updateYearInBiz(idYearInBizMap);
	}// eof startUpdateURL()

	/**
	 * This method is used to update employee value in form of min & max value for
	 * particular the company records in the database.
	 * 
	 * @param indexId
	 * @param indexEmpCount
	 *            :- The value is index of the particular column in the file that
	 *            contain the range of employee.<br>
	 *            For e.g. : 200-500, 0-5, 200 Emp, 1-10, etc.
	 * @param fileName
	 */
	public void startUpdateEmpCount(int indexId, int indexEmpCount, String fileName) {

		Map<Integer, String[]> idEmpCountMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;
			if (lines[indexEmpCount].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID
			String[] vals = Employee.getMinAndMaxEmp(lines[indexEmpCount].trim());

			if (vals[0].isEmpty() && vals[1].isEmpty())
				continue;

			if (vals[0].isEmpty())
				vals[0] = "0";
			if (vals[1].isEmpty())
				vals[1] = "0";
			long[] empCounts = new long[] { Long.parseLong(vals[0]), Long.parseLong(vals[1]) };

			if (empCounts[0] == 0 && empCounts[1] == 0)
				continue;

			else if (empCounts[0] == 0 && empCounts[1] > 1) {
				vals[0] = "1";
				vals[1] = String.valueOf(empCounts[1]);
			} else if (empCounts[0] > empCounts[1]) {
				if (empCounts[1] == 0) {
					vals[0] = String.valueOf(empCounts[0]);
					vals[1] = null;
				} else if (empCounts[1] >= 1) {
					vals[0] = String.valueOf(empCounts[1]);
					vals[1] = String.valueOf(empCounts[0]);
				}
			} else if (empCounts[0] < empCounts[1]) {
				if (empCounts[0] == 0) {
					vals[0] = String.valueOf(empCounts[1]);
					vals[1] = null;
				} else if (empCounts[0] >= 1) {
					vals[0] = String.valueOf(empCounts[0]);
					vals[1] = String.valueOf(empCounts[1]);
				}
			} else {
				if (empCounts[0] == empCounts[1] && empCounts[1] == 0) {
					vals[0] = vals[1] = null;
				} else if (empCounts[0] == empCounts[1] && empCounts[1] > 0) {
					vals[0] = String.valueOf(empCounts[0]);
					vals[1] = null;
				}
			}

			if (vals[0] != null || vals[1] != null)
				idEmpCountMap.put(id, new String[] { vals[0], vals[1] });

		}
		U.log("Total Employee Count for update ::" + idEmpCountMap.size());
		/*
		 * Start updating here
		 */
		updateEmployeeCount(idEmpCountMap);
	}// eof startUpdateEmpCount()

	/**
	 * This method is used to update employee value in form of min & max value for
	 * particular the company records in the database.
	 * 
	 * @param indexId
	 * @param indexMinEmpCount
	 *            :- The value is index of the particular column in the file that
	 *            contain the min value of employee.
	 * @param indexMaxEmpCount
	 *            :- The value is index of the particular column in the file that
	 *            contain the max value of employee.
	 * @param fileName
	 */
	public void startUpdateEmpCount(int indexId, int indexMinEmpCount, int indexMaxEmpCount, String fileName) {
		Map<Integer, String[]> idEmpCountMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String minEmpCount = lines[indexMinEmpCount].trim();
			String maxEmpCount = lines[indexMaxEmpCount].trim();

			if (minEmpCount.isEmpty() && maxEmpCount.isEmpty())
				continue;

			if (minEmpCount.isEmpty())
				minEmpCount = null;
			if (maxEmpCount.isEmpty())
				maxEmpCount = null;

			if (minEmpCount != null || maxEmpCount != null)
				idEmpCountMap.put(id, new String[] { minEmpCount, maxEmpCount });

		}
		U.log("Total Employee Count for update ::" + idEmpCountMap.size());
		/*
		 * Start updating here
		 */
		updateEmployeeCount(idEmpCountMap);
	}// eof startUpdateEmpCount()

	/**
	 * This method is used to update annual sales value in form of min & max value
	 * for particular the company records in the database.
	 * 
	 * @param indexId
	 * @param indexMinAnnualSales
	 *            :- The value is index of the particular column in the file that
	 *            contain the min value of annual sales.
	 * @param indexMaxAnnualSales
	 *            :- The value is index of the particular column in the file that
	 *            contain the max value of annual sales.
	 * @param fileName
	 */
	public void startUpdateAnnualSales(int indexId, int indexMinAnnualSales, int indexMaxAnnualSales, String fileName) {
		Map<Integer, String[]> idAnnualSalesMap = new HashMap<>();

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);

		U.log("Total File Size ::" + readLines.size());
		for (String lines[] : readLines) {

			if (lines[indexId].trim().isEmpty())
				continue;

			int id = Integer.parseInt(lines[indexId].trim()); // ID

			String minAnnualSale = lines[indexMinAnnualSales].trim();
			String maxAnnualSale = lines[indexMaxAnnualSales].trim();

			if (minAnnualSale.isEmpty() && maxAnnualSale.isEmpty())
				continue;

			if (minAnnualSale.isEmpty())
				minAnnualSale = null;
			if (maxAnnualSale.isEmpty())
				maxAnnualSale = null;

			if (minAnnualSale != null || maxAnnualSale != null)
				idAnnualSalesMap.put(id, new String[] { minAnnualSale, maxAnnualSale });

		}
		U.log("Total Annual Sales for update ::" + idAnnualSalesMap.size());
		/*
		 * Start updating here
		 */
		updateAnnualSales(idAnnualSalesMap);
	}// eof startUpdateAnnualSales()

	/**
	 * This method is used to updated latitude and longitude at database. Also, its
	 * validate the boundaries inside dataV table.
	 * 
	 * @param indexId
	 * @param indexLat
	 * @param indexLng
	 * @param fileName
	 */
	public void startUpdateLatLng(int indexId, int indexLat, int indexLng, String fileName) {

		Map<Integer, String[]> idLatLngMap = new HashMap<>();

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

			idLatLngMap.put(id, new String[] { lat, lng });
		}
		U.log("Total Email's for update ::" + idLatLngMap.size());
		/*
		 * Start updating here
		 */
		updateLatLng(idLatLngMap);
	}

	/**
	 * This method is used to update Min_Emp_Count as well as Max_Emp_Count in the
	 * database. Also, it validate these fields at dataV.
	 * 
	 * @param idEmpCountMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the arrays that hold min & max emp count (size
	 *            is 2 and index 0=min & 1=max) for update as per its Key ID.
	 */
	private void updateEmployeeCount(Map<Integer, String[]> idEmpCountMap) {
		List<Integer> updatedIdListForMinEmp = new ArrayList<>();
		List<Integer> updatedIdListForMaxEmp = new ArrayList<>();

		String query = "update dataset set EMP_COUNT_MIN=?,EMP_COUNT_MAX=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;
		/*
		 * Update Min & max emp count
		 */
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating EMP_COUNT_MIN and EMP_COUNT_MAX here ....");

			for (Entry<Integer, String[]> entry : idEmpCountMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] == null || empCount[1] == null)
					continue;

				pstmt.setString(1, empCount[0]); // min emp count
				pstmt.setString(2, empCount[1]); // max emp count
				pstmt.setString(3, U.getTodayDate());
				pstmt.setInt(4, entry.getKey()); // ID

				pstmt.addBatch();

				// Added for updating flag at dataV
				updatedIdListForMinEmp.add(entry.getKey());
				updatedIdListForMaxEmp.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update only min emp count
		 */
		query = "update dataset set EMP_COUNT_MIN=?, updated_date=? where id=?";
		x = 0;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Only EMP_COUNT_MIN here ....");

			for (Entry<Integer, String[]> entry : idEmpCountMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] == null && empCount[1] != null)
					continue;
				if (empCount[0] != null && empCount[1] != null)
					continue;

				pstmt.setString(1, empCount[0]); // min emp count
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();
				// Added for updating flag at dataV
				updatedIdListForMinEmp.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*
		 * Update only max emp count
		 */
		query = "update dataset set EMP_COUNT_MAX=?, updated_date=? where id=?";
		x = 0;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Only EMP_COUNT_MAX here ....");

			for (Entry<Integer, String[]> entry : idEmpCountMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] != null && empCount[1] == null)
					continue;
				if (empCount[0] != null && empCount[1] != null)
					continue;

				pstmt.setString(1, empCount[1]); // Max emp count
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();
				// Added for updating flag at dataV
				updatedIdListForMaxEmp.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idEmpCountMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idEmpCountMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id for Min_Emp_Count at dataV table
			 */
			CorrectorAtValidateTable.validateMinEmpCountAtValidationTable(updatedIdListForMinEmp);
			/*
			 * validate id for Max_Emp_Count at dataV table
			 */
			CorrectorAtValidateTable.validateMaxEmpCountAtValidationTable(updatedIdListForMaxEmp);
		}
	}// eof updateEmployeeCount()

	/**
	 * This method is used to update ANNUAL_SALES_VOL_MIN as well as
	 * ANNUAL_SALES_VOL_MAX in the database. Also, it validate these fields at
	 * dataV.
	 * 
	 * @param idAnnualSalesMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the arrays that hold min & max annual sales
	 *            (size is 2 and index 0=min & 1=max) for update as per its Key ID.
	 */
	private void updateAnnualSales(Map<Integer, String[]> idAnnualSalesMap) {
		List<Integer> updatedIdListForMinAnnualSale = new ArrayList<>();
		List<Integer> updatedIdListForMaxAnnualSale = new ArrayList<>();

		String query = "update dataset set ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;
		/*
		 * Update Min & max annual Sales
		 */
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating ANNUAL_SALES_VOL_MIN and ANNUAL_SALES_VOL_MAX here ....");

			for (Entry<Integer, String[]> entry : idAnnualSalesMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] == null || empCount[1] == null)
					continue;

				pstmt.setString(1, empCount[0]); // min annual sales
				pstmt.setString(2, empCount[1]); // max annual sales
				pstmt.setString(3, U.getTodayDate());
				pstmt.setInt(4, entry.getKey()); // ID

				pstmt.addBatch();

				// Added for updating flag at dataV
				updatedIdListForMinAnnualSale.add(entry.getKey());
				updatedIdListForMaxAnnualSale.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update only min annual sales
		 */
		query = "update dataset set ANNUAL_SALES_VOL_MIN=?, updated_date=? where id=?";
		x = 0;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Only ANNUAL_SALES_VOL_MIN here ....");

			for (Entry<Integer, String[]> entry : idAnnualSalesMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] == null && empCount[1] != null)
					continue;
				if (empCount[0] != null && empCount[1] != null)
					continue;

				pstmt.setString(1, empCount[0]); // min annual sales
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();
				// Added for updating flag at dataV
				updatedIdListForMinAnnualSale.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		/*
		 * Update only max annual sales
		 */
		query = "update dataset set ANNUAL_SALES_VOL_MAX=?, updated_date=? where id=?";
		x = 0;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Only ANNUAL_SALES_VOL_MAX here ....");

			for (Entry<Integer, String[]> entry : idAnnualSalesMap.entrySet()) {
				String[] empCount = entry.getValue();
				if (empCount[0] != null && empCount[1] == null)
					continue;
				if (empCount[0] != null && empCount[1] != null)
					continue;

				pstmt.setString(1, empCount[1]); // Max annual sales
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();
				// Added for updating flag at dataV
				updatedIdListForMaxAnnualSale.add(entry.getKey());

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idAnnualSalesMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idAnnualSalesMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id for Min_Emp_Count at dataV table
			 */
			CorrectorAtValidateTable.validateMinAnnualSalesAtValidationTable(updatedIdListForMinAnnualSale);
			/*
			 * validate id for Max_Emp_Count at dataV table
			 */
			CorrectorAtValidateTable.validateMaxAnnualSalesAtValidationTable(updatedIdListForMaxAnnualSale);
		}
	}// eof updateAnnualSales()

	/**
	 * This method is used to update latitude and longitude at database.
	 * 
	 * @param idLatLngMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the arrays of latlng(size is 2 and index 0=lat &
	 *            1=lng) for update as per its Key ID.
	 */
	public void updateLatLng(Map<Integer, String[]> idLatLngMap) {

		String query = "update dataset set latitude=?,longitude=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating latitude and longitude here ....");

			for (Entry<Integer, String[]> entry : idLatLngMap.entrySet()) {
				String[] latLng = entry.getValue();

				pstmt.setString(1, latLng[0]); // Lat
				pstmt.setString(2, latLng[1]); // Lng
				pstmt.setString(3, U.getTodayDate());
				pstmt.setInt(4, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idLatLngMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idLatLngMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateLatLngAtValidationTable(updatedIdList);
		}

	}// eof updateLatLng()

	/*
	 * private void updateLatLng(Map<Integer,String[]> idEmpCountMap){
	 * 
	 * String query =
	 * "update dataset set EMP_COUNT_MIN=?, EMP_COUNT_MAX=?, updated_date=? where id=?"
	 * ; PreparedStatement pstmt = null;
	 * 
	 * int x = 0; int updateCount[];
	 * 
	 * try { conn.setAutoCommit(false); pstmt = conn.prepareStatement(query);
	 * U.log("*** Start updating latitude and longitude here ....");
	 * 
	 * for(Entry<Integer, String[]> entry : idEmpCountMap.entrySet()){ String[]
	 * empCount = entry.getValue();
	 * 
	 * pstmt.setString(1, empCount[0]); //Lat pstmt.setString(2, empCount[1]); //Lng
	 * pstmt.setString(3, U.getTodayDate()); pstmt.setInt(4, entry.getKey()); //ID
	 * 
	 * pstmt.addBatch();
	 * 
	 * if((++x % 5000) == 0){ updateCount = pstmt.executeBatch();
	 * System.out.println("Number of rows updated: "+ updateCount.length + "\t" +
	 * x); conn.commit(); System.out.println("Commit the batch"); } }//eof for
	 * updateCount = pstmt.executeBatch();
	 * System.out.println("Number of rows updated: "+ updateCount.length + "\t" +
	 * x); conn.commit(); System.out.println("Commit the batch");
	 * 
	 * pstmt.close(); conn.setAutoCommit(true); }catch(SQLException e){
	 * e.printStackTrace(); }
	 * 
	 * 
	 * Update report at updatedDataset
	 * 
	 * if(idLatLngMap.size()> 0){ List<Integer> updatedIdList = new
	 * ArrayList<>(idLatLngMap.keySet()); Collections.sort(updatedIdList);
	 * U.log("Size of Unique List ::"+updatedIdList.size());
	 * 
	 * if(updatedIdList.size() > 0){ UpdateReportAtTable report = new
	 * UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	 * report.startExtractingFromDB(updatedIdList); report.disconnect(); }
	 * 
	 * validate id at dataV table
	 * 
	 * CorrectorAtValidateTable.validateLatLngAtValidationTable(updatedIdList); }
	 * 
	 * }
	 */

	/**
	 * This method is used to update Title at database.
	 * 
	 * @param idTitle
	 *            :- It is a {@code Map}, key is contain the Id's and values is
	 *            contain the title for contact person to update as per its Key ID.
	 */
	public void updateTitle(Map<Integer, String> idTitle) {

		String query = "update dataset set TITLE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Title(Designation) here ....");

			for (Entry<Integer, String> entry : idTitle.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Title for Contact Person
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idTitle.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idTitle.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateTitleAtValidationTable(updatedIdList);
		}
	}// eof updateTitle()

	/**
	 * This method is used to update phone at database.
	 * 
	 * @param idPhoneMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the phone for update as per its Key ID.
	 */
	public void updatePhone(Map<Integer, String> idPhoneMap) {
		// List<Integer> updatedIdList = new ArrayList<>();

		String query = "update dataset set phone=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating phones here ....");

			for (Entry<Integer, String> entry : idPhoneMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Phone
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idPhoneMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idPhoneMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validatePhoneAtValidationTable(updatedIdList);
		}

	}// eof updatePhoneNumber();

	/**
	 * This method is used to update fax at database.
	 * 
	 * @param idFaxMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the fax for update as per its Key ID.
	 */
	public void updateFax(Map<Integer, String> idFaxMap) {
		// List<Integer> updatedIdList = new ArrayList<>();

		String query = "update dataset set fax=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Fax here ....");

			for (Entry<Integer, String> entry : idFaxMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Fax
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idFaxMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idFaxMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateFaxAtValidationTable(updatedIdList);
		}
	}// eof updateFax()

	/**
	 * This method is used to update URL's at database.
	 * 
	 * @param idUrlMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the URL's for update as per its Key ID.
	 * @param mustNull
	 *            :- <b>true</b> :- if URL is null and want to update URL for null,
	 *            <b>false</b> :- if URL is not null and want to replace URL for not
	 *            null.
	 */
	public void updateURL(Map<Integer, String> idUrlMap, boolean mustNull) {
		Set<String> updatedDateSet = new HashSet<>();

		List<Integer> updatedIdList = new ArrayList<>();

		String updateQuery = "update dataset set URL=?, UPDATED_DATE=? where ID=?";

		if (mustNull)
			updateQuery = updateQuery + " AND URL is null";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			U.log("*** Start updating URL's here ....");

			for (Entry<Integer, String> entry : idUrlMap.entrySet()) {

				String date = U.getTodayDate();
				updatedDateSet.add(date); // add date to set

				pstmt.setString(1, entry.getValue()); // URL
				pstmt.setString(2, date);
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				updatedIdList.add(entry.getKey()); // add id's here
			} // eof for

			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Total count of updated url at database ::" + x);
		/*
		 * Update report at updatedDataset
		 */
		if (idUrlMap.size() > 0) {
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateUrlAtValidationTable(updatedIdList);
		}

		U.log("Updating dates for url ::");
		for (String date : updatedDateSet) {
			U.log(date);
		}

	}// eof updateURL()

	/**
	 * This method is used to update Email's at database.
	 * 
	 * @param idEmailMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the Email's for update as per its Key ID.
	 * @param mustNull
	 *            :- <b>true</b> :- if Email is null and want to update Email for
	 *            null, <b>false</b> :- if Email is not null and want to replace
	 *            Email for not null.
	 */
	public void updateEmail(Map<Integer, String> idEmailMap, boolean mustNull) {
		Set<String> updatedDateSet = new HashSet<>();

		List<Integer> updatedIdList = new ArrayList<>();

		String updateQuery = "update dataset set EMAIL=?, UPDATED_DATE=? where ID=?";

		if (mustNull)
			updateQuery = updateQuery + " AND EMAIL is null";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			U.log("*** Start updating EMAIL's here ....");

			for (Entry<Integer, String> entry : idEmailMap.entrySet()) {

				String date = U.getTodayDate();
				updatedDateSet.add(date); // add date to set

				pstmt.setString(1, entry.getValue()); // Email
				pstmt.setString(2, date);
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				updatedIdList.add(entry.getKey()); // add id's here
			} // eof for

			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Total count of updated EMAIL at database ::" + x);
		/*
		 * Update report at updatedDataset
		 */
		if (idEmailMap.size() > 0) {
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateEmailAtValidationTable(updatedIdList);
		}

		U.log("Updating dates for email ::");
		for (String date : updatedDateSet) {
			U.log(date);
		}

	}// eof updateEmail()

	/**
	 * This method is used to update Zip's at database.
	 * 
	 * @param idZipMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the Zip's for update as per its Key ID.
	 */
	private void updateZip(Map<Integer, String> idZipMap) {

		String query = "update dataset set ZIP=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Zip's here ....");

			for (Entry<Integer, String> entry : idZipMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Zip
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idZipMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idZipMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateZipAtValidationTable(updatedIdList);
		}

	}// eof updatePhoneNumber();

	private void updateHoursOfOperation(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set Hours_Of_Operation=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Hours Of Operation here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateHoursOfOperationAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	private void updateLocationSource(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set LOCATION_SOURCE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating LocationSource here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateLocationSourceAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();
	private void updateQualityScore(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set QUALITY_SCORE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating LocationSource here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateQualtiyScoreAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	private void updateGeoDistance(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set GEO_DISTANCE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating LocationSource here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateGeoDistanceAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	private void updateGeoAccuracyCode(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set GEO_ACCURACY_CODE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating LocationSource here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateGeoAccuracyAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	private void updateAddressVerficationCode(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set ADDRESS_VERIFICATION_CODE=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating LocationSource here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateAddressVerificationCodeAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	


	private void updateYearInBiz(Map<Integer, String> idYearInBizMap) {

		String query = "update dataset set YEARS_IN_BIZ=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating YEARS_IN_BIZ here ....");

			for (Entry<Integer, String> entry : idYearInBizMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // YEARS_IN_BIZ
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idYearInBizMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idYearInBizMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateYearInBizAtValidationTable(updatedIdList);
		}

	}// eof updateYearInBiz();

	/**
	 * This method is used to update companyNames at database.
	 * 
	 * @param idCompanyNameMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the companyNames for update as per its Key ID.
	 */
	public void updateCompanyName(Map<Integer, String> idCompanyNameMap) {

		String query = "update dataset set COMPANY_NAME=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Company names here ....");

			for (Entry<Integer, String> entry : idCompanyNameMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Company Name
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idCompanyNameMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idCompanyNameMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateCompanyNameAtValidationTable(updatedIdList);
		}

	}// eof updateCompanyName();

	/**
	 * This method is used to update Neighborhood at database.
	 * 
	 * @param idNeighborhoodMap
	 *            :- It is a Map collection, where key is contain the Id's and
	 *            values is contain the Neighborhood for update as per its Key ID.
	 */
	public void updateNeighborhood(Map<Integer, String> idNeighborhoodMap) {

		String query = "update dataset set NEIGHBORHOOD=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating Neighborhood here ....");

			for (Entry<Integer, String> entry : idNeighborhoodMap.entrySet()) {

				pstmt.setString(1, entry.getValue()); // Neighbourhood
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idNeighborhoodMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idNeighborhoodMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateNeighborhoodAtValidationTable(updatedIdList);
		}

	}// eof updateNeighborhood();

	/**
	 * This method is used to update unique key field's in the database.
	 * 
	 * @param idUniqueFieldMap
	 *            :- It is a Map collection, where key is contain the Id's <br>
	 *            and values is contain array of string with its modified row
	 *            fields.
	 */
	public void updateOnlyUniqueFieldsAtDB(Map<Integer, String[]> idUniqueFieldMap) {
		String[] arr=new String[50];
		U.log("*** Total count for update unique key field ::" + idUniqueFieldMap.size());
		String query = "UPDATE dataset SET INDUSTRY_SECTOR=?, SPANISH_INDUSTRY_SECTOR=?, SIC_MAJOR=?, SIC_SUB=?, PRODUCT_DESC=?, SPANISH_PRODUCT_DESC=?,"
				+ "COMPANY_NAME=?, ADDRESS=?, CITY=?, STATE=?, CONTACT_PERSON=?, UPDATED_DATE=? WHERE ID=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		int counter=0;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("Start updating unique keys field in the database here ....");
			
			for (Entry<Integer, String[]> entry : idUniqueFieldMap.entrySet()) {

				String[] data = entry.getValue();

				if (data[4].trim().length() == 3) {
					data[4] = "0" + data[4].trim();
				}
				String[] sicInfo = Sic.sicInfo(data[4]);
				if (sicInfo == null) {
					System.err
							.println("Not updated for id :" + entry.getKey() + " >> since sic code is unknown to us.");
					continue;
				}

				pstmt.setString(1, U.toTitleCase(sicInfo[0])); // Industry Sector
				pstmt.setString(2, U.toTitleCase(sicInfo[1])); // Spanish_Industry_Sector
				pstmt.setString(3, U.toTitleCase(sicInfo[3])); // Major Sic
				pstmt.setString(4, U.toTitleCase(sicInfo[4])); // Sub Sic
				pstmt.setString(5, U.toTitleCase(sicInfo[5])); // Product Description
				pstmt.setString(6, U.toTitleCase(sicInfo[6])); // Spanish_Product_Description

				pstmt.setString(7, data[7]); // Company name
				pstmt.setString(8, data[8]); // Address
				pstmt.setString(9, data[10]); // City
				pstmt.setString(10, data[11]); // State

				if (data[17] != null && (data[17].isEmpty() || data[17].equals(TextFormat.ALLOW_BLANK)))
					data[17] = null;

				pstmt.setString(11, data[17]); // Contact Person

				pstmt.setString(12, U.getTodayDate()); // Updated date
				pstmt.setInt(13, entry.getKey()); // ID
				arr[counter]=entry.getKey()+"";
				pstmt.addBatch();
				counter++;
				if ((++x % 50) == 0) {
					try {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					} catch (SQLException e) {
						U.log(Arrays.toString(arr));
						e.printStackTrace();
					}
					counter=0;
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			U.log(Arrays.toString(arr));
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idUniqueFieldMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idUniqueFieldMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
		}
	}// eof updateOnlyUniqueFieldsAtDB();

	/**
	 * This method is used to update unique key field's as well as non unique key
	 * fields in the database.
	 * 
	 * @param idUniqueFieldMap
	 *            :- It is a Map collection, where key is contain the Id's <br>
	 *            and values is contain array of string with its modified row
	 *            fields.
	 */
	public void updateOnlyUniqueFieldsWithOtherAtDB(Map<Integer, String[]> idUniqueFieldMap) {
		Set<String[]> dataset = new HashSet<>(idUniqueFieldMap.values());
		updateOnlyUniqueFieldsWithOtherAtDB(dataset);
	}

	/**
	 * This method is used to update unique key field's as well as non unique key
	 * fields in the database.
	 * 
	 * @param idUniqueFieldSet
	 *            :- It is a Set collection, where values is contain array of string
	 *            with its modified row fields.
	 */
	public void updateOnlyUniqueFieldsWithOtherAtDB(Set<String[]> idUniqueFieldSet) {

		U.log("*** Total count for update unique & non unique key field ::" + idUniqueFieldSet.size());
		List<Integer> updatedIdList = new ArrayList<>();

		String query = "UPDATE dataset SET INDUSTRY_SECTOR=?, SPANISH_INDUSTRY_SECTOR=?, SIC_MAJOR=?, SIC_SUB=?, PRODUCT_DESC=?, SPANISH_PRODUCT_DESC=?,"
				+ "COMPANY_NAME=?, ADDRESS=?, CITY=?, STATE=?, CONTACT_PERSON=?,"
				+ "NEIGHBORHOOD=?, PHONE=?, FAX=?, URL=?, EMAIL=?, TITLE=?, ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?,"
				+ "EMP_COUNT_MIN=?, EMP_COUNT_MAX=?, YEARS_IN_BIZ=?, LONGITUDE=?, LATITUDE=?,UPDATED_DATE=?"
				+ "WHERE ID=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("Start updating unique keys field and other fields in the database here ....");

			for (String[] data : idUniqueFieldSet) {

				if (data[4].trim().length() == 3) {
					data[4] = "0" + data[4].trim();
				}
				String[] sicInfo = Sic.sicInfo(data[4]);
				if (sicInfo == null) {
					System.err.println("Not updated for id :" + data[0]);
					continue;
				}
				// U.log(data[0]);
				pstmt.setString(1, U.toTitleCase(sicInfo[0])); // Industry Sector
				pstmt.setString(2, U.toTitleCase(sicInfo[1])); // Spanish_Industry_Sector
				pstmt.setString(3, U.toTitleCase(sicInfo[3])); // Major Sic
				pstmt.setString(4, U.toTitleCase(sicInfo[4])); // Sub Sic
				pstmt.setString(5, U.toTitleCase(sicInfo[5])); // Product Description
				pstmt.setString(6, U.toTitleCase(sicInfo[6])); // Spanish_Product_Description

				pstmt.setString(7, data[7]); // Company name
				pstmt.setString(8, data[8]); // Address
				pstmt.setString(9, data[10]); // City
				pstmt.setString(10, data[11]); // State

				if (data[17] != null && (data[17].isEmpty() || data[17].equals(TextFormat.ALLOW_BLANK)))
					data[17] = null;

				pstmt.setString(11, data[17]); // Contact Person

				pstmt.setString(12, data[9]); // NEIGHBORHOOD
				pstmt.setString(13, data[13]); // PHONE
				pstmt.setString(14, data[14]); // FAX
				pstmt.setString(15, data[15]); // URL
				pstmt.setString(16, data[16]); // EMAIL
				pstmt.setString(17, data[18]); // TITLE
				pstmt.setString(18, data[19]); // ANNUAL_SALES_VOL_MIN
				pstmt.setString(19, data[20]); // ANNUAL_SALES_VOL_MAX

				pstmt.setString(20, data[21]); // EMP_COUNT_MIN
				pstmt.setString(21, data[22]); // EMP_COUNT_MAX
				pstmt.setString(22, data[23]); // YEARS_IN_BIZ
				pstmt.setString(23, data[24]); // LONGITUDE
				pstmt.setString(24, data[25]); // LATITUDE
				pstmt.setString(25, U.getTodayDate()); // today date
				pstmt.setString(26, data[0]); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				updatedIdList.add(Integer.parseInt(data[0].trim()));// id for update
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idUniqueFieldSet.size() > 0) {
			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
		}

	}// eof updateOnlyUniqueFieldsAtDB();

	/**
	 * This method is used to update sic sub information in the database. When sic
	 * sub value is same for that ID record.
	 * 
	 * @param idSicSubMap
	 *            :- It is a {@code Map} that key contain the ID and value contain
	 *            sic sub(Sic Sub is same for that record).
	 */
	public void updateSicInfo(Map<Integer, String> idSicSubMap) {

		U.log("*** Total count for update Sic Information ::" + idSicSubMap.size());
		String query = "UPDATE dataset SET INDUSTRY_SECTOR=?, SPANISH_INDUSTRY_SECTOR=?, SIC_MAJOR=?, SIC_SUB=?, PRODUCT_DESC=?, SPANISH_PRODUCT_DESC=?,"
				+ "UPDATED_DATE=? WHERE ID=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("Start updating Sic sub Information in the database here ....");
			for (Entry<Integer, String> entry : idSicSubMap.entrySet()) {

				String sicSub = entry.getValue();

				if (sicSub.trim().length() == 3) {
					sicSub = "0" + sicSub.trim();
				}
				String[] sicInfo = Sic.sicInfo(sicSub);
				if (sicInfo == null) {
					System.err.println("Not updated for id :" + entry.getKey());
					continue;
				}

				pstmt.setString(1, U.toTitleCase(sicInfo[0])); // Industry Sector
				pstmt.setString(2, U.toTitleCase(sicInfo[1])); // Spanish_Industry_Sector
				pstmt.setString(3, U.toTitleCase(sicInfo[3])); // Major Sic
				pstmt.setString(4, U.toTitleCase(sicInfo[4])); // Sub Sic
				pstmt.setString(5, U.toTitleCase(sicInfo[5])); // Product Description
				pstmt.setString(6, U.toTitleCase(sicInfo[6])); // Spanish_Product_Description

				pstmt.setString(7, U.getTodayDate()); // Updated date
				pstmt.setInt(8, entry.getKey()); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/*
		 * Update report at updatedDataset
		 */
		if (idSicSubMap.size() > 0) {
			List<Integer> updatedIdList = new ArrayList<>(idSicSubMap.keySet());
			Collections.sort(updatedIdList);
			U.log("Size of Unique List ::" + updatedIdList.size());

			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
		}
	}// eof updateSicInfo()

	/**
	 * This method is used to update company records and other details except its
	 * unique key fields.
	 * 
	 * @param dataset
	 *            :- Is collection of updated records set.
	 */
	public void updateCompanyNameWithOthers(Set<String[]> dataset) {
		List<Integer> updatedIdList = new ArrayList<>();

		String update = "update dataset set NEIGHBORHOOD=?,PHONE=?,FAX=?,URL=?,EMAIL=?,TITLE=?,ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?,EMP_COUNT_MIN=?,EMP_COUNT_MAX=?,YEARS_IN_BIZ=?,LONGITUDE=?,LATITUDE=?,UPDATED_DATE=?,COMPANY_NAME=? "
				+ "where ID=?";

		int updateCount[];
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(update);
			conn.setAutoCommit(false);
			int i = 0;
			U.log("*** Start updating Company names with other non unique key fields here ....");

			for (String[] correctData : dataset) {

				pstmt.setString(1, correctData[9]); // NEIGHBORHOOD
				pstmt.setString(2, correctData[13]); // PHONE
				pstmt.setString(3, correctData[14]); // FAX
				pstmt.setString(4, correctData[15]); // URL
				pstmt.setString(5, correctData[16]); // EMAIL
				pstmt.setString(6, correctData[18]); // TITLE
				pstmt.setString(7, correctData[19]); // ANNUAL_SALES_VOL_MIN
				pstmt.setString(8, correctData[20]); // ANNUAL_SALES_VOL_MAX
				pstmt.setString(9, correctData[21]); // EMP_COUNT_MIN
				pstmt.setString(10, correctData[22]); // EMP_COUNT_MAX
				pstmt.setString(11, correctData[23]); // YEARS_IN_BIZ
				pstmt.setString(12, correctData[24]); // LONGITUDE
				pstmt.setString(13, correctData[25]); // LATITUDE
				pstmt.setString(14, U.getTodayDate()); // today date
				pstmt.setString(15, correctData[7]); // Correct Company Name
				pstmt.setString(16, correctData[0]); // ID

				pstmt.addBatch();
				if ((++i % 100) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows Updated: " + updateCount.length + "\t" + i);
					conn.commit();
					U.log("Batch Committed");
				}
				updatedIdList.add(Integer.parseInt(correctData[0].trim()));// id for update
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows Updated: " + updateCount.length + "\t" + i);
			conn.commit();
			U.log("Batch Committed");
			conn.setAutoCommit(true);

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (dataset.size() > 0) {
			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
			/*
			 * validate id at dataV table
			 */
			CorrectorAtValidateTable.validateCompanyNameAtValidationTable(updatedIdList);
		}

	}// eof updateCompanyNameWithOthers()

	/**
	 * This method is used to update non unique key fields for specified record id.
	 * 
	 * @param dataset
	 *            :-Is collection of updated records.
	 */
	public void updateNonUniqueKeyFields(List<String[]> dataset) {
		List<Integer> updatedIdList = new ArrayList<>();

		String update = "update dataset set NEIGHBORHOOD=?,PHONE=?,FAX=?,URL=?,EMAIL=?,TITLE=?,ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?,EMP_COUNT_MIN=?,EMP_COUNT_MAX=?,YEARS_IN_BIZ=?,LONGITUDE=?,LATITUDE=?,UPDATED_DATE=? "
				+ "where ID=?";

		int updateCount[];
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(update);
			conn.setAutoCommit(false);
			int i = 0;
			U.log("*** Start updating non unique key fields records here...");

			for (String[] correctData : dataset) {

				pstmt.setString(1, correctData[9]); // NEIGHBORHOOD
				pstmt.setString(2, correctData[13]); // PHONE
				pstmt.setString(3, correctData[14]); // FAX
				pstmt.setString(4, correctData[15]); // URL
				pstmt.setString(5, correctData[16]); // EMAIL
				pstmt.setString(6, correctData[18]); // TITLE
				pstmt.setString(7, correctData[19]); // ANNUAL_SALES_VOL_MIN
				pstmt.setString(8, correctData[20]); // ANNUAL_SALES_VOL_MAX
				pstmt.setString(9, correctData[21]); // EMP_COUNT_MIN
				pstmt.setString(10, correctData[22]); // EMP_COUNT_MAX
				pstmt.setString(11, correctData[23]); // YEARS_IN_BIZ
				pstmt.setString(12, correctData[24]); // LONGITUDE
				pstmt.setString(13, correctData[25]); // LATITUDE
				pstmt.setString(14, U.getTodayDate()); // today date
				pstmt.setString(15, correctData[0]); // ID

				pstmt.addBatch();
				if ((++i % 100) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows Updated: " + updateCount.length + "\t" + i);
					conn.commit();
					U.log("Batch Committed");
				}
				updatedIdList.add(Integer.parseInt(correctData[0].trim()));// id for update
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows Updated: " + updateCount.length + "\t" + i);
			conn.commit();
			U.log("Batch Committed");
			conn.setAutoCommit(true);

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (dataset.size() > 0) {
			if (updatedIdList.size() > 0) {
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
		}

	}// eof updateNonUniqueKeyFields()

	/**
	 * This method is used to update status 'CLOSED' in the database.
	 * 
	 * @param idList
	 *            :- Is list collection containing ID's of permanent closed record.
	 */
	public void updateClosedStatus(List<Integer> idList) {
		Set<Integer> idSet = new HashSet<>(idList);
		updateClosedStatus(idSet);
	}// eof updateClosedStatus()

	/**
	 * This method is used to update status 'CLOSED' in the database.
	 * 
	 * @param idSet
	 *            :- Is Set collection containing ID's of permanent closed record.
	 */
	public void updateClosedStatus(Set<Integer> idSet) {
		U.log("*** Total ID record has closed ::" + idSet.size());
		String query = "update dataset set _STATUS=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start updating _status closed here ....");

			for (int id : idSet) {
				pstmt.setString(1, "CLOSED"); // _STATUS
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}// eof updateClosedStatus()

	/**
	 * This method is used to update URL field to null for specified ID's list.
	 * 
	 * @param idList
	 *            :- Is list collection that containing ID's for update URL to null.
	 */
	public void deletedURL(List<Integer> idList) {
		U.log("*** Total ID record for delete it's URL in the database ::" + idList.size());

		String query = "update dataset set URL = null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting URL i.e. set URL=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.invalidateUrlAtValidationTable(idList);
		}
	}// EOF deletedURL()

	/**
	 * This method is used to update EMAIL field to null for specified ID's list.
	 * 
	 * @param idList
	 *            :- Is list collection that containing ID's for update EMAIL to
	 *            null.
	 */
	public void deletedEmail(List<Integer> idList) {
		U.log("*** Total ID record for delete it's EMAIL in the database ::" + idList.size());

		String query = "update dataset set EMAIL = null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting EMAIL i.e. set EMAIL=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.invalidateEmailAtValidationTable(idList);
		}
	}// EOF deletedEmail()

	/**
	 * This method is used to update PHONE field to null for specified ID's list.
	 * 
	 * @param idList
	 */
	public void deletedPhone(List<Integer> idList) {
		U.log("*** Total ID record for delete it's PHONE in the database ::" + idList.size());

		String query = "update dataset set PHONE = null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting PHONE i.e. set PHONE=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.invalidatePhoneAtValidationTable(idList);
		}
	}// EOF deletedPhone()

	/**
	 * This method is used to update FAX field to null for specified ID's list.
	 * 
	 * @param idList
	 */
	public void deletedFax(List<Integer> idList) {
		U.log("*** Total ID record for delete it's FAX in the database ::" + idList.size());

		String query = "update dataset set FAX = null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting FAX i.e. set FAX=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.invalidateFaxAtValidationTable(idList);
		}
	}

	/**
	 * Added By : Prii - 17June2019 This method is used to update Neighborhood field
	 * to null for specified ID's list.
	 * 
	 * @param idList
	 */
	public void deletedNeighborhood(List<Integer> idList) {
		U.log("*** Total ID record for delete it's Neighborhood in the database ::" + idList.size());

		String query = "update dataset set NEIGHBORHOOD = null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting NEIGHBORHOOD i.e. set NEIGHBORHOOD=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.validateNeighborhoodAtValidationTable(idList);
		}
	}// EOF deletedNeighborhood()

	/**
	 * Added By - Priti - 17June2019 This method is used to update latLng field to
	 * null for specified ID's list.
	 * 
	 * @param idList
	 */
	public void deletedLatLng(List<Integer> idList) {
		U.log("*** Total ID record for delete it's LatLng in the database ::" + idList.size());

		String query = "update dataset set LATITUDE = null,LONGITUDE=null, UPDATED_DATE=? where id=?";

		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];

		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			U.log("*** Start deleting latLng i.e. set LatLng=null here ....");

			for (int id : idList) {
				pstmt.setString(1, U.getTodayDate()); // today date
				pstmt.setInt(2, id); // ID

				pstmt.addBatch();

				if ((++x % 5000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
			} // eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: " + updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (idList.size() > 0) {
			CorrectorAtValidateTable.validateLatLngAtValidationTable(idList);
		}
	}// EOF deletedLatLng()

}// eof CorrectNonUniqueFields class
