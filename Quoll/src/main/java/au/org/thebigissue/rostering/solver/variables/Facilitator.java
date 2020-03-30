package au.org.thebigissue.rostering.solver.variables;

public class Facilitator extends Staff {

    private boolean isOfficeStaff = false;

    public Facilitator(String firstName, String lastName, Availability availability, String[] trained) {
        super(firstName, lastName, availability, trained);
    }

    public Facilitator(String firstName, String lastName, Availability availability, String[] trained, boolean isOfficeStaff) {
        super(firstName, lastName, availability, trained);

        this.isOfficeStaff = isOfficeStaff;
    }



    public boolean isTrained(String courseName) {
        return super.isTrained(courseName);
    }

    public boolean isOfficeStaff() { return isOfficeStaff;}
}
