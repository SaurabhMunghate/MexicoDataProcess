package com.chinmay.test;

import java.io.File;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ConvertTextFileToDB {
	static final String folderPath="/home/chinmay/Cache/maps.googleapis.com";
	public static void main(String[] args) {
		try {
			File folder=new File(folderPath);
			for (File fileName : folder.listFiles()) {
				U.log(FileUtil.readAllText(fileName.toString()));
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}	
