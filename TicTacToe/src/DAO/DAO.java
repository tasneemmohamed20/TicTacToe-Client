package DAO;

import Users.Users;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DAO {

    private static final String URL = "jdbc:derby://localhost:1527/Users";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    private Connection connection;

    public DAO() throws SQLException {
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException e) {
            System.out.println("Derby Driver not found: " + e.getMessage());
            throw new SQLException("Driver not found", e);
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            throw e;
        }
    }

    public int addUser(Users user) throws SQLException {
        String query = "INSERT INTO Users (USERNAME, PASSWORD) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            return ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            throw e;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing the database connection: " + e.getMessage());
        }
    }
}
