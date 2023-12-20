package com.shatam.conversion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

import com.shatam.utils.U;

public final class Sic {
	public static void main(String[] args) {
		//U.log(Arrays.toString(sicInfo("3273")));
		checkDifferenceInSicSer();
	}
	private Sic(){}
	
	private static final HashMap<String, String[]> sicMap = U.deserializedSicData("/home/shatam-100/CODE_Repository/Maxico/MexicoDataProcess/resources/updateSicCodeData.ser");
	private static final HashMap<String, String[]> oldsicMap = U.deserializedSicData("/home/shatam-100/CODE_Repository/Maxico/MexicoDataProcess/resources/updateSicCodeData.ser");
	/**
	 * This method is used to extract SIC code with its information by passing Sub-SIC code. This method is only for Tequila.
	 * @param subSIC :- Pass sub SIC code.
	 * @return
	 * It return Array of String with size of 8.<br>
	 * data[0] = 'Industry Sector'<br>
	 * data[1] = 'Spanish_Industry_Sector'<br>
	 * data[3] = 'Major SIC Category / Principales Categoría SIC'<br>
	 * data[4] = 'SubSicCategory/CategoríaDelSic'<br>
	 * data[5] = 'Product Description'<br>
	 * data[6] = 'Spanish_Product_Description'<br>
	 */
	
	public final static String[] sicInfo(String subSIC) {		
		if(subSIC.isEmpty()) return null;
		if(subSIC.trim().length() != 4) return null;
//		U.log("subSIC : "+subSIC);
		
		String data[] = sicMap.get(subSIC);
//		U.log("data : "+data);
		if(data != null){
			data[5] = data[5].replace("’", "'");
		}
		return data;
	}
	
	public static void checkDifferenceInSicSer() {
		Set<String>keys=sicMap.keySet();
		U.log((sicMap.size()==oldsicMap.size()?"Both Are Equal":"Size Not matched"));
		
		for (String key : keys) {
			String[]newVal=sicMap.get(key);
			String[]oldVal=oldsicMap.get(key);
			
			for (int i = 0; i < oldVal.length; i++) {
				if (!oldVal[i].toLowerCase().equals(newVal[i].toLowerCase())) {
					U.log(newVal[4]+"\n\t\tNOT-EQUAL\n"+oldVal[4] );
					break;
				}
			}
			if (newVal[4].equals("2394")) {
				U.log(Arrays.toString(newVal)+"\n"+Arrays.toString(oldVal));
			}
		}
		
	}
}
