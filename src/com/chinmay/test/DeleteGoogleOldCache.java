package com.chinmay.test;

import java.io.File;

import com.shatam.utils.U;

public class DeleteGoogleOldCache {
	public static void main(String[] args) {
		String folderPath="/home/chinmay/MexicoCache/Cache/google.com";
		File f = new File(folderPath);
		U.log(f.list().length);
//		for (String fileName : f.list()) {
//			U.log(fileName);
//		}
	}
}
