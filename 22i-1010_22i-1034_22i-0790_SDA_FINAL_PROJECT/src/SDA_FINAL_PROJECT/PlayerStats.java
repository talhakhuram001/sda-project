package SDA_FINAL_PROJECT;

public class PlayerStats{
	int Goals;
	int assists;
	int Points;
	int Rating;
	
	PlayerStats(){
		Goals=0;
		assists=0;
		Points=0;
	}
	
	public void UpdateStats(int goals,int assists,int Points){
		this.Goals+=Goals;
		this.assists+=assists;
		this.Points+=Points;
		int x=(this.Goals+this.assists+this.Points)/5;
		x=x%5;
		this.Rating=x;
	}
	
	public  int srating() {
		return this.Rating;
	}
}