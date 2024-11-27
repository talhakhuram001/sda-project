package SDA_FINAL_PROJECT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionManager {

    private static Player currentPlayer;
    private static TurfOwner currentTurfOwner;

    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
        System.out.println("Current player set to: " + player.getUsername());
    }

    
    public static TurfOwner getCurrentTurfOwner() {
        return currentTurfOwner;
    }

    public static void setCurrentTurfOwner(TurfOwner owner) {
        currentTurfOwner = owner;
        System.out.println("Current turf owner set to: " + owner.getUsername());
    }

    // Clear session data
    public static void clearSession() {
        currentPlayer = null;
        currentTurfOwner = null;
        System.out.println("Session cleared.");
    }

    public static void login(Object user) {
        if (user instanceof Player) {
            setCurrentPlayer((Player) user);
        } else if (user instanceof TurfOwner) {
            setCurrentTurfOwner((TurfOwner) user);
        }
    }
    
    public static int getId() {
        if (currentPlayer != null) {
            return fetchUserIdFromDatabase(currentPlayer.getUsername(), "Player");
        } else if (currentTurfOwner != null) {
            return fetchUserIdFromDatabase(currentTurfOwner.getUsername(), "TurfOwner");
        }
        throw new IllegalStateException("No user is currently logged in.");
    }

    private static int fetchUserIdFromDatabase(String username, String userType) {
        String query = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("user_id");
            } else {
                throw new IllegalStateException("User not found in the database: " + username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Database error occurred while fetching user ID.");
        }
    }

}
