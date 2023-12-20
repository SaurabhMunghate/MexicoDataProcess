package com.tequila.database.corrector;

import java.sql.Connection;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class UpdateProductDesc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		U.log(U.toTitleCase("Sawan's"));

	}
	private static final String MAIN_DB_NAME = "tequila.db";
	
	Connection conn = null;
	public UpdateProductDesc() {
//		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	
	private static final String UPDATE_QUERY = "update dataset set product_desc=? where product_desc=?";
	private static final String FILE_NAME = "";
	
	void start(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);

		
		for(String [] lines : readLines){
			
		}
	}
	
}
