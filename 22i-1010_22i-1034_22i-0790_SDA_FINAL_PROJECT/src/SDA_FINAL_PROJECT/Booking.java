package SDA_FINAL_PROJECT;

public class Booking {
    private int pid;
    private Turf turf;     

    public Booking(int p, Turf turf) {
        this.pid=p;
        this.turf = turf;
    }

    public int getPlayer() {
        return pid;
    }

    public Turf getTurf() {
        return turf;
    }

}
