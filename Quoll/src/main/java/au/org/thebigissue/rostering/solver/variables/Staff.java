package au.org.thebigissue.rostering.solver.variables;

import au.org.thebigissue.rostering.solver.AbstractPersistable;
import au.org.thebigissue.rostering.solver.entities.Workshop;

import java.time.LocalDateTime;


public class Staff extends AbstractPersistable {

    private final int FROM = 0;
    private final int UNTIL = 1;

    String firstName;
    String lastName;

    Availability availability;


    String[] trained;


    public Staff(String firstName, String lastName, Availability availability, String[] trained) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.availability = availability;
        this.trained = trained;
    }

    public String getName() {

        return (firstName + " " + lastName);
    }

    public boolean isUnavailable(LocalDateTime dateTime) {
       return availability.isUnavailable(dateTime);
    }

    public boolean isUnavailable(Workshop workshop) {
        return availability.isUnavailable(workshop.getStartDateTime()) || availability.isUnavailable(workshop.getEndDateTime());
    }

    // checks if staff member is trained for courseName
    public boolean isTrained(String courseName) {
        int numTrained = this.trained.length;

        for(int i = 0; i < numTrained; i++){
            if(this.trained[i].equals(courseName)){
                return true;
            }
        }

        return false;
    }

    // equals method based on unique names
    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Staff)){
            return false;
        }
        return (((Staff) obj).getName()).equals(this.getName());
    }

    @Override
    public String toString(){
        return firstName + " " + lastName;
    }

}
