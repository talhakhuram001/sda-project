package SDA_FINAL_PROJECT;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.sql.*;

public class PlayerInterface {

    public List<Booking> bookings = new ArrayList<>();


    public void CreateTeam(Player player) {
        if (player.Team == null) {
            System.out.println("Enter the name of your team");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the name of your team:");
            String teamName = scanner.next();
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/turf_management", "root", "zayan")) {
                String teamQuery = "INSERT INTO Team (team_name, created_on) VALUES (?, ?)";
                try (PreparedStatement teamStmt = connection.prepareStatement(teamQuery, Statement.RETURN_GENERATED_KEYS)) {
                    teamStmt.setString(1, teamName);
                    teamStmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                    teamStmt.executeUpdate();
                    ResultSet generatedKeys = teamStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int teamId = generatedKeys.getInt(1);
                        player.Team = new Teaam(teamId, teamName);
                        String playerQuery = "UPDATE Player SET team_id = ? WHERE username = ?";
                        try (PreparedStatement playerStmt = connection.prepareStatement(playerQuery)) {
                            playerStmt.setInt(1, teamId);
                            playerStmt.setString(2, player.getUsername());
                            playerStmt.executeUpdate();
                        }
                        System.out.println("Team " + teamName + " created and added to player " + player.getUsername());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("An error occurred while creating the team.");
            }
        } else {
            System.out.print("You are already a part of Team: ");
            System.out.println(player.Team.getTeamName());
        }
    }
    
    public void FindPlayer(List<Player> players, Player currentUser) {
        List<Player> playersWithoutTeam = getPlayersWithoutTeam();
        if (playersWithoutTeam.isEmpty()) {
            System.out.println("No players available to add to your team.");
            return;
        }

        System.out.println("Players without a team:");
        for (int i = 0; i < playersWithoutTeam.size(); i++) {
            Player player = playersWithoutTeam.get(i);
            System.out.println((i + 1) + ". " + player.getUsername() + " - Rating: " + player.getRanking());
        }

        System.out.println("Enter the number of the player you want to add to your team:");
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();

        if (number < 1 || number > playersWithoutTeam.size()) {
            System.out.println("Invalid player selection.");
            return;
        }

        Player selectedPlayer = playersWithoutTeam.get(number - 1);
        addPlayerToTeam(currentUser, selectedPlayer);
        System.out.println(selectedPlayer.getUsername() + " has been added to your team.");
    }

    private void addPlayerToTeam(Player teamOwner, Player playerToAdd) {
        
        Integer teamId = getTeamIdFromDatabase(teamOwner.getUsername());
        
        if (teamId == null) {
            System.out.println("The team owner does not have a valid team assigned.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/turf_management", "root", "zayan")) {
            String updateQuery = "UPDATE Player SET team_id = ? WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setInt(1, teamId);
                stmt.setString(2, playerToAdd.getUsername());
                stmt.executeUpdate();

                System.out.println(playerToAdd.getUsername() + " has been added to your team.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while adding the player to your team.");
        }
    }

    private Integer getTeamIdFromDatabase(String username) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/turf_management", "root", "zayan")) {
            String query = "SELECT team_id FROM Player WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("team_id");
                    } else {
                        System.out.println("No team found for player: " + username);
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while retrieving the team ID.");
            return null; 
        }
    }
private List<Player> getPlayersWithoutTeam() {
        System.out.println("Attempting to connect to the database...");
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/turf_management", "root", "zayan")) {
            System.out.println("Connection established successfully!");

            String query = "SELECT * FROM Player WHERE team_id IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                List<Player> players = new ArrayList<>();
                while (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    int ranking = rs.getInt("ranking");

                    players.add(new Player(username, password, email, ranking));
                }

                System.out.println("Players fetched from database: " + players.size());
                return players;
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            return new ArrayList<>();
        }
    }

    public void CancelBooking() {
        if (bookings.size() != 0) {
            System.out.println("Tell the number of booking you want to cancel:");
            for (int i = 0; i < bookings.size(); i++) {
                System.out.println(i + 1);
                System.out.println(bookings.get(i).getTurf().Name);
            }
            Scanner scanner = new Scanner(System.in);
            int number = scanner.nextInt();
            bookings.remove(number - 1);
            System.out.println("Booking canceled.");
        } else {
            System.out.println("You have no current bookings.");
        }
    }
}
