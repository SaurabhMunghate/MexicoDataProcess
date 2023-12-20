package com.shatam.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.List;

import com.opencsv.CSVReader;

public final class SawanUtil {

	private final static String USER_HOME= System.getProperty("user.home")+File.separator;
	
	public static final void toWrite(String html){
		Writer writer = null;
		try {
		    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(USER_HOME+"filename.txt",true), "utf-8"));
		    writer.write(html);
		    writer.flush();
		    writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * To get a general File's size (works for directory and non-directory)
	 * @param file
	 * @return
	 */
	public static long getSize(File file) {
	    long size;
	    if (file.isDirectory()) {
	        size = 0;
	        for (File child : file.listFiles()) {
	            size += getSize(child);
	        }
	    } else {
	        size = file.length();
	    }
	    return size;
	}
	/**
	 * To get a user-readable String from the long returned
	 * @param size
	 * @return
	 */
	public static final String getReadableSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
	            + " " + units[digitGroups];
	}
	/**
	 * To get the size of directory/file in readable format. 
	 * @param file
	 * @return
	 */
	public static final String getReadableSize(File file) {
		long size = getSize(file);
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups))
	            + " " + units[digitGroups];
	}
	
	
	public static List<String[]> loadCsvWithoutHeader(String fileName){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName),',','"',1);){
			readLines = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readLines;
	}
	
	public static List<String[]> loadCsvWithHeader(String fileName){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName),',');){
			readLines = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return readLines;
	}
}
