package SDA_FINAL_PROJECT;

import java.util.ArrayList;
import java.util.List;

public class TurfOwner extends User{
	 protected List<Turf> turfs = new ArrayList<>();
	TurfOwner(String username, String password, String email) {
        super(username, password, email,"Turf Owner");
        
    }

	@Override
	public void performRole() {
		System.out.println("Turf owner can add and remove turfs");
		
	}

	@Override
	public void viewProfile() {
		
		
	}
	
	
	
}
