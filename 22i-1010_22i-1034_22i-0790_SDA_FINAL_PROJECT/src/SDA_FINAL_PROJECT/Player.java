package SDA_FINAL_PROJECT;

public class Player extends User {
    private int ranking;
    public PlayerStats stats;
    public PlayerInterface pI;
    public Teaam Team;
    public Player(String username, String password, String email, int ranking) {
        super(username, password, email, "Player");
        this.ranking = ranking; 
        this.Team=null;
    }

	@Override
    public void performRole() {
        System.out.println("Player can book turfs, view their performance, and update their profile.");
    }

    @Override
    public void viewProfile() {
        System.out.println("Player Profile: " + getUsername() + ", Ranking: " + ranking);
    }
    
    public Teaam getTeam() {
    	return this.Team;
    }
    
    public void setTeam(Teaam team) {
    	this.Team=team;
    }
    
    public int getRating() {
    	return this.stats.Rating;
    	
    }
    
    public int getRanking() {
    	return this.ranking;
    }
    

}
