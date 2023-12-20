package com.shatam.maps;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.shatam.utils.U;

public class MapTest {

	private static final String MAP_API_DIR = "/home/glady/GeoCode/Cache/maps.googleapis.com/";
	
	private static final String DIS_METRIX_API_DIR = "/home/glady/GeoCode/Cache/splite/maps.googleapis.com/";
	
	public static void main(String[] args) {
		deleteGoogleApiFiles(MAP_API_DIR);
//		copyDistanceMetrixFromSrcToDest();
	}
	
	public static void copyDistanceMetrixFromSrcToDest(){
		File destFile = new File(DIS_METRIX_API_DIR);		
		if(!destFile.exists()) destFile.mkdirs();
		
		File[] files = new File(MAP_API_DIR).listFiles();
		int count = 0;
		for(File file :files){
			if (file.isFile()) {
				if(file.getName().contains("distancematrix")){
					U.log(file.getName());
					try {
						FileUtils.copyFile(file, new File(DIS_METRIX_API_DIR+file.getName()));
						file.delete();
		   				count++;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		U.log("Total Files :"+files.length);
		U.log("Total Moves count :"+count);
		
	}
	
	/**
	 * This method is used to delete the files which are either 0Kb or less than 400bytes from google api folder.
	 */
	public static void deleteGoogleApiFiles(String dirPath){
		File[] files = new File(dirPath).listFiles();
		int count = 0;
		for(File file :files){
			if (file.isFile()) {
/*				if(file.getName().contains("httpsmapsgoogleapiscommapsapidistancematrixjsonunitsimperialoriginsPonceDeLeonNo2200ChihuahuaChihuahuadestinations286195779106064622.txt"))
					U.log(file.getName());
*/	    		if(file.length() < 360){
//					U.log(file.getName());

/*	    			if(file.delete()){
	    				U.log("delete::"+file.getName()+" \t(bytes) :" + file.length());
	    				count++;
	    			}*/
	    		}
			}
		}
		U.log("Total Files :"+files.length);
		U.log("Deleted Count :"+count);
	}//eof deleteGoogleApiFiles()
	
}//eof MapTest
