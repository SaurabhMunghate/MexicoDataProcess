package com.shatam.test.Tequila;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.database.connection.SQLite;

public class TestTequila {

	private static String databasePath = "D:/sqlite/sqliteSpatial/";
	private static String databaseName = "tequila.db";
	private static Connection con = null;

	@BeforeClass
	public static void setupDb() {
		System.out.println("Setup");
		SQLite sqlite = new SQLite(databasePath, databaseName);
		try {
			con = sqlite.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws SQLException {

	}

	@Test
	public void testForIndustrySector() throws SQLException {
		int initialCount = 81;
		Statement stament = con.createStatement();
		ResultSet rs = stament
				.executeQuery("select Industry_Sector from dataset group by Industry_Sector;");
		int industrySectorCount = 0;
		while (rs.next()) {
			industrySectorCount++;
		}
		assertEquals(initialCount, industrySectorCount);
	}

	@Test
	public void testForSicAndSubSicCodeLength() throws SQLException {

		Statement stament = con.createStatement();
		ResultSet rs = stament
				.executeQuery("select sic_major,sic_sub from dataset group by sic_major;");
		boolean isValideLength = true;
		while (rs.next()) {
			String sicMajor = rs.getString("sic_major");
			String sicSub = rs.getString("sic_sub");

			// Validate Here.
			if (sicMajor.length() > 4) {
				isValideLength = false;
				break;
			}
			if (sicMajor.length() == 1) {

				if (!sicMajor.equalsIgnoreCase(sicSub.substring(0, 1))) {
					isValideLength = false;
					break;
				}

			}
			if (sicMajor.length() == 2) {

				if (!sicMajor.equalsIgnoreCase(sicSub.substring(0, 2))) {
					isValideLength = false;
					break;
				}
			}
		}
		// Check here
		assertEquals(true, isValideLength);
	}

	@Test
	public void testForUniqueUndustrySector() throws SQLException {

		Statement stament = con.createStatement();
		Set<String> sicMajor = new HashSet<String>();
		Set<String> industrySector = new HashSet<String>();
		ResultSet rs = stament
				.executeQuery("select distinct sic_major,industry_sector from dataset");

		while (rs.next()) {

			String sicMajor1 = rs.getString("sic_Major");
			String industrySector1 = rs.getString("industry_sector");
			sicMajor.add(sicMajor1);
			industrySector.add(industrySector1);
		}

		// SIC major and its Industry sector count should be same
		int result = sicMajor.size() - industrySector.size();
		System.out.println(result);
		assertEquals(0, result);
	}

	@Test
	public void testForStateCount() throws SQLException {

		Statement stament = con.createStatement();
		ResultSet rs = stament
				.executeQuery("select distinct state from dataset");

		int count = 0;
		while (rs.next()) {
			count++;
		}
		assertEquals(32, count); // As there total 32 Test in Mexico
	}

	protected void setUp() throws Exception {
		// Do nothing.
	};

	protected void tearDown() throws Exception {
		// DO nothing.
	};

	@AfterClass
	public static void closeDb() throws SQLException {
		if (con != null) {
			System.out.println("Closing Connection");
			con.close();
		}
	}

	@Test
	public void testForOPENDE_INDataFormat() {

	}

}
