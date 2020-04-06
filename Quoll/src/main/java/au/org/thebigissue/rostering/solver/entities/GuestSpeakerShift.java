package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.solver.solution.Roster;
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
    private Roster roster;

    // required by OptaPlanner
    public GuestSpeakerShift() {}

    public GuestSpeakerShift(long id, Staff staff, LocalDate date, Roster roster) {
        super(id, staff, date);
        this.roster = roster;
    }

    public GuestSpeakerShift(long id, GuestSpeaker staff, LocalDate date, Roster roster) {
        super(id, (Staff) staff, date);
        this.roster = roster;
    }

    @InverseRelationShadowVariable(sourceVariableName = "guestSpeakerShift")
    public List<Workshop> getWorkshopList() {
        return super.getWorkshopList();
    }

    public int getNumWorkshopsPerWeek() {
        List<GuestSpeakerShift> guestSpeakerShiftList = roster.getGuestSpeakerShiftList();
        int totalNumWorkshops = 0;

        for (GuestSpeakerShift guestSpeakerShift : guestSpeakerShiftList) {
            if (guestSpeakerShift.getStaffName().equals(this.getStaffName()))
                totalNumWorkshops += guestSpeakerShift.getNumWorkshops();
        }

        return totalNumWorkshops;
    }

    public GuestSpeaker getStaff() {
        return (GuestSpeaker) super.getStaff();
    }

    @Override
    public String toString(){
        return "GuestSpeakerShift-" + getStaffName();
    }
}
