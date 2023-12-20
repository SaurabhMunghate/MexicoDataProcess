package com.chinmay.test;

import java.io.IOException;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractUrls {
	public static void main(String[] args) throws IOException {
		String txtData=FileUtil.readAllText("/home/chinmay/Downloads/temp.txt");
//		U.log(txtData);
		String urls[]=U.getValues(txtData, "\"id\":\"", "\"");
		int i=0;
		for (String url : urls) {
			U.log(url);
		}
	}
}
