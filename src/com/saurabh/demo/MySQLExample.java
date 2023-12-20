package com.saurabh.demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/saurabh";
        String username = "root";
        String password = "root";
        
        // Establish the database connection
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Create the table
            String createTableQuery = "CREATE TABLE saurabh (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), phone VARCHAR(20))";
            
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(createTableQuery);
                System.out.println("Table created successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
