package SDA_FINAL_PROJECT;

public class Teaam {
    private int teamId; 
    private String teamName;
    private Player[] players;
    private int playerCount; 

    public Teaam(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.players = new Player[5]; 
        this.playerCount = 0;
    }

    public boolean addPlayer(Player player) {
        if (playerCount < players.length) {
            players[playerCount++] = player;
            return true;
        } else {
            System.out.println("Team is already full. Cannot add more players.");
            return false;
        }
    }

    public void displayTeam() {
        System.out.println("Team ID: " + teamId);
        System.out.println("Team Name: " + teamName);
        System.out.println("Players:");
        for (int i = 0; i < playerCount; i++) {
            players[i].viewProfile();
        }
    }

    public int getTotalSkillLevel() {
        int totalSkill = 0;
        for (int i = 0; i < playerCount; i++) {
            totalSkill += players[i].stats.Rating;
        }
        return totalSkill;
    }

    public double getAveragePerformance() {
        if (playerCount == 0) return 0.0; 
        double totalPerformance = 0.0;
        for (int i = 0; i < playerCount; i++) {
            totalPerformance += players[i].stats.Rating;
        }
        return totalPerformance / playerCount;
    }

    public String getTeamName() {
        return this.teamName;
    }

    public int getTeamId() {
        return this.teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }
}
