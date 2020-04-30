package au.org.thebigissue.rostering.solver.variables;

public class  GuestSpeaker extends Staff {

    private int reliability;

    public GuestSpeaker(String firstName, String lastName, Availability availability, int maxSessions, String[] trained, int reliability) {
        super(firstName, lastName, availability, maxSessions, trained);
        this.reliability = reliability;
    }

    public int getReliability() {
        return reliability;
    }

    public boolean isTrained(String courseName) {
        return super.isTrained(courseName);
    }

    public boolean isDummy() { return firstName.equals("None"); }
}
