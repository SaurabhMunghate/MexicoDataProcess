package com.shatam.MexicoExtraction_1_15;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class SerializeExistingDatabaseURls {
	Connection conn=null;
	public SerializeExistingDatabaseURls() {
		conn= DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}
	public static void main(String[] args) {
		SerializeExistingDatabaseURls startProcess=new SerializeExistingDatabaseURls();
		startProcess.loadDatabaseSourceUrls();
	}
	public void loadDatabaseSourceUrls(){
		String sqlQuery="SELECT id,_SOURCE_URL from dataset where _SOURCE_URL is not null";
		try {
			HashMap<String , String>data=new HashMap<>();
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQuery);
			while(rs.next()) {
				data.put(rs.getString("_SOURCE_URL"), rs.getString("ID"));
			}
			U.log(data.size());
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
