package au.org.thebigissue.rostering.solver.entities;

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

    // required by OptaPlanner
    public FacilitatorShift() {}

    //public Shift(long id, String staffName, LocalDate date, int hourFrom, int minuteFrom, int hourTo, int minuteTo ) {
    //public FacilitatorShift(long id, String staffName, LocalDate date) {
    public FacilitatorShift(long id, Staff staff, LocalDate date) {
        super(id, staff, date);
    }

    public FacilitatorShift(long id, Facilitator staff, LocalDate date) {
        super(id, (Staff) staff, date);
    }

    @InverseRelationShadowVariable(sourceVariableName = "facilitatorShift")
    public List<Workshop> getWorkshopList() {
        return super.getWorkshopList();
    }

    public Facilitator getStaff() {
        return (Facilitator) super.getStaff();
    }

    public int getNumWorkshops() { return getWorkshopList().size();}

    @Override
    public String toString(){
        return "FacilitatorShift-" + getStaffName();
    }

}
