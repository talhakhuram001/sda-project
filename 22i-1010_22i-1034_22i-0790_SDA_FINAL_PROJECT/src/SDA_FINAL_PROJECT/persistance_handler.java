package SDA_FINAL_PROJECT;

import java.sql.*;

public class persistance_handler {
    private Connection connection;

    public persistance_handler() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/turf_management"; 
        String user = "root"; 
        String password = "zayan"; 
        connection = DriverManager.getConnection(url, user, password);
    }

    public boolean authenticateUser(String username, String password, String userType) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND userType = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, userType);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        }
    }

    public void registerUser(String username, String password, String email, String userType) throws SQLException {
        String query = "INSERT INTO Users (username, password, email, userType) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, userType);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new SQLException("Username already exists.");
        }
    }
    
    public Connection getConnection() {
    	return this.connection;
    }

}
