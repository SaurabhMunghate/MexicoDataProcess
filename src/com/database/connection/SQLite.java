package com.database.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.sqlite.SQLiteConfig;

public class SQLite {

	private String path;
	private String dbName;
	private String tableName = "dataset";
	private boolean isLoadSpatialite = false;
	private Connection conn = null;

	public SQLite(String path, String dbName) {
		this.path = path;
		this.dbName = dbName;
	}

	public SQLite(String path, String dbName, boolean isLoadSpatialite)
			throws SQLException {
		this.path = path;
		this.dbName = dbName;
		this.isLoadSpatialite = isLoadSpatialite;
	}

	private Connection _loadExtenstion() throws SQLException {

		// enabling dynamic extension loading
		// absolutely required by SpatiaLite
		SQLiteConfig config = new SQLiteConfig();
		config.enableLoadExtension(true);
		// create a database connection
		// Connection conn = DriverManager.getConnection("jdbc:sqlite:"
		// + "D:/sqlite/add.sqlite", config.toProperties());

		conn = DBConnection.getConnection(path, dbName, config);
		System.out.println(conn);
		try (Statement stmt = conn.createStatement()) {
			// set timeout to 30 sec.
			stmt.setQueryTimeout(30);
			stmt.execute("SELECT load_extension('mod_spatialite.dll')");
		}
		return conn;

	}

	public static void main(String args[]) {

	}

	public Connection getConnection() throws SQLException {

		if (isLoadSpatialite) {
			conn = _loadExtenstion();
		} else {
			conn = DBConnection.getConnection(path, dbName);
		}
		return conn;
	}

	public ResultSet select(String query) throws SQLException {

		conn = getConnection();
		Statement st = conn.createStatement();
		return st.executeQuery(query);

	}

	public int update(Map<String, String> map) throws SQLException {

		Connection con = DBConnection.getConnection(path, dbName);
		Statement st = con.createStatement();
		String update = getUpdateQuery(map);
		return st.executeUpdate(update);
	}

	public int updateWhere(Map<String, String> map, Map<String, String> whereMap)
			throws SQLException {

		Connection con = DBConnection.getConnection(path, dbName);
		Statement st = con.createStatement();
		String update = getUpdateQuery(map);
		return st.executeUpdate(update);

	}

	public void updateAll(List<Map<String, String>> list) {

		Connection con = DBConnection.getConnection(path, dbName);

	}

	private String getUpdateQuery(Map<String, String> map) {

		String update = "update dataset set URL = " + map.get("URL")
				+ " where Id = " + map.get("ID");
		map = null;
		return update;

	}

}
