/**
 * @author Sawan
 * @date 18 Sept 2018
 */
package com.tequila.database.validator;

import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CorrectorAtValidateTable {

	private static String fileName = 
			"/home/glady/Downloads/MexicoProject/pallavi/Oct_2018/22_Oct/Valid_DB_Records_Unique_Urls_22_10_2018-1_22_10_2018.csv";

	public static void main(String[] args) {

//		validatedUrlAtValidationTable(0);
		validatedUrlAtValidationTable(0, 8);
//		validatedPhoneAtValidationTable(0);
	}
	
	private static UpdateValidateTable connect(){
		return new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}
	
	private static List<Integer> loadIdList(int idIndex){
		List<Integer> updatedIdList = new ArrayList<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		U.log("Total File Size ::"+readLines.size());
		for(String [] lines : readLines){
			if(lines[idIndex].trim().isEmpty())continue;
			
			updatedIdList.add(Integer.parseInt(lines[idIndex].trim()));
		}
		U.log("Updated Id List ::"+updatedIdList.size());
		return updatedIdList;
	}
	
	private static List<Integer> loadIdList(int idIndex, int validFieldIndex){
		List<Integer> updatedIdList = new ArrayList<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		U.log("Total File Size ::"+readLines.size());
		for(String [] lines : readLines){
			if(lines[idIndex].trim().isEmpty())continue;
			if(lines[validFieldIndex].trim().isEmpty())continue;
			
			updatedIdList.add(Integer.parseInt(lines[idIndex].trim()));
		}
		U.log("Updated Id List ::"+updatedIdList.size());
		return updatedIdList;
	}

	/**
	 * This method is used to set flag for validated urls.
	 * @param idIndex :-Index of ID in the '.csv' file.
	 */
	public static void validatedUrlAtValidationTable(int idIndex){
		List<Integer> updatedIdList = loadIdList(idIndex);
		validateUrlAtValidationTable(updatedIdList);
	}
	
	public static void validatedUrlAtValidationTable(int idIndex, int urlIndex){
		List<Integer> updatedIdList = loadIdList(idIndex,urlIndex);
		validateUrlAtValidationTable(updatedIdList);
	}
	
	public static void validatedPhoneAtValidationTable(int idIndex){
		List<Integer> updatedIdList = loadIdList(idIndex);

		//Update Validate Url
		if(updatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.updatePhone(updatedIdList);
			//uvt.disconnect();
		}
	}
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Url</strong>, 
	 * that indicate <b>Url</b> is valid for that record and its tested by our tester manually. 
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateUrlAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Url
		if(validatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.updateUrl(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateUrlAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'null'</b> for <strong>Url</strong> that was validated before, 
	 * now <b>Url</b> is being invalid for that record and its tested by our tester manually. 
	 * @param invalidatedIdList :- It is collection of integer that contains ID's.
	 */
	public static void invalidateUrlAtValidationTable(List<Integer> invalidatedIdList){
		//Update Validate Url
		if(invalidatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.deleteUrl(invalidatedIdList);
			//uvt.disconnect();
		}
	}//eof invalidateUrlAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Email</strong>, 
	 * that indicate <b>Email</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateEmailAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.updateEmail(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateEmailAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'null'</b> for <strong>Url</strong> that was validated before, 
	 * now <b>Url</b> is being invalid for that record and its tested by our tester manually.
	 * @param invalidatedIdList :- It is collection of integer that contains ID's.
	 */
	public static void invalidateEmailAtValidationTable(List<Integer> invalidatedIdList){
		//Update Validate Email
		if(invalidatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.deleteEmail(invalidatedIdList);
			//uvt.disconnect();
		}
	}//eof invalidateEmailAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'null'</b> for <strong>Phone</strong> that was validated before, 
	 * now <b>Phone</b> is being invalid for that record and its tested by our tester manually.
	 * @param invalidatedIdList :- It is collection of integer that contains ID's.
	 */
	public static void invalidatePhoneAtValidationTable(List<Integer> invalidatedIdList){
		//Update Validate Phone
		if(invalidatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.deletePhone(invalidatedIdList);
			//uvt.disconnect();
		}
	}//eof invalidatePhoneAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'null'</b> for <strong>Fax</strong> that was validated before, 
	 * now <b>Fax</b> is being invalid for that record and its tested by our tester manually.
	 * @param invalidatedIdList :- It is collection of integer that contains ID's.
	 */
	public static void invalidateFaxAtValidationTable(List<Integer> invalidatedIdList){
		//Update Validate Phone
		if(invalidatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.deleteFax(invalidatedIdList);
			//uvt.disconnect();
		}
	}//eof invalidateFaxAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Fax</strong>, 
	 * that indicate <b>Fax</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateFaxAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
//			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			UpdateValidateTable uvt = connect();
			uvt.updateFax(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateFaxAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Contact Person</strong>, 
	 * that indicate <b>Contact Person</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateContactPersonAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateContactPerson(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateContactPersonAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Title</strong>, 
	 * that indicate <b>Title</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateTitleAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateTitle(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateTitleAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Year In Biz</strong>, 
	 * that indicate <b>Year In Biz</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateYearInBizAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateYearInBiz(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	public static void validateHoursOfOperationAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateHoursOfOperation(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	public static void validateLocationSourceAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateLocationSource(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	
	public static void validateQualtiyScoreAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateQualityScore(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	
	public static void validateGeoDistanceAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateGeoDistance(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	
	public static void validateGeoAccuracyAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateGeoAccuracyCode(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	
	public static void validateAddressVerificationCodeAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Email
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateAddressVerificationCode(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateYearInBizAtValidationTable()
	
	
	/**
	 * This method is used to set flag for validated phones.
	 * @param validatedIdList :- It is list of Integer that containing Id's for validation. 
	 */
	public static void validatePhoneAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Phones
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updatePhone(validatedIdList);
//			//uvt.disconnect();
		}
	}
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>LatLng</strong>, 
	 * that indicate <b>LatLng</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateLatLngAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Geo Boundaries
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateGeo(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateLatLngAtValidationTable()
		
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>EMP_COUNT_MIN</strong>, 
	 * that indicate <b>EMP_COUNT_MIN</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateMinEmpCountAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Min emp count
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = connect();
			uvt.updateMinEmpCount(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateMinEmpCountAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>EMP_COUNT_MAX</strong>, 
	 * that indicate <b>EMP_COUNT_MAX</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateMaxEmpCountAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Max emp count
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = connect();
			uvt.updateMaxEmpCount(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateMaxEmpCountAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>ANNUAL_SALES_VOL_MIN</strong>, 
	 * that indicate <b>ANNUAL_SALES_VOL_MIN</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateMinAnnualSalesAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Min Annual Sales
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = connect();
			uvt.updateMinAnnualSales(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateMinAnnualSalesAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>ANNUAL_SALES_VOL_MAX</strong>, 
	 * that indicate <b>ANNUAL_SALES_VOL_MAX</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateMaxAnnualSalesAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Max Annual Sales
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = connect();
			uvt.updateMaxAnnualSales(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateMaxAnnualSalesAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Zip</strong>, 
	 * that indicate <b>Zip</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList
	 */
	public static void validateZipAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Zip
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateZip(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateZipAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Company Name</strong>, 
	 * that indicate <b>Company Name</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateCompanyNameAtValidationTable(List<Integer> validatedIdList){
		//Update Validate CompanyName
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateCompanyName(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateCompanyNameAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Address</strong>, 
	 * that indicate <b>Address</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateAddressAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Address
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateAddress(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateAddressAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>City</strong>, 
	 * that indicate <b>City</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateCityAtValidationTable(List<Integer> validatedIdList){
		//Update Validate City
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateCity(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateCityAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>State</strong>, 
	 * that indicate <b>State</b> is valid for that record and its tested by our tester manually.
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateStateAtValidationTable(List<Integer> validatedIdList){
		//Update Validate State
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateState(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateStateAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Neighborhood</strong>, 
	 * that indicate <b>Neighborhood</b> is valid for that record and its tested by our tester manually. 
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateNeighborhoodAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Neighborhood
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateNeighborhood(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateNeighborhoodAtValidationTable()
	
	/**
	 * This method is used to set flag <b>'1'</b> for <strong>Sic Sub</strong>, 
	 * that indicate <b>Sic Sub</b> is valid for that record and its tested by our tester manually. 
	 * @param validatedIdList :- It is collection of integer that contains validated ID's.
	 */
	public static void validateSicSubAtValidationTable(List<Integer> validatedIdList){
		//Update Validate Sic Sub
		if(validatedIdList.size() > 1){
			UpdateValidateTable uvt = new UpdateValidateTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			uvt.updateSicSub(validatedIdList);
			//uvt.disconnect();
		}
	}//eof validateSicSubAtValidationTable()
	
}
