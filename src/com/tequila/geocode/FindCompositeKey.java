package com.tequila.geocode;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.spi.DirStateFactory.Result;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class FindCompositeKey {

	public FindCompositeKey() {
		// TODO Auto-generated constructor stub
	}
	
/*	private List<String> loadUniqueIDFromDB() throws SQLException{
		List<String> uniqueId = 
		Connection conn = DBConnection.getConnection(Path.DB_PATH, "TequilaLatLong.db");
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select id from latlongdata");
		while(rs.next()){
			
		}
	}*/

	public static void main(String[] args) {
		FindCompositeKey key = new FindCompositeKey();
		key.loadResource("uniqueIdAtDB.ser");
	}
	
	public void loadResource(String fileName){
		File file = new File(getClass().getClassLoader().getResource(fileName).getFile());
		if(file.exists()){
			U.log("found");
		}else
			U.log("not");
		
	}
}
