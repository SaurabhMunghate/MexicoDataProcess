package com.shatam.utils;

public class CsvData {

	private String[] lines = null;
	
	public CsvData(String lines[]) {
		this.lines = lines;
	}
	
	public final String get(Field field){
		String val = null;
		switch(field){
			case ID :
				val = lines[0];
				break;
				
			case INDUSTRY_SECTOR :
				val = lines[1];
				break;
				
			case SPANISH_INDUSTRY_SECTOR :
				val = lines[2];
				break;
				
			case SIC_MAJOR :
				val = lines[3];
				break;
				
			case SIC_SUB :
				val = lines[4];
				break;
				
			case PRODUCT_DESC :
				val = lines[5];
				break;
				
			case SPANISH_PRODUCT_DESC :
				val = lines[6];
				break;
				
			case COMPANY_NAME :
				val = lines[7];
				break;
				
			case ADDRESS :
				val = lines[8];
				break;
				
			case NEIGHBORHOOD :
				val = lines[9];
				break;
				
			case CITY :
				val = lines[10];
				break;
				
			case STATE :
				val = lines[11];
				break;
				
			case ZIP :
				val = lines[12];
				break;
				
			case PHONE :
				val = lines[13];
				break;
				
			case FAX :
				val = lines[14];
				break;
				
			case URL :
				val = lines[15];
				break;
				
			case EMAIL :
				val = lines[16];
				break;
				
			case CONTACT_PERSON :
				val = lines[17];
				break;
				
			case TITLE :
				val = lines[18];
				break;
				
			case ANNUAL_SALES :
				val = lines[19];
				break;
				
			case EMP_COUNT :
				val = lines[20];
				break;
				
			case YEARS_IN_BIZ :
				val = lines[21];
				break;
				
			case LATITUDE :
				val = lines[22];
				if(val != null && !val.isEmpty())
					if(val.startsWith("-"))
						throw new IllegalArgumentException("Latitude is not correct, since Mexico's latitude does not start with prefix '-'.");
				break;
				
			case LONGITUDE :
				val = lines[23];
				break;
				
			case SOURCE_URL :
				val = lines[24];
				break;
				
			default :
				throw new IllegalArgumentException("Given argument is not present in the csv row data.");
		}
		return val;
	}
}
