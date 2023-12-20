/**
 * @author Sawan
 */
package com.database.connection;
import java.sql.Connection;
import java.sql.SQLException;

import com.shatam.utils.Path;
import com.shatam.utils.U;
public abstract class Connect {
	public static Connection conn = null;
	
	public Connect(){
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
		U.log("Database connection is establish... from default constructor");
	}
	public Connect(Connection conn){
		this.conn = conn;
		U.log("Database connection is establish... from parameterized Connection");
	}
	
	public Connect(String path, String dbName){
		conn = DBConnection.getConnection(path, dbName);
		U.log("Database connection is establish... from parameterized database path and name");
	}
	
	public void disconnect(){
		try{
			if(conn != null && !conn.isClosed()){
				conn.close();
//				conn = null;
				U.log("Connection is closed.");
				U.log("------------------------------------------------------------------------------------------");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
}
