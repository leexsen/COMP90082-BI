package au.org.thebigissue.rostering.solver.variables;

public class Facilitator extends Staff {

    private boolean isCasualStaff = false;

    public Facilitator(String firstName, String lastName, Availability availability, int maxSessions, String[] trained) {
        super(firstName, lastName, availability, maxSessions, trained);
    }

    public Facilitator(String firstName, String lastName, Availability availability, int maxSessions, String[] trained, boolean isCasualStaff) {
        super(firstName, lastName, availability, maxSessions, trained);

        this.isCasualStaff = isCasualStaff;
    }

    public boolean isTrained(String courseName) {
        return super.isTrained(courseName);
    }

    public boolean isCasualStaff() { return isCasualStaff;}
}
