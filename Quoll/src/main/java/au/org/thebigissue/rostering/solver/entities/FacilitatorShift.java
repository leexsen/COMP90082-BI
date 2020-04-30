package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.solver.solution.Roster;
import au.org.thebigissue.rostering.solver.variables.Facilitator;
import au.org.thebigissue.rostering.solver.variables.Staff;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.time.LocalDate;
import java.util.List;

/**
 * Shift for facilitators (staff who run the workshops)
 */
@PlanningEntity
public class FacilitatorShift extends Shift {
    private Roster roster;

    // required by OptaPlanner
    public FacilitatorShift() {}

    //public Shift(long id, String staffName, LocalDate date, int hourFrom, int minuteFrom, int hourTo, int minuteTo ) {
    //public FacilitatorShift(long id, String staffName, LocalDate date) {
    public FacilitatorShift(long id, Staff staff, LocalDate date, Roster roster) {
        super(id, staff, date);
        this.roster = roster;
    }

    public FacilitatorShift(long id, Facilitator staff, LocalDate date, Roster roster) {
        super(id, staff, date);
        this.roster = roster;
    }

    @InverseRelationShadowVariable(sourceVariableName = "facilitatorShift")
    public List<Workshop> getWorkshopList() {
        return super.getWorkshopList();
    }

    public Facilitator getStaff() {
        return (Facilitator) super.getStaff();
    }

    public int getNumWorkshopsPerWeek() {
        List<FacilitatorShift> facilitatorShiftList = roster.getFacilitatorShiftList();
        int totalNumWorkshops = 0;

        for (FacilitatorShift facilitatorShift : facilitatorShiftList) {
            if (facilitatorShift.getStaffName().equals(this.getStaffName()))
                totalNumWorkshops += facilitatorShift.getNumWorkshops();
        }

        return totalNumWorkshops;
    }

    @Override
    public String toString(){
        return "FacilitatorShift-" + getStaffName();
    }

}
