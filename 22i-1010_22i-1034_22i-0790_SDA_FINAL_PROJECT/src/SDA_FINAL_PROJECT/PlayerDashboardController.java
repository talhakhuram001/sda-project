package SDA_FINAL_PROJECT;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerDashboardController {

    private PlayerInterface playerInterface = new PlayerInterface();
    private Player currentPlayer; 
    private List<Turf> turfs;     
    private List<Player> players; 
    private User user;             
    private MainMenu mainMenu;
    private List<Booking> bookings;

    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }

    public void initialize(Player currentPlayer, User user) {
        this.currentPlayer = SessionManager.getCurrentPlayer(); 
        System.out.println("Initialized currentPlayer: " + this.currentPlayer); 
        this.turfs = turfs;
        this.players = players;
        this.user = user;
    }


    @FXML
    private void handleFindPlayer() {
        System.out.println("Current Player: " + currentPlayer);

        playerInterface.FindPlayer(players, currentPlayer);
    }

    @FXML
    private void handleCreateTeam() {
    
        if (currentPlayer != null) {
        	System.out.println("In handle Create team");
            playerInterface.CreateTeam(currentPlayer);
        } else {
            showAlert("Create Team", "No current player is logged in.");
        }
    }
    @FXML
    private void handleBookTurf() {
        try {
            List<Turf> availabilityTurfs = getavailabilityTurfs();
            if (availabilityTurfs.isEmpty()) {
                showAlert("No Availability Turfs", "There are no available turfs to book.");
                return;
            }
            Turf selectedTurf = showTurfSelectionDialog(availabilityTurfs);
            if (selectedTurf == null) {
                showAlert("No Selection", "No turf was selected.");
                return;
            }
            bookTurf(selectedTurf.getId());

            addBookingToDatabase(SessionManager.getId(), selectedTurf.getId());

            showAlert("Success", "Turf booked successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error booking turf: " + e.getMessage());
        }
    }

    private List<Turf> getavailabilityTurfs() throws SQLException {
        List<Turf> turfs = new ArrayList<>();
        String query = "SELECT * FROM turf WHERE availability = 1"; 

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                Turf turf = new Turf();
                turf.setId(resultSet.getInt("turf_id"));
                turf.setName(resultSet.getString("name"));
                turf.setLocation(resultSet.getString("location"));
                turf.setRating(resultSet.getInt("rating")); 
                turfs.add(turf);
            }
        }

        return turfs;
    }

    private Turf showTurfSelectionDialog(List<Turf> turfs) {
        ChoiceDialog<Turf> dialog = new ChoiceDialog<>(null, turfs);
        dialog.setTitle("Select Turf to Book");
        dialog.setHeaderText("Please select the turf you want to book.");
        dialog.setContentText("Turf:");

        Optional<Turf> result = dialog.showAndWait();
        return result.orElse(null); 
    }

    private void bookTurf(int turfId) throws SQLException {
        String query = "UPDATE turf SET availability = 0 WHERE turf_id = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, turfId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("No turf found with the given ID or turf could not be booked.");
            }
        }
    }

    private void addBookingToDatabase(int playerId, int turfId) throws SQLException {
        String query = "INSERT INTO bookings (player_id, turf_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playerId);
            preparedStatement.setInt(2, turfId);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Failed to add booking to the database.");
            }
        }
    }

    @FXML
    private void handleCancelBooking() {
        try {
           
            int currentPlayerId = SessionManager.getId(); 
            List<Booking> bookings = getBookingsForPlayer(currentPlayerId);

            if (bookings.isEmpty()) {
                showAlert("No Bookings", "You have no active bookings.");
                return;
            }
            Booking selectedBooking = showBookingSelectionDialog(bookings);

            if (selectedBooking == null) {
                showAlert("No Selection", "No booking was selected.");
                return;
            }
            cancelBooking(selectedBooking.getTurf().getId(), currentPlayerId);
            showAlert("Success", "Booking cancelled successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error cancelling booking: " + e.getMessage());
        }
    }

    
    private List<Booking> getBookingsForPlayer(int playerId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String query = """
            SELECT 
                b.turf_id, 
                t.name AS turf_name, 
                t.location, 
                t.rating
            FROM bookings b
            JOIN turf t ON b.turf_id = t.turf_id
            WHERE b.player_id = ?
        """;

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, playerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int turfId = resultSet.getInt("turf_id");
                String turfName = resultSet.getString("turf_name");
                String turfLocation = resultSet.getString("location");
                int turfRating = resultSet.getInt("rating");

                Turf turf = new Turf(turfName, turfLocation);
                turf.setId(turfId);
                turf.setRating(turfRating);

                bookings.add(new Booking(playerId, turf));
            }
        }

        return bookings;
    }

    private Booking showBookingSelectionDialog(List<Booking> bookings) {
        ChoiceDialog<Booking> dialog = new ChoiceDialog<>(null, bookings);
        dialog.setTitle("Select Booking to Cancel");
        dialog.setHeaderText("Please select the booking you want to cancel.");
        dialog.setContentText("Booking:");

        Optional<Booking> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void cancelBooking(int turfId, int playerId) throws SQLException {
        String query = "UPDATE turf SET availability = 1 WHERE turf_id = ?";
        
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/turf_management", "root", "zayan");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setInt(1, turfId);
            int rowsAffected = preparedStatement.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("No turf found with the given ID.");
            }
            String deleteBookingQuery = "DELETE FROM bookings WHERE player_id = ? AND turf_id = ?";
            
            try (PreparedStatement deleteBookingStatement = connection.prepareStatement(deleteBookingQuery)) {
                deleteBookingStatement.setInt(1, playerId);
                deleteBookingStatement.setInt(2, turfId);
                deleteBookingStatement.executeUpdate();
            }
        }
    }

    
    @FXML
    private void handleCheckWeather() {
        int currentPlayerId = SessionManager.getId(); 
        List<Booking> bookings = null;
		try {
			bookings = getBookingsForPlayer(currentPlayerId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (bookings.isEmpty()) {
            showAlert("No Bookings", "You have no active bookings.");
            return;
        }
        Booking selectedBooking = showBookingSelectionDialog(bookings);
        selectedBooking.getTurf().getWeather();
    }

    @FXML
    private void handleFeedback() {
        showAlert("Feedback", "Feedback functionality is under development.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
}
