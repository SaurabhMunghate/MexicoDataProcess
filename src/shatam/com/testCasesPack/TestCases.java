package shatam.com.testCasesPack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.opencsv.CSVReader;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class TestCases {

	private static Connection con = null;
	static boolean mexicoDbFlag = true;
	static String tableName = "mexicotable";
	// ====================================================================================================================================================

	@BeforeClass
	public static void setupDb() {
		System.out.println("\t\t\tSetup For Test Cases");
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:/home/mypremserver/DatabasesTequila/tequila.db");
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("Select * from dataset where id=1");
			ResultSetMetaData rsMetaData = rs.getMetaData();
			if (!rsMetaData.getColumnName(7).equals("SPANISH_PRODUCT_DESC")) {
				mexicoDbFlag = false;
				tableName = "canadatable";
			}
			// createTable();
			// readCsv();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// ====================================================================================================================================================

	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		// setupDb();
		// mexicoDbFlag=false;
		// createTable();
		// readCsv();
		// TestCases tCase = new TestCases();
		// tCase.testForCity();
		// setupDb();
		//testForLatLong();
	}

	// ====================================================================================================================================================

	@Test
	public void testForSicSubCount() throws SQLException {
		boolean flag = true;
		Statement stament = con.createStatement();
		ResultSet rs = stament.executeQuery("select sic_sub,sic_major,id from dataset group by sic_sub");

		while (rs.next()) {
			if (!rs.getString(1).startsWith(rs.getString(2))) {
				flag = false;

				System.out.println("ID :\t" + rs.getString(3) + "\t" + rs.getString(1));
				break;
			}
		}
		System.out.println("Test For Sic Sub\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stament.close();
		assertTrue(flag);
	}

	// ====================================================================================================================================================
	@Test
	public void testForCompanyName() throws SQLException {
		boolean flag = true;
		Statement stament = con.createStatement();
		ResultSet rs = stament.executeQuery("select company_name,id from dataset group by company_name");

		while (rs.next()) {
			if (rs.getString(1).contains("  ") || rs.getString(1).endsWith(" ") || rs.getString(1).startsWith(" ")) {
				flag = false;

				System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
				break;
			}
		}
		System.out.println("Test For Company Name\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stament.close();
		assertTrue(flag);
	}

	// ====================================================================================================================================================
	@Test
	public void testForAddress() throws SQLException {
		boolean flag = true;
		Statement stament = con.createStatement();
		String addst = "";
		if (!mexicoDbFlag) {
			addst = " where address not null";
		}
		ResultSet rs = stament.executeQuery("select distinct address,id from dataset" + addst);

		while (rs.next()) {
			if (rs.getString(1).equals(null) || rs.getString(1).length() < 2 || rs.getString(1).contains("  ")
					|| rs.getString(1).endsWith(" ") || rs.getString(1).startsWith(" ")) {
				flag = false;

				System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
				break;
			}
		}
		System.out.println("Test For Address\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stament.close();
		assertTrue(flag);
	}
	// ====================================================================================================================================================

	@Test
	public void testForStateCount() throws SQLException {

		Statement stament = con.createStatement();
		ResultSet rs = stament.executeQuery("select state,id from dataset group by state");
		int count = 0;
		while (rs.next()) {
			// System.out.println(rs.getString(1)+"\t"+rs.getString(2));
			count++;
		}

		boolean flag = true;
		if (count != 32 && count != 13) {
			flag = false;
		} // As there total 32 Test in Mexico

		System.out.println("====================================================================\n");
		System.out.println("Test For States\t>>\t" + count + "\t" + flag);
		System.out.println("====================================================================\n");
		stament.close();
		assertTrue(flag);
	}
	// ====================================================================================================================================================

	@Test
	public void testForPhoneNo() throws SQLException {
		boolean flag = true;
		String regex = "[0-9-;+]+";
		String regex1 = "([0-9-;+]+ Ext. \\d+)+";
		Pattern pattern = Pattern.compile(regex);
		Pattern pattern1 = Pattern.compile(regex1);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select Phone,id from dataset where phone not null group by phone");
		Matcher match1;
		while (rs.next()) {
			if (rs.getString(1).length() < 6) {

				System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
				flag = false;
				break;
				// continue;
			}
			match1 = pattern.matcher(rs.getString(1));
			boolean b1 = match1.matches();
			if (!b1) {
				match1 = pattern1.matcher(rs.getString(1));
				b1 = match1.matches();
				if (!b1) {
					flag = false;
					System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
					break;
				}

			}
		}
		System.out.println("Test For Phone No\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stmt.close();
		assertTrue(flag);

	}

	// ====================================================================================================================================================
	@Test
	public void testForFAXNo() throws SQLException {
		boolean flag = true;
		String regex = "[0-9-;+]+";
		String regex1 = "[0-9-;+]+ Ext. \\d+";
		Pattern pattern = Pattern.compile(regex);
		Pattern pattern1 = Pattern.compile(regex1);
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select fax,id from dataset where fax not null group by fax");
		Matcher match1;
		while (rs.next()) {
			if (rs.getString(1).length() < 6) {
				System.out.println(rs.getObject(1));
				System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
				flag = false;
				break;
				// continue;
			}
			match1 = pattern.matcher(rs.getString(1));
			boolean b1 = match1.matches();
			if (!b1) {
				match1 = pattern1.matcher(rs.getString(1));
				b1 = match1.matches();
				if (!b1) {
					System.out.println(rs.getString(1));
					System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
					flag = false;
					break;
				}
			}
		}
		System.out.println("Test For FAX\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stmt.close();
		assertTrue(flag);
	}

	// ====================================================================================================================================================

	@Test
	public void testForEmail() throws SQLException {
		boolean flag = true;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select email,id from dataset where email not null group by email");
		while (rs.next()) {
			if (!rs.getString(1).contains("@") || rs.getString(1).length() < 5 || rs.getString(1).contains(" ")) {
				flag = false;
				System.out.println(rs.getString(1));
				System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
				break;
			}
		}
		System.out.println("Test For Email\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stmt.close();
		assertTrue(flag);
	}

	// ====================================================================================================================================================

	@Test
	public void testForZip() throws SQLException {
		boolean flag = true;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select zip,id from dataset where zip not null group by zip");
		if (mexicoDbFlag) {
			while (rs.next()) {
				if (rs.getString(1).length() < 4) {
					flag = false;
					System.out.println(rs.getString(1));
					System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
					break;
				}
			}
		} else {
			while (rs.next()) {
				if (rs.getString(1).length() < 4 || Util.match(rs.getString(1),
						"[ABCEGHJKLMNPRSTVXY]\\d[ABCEGHJ-NPRSTV-Z][ ]?\\d[ABCEGHJ-NPRSTV-Z]\\d") == null) {
					flag = false;
					System.out.println(rs.getString(1));
					System.out.println("ID :\t" + rs.getString(2) + "\t" + rs.getString(1));
					break;
				}
			}
		}
		System.out.println("Test For Zip\t>>\t" + flag);
		System.out.println("====================================================================\n");
		stmt.close();
		assertTrue(flag);
	}
	// ====================================================================================================================================================

	@Test
	public void testForURL() throws SQLException {
		boolean flag = true;
		Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("select distinct url,id from dataset where url not null");
		while (rs.next()) {
			if (!rs.getString(1).contains("www.") && !rs.getString(1).equals("http")) {
				flag = false;
				System.out.println(rs.getString(1));
				System.out.println("ID :\t" + rs.getString(2));
				break;
			}
		}
		System.out.println("Test For URL\t>>\t" + flag);
		System.out.println("====================================================================\n");
		assertTrue(flag);
		stmt.close();
	}

	// ====================================================================================================================================================

	@Test
	public void testForProductDesc() throws SQLException {
		boolean flag = true;
		Statement stmt = con.createStatement();
		Statement stmt1 = con.createStatement();

		ResultSet rs = stmt.executeQuery("select count(*) from (select sic_sub from dataset group by sic_sub)");
		ResultSet rs1 = stmt1
				.executeQuery("select count(*) from (select product_desc from dataset group by product_desc)");
		while (rs.next()) {
			if (!rs.getString(1).equals(rs1.getString(1))) {
				System.out.println(
						"Count of Sub_SIC Code\t" + rs.getString(1) + "\tCount of Description\t" + rs1.getString(1));
				flag = false;
			}
		}
		System.out.println("Test For Product Description\t>>" + flag);
		System.out.println("====================================================================\n");
		stmt.close();
		stmt1.close();
		assertTrue(flag);
	}
	// ====================================================================================================================================================

	@Test
	public void testForEmpCount() throws SQLException {
		boolean flag = true;
		Statement stmt = con.createStatement();
		ResultSet rs = null;
		if (mexicoDbFlag) {
			rs = stmt.executeQuery(
					"select emp_count_min,emp_count_max,id from dataset where emp_count_min not null and emp_count_max not null group by company_name");

			while (rs.next()) {
				try {
					if (rs.getString(1).length() < 1 || rs.getString(2).length() < 1
							|| Integer.parseInt(rs.getString(1)) > Integer.parseInt(rs.getString(2))) {
						flag = false;
						System.out.println(rs.getString(1));
						System.out.println("ID :\t" + rs.getString(3));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			System.out.println("Test For Emp Count\t>>\t" + flag);
			System.out.println("====================================================================\n");
		} else {
			rs = stmt.executeQuery("select emp_count,id from dataset where emp_count not null group by company_name");
			while (rs.next()) {
				try {
					if (rs.getString(1).length() < 1 || Util.match(rs.getString(1), "\\d+") == null) {
						flag = false;
						System.out.println(rs.getString(1));
						System.out.println("ID :\t" + rs.getString(3));
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			System.out.println("Test For Emp Count\t>>\t" + flag);
			System.out.println("====================================================================\n");

		}
		stmt.close();
		assertTrue(flag);
	}

	// ====================================================================================================================================================

	@Test
	public void testForCity() throws SQLException, ClassNotFoundException {
		boolean flag = false;
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/mypremserver/MexicoData/chinmay/CityState.db");
		Statement st = conn.createStatement();
		Statement stmt = con.createStatement();
		int count = 0;
		ResultSet rs = stmt.executeQuery("select distinct city,state from dataset where city not null");
		while (rs.next()) {
			count++;
			flag = false;
			
			//System.out.println("ffffff"+rs.getString(1));
			if (!rs.getString(2).equals("Mexico City")) {
				ResultSet rsst = st.executeQuery("select distinct state from " + tableName + " where city=\"" + rs.getString(1) + "\"");
				if (rsst.next()) {
					do{
						if (rsst.getString(1).toLowerCase().equals(rs.getString(2).toLowerCase())) {
							System.out.println("hello");
							flag = true;
							break;
						}
					}while (rsst.next());
				}else {
					ResultSet rsst1 = st.executeQuery(
							"select distinct state from " + tableName + " where municipality like \"" + rs.getString(1) + "\"");
					while (rsst1.next()) {
						if (rsst1.getString(1).toLowerCase().equals(rs.getString(2).toLowerCase())) {
							flag = true;
							break;
						}
					}
				}
				 
				// System.out.println(flag);
			} else {
				ResultSet rsst = st.executeQuery(
						"select distinct state from " + tableName + " where municipality like \"" + rs.getString(1) + "\"");
				
				while (rsst.next()) {
					if (rsst.getString(1).toLowerCase().equals(rs.getString(2).toLowerCase())) {
						flag = true;
						break;
					}
				}
			}
			if (flag != true) {
				System.out.println(
						"select distinct state from " + tableName + " where city like \"" + rs.getString(1) + "\"");
				break;
			}

		}
		System.out.println("Test For City \t>>\t" + flag + "\t" + count);
		System.out.println("====================================================================\n");
		st.close();
		stmt.close();
		conn.close();
		assertTrue(flag);

	}

	
	// ====================================================================================================================================================

		@Test
		public void testForLatLong() throws SQLException {
			boolean flag = true;
			put_LatLong_In_Map();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select latitude,longitude,state,id from dataset where latitude not null and longitude not null");
			int count=0;
			while (rs.next()) {
				String[] latlong= {rs.getString(1),rs.getString(2)};
				String s=checkBoundryCondition(latlong, rs.getString(3));
				
				if(!s.equals("INSIDE_STATE")) {
					flag=false;
					System.out.println(s);
				System.out.println("ID="+rs.getString(4)+"\t"+rs.getString(3)+"\t"+rs.getString(1)+"\t"+rs.getString(2));
					break;
				}
				count++;
			}
			System.out.println("Test For Lat long inside state\t>>"+count+"\t" + flag);
			System.out.println("====================================================================\n");
			stmt.close();
			assertTrue(flag);
		}
	// ====================================================================================================================================================

	private static void createTable() {
		try {

			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/shatam-20/CityState.db");
			Statement stmt = conn.createStatement();
			String sql = "";
			if (mexicoDbFlag) {
				sql = "CREATE TABLE IF NOT EXISTS mexicoTable (state text,settlement text,type_of_settlement text,postal_code text,municipality text,city text,zone text,latitude text,longitude text)";
				System.out.println("mexicoTable Cterated...");
			} else {
				sql = "CREATE TABLE IF NOT EXISTS canadaTable (postal_code text,city text,state text,state_abbr text,area_name text,area_code text,latitude text,longitude text)";
				System.out.println("canadaTable Cterated...");
			}
			stmt.execute(sql);
			System.out.println("Table create succesfully.....");
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// ====================================================================================================================================================

	private static void readCsv() {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/shatam-20/CityState.db");
			List<String> listEntries = null;
			// read csv file
			String insertQuery = "";
			if (mexicoDbFlag) {
				listEntries = Files.readAllLines(java.nio.file.Paths.get("Mexico_Address_Direcory_MiCodigo.csv"));
				System.out.println(listEntries.size());
				insertQuery = "Insert into mexicoTable (state,settlement,type_of_settlement,postal_code, municipality,city,zone,latitude,longitude) values (?,?,?,?,?,?,?,?,?)";
			} else {
				listEntries = Files.readAllLines(java.nio.file.Paths.get("CanadaCities_StateData.csv"));
				System.out.println(listEntries.size());
				insertQuery = "Insert into canadaTable (postal_code,city,state,state_abbr,area_name,area_code,latitude,longitude) values (?,?,?,?,?,?,?,?)";
			}
			PreparedStatement pstmt = conn.prepareStatement(insertQuery);
			String[] rowData = null;
			int i = 0;
			Iterator<String> itr = listEntries.iterator();
			conn.setAutoCommit(false);
			while (itr.hasNext()) {
				if (i == 0) {
					i++;
					String line = itr.next();
					continue;
				}
				String line = itr.next();
				rowData = line.split("\",\"");
				System.out.println(line);
				for (int j = 0; j < rowData.length; j++) {
					pstmt.setString((j + 1), rowData[j].replaceAll("\"", ""));
				}
				pstmt.addBatch();
				i++;
				if (i % 1000 == 0) {
					try {
						pstmt.executeBatch();
					} catch (BatchUpdateException be) {
						int[] updateCounts = be.getUpdateCounts();

					}

					System.out.println(i - 1 + " rows inserted in table...");
				}

			}
			pstmt.executeBatch();
			conn.commit();
			System.out.println(i + " rows inserted in table...");
			System.out.println("Data Successfully Uploaded....");
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ====================================================================================================================================================
	static private HashMap<String, String[]> stateData = new HashMap();

	final static String INSIDE_STATE = "INSIDE_STATE";
	final static String OUTSIDE_STATE = "OUTSIDE_STATE";
	final static String OUTSIDE_COUNTRY = "OUTSIDE_COUNTRY";

	static private void put_LatLong_In_Map() {
		try {
			CSVReader csvR = new CSVReader(new FileReader("/home/mypremserver/MexicoData/chinmay/StateLatLon.csv"));
			for (String[] latV : csvR) {
				String[] val = { latV[1].trim(), latV[2].trim(), latV[3].trim(), latV[4].trim() };
				stateData.put(latV[0].trim(), val);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	// ====================================================================================================================================================

	private static String checkBoundryCondition(String[] latlon, String State) {
		String stateLatlngs[] = stateData.get(State.trim());
		if (stateLatlngs != null) {
			/*double caNorthEastLat = 83.63809999999999;
			double caNorthEastLon = -50.9766;
			double caSouthWestLat = 41.6765559;
			double caSouthWestLon = -141.00187;*/

			double northEastLat = Double.parseDouble(stateLatlngs[0]);
			double northEastLon = Double.parseDouble(stateLatlngs[1]);

			double southWestLat = Double.parseDouble(stateLatlngs[2]);
			double southWestLon = Double.parseDouble(stateLatlngs[3]);

			double latitude = Double.parseDouble(latlon[0]);
			double longitude = Double.parseDouble(latlon[1]);

			/*if (latitude <= caNorthEastLat && Math.abs(longitude) >= Math.abs(caNorthEastLon)) { // Checking for
																									// country.
				if (latitude >= caSouthWestLat && Math.abs(longitude) <= Math.abs(caSouthWestLon)) {*/

					if (latitude <= northEastLat && Math.abs(longitude) >= Math.abs(northEastLon)) { // Checking State
																										// NorthEast.
						if (latitude >= southWestLat && Math.abs(longitude) <= Math.abs(southWestLon)) { // Checking
																											// State
																											// SouthWest.
							return INSIDE_STATE;
						} else {
							return OUTSIDE_STATE;
						}
					} else {
						return OUTSIDE_STATE;
					}
				/*} else {
					return OUTSIDE_COUNTRY;
				}
			} else {
				return OUTSIDE_COUNTRY;
			}*/
		}
		return null;
	}

	// ====================================================================================================================================================

	@AfterClass
	public static void closeDb() throws SQLException {
		if (con != null) {
			System.out.println("\t\t\tClosing Connection");
			con.close();
		}
		System.out.println("<=============================>E N D<=============================>\n");
	}

}
