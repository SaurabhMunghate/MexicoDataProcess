package com.tequila.geocode;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.shatam.maps.BingMap;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class Demo {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		String [] latLong = {"19.535498","-99.192625"};
/*		"19.535498","-99.192625"
 * 			"19.589595","-99.020787"
		"20.969969","-89.621652"
		"19.339814","-99.160756"
*/
/*		BingMap map = new BingMap();
		String [] address =map.getAddress(latLong);
		
		U.log("add :::"+Arrays.toString(address));
		String fileName = Path.CACHE_PATH + "Tequila_Without_LatLng_dataset_1.csv";
		List<String> lines = Files.readAllLines(Paths.get(fileName));
		U.log("Size ::"+lines.size());
*/		
		
/*		String html = U.getHTML("https://www.manta.com/ic/mxm5ddg/ca/1242752-ontario-inc");
		U.log(html);
		String url = "https://www.manta.com/ic/mxm5ddg/ca/1242752-ontario-inc";
		
		System.setProperty("webdriver.chrome.driver","/home/glady/chromedriver");
		WebDriver driver =  new ChromeDriver();
		driver.get(url);
		Thread.sleep(6000);
		String value = driver.findElement(By.xpath("//*[@id=\"info\"]/div/div/div[2]/table/tbody/tr[3]/td")).getText();
		U.log(value);
		
		driver.close();
		try{
			driver.quit();
		}catch(SessionNotCreatedException e){
			
		}
*/
		int count = 0;
		File[] files = new File("/home/glady/GeoCode/Cache/bbb.org/").listFiles();
		for(File file : files){
//			U.log(file.getName());
			if(file.getName().contains(U.getCacheFileName("https://www.bbb.org/api/search?find_text=BB+Guns&find_type=Category&page=1&pageSize=10000&country=MEX"))){ //"httpsbbborgmexicobusinessreviews")){ //find_typeCategorypage1pageSize10000countryMEX.txt")){
				count++;
				file.delete();
			}
		}
		U.log("Total found :"+count);
				
	}

}
