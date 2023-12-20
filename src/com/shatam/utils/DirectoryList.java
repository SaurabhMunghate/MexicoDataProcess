package com.shatam.utils;

import java.awt.Container;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;

public abstract class DirectoryList {
	public static final String ALLOW_BLANK = "-";

	protected abstract void extractProcess() throws Exception;
	
	public static final String headerForMexico[]={
			"SRNo=id","INDUSTRY_SECTOR=INDUSTRIA_SECTOR","SPANISH_INDUSTRY_SECTOR=INDUSTRIA_ESPANOLA_SECTOR",
			"MAJOR_SIC_Category=Categoria_Principal_SIC","SUB_SIC_Category=Categoria_Sub_SIC",
			"PRODUCT_DESC=PRODUCTO_DESC","Spanish_product_description=Des_Del_Producto_Espanol",
			"Company_Name=Nombre_De_Empresa","ADDRESS=DIRECCION","NEIGHBORHOOD=BARRIO" ,"CITY=CIUDAD","STATE=ESTADO",
			"ZIP_CODE=CODIGO_POSTAL","PHONE=TELEFONO","FAX=FAX","URL=URL","EMAIL=CORREO_ELECTRONICO",
			"CONTACT_PERSON=PERSONA_DE_CONTACTO","TTTLE=TTTLE","ANNUAL_SALES_VOL=VENTAS_ANUALES_VOL",
			"EMP_COUNT=Número_De_Empleados","YEARS_IN_BIZ=Años_En_Negocios","HOURS_OF_OPERATION","LATITUDE","LONGITUDE","REF_URL","FETCHING_TIME"
			};

	public static final String HEADER[] = {
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.HOURS_OF_OPERATION.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	
	private Map<String,String[]> sicMap = new HashMap<>();
	
	private ArrayList<String> sicSubList = new ArrayList<>();
	private ArrayList<String> companyNameList = new ArrayList<>();
	private ArrayList<String> streetList = new ArrayList<>();
	private ArrayList<String> neighbourhoodList = new ArrayList<>();
	private ArrayList<String> cityList = new ArrayList<>();
	private ArrayList<String> stateList = new ArrayList<>();
	private ArrayList<String> zipList = new ArrayList<>();
	private ArrayList<String> phoneList = new ArrayList<>();
	private ArrayList<String> faxList = new ArrayList<>();
	private ArrayList<String> urlList = new ArrayList<>();
	private ArrayList<String> emailList = new ArrayList<>();
	private ArrayList<String> contactPersonList = new ArrayList<>();
	private ArrayList<String> contactTitleList = new ArrayList<>();
	private ArrayList<String> annualSalesList = new ArrayList<>();
	private ArrayList<String> empCountList = new ArrayList<>();
	private ArrayList<String> yearInBizList = new ArrayList<>();
	private ArrayList<String> latitudeList = new ArrayList<>();
	private ArrayList<String> longitudeList = new ArrayList<>();
	private ArrayList<String> referenceUrlList = new ArrayList<>();
	private ArrayList<String> hoursOfOperationList = new ArrayList<>();
//	private ArrayList<String> referenceUrlList = new ArrayList<>();
//	private ArrayList<String> referenceUrlList = new ArrayList<>();
//	private ArrayList<String> referenceUrlList = new ArrayList<>();
	
	private HashSet<String> dupCheckMap = new HashSet<>();
	private String fileName = null;
	
	public DirectoryList() {
	}
	/**
	 * This constructor is used to set file name.
	 * @param fileName
	 */
	public DirectoryList(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * This method is used to returns current class instance.
	 * @return DirectoryList
	 */
	public DirectoryList getInstance(){
		return this;
	}
	/**
	 * This method is used to add address details.
	 * @param address
	 * @param neighbourhood
	 * @param city
	 * @param state
	 * @param zip
	 */
	public void addAddress(String address, String neighbourhood, String city, String state, String zip) {
		this.streetList.add(trim(U.toTitleCase(address)));
		this.neighbourhoodList.add(U.toTitleCase(trim(neighbourhood)));
		this.cityList.add(U.toTitleCase(trim(city)));
		this.stateList.add(U.toTitleCase(trim(state)));
		this.zipList.add(U.toTitleCase(trim(zip)));
	}

	private void addCompanyDetails(String sicSub, String companyName, String phone, String fax, String url) {
		this.sicSubList.add(trim(sicSub));
		this.companyNameList.add(U.toTitleCase(trim(companyName)));
		this.phoneList.add(trim(phone));
		this.faxList.add(trim(fax));
		this.urlList.add(trim(url));
	}
	/**
	 * This method is used add mexico company details.
	 * @param sicSub
	 * @param companyName
	 * @param phone
	 * @param fax
	 * @param url
	 */
	public void addCompanyDetailsFromMexico(String sicSub, String companyName, String phone, String fax, String url) {
//		U.log(sicSub);
		String[] info = Sic.sicInfo(sicSub);
//		U.log("info : "+info);
			sicMap.put(sicSub, new String[]{info[0], info[1], info[3], info[4], info[5],info[6]});
			addCompanyDetails(sicSub, companyName, phone, fax, url);
		
	}
	public void addCompanyHoursOfOperation(String hoursOfOperation) {
		this.hoursOfOperationList.add(trim(hoursOfOperation));
	}
	/**
	 * This method is used to add canada company details
	 * @param sicSub
	 * @param companyName
	 * @param phone
	 * @param fax
	 * @param url 
	 */
	public void addCompanyDetailsForCanada(String sicSub, String companyName, String phone, String fax, String url) {
		String[] info = Sic.sicInfo(sicSub);
		sicMap.put(sicSub, new String[]{info[0], info[2], info[3], info[4], info[5],info[7]});
		addCompanyDetails(sicSub, companyName, phone, fax, url);
	}
	/**
	 * This method is used to add other's company details.	
	 * @param annualSalesVolume
	 * @param empCount
	 * @param yearsInBiz
	 */
	public void addCompanyOtherDetails(String annualSalesVolume, String empCount, String yearsInBiz){
		this.annualSalesList.add(trim(annualSalesVolume));
		this.empCountList.add(trim(empCount));
		this.yearInBizList.add(trim(yearsInBiz));
	}
	/**
	 * This method is used to add contact person details.
	 * @param contactPerson
	 * @param personTitle
	 * @param email
	 */
	public void addContactPersonDetails(String contactPerson, String personTitle, String email) {
		this.contactPersonList.add(trim(contactPerson));
		this.contactTitleList.add(trim(personTitle));
		this.emailList.add(trim(email));
	}
	/**
	 * This method is used to add boundaries(lat-lng).
	 * @param latitude
	 * @param longitude
	 */
	public void addBoundaries(String latitude, String longitude) {
		this.latitudeList.add(trim(latitude));
		this.longitudeList.add(trim(longitude));
	}
	/**
	 * This method is used to add reference Url.
	 * @param referenceUrl
	 */
	public void addReferenceUrl(String referenceUrl) {
		this.referenceUrlList.add(trim(referenceUrl));
	}
	/**
	 * This method is used to check if inserted record is duplicated in csv or not.  
	 * @param sicSub
	 * @param companyName
	 * @param address
	 * @param city
	 * @param state
	 * @param contactPerson
	 * @return {@code True} : If record is duplicated, {@code False} : Record is unique.
	 */
	public boolean isDuplicateRecord(String sicSub, String companyName, 
			String address, String city, String state, String contactPerson){
		
		String uniqueKey = trim(sicSub) + "_" + trim(companyName) + "_" + trim(address) + "_" +
				trim(city) + "_" + trim(state) + "_" + trim(contactPerson);
		
		if(!dupCheckMap.add(uniqueKey.toLowerCase())){
			return true;
		}
		return false;
		
/*		int sicSubSize = sicSubList.size();
		int companyNameSize = companyNameList.size();
		int streetListSize = streetList.size();
		int cityListSize = cityList.size();
		int stateListSize = stateList.size();
		int contactTitleListSize = contactTitleList.size();
		
		if( sicSubSize == companyNameSize && companyNameSize == streetListSize && streetListSize == cityListSize 
				&& cityListSize == stateListSize && stateListSize == contactTitleListSize && sicSubSize > 0){
			
			String uniqueKey = sicSubList.get(sicSubSize) + "_" +
					companyNameList.get(companyNameSize) +
					streetList.get(streetListSize) + "_" +
					cityList.get(cityListSize) + "_" +
					stateList.get(stateListSize) + "_" +
					contactTitleList.get(contactTitleListSize);
			
			if(!dupCheckMap.add(uniqueKey.toLowerCase())){
				return true;
			}
			return false;
		}else{
			throw new Exception("Values are missing. Check, if values are specified to Sic_Sub, Company_Name, Street, City, State, Contact_Person.");
		}*/
	}
	
	public void printAll() throws Exception {
		if(fileName == null || fileName.isEmpty()) throw new FileNotFoundException("File name is not defined. Check, whether file name is empty or null.");
		printAll(fileName);
	}
	
	public void printAll(String fileName) throws Exception {
		
		StringWriter sw = new StringWriter();
		CSVWriter writer = new CSVWriter(sw, ',');
/*		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
*/		writer.writeNext(HEADER);


		U.log("# ---- Total = " + sicSubList.size() + " ----------");
		for (int i = 0; i < sicSubList.size(); i++) {

			String[] entries = new String[HEADER.length];
			String[] data = sicMap.get(get(sicSubList, i));
			int j = 0;
			entries[j++] = "" + i;
			entries[j++] = trim(data[0]);
			entries[j++] = trim(data[1]);
			entries[j++] = trim(data[2]);
			entries[j++] = trim(data[3]);
			entries[j++] = trim(data[4]);
			entries[j++] = trim(data[5]);
			entries[j++] = get(companyNameList, i);
			entries[j++] = get(streetList, i);
			entries[j++] = get(neighbourhoodList, i);
			entries[j++] = get(cityList, i);
			entries[j++] = get(stateList, i);
			entries[j++] = get(zipList, i);
			entries[j++] = get(phoneList, i);
			entries[j++] = get(faxList, i);
			entries[j++] = get(urlList, i);
			entries[j++] = get(emailList, i);
			entries[j++] = get(contactPersonList, i);
			entries[j++] = get(contactTitleList, i);
			entries[j++] = get(annualSalesList, i);
			entries[j++] = get(empCountList, i);
			entries[j++] = get(yearInBizList, i);
//			entries[j++] = get(hoursOfOperationList, i);
			entries[j++] = get(latitudeList, i);
			entries[j++] = get(longitudeList, i);
			entries[j++] = get(referenceUrlList, i);
			entries[j++] = U.getTodayDate();
			writer.writeNext(entries);
		}//eof for
		writer.close();
//		U.log(sw.toString());
		//if(!fileName.contains("/")) fileName = Path.CACHE_PATH_FOR_EXTRACTION + fileName;
		fileName = Path.CACHE_PATH_FOR_EXTRACTION + fileName;
		FileUtil.writeAllText(fileName, sw.toString());
		U.log("File is created at location ::"+fileName);
	}

	private String trim(String s) {
		if(s == null)s = "";
		s = s.replaceAll("\\s+", " ").trim();
		return s;
	}
	
	private String get(ArrayList<String> a, int i) {
		String v = a.get(i);
		
		if(v == null) v = "";
		if (v.equals(ALLOW_BLANK)) v = "";
		
		return v;
	}

}
