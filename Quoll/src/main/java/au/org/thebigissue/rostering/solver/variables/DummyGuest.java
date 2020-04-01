package au.org.thebigissue.rostering.solver.variables;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;

/**  Dummy guest speakers are assigned to Workshops that do not require a guest speaker */
public class DummyGuest extends GuestSpeaker{

    final static String dummyName = "None";

    // Creates a dummy guest with no availability, no trained courses and 1 reliability
    public DummyGuest(String[] courseList){
        super("None", "", new Availability(), 6, courseList, 1);

        EnumSet<DayOfWeek> daysOfWeek = EnumSet.allOf(DayOfWeek.class);

        for(DayOfWeek day : daysOfWeek) {
            this.availability.update(day, LocalTime.of(00,00), LocalTime.of(23,59));
        }
    }

    public int getReliability() {
        return super.getReliability();
    }

    public boolean isTrained(String courseName) {
        return super.isTrained(courseName);
    }

    public boolean isDummy() { return true; }
}
