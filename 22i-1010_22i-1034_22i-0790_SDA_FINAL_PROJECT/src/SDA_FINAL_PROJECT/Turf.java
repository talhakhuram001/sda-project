package SDA_FINAL_PROJECT;
import java.util.Random;


public class Turf{
	int rating;
	boolean availability;
	String Name;
	String Location;
	Weather w;
	int id;
	Turf(){
		
	}
	Turf(String Name,String Location){
		Random random = new Random();
		rating=random.nextInt(5) + 1;
		this.availability=true;
		this.Name=Name;
		this.Location=Location;
	
	}
	public String getName(){
		return this.Name;
	}
	public int getRating() {
	    return rating;
	}

	public boolean isAvailable() {
	    return availability;
	}

	public String getLocation() {
	    return Location;
	}
	public String getWeather() {
	String weather = null;
		try {
			 weather=w.getWeather(Location);
			 w.displayWeatherDetails(weather);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return weather;
	}
	public void setId(int int1) {
		this.id=int1;
		
	}
	public void setName(String string) {
		this.Name=string;
		
	}
	public void setLocation(String string) {
	    this.Location=string;		
	}
	
	public int getId()
	{
		return this.id;
	}
	
	 @Override
	    public String toString() {
	        return Name + " (Rating: " + rating + ")";
	    }
	public void setRating(int int1) {
		this.rating=int1;
	}
}