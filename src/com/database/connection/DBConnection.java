package com.database.connection;

import java.sql.Connection;
import java.sql.DriverManager;

import org.sqlite.SQLiteConfig;

public class DBConnection {
	@Deprecated
	public static Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:/home/chinmay/Mexico/MexicoDataFiles/Database/tequila.db"); // Oct.db
			// System.out.println("Opened database successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return connection;
	}

	/**
	 * It is used to connect database
	 * 
	 * @param databaseName
	 *            :- databaseName is parameters that will load and connect given
	 *            database
	 * @return java.sql.Connection object
	 */
	@Deprecated
	public static Connection getConnection(String databaseName) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager
					.getConnection("jdbc:sqlite:/home/chinmay/Mexico/MexicoDataFiles/Database/"
							+ databaseName.trim()); // Oct.db
			// System.out.println("Opened database successfully");
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return connection;
	}

	public static Connection getConnection(String path, String databaseName) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + path
					+ databaseName.trim()); // Oct.db
			// System.out.println("Opened database successfully");
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return connection;
	}

	public static Connection getConnection(String path, String databaseName,
			SQLiteConfig config) {
		Connection connection = null;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + path
					+ databaseName.trim(), config.toProperties());
			
			System.out.println("getConnection called ---------------- ");
			
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return connection;
	}
}
