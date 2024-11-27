package SDA_FINAL_PROJECT;

import java.util.ArrayList;
import java.util.List;

public class MainMenu {

    private List<Player> players = new ArrayList<>();
    private List<TurfOwner> turfOwners = new ArrayList<>();
    private List<Turf> turfs = new ArrayList<>();

    public void addPlayer(Player player) {
        players.add(player);
        System.out.println("Player added: " + player.getUsername());
    }

    public void addTurfOwner(TurfOwner owner) {
        turfOwners.add(owner);
        System.out.println("Turf owner added: " + owner.getUsername());
    }

    public void addTurf(Turf turf, TurfOwner owner) {
        if (turfOwners.contains(owner)) {
            turfs.add(turf);
            owner.turfs.add(turf);
            System.out.println("Turf added: " + turf.getName() + " by " + owner.getUsername());
        } else {
            System.out.println("Permission denied: Only registered turf owners can add turfs.");
        }
    }

    public void cancelBooking() {
        Player currentPlayer = SessionManager.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.pI.CancelBooking();
        } else {
            System.out.println("No player is currently logged in.");
        }
    }

    public void findPlayer() {
        Player currentPlayer = SessionManager.getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.pI.FindPlayer(players, currentPlayer);
        } else {
            System.out.println("No player is currently logged in.");
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<TurfOwner> getTurfOwners() {
        return turfOwners;
    }

    public List<Turf> getTurfs() {
        return turfs;
    }
}
