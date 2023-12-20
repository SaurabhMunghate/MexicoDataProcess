package com.tequila.database.corrector;

import java.util.Arrays;

import com.shatam.scrapper.CSVCorrectorInValidFormat;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class TequilaCorrector{


	/**
	 * This method is used to compare phone no. of original record and duplicated record.<br>
	 * If the phone no. of duplicate record is not contain at original record then it will add to phone no. of original record.
	 * @param oldPhoneNo
	 * : This variable is contain the phone no. of original record.
	 * @param newPhoneNo
	 * : This variable is contain the phone no. of duplicate record.
	 */
	public static final String phoneAndFaxCompare(String oldPhoneNo, String newPhoneNo){
		if(oldPhoneNo == null && newPhoneNo == null)return null;
		if(oldPhoneNo == null && newPhoneNo != null){
			if(!newPhoneNo.trim().isEmpty())return newPhoneNo;
		}
		if(oldPhoneNo != null && newPhoneNo == null){
			if(!oldPhoneNo.trim().isEmpty())return oldPhoneNo;
		}
		
/*		if(oldPhoneNo.trim().isEmpty() && newPhoneNo.trim().isEmpty()){
			return oldPhoneNo.trim();
		}*/
		
		String tempOld = oldPhoneNo.replace("-", "");
		String temNew = newPhoneNo.replace("-", "");
/*		if(tempOld.isEmpty() && !temNew.isEmpty()){
			return newPhoneNo;
		}*/
		
		if(!tempOld.isEmpty() && !temNew.isEmpty()){
			if((!tempOld.contains(";") || tempOld.contains(";")) && !temNew.contains(";")){
				if(!tempOld.contains(temNew)){
					return TextFormat.getUniqueNumber(oldPhoneNo+";"+newPhoneNo);
				}
			}
			if(!tempOld.contains(";") && temNew.contains(";")){
				String [] newPhoneVals = newPhoneNo.split(";");
				
				String [] newVals = temNew.split(";");
				for(int i = 0; i < newVals.length; i++){
					if(!tempOld.contains(newVals[i])){
						oldPhoneNo = oldPhoneNo+";"+newPhoneVals[i];
					}
				}
			}
			return TextFormat.getUniqueNumber(oldPhoneNo);
		}
		return TextFormat.getUniqueNumber(oldPhoneNo);
	}

	/**
	 * This method is used to compare if old url is empty and new url is not then it will return new url.
	 * @param oldUrl
	 * @param newUrl
	 * @return
	 */
	public static final String urlCompare(String oldUrl, String newUrl){
		if(oldUrl == null && newUrl == null)return null;
		if(oldUrl == null && newUrl != null){
			if(!newUrl.isEmpty())return newUrl.trim();
		}
		
/*		if(oldUrl.trim().isEmpty() && newUrl.trim().isEmpty()){
			return oldUrl.trim();
		}
		if(oldUrl.trim().isEmpty() && !newUrl.trim().isEmpty()){
			return newUrl.trim();
		}
*/		
		/*if(!oldUrl.trim().isEmpty() && !newUrl.trim().isEmpty()){
			if(!oldUrl.trim().contains(newUrl.trim())){
				return newUrl.trim();
			}
		}*/
		return oldUrl.trim();
	}
	/**
	 * This method is used to compare if old email is empty and new email is not then it will return new email.
	 * @param oldEmail
	 * @param newEmail
	 * @return
	 */
	public static final String emailCompare(String oldEmail, String newEmail){

		if(oldEmail == null && newEmail == null){
			return null;
		}
		if(oldEmail == null && newEmail != null){
			if(!newEmail.isEmpty())return newEmail;
		}
		if(oldEmail != null && newEmail == null){
			if(!oldEmail.isEmpty())return oldEmail;
		}
		
/*		if(oldEmail.trim().isEmpty() && newEmail.trim().isEmpty()){
			return oldEmail.trim();
		}
		if(oldEmail.trim().isEmpty() && !newEmail.trim().isEmpty()){
			return newEmail.trim();
		}*/
		
		if(!oldEmail.trim().isEmpty() && !newEmail.trim().isEmpty()){
			if(oldEmail.trim().contains(",")|| oldEmail.trim().contains(";")){
				if(!newEmail.trim().contains(";") || !newEmail.trim().contains(",")){
					if(!oldEmail.trim().contains(newEmail.trim())){
						oldEmail = oldEmail.trim() +";"+newEmail;
					}
				}
				if(newEmail.trim().contains(";") || newEmail.trim().contains(",")){
					String tempNewEmails[] = null;
					if(newEmail.contains(".com,") || newEmail.contains(".mx,") || newEmail.contains(".net,")){
						tempNewEmails = newEmail.split(",");
					}else if(newEmail.contains(".com;") || newEmail.contains(".mx;") || newEmail.contains(".net;") || !newEmail.endsWith(";")){
						tempNewEmails = newEmail.split(";");
					}
					for(int i = 0;  i < tempNewEmails.length; i++){
						if(!oldEmail.trim().contains(tempNewEmails[i])){
							oldEmail = oldEmail.trim() +";"+tempNewEmails[i];
						}						
					}
				}
			}else if(!oldEmail.trim().contains(",")|| !oldEmail.trim().contains(";")){
				if(!newEmail.trim().contains(";") || !newEmail.trim().contains(",")){
					if(!oldEmail.trim().contains(newEmail.trim())){
						oldEmail = oldEmail.trim() +";"+newEmail;
					}
				}
				if(newEmail.trim().contains(";") || newEmail.trim().contains(",")){
					String tempNewEmails[] = null;
					if(newEmail.contains(".com,") || newEmail.contains(".mx,") || newEmail.contains(".net,")){
						tempNewEmails = newEmail.split(",");
					}else if(newEmail.contains(".com;") || newEmail.contains(".mx;") || newEmail.contains(".net;") || !newEmail.endsWith(";")){
						tempNewEmails = newEmail.split(";");
					}
					for(int i = 0;  i < tempNewEmails.length; i++){
						if(!oldEmail.trim().contains(tempNewEmails[i])){
							oldEmail = oldEmail.trim() +";"+tempNewEmails[i];
						}						
					}
				}
			}//else if
		}
		return TextFormat.getUniqueEmail(oldEmail.trim());
//		return oldEmail.trim();
	}
	
	public static final String compareAnnualSales(String oldAnnual, String newAnnual){
		if(oldAnnual == null && (newAnnual == null || newAnnual.isEmpty())){
			return null;
		}
		if(oldAnnual == null && newAnnual != null){
			if(!newAnnual.isEmpty())return newAnnual;
		}
		if(oldAnnual != null && newAnnual != null){
			if(!oldAnnual.isEmpty() && !newAnnual.isEmpty()){
				long oldVal = Long.parseLong(oldAnnual);
				long newVal = Long.parseLong(newAnnual);
				if(oldVal < newVal){
					return newAnnual;
				}
			}
		}
		return oldAnnual;
	}
	
	public static String compareEmployeeCount(String oldEmpCount, String newEmpCount){
		if(oldEmpCount == null && (newEmpCount == null || newEmpCount.isEmpty())){
			return null;
		}
		if(oldEmpCount == null && newEmpCount != null){
			if(!newEmpCount.isEmpty())return newEmpCount;
		}
		if(oldEmpCount != null && newEmpCount != null){
			if(!oldEmpCount.isEmpty() && !newEmpCount.isEmpty()){
				long oldVal = Long.parseLong(oldEmpCount);
				long newVal = Long.parseLong(newEmpCount);
				if(oldVal < newVal){
					return newEmpCount;
				}
			}
		}
		return oldEmpCount;
	}
	public static final String compareYearInBiz(String oldDate, String newDate){
		if(oldDate == null && newDate == null){
			return null;
		}
		if(oldDate == null && newDate != null){
			if(!newDate.isEmpty())return newDate;
		}
		return oldDate;
	}
	
	public static final String compareLatLong(String oldVal, String newVal){
		if(oldVal == null && newVal == null){
			return null;
		}
		if(oldVal == null && newVal != null){
			if(!newVal.isEmpty())return newVal;
		}
		return oldVal;
	}
	
	public static final String compareNeighbourhood(String oldVal, String newVal){
		if(oldVal == null && newVal == null){
			return null;
		}
		if(oldVal == null && newVal != null){
			if(!newVal.isEmpty())return newVal;
		}
		
/*		if(oldVal.isEmpty() && newVal.isEmpty()){
			return oldVal.trim();
		}
		if(oldVal.isEmpty() && !newVal.isEmpty()){
			return newVal.trim();
		}
*/		return oldVal;
	}
	
	public static final String compareDesignation(String oldVal, String newVal){
		if(oldVal == null && newVal == null){
			return null;
		}
		if(oldVal == null && newVal != null){
			if(!newVal.isEmpty())return newVal;
		}
		
/*		if(oldVal.isEmpty() && newVal.isEmpty()){
			return oldVal.trim();
		}
		if(oldVal.isEmpty() && !newVal.isEmpty()){
			return newVal.trim();
		}
*/		return oldVal;
	}
	
	
	/**
	 * This method is used to compare between correct data record with incorrect data record.<br>
	 * For comparison field are :<br><p>NEIGHBOURHOOD, PHONE, FAX, URL, EMAIL, TITLE, ANNAULES SALE MIN & MAX, EMPLOYEE COUNT MIN & MAX, YEAR IN BIZ, LATITUDE, LONGITUDE, etc.</p>
	 * @param correctData :- Is array of string containing an entire row details for correct ID data
	 * @param incorrectData :- Is array of string containing an entire row details for uncorrected ID data.
	 * @return :- returns the array of string for modified correctData. 
	 */
	public static String[] validateDataAndPerformOperation(String[] correctData, String[] incorrectData) {

		correctData[9] = TequilaCorrector.compareNeighbourhood(correctData[9], incorrectData[9]); 
		
		correctData[13] = TequilaCorrector.phoneAndFaxCompare(correctData[13], incorrectData[13]);
		if(correctData[13] != null) correctData[13] = TextFormat.getUniqueNumber(correctData[13]);
		
		correctData[14] = TequilaCorrector.phoneAndFaxCompare(correctData[14], incorrectData[14]);
		if(correctData[14] != null) correctData[14] = TextFormat.getUniqueNumber(correctData[14]);
		
		correctData[15] = TequilaCorrector.urlCompare(correctData[15], incorrectData[15]);		
		correctData[16] = TequilaCorrector.emailCompare(correctData[16], incorrectData[16]);
		
		correctData[18] = TequilaCorrector.compareDesignation(correctData[18], incorrectData[18]);
		
		correctData[19] = TequilaCorrector.compareAnnualSales(correctData[19], incorrectData[19]);
		correctData[20] = TequilaCorrector.compareAnnualSales(correctData[20], incorrectData[20]);
		
		correctData[21] = TequilaCorrector.compareEmployeeCount(correctData[21], incorrectData[21]);
		correctData[22] = TequilaCorrector.compareEmployeeCount(correctData[22], incorrectData[22]);
		
		correctData[23] = TequilaCorrector.compareYearInBiz(correctData[23], incorrectData[23]);
		
		correctData[24] = TequilaCorrector.compareLatLong(correctData[24], incorrectData[24]);
		correctData[25] = TequilaCorrector.compareLatLong(correctData[25], incorrectData[25]);

		if(correctData[19] != null && correctData[20] != null){
			//U.log(correctData[19]+"\t"+correctData[20]);
			long min = Long.parseLong(correctData[19].trim());
			long max = Long.parseLong(correctData[20].trim());
			if(min > max){
				String temp = correctData[19];
				correctData[19] = correctData[20];
				correctData[20] = temp;
			}else if (min == max){
				correctData[20] = null;
			}	
		}
		if(correctData[21] != null && correctData[22] != null){
			int min = Integer.parseInt(correctData[21].trim());
			int max = Integer.parseInt(correctData[22].trim());
			if(min > max){
				String temp = correctData[21];
				correctData[21] = correctData[22];
				correctData[22] = temp;
			}else if (min == max){
				correctData[22] = null;
			}	
		}
		return correctData;
	}
}
