package com.database.connection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UpdateGeomentry {

	public static void main(String args[]) throws SQLException {

		SQLite sqlite = new SQLite("D:/sqlite/sqliteSpatial/", "tequila.db");
		ResultSet rs = sqlite.select("select * from dataset");
		List<String> list = new ArrayList<String>();
		long start = System.currentTimeMillis();
		int count = 0;
		while (rs.next()) {

			String lat = rs.getString("Latitude");
			String lng = rs.getString("Longitude");
			String id = rs.getString("ID");
			if (lat == null) {
				continue;
			}
			list.add(id + "\t" + lat + "\t" + lng);
			count++;
			if (list.size() == 10000) {
				updateBatch(list);
				list.clear();
			}
		}

		if (list.size() > 0) {
			updateBatch(list);
		}

		long end = System.currentTimeMillis();
		System.out.format("Total Time For %dms is %s", (end - start), count);
	}

	private static void updateBatch(List<String> list) throws SQLException {

		SQLite sqliteupdate = new SQLite("D:/sqlite/sqliteSpatial/",
				"mexicodatabase.sqlite", true);
		Connection con = sqliteupdate.getConnection();
		con.setAutoCommit(false);
		Statement st = con.createStatement();
		for (String d : list) {
			String arr[] = d.split("\t");
			st.addBatch("UPDATE dataset SET Geometry=MakePoint(" + arr[2] + ","
					+ arr[1] + ", 4326) where Id = " + arr[0] + ";");
		}
		try {
			int r[] = st.executeBatch();
			con.commit();
		} catch (Exception e) {
			System.out.println(e);
			con.rollback();
		} finally {
			if (con != null) {
				con.close();
			}
		}
		list.clear();
	}
}
