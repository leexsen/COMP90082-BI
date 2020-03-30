package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.solver.variables.GuestSpeaker;
import au.org.thebigissue.rostering.solver.variables.Staff;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.time.LocalDate;
import java.util.List;

/**
 * A shift for guest speakers, who attend workshops
 */
@PlanningEntity
public class GuestSpeakerShift extends Shift {

    // required by OptaPlanner
    public GuestSpeakerShift() {}

    public GuestSpeakerShift(long id, Staff staff, LocalDate date) {
        super(id, staff, date);
    }

    public GuestSpeakerShift(long id, GuestSpeaker staff, LocalDate date) {
        super(id, (Staff) staff, date);
    }

    @InverseRelationShadowVariable(sourceVariableName = "guestSpeakerShift")
    public List<Workshop> getWorkshopList() {
        return super.getWorkshopList();
    }

    public GuestSpeaker getStaff() {
        return (GuestSpeaker) super.getStaff();
    }

    @Override
    public String toString(){
        return "GuestSpeakerShift-" + getStaffName();
    }
}
