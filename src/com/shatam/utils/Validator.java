package com.shatam.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shatam.translate.TranslateEnglish;

public class Validator {
	private Validator(){}
	
	/**
	 * This method is used to validate industry sector for english as well as spanish linguistic format.
	 * @param type
	 * @param data
	 * @throws Exception 
	 */
	public final static void industrySectorEnglishAndSpanish(Field type, String data) throws Exception{
		switch (type) {
			case INDUSTRY_SECTOR:
				validateBasicCases(type.toString(), data, 8, 200);
				break;
			case SPANISH_INDUSTRY_SECTOR:
				validateBasicCases(type.toString(), data, 7, 200);
				break;
			default: throw new Exception(type+" is not evaluated in this method");
//				break;
		}
	}
	static List<String[]>  readLines = null;
	static Map<String, String> loadStateMap(){
		Map<String, String> correctStateMap = new HashMap<>();
		Set<String> stateSet = new HashSet<String>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			if (lines[11].trim().length()<3) {
				U.log(Arrays.toString(lines));
				U.log(lines[0]+" ->"+lines[11]);
				
			}
			stateSet.add(lines[11].trim());  //state
		}
		
		for(String state : stateSet){
			U.log(state);
			String correctFormat = U.matchState(state);
			if(correctFormat.equals("-")){
				throw new IllegalArgumentException("State name is not found for "+state);
			}
			U.log(state+"  "+correctFormat);
			correctStateMap.put(state, correctFormat);
		}
		return correctStateMap;
	}
	
	public static HashMap<String , String> zipCityMap=new HashMap<>();
	public final static void _loadPostalFile() {
		String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
		List<String[]>postalFileData=U.readCsvFile(postalCodeFileName);
		for (String[] postalfile : postalFileData) {
			if (postalfile[0].contains("State=Estados"))
				continue;
			String city=postalfile[5];
			if (U.isEmpty(city)||postalfile[0].contains("Mexico City")) {
				city=postalfile[4];
			}
//			U.log(Arrays.toString(postalfile));
//			if (postalfile[3].contains("1160")) {
//				U.log(postalfile[3]+""+city+""+postalfile[0]);
//			}
			
			zipCityMap.put(postalfile[3], TranslateEnglish.convertToEnglish(city)+";;"+U.matchState(postalfile[0]));
			//String correctFormat = U.matchState(state);
			
		}
	}
	public final static void validateCityStateZip(String city, String state, String zip) {
		if(!U.isEmpty(city)&&!U.isEmpty(state)&&!U.isEmpty(zip)) {
			int flag=0;
			if (!zipCityMap.containsKey(zip)) {
				System.err.println("Zip Not Found In Map");
				flag=1;
			}else {
				String cityState[]=zipCityMap.get(zip).split(";;");
				if (!cityState[0].equals(city)) {
					System.err.println("CITY Zip Match Not found");
					flag=1;
				}else if (!cityState[1].equals(state)){
					System.err.println("State Zip Match Not found");
					flag=1;
				}
			}
			if (flag==0) {
				System.out.println(city+" "+state+" "+" "+zip+"----MATCHED");
			}
			
		}else
			System.err.println("CITY STATE ZIP is blank");
	}
	/**
	 * This method is used to validate product description for english as well as spanish linguistic format.
	 * @param type
	 * @param data
	 * @throws Exception
	 */
	public final static void productDescEnglishAndSpanish(Field type, String data) throws Exception{
		switch (type) {
			case PRODUCT_DESC:
				validateBasicCases(type.toString(), data, 4, 200);
				break;
			case SPANISH_PRODUCT_DESC:
				validateBasicCases(type.toString(), data, 3, 200);
				break;
			default: throw new Exception(type+" is not evaluated in this method");
//				break;
		}
	}
	/**
	 * This method is used to validate company name.
	 * @param type
	 * @param data
	 * @throws Exception
	 */
	public final static void companyName(Field type, String data) throws Exception{
		switch (type) {
			case COMPANY_NAME:
				validateBasicCases(type.toString(), data, 2, 200);
				break;
			default: throw new Exception(type+" is not evaluated in this method");
		}
	}
	
	public final static void addressAndNeighbourhood(Field type, String data) throws Exception{
		switch (type) {
			case ADDRESS:
				validateBasicCases(type.toString(), data, 4, 200);
				break;
			case NEIGHBORHOOD:
				validateBasicCases(type.toString(), data, 2, 200);
				break;
			default: throw new Exception(type+" is not evaluated in this method");
//				break;
		}
	}
	
	public final static void city(Field type, String data) throws Exception{
		switch (type) {
			case CITY:
				validateBasicCases(type.toString(), data, 4, 75);
				break;
			default: throw new Exception(type+" is not evaluated in this method");
		}
	}

	public final static void state(Field type, String data) throws Exception{
		if(data.trim().isEmpty()) throw new Exception(type+" must not be empty");
		if(!MexicoStateList.STATES.contains(data.trim())) throw new Exception("\""+data+"\" "+type+" is not match with given state list.\nIt may be in either incorrect format or given data is not "+type);
		validateBasicCases(type.toString(), data, 6, 20);
	}
	
	public final static void zip(Field type, String data) throws Exception{
		if(!data.trim().isEmpty()){
			if(!U.matches(data, "\\d{4,5}")){
				throw new Exception(type +" \""+ data +"\" is not numeric.");
			}
		}
		validateBasicCases(type.toString(), data, 4, 5);
	}

	public final static void phoneAndFax(Field type, String data) throws Exception{
		if(!data.trim().isEmpty()){
			if(data.contains("Y") || data.contains("y") || data.contains("al") || data.contains("Al") || data.contains("AL")){
				throw new Exception(type +" \""+data+"\" does not contain char/word such as Y,AL etc. except for 'Ext.'");
			}
		}
		validateBasicCases(type.toString(), data, 12, 200);
	}
	
	public final static void email(Field type, String data) throws Exception{
		validateBasicCases(type.toString(), data, 10, 200);	
	}

	public final static void contactPerson(Field type, String data) throws Exception{
		validateBasicCases(type.toString(), data, 3, 100);
	}
	
	public final static void title(Field type, String data) throws Exception{
		validateBasicCases(type.toString(), data, 3, 80);
	}
	public static void latlon(String lat,String lon) throws Exception {
		String latRegex="^\\d{2,3}\\.\\d+$";
		String lonRegex="^-\\d{2,3}\\.\\d+$";
		if(Util.match(lat, latRegex)==null) {
			throw new Exception("Wrong lat "+lat);
		}
		if(Util.match(lon, lonRegex)==null) {
			throw new Exception("Wrong lon " + lon);
		}
	}
	
    public static void url(Field type, String commUrl) throws Exception {
    	if (commUrl.indexOf("//") > 7)
    		throw new Exception("Double slash not allowed in a " + type + " " + commUrl);
    	else if (commUrl.endsWith("\\"))
    		throw new Exception(type + " cannot end with a slash (\\) " + commUrl);
	
	   validateBasicCases(type.toString(), commUrl, 6, 200);	
    }//validateUrl


    private static void validateBasicCases(String type, String data, int minLength, int maxLength) throws Exception {

    	if (data == null)
    		throw new Exception(type + " cannot be NULL: " + data);

/*    	if (data.matches("<(img|p|br|div|span|a|body|html|head|script)"))
    		throw new Exception(type + " cannot contain HTML " + data);
    	else if (data.contains("<") && data.contains(">"))
    		throw new Exception(type + " cannot contain HTML " + data);*/

    	if (!data.trim().isEmpty() || data.trim().length() >1) {
    		if (data.length() < minLength || data.length() > maxLength) {
    			
    			throw new Exception(type + " length must be between " + minLength + " and " + maxLength + "\n" + data + "\nYour value length=" + data.length());
    		}
    	}
    }//validateBasicCases
}
