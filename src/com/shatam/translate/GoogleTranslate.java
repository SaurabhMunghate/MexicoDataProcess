package com.shatam.translate;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.shatam.utils.U;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


public class GoogleTranslate {
	private static final String FILE_NAME = "/home/glady/MexicoCache/Tequila_DATA/translateData/spanishToEnglish.csv";
	
	
	static final String googleUrl = "https://translate.google.com/";
	
	public static void main(String[] args) throws InterruptedException {
		
//		String str = "Preparación Y Envasado De Té";
		loadReadFile();
		findTranslateData();

		
	}
	
	static void findTranslateData() throws InterruptedException{
		System.setProperty("webdriver.chrome.driver", "/home/glady/chromedriver");
		WebDriver driver = new ChromeDriver();
		driver.get(googleUrl);
		
		driver.findElement(By.xpath("//*[@id=\"sugg-item-es\"]")).click();
		
		String[] lines = null;
		int x = 0;
		String result = null;
		List<String[]> outputLines = new ArrayList<>();
		
		Iterator<String[]> it = readLines.iterator();
		
		while (it.hasNext()) {
			lines= it.next();
			U.log((x++) +"]]");
			
			if(lines.length == 1 || ((lines.length == 2) && lines[1].isEmpty()))
				result = getTranslateText(lines[0].trim(), driver);
			

			
			if(result == null) result = "";
			
			U.log(lines[0]+"\t"+result);
			outputLines.add(new String[]{lines[0],result});
		}
		writeFile(outputLines);
		driver.close();
	}
	
	
	static String getTranslateText(String str, WebDriver driver) throws InterruptedException{
		String result = null;
		
		Thread.sleep(1000);
		
		WebElement element = driver.findElement(By.xpath("//*[@id=\"gt-c\"]"));
		Thread.sleep(2000);
		
		element.findElement(By.xpath("//*[@id=\"source\"]")).clear();
		element.findElement(By.xpath("//*[@id=\"source\"]")).sendKeys(str);
		Thread.sleep(3000);
		
/*		try{
			element.findElement(By.xpath("//*[@id=\"result_box\"]")).clear();
		}catch(Exception e){
		}*/
		
		result = element.findElement(By.xpath("//*[@id=\"result_box\"]")).getText();
		U.log(result);
		return result;
	}
	
	static List<String[]>  readLines = null;
	
	static void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_NAME),'\t');
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
	}
	
	public static void writeFile(List<String[]> outputLines){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(FILE_NAME.replace(".csv", "_Translate.csv")),',');
			writer.writeAll(outputLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log(FILE_NAME+" Writing file.....Done");
	}
	
	
}
