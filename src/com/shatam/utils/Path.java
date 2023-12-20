package com.shatam.utils;

import java.io.File;

public class Path {
	private Path(){
	}
	
	public static final String PROJECT_NAME = "MexicoCache"; //"MexicoCache"; //"mexicoProject"; //"GeoCode";
	
	public static final String PROJECT_NAME_FOR_EXTRACTION = "GeoCode";
	
	
	private static final String HOME = System.getProperty("user.home");

	public static final String PROJECT_PATH_FOR_EXTRACTION = HOME +File.separator+ PROJECT_NAME_FOR_EXTRACTION + File.separator;

	public static final String CACHE_PATH_FOR_EXTRACTION = HOME +File.separator+ PROJECT_NAME +File.separator +"Cache"+ File.separator;

	/**
	 * CACHE_PATH = /home/glady/MexicoCache/
	 */
	public static final String CACHE_PATH = HOME +File.separator+ PROJECT_NAME +File.separator;// +"Cache"+ File.separator;
	
	/**
	 *  DB_CACHE = /home/glady/mexicoProject/
	 */
	public static final String DB_CACHE = HOME + "/mexicoProject/";
	
	public static final String WRONG_RECORD_CACHE = HOME + "Tequila/wrong_record/";
	
	public static final String WRONG_COMPOSITE_DATA_CACHE = HOME + "Tequila/";
	
	/**
	 * This is path of serialise file of composite key<br>
	 * Value is <br> '/home/glady/MexicoCache/serfiles/'
	 */
	public static final String SER_RESOURCE_PATH = CACHE_PATH +"serfiles"+ File.separator;  ///home/glady/MexicoCache/serfiles
	
	/**
	 * This is file path of serialise file of composite key<br>
	 *  Value is <br> '/home/glady/MexicoCache/serfiles/uniqueKeyTequila.ser'
	 */
	public static final String COMPOSITE_KEY_TEQUILA_SER = SER_RESOURCE_PATH + "uniqueKeyTequila.ser";
	
	/**
	 * This is path to tequila database<br>
	 * Value is<br> '/home/glady/MexicoCache/database/'
	 */
	public static final String TEQUILA_DB_PATH = "/home/shatam-100/CODE_Repository/Maxico/tequila.db_latest" + File.separator;
	
	/**
	 * This is path of files that failed at while inserting new data at 'Tequila' db. <br>
	 * Value is<br> '/home/glady/MexicoCache/database/FailedData/'
	 */
	public static final String FAILED_DB_PATH = TEQUILA_DB_PATH + "FailedData" +File.separator; 
	
	
	public static final String CORRECTORS_DIR = TEQUILA_DB_PATH+"Correctors/";
	
	public static final String SOURCE_DIR = CACHE_PATH + "source" + File.separator;
	
	public static final String ADDRESS_SOURCE_DIR = SOURCE_DIR + "Mexico" + File.separator;
	
	
	public static final String RESOURCES = System.getProperty("user.dir") + File.separator +"resources" + File.separator;
	
	public static final String RESOURCES_SER = RESOURCES +"MexicoAddress" + File.separator;
	
	public static final String TEQUILA_MAIN_DB_NAME = "tequila.db";
	
	
	/**
	 * DB_PATH = /home/glady/GeoCode/database/
	 */
	
	public static final String DB_PATH = U.getProjectPath() + "database" + File.separator;
	
	/**
	 * GOOGLE_KEY_CACHE_PATH = /home/glady/GeoCode/Google_Cache/
	 */
	public static final String GOOGLE_KEY_CACHE_PATH = U.getProjectPath() +"Google_Cache"+ File.separator;
	
	public static final String DELETED_RECORD_PATH = TEQUILA_DB_PATH + "Deleted_Record" + File.separator;
	
}
