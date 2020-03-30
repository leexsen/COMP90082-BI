package au.org.thebigissue.rostering.solver.variables;

public class  GuestSpeaker extends Staff {

    private int reliability;

    public GuestSpeaker(String firstName, String lastName, Availability availability, String[] trained, int reliability) {
        super(firstName, lastName, availability, trained);
        this.reliability = reliability;
    }

    public int getReliability() {
        return reliability;
    }

    public boolean isTrained(String courseName) {
        return super.isTrained(courseName);
    }

    public boolean isDummy() {
        if (firstName.equals("None")) {
            return true;
        } else {
            return false;
        }
    }

}
