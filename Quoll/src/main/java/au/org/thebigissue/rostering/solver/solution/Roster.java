package au.org.thebigissue.rostering.solver.solution;

import au.org.thebigissue.rostering.solver.AbstractPersistable;
import au.org.thebigissue.rostering.solver.entities.FacilitatorShift;
import au.org.thebigissue.rostering.solver.entities.GuestSpeakerShift;
import au.org.thebigissue.rostering.solver.entities.Shift;
import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.variables.Staff;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.persistence.xstream.api.score.buildin.hardsoft.HardSoftScoreXStreamConverter;

import java.time.LocalDate;
import java.util.*;


/**
 * The roster for the week.
 * Holds all objects pertaining to the roster for the week, including workshops (bookings) and other inputs such as
 * staff and locations.
 */
@PlanningSolution
@XStreamAlias("Roster")
public class Roster extends AbstractPersistable {

    private String name;

    private List<FacilitatorShift> facilitatorShiftList;
    private List<GuestSpeakerShift> guestSpeakerShiftList;
    private List<Workshop> workshopList;

    @XStreamConverter(HardSoftScoreXStreamConverter.class)
    private HardSoftScore score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

/*    @ValueRangeProvider(id = "facilitatorShiftRange")*/
    @ProblemFactCollectionProperty
    public List<FacilitatorShift> getFacilitatorShiftList() {
        return facilitatorShiftList;
    }

    public void setFacilitatorShiftList(List<FacilitatorShift> facilitatorShiftList) {
        this.facilitatorShiftList = facilitatorShiftList;
    }

/*    @ValueRangeProvider(id = "guestSpeakerShiftRange")*/
    @ProblemFactCollectionProperty
    public List<GuestSpeakerShift> getGuestSpeakerShiftList() {
        return guestSpeakerShiftList;
    }

    public void setGuestSpeakerShiftList(List<GuestSpeakerShift> guestSpeakerShiftList) {
        this.guestSpeakerShiftList = guestSpeakerShiftList;
    }

    @PlanningEntityCollectionProperty
    public List<Workshop> getWorkshopList() {
        return workshopList;
    }
    public void setWorkshopList(List<Workshop> workshopList) {
        this.workshopList = workshopList;
    }


/*    public RosterConstraintConfiguration getConstraintConfiguration() {
        return constraintConfiguration;
    }*/

    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    /**
     * Creates the Shift objects to be used by OptaPlanner, to allocate staff to workshops.
     * A Shift is created for each staff member, for each day in the rostering period, with start and
     * end times based on the staff member's availability.
     * @param facilitators list of facilitator names
     * @param guestSpeakers list of guest speaker names
     */
    public void setShiftList(ArrayList<Staff> facilitators, ArrayList<Staff> guestSpeakers,
                             LocalDate start, LocalDate end) {

        LocalDate firstDate = start;
        LocalDate lastDate = end;

        int id = 0;
        facilitatorShiftList = new ArrayList<>();
        guestSpeakerShiftList = new ArrayList<>();


        // create the shifts
        for (LocalDate date = firstDate; date.isBefore(lastDate) || date.equals(lastDate); date = date.plusDays(1)) {
            //TODO can check if staff member is unavailable for the whole date - then no need to add the shift
            for (Staff staff : facilitators) {
                facilitatorShiftList.add(new FacilitatorShift(id, staff, date));
                id += 1;
            }
            for (Staff staff : guestSpeakers) {
                guestSpeakerShiftList.add(new GuestSpeakerShift(id, staff, date));
                id += 1;
            }
        }
    }

    /**
     * Takes list of workshops that were overridden and updates relevant shift object's workshop lists accordingly.
     * @param overriddenWorkshops list of overridden workshops, should be passed from RosteringImporter
     */
    public void updateOverrideShifts(List<Workshop> overriddenWorkshops){
        // Shift lists to store updated shifts, initialized as copy of current shift lists
        List<FacilitatorShift> updatedFacilitatorShifts = new ArrayList<>(getFacilitatorShiftList());
        List<GuestSpeakerShift> updatedGuestSpeakerShifts = new ArrayList<>(getGuestSpeakerShiftList());

        // Iterate over all overridden workshops
        for(Workshop w : overriddenWorkshops){

            // Updating the facilitator shifts
            for(FacilitatorShift fs : getFacilitatorShiftList()){

                // if match found with workshop's shift,
                if(w.getFacilitatorShift().equals(fs)){
                    // remove shift from updated shift list,
                    updatedFacilitatorShifts.remove(fs);
                    // shift's workshops (before updating)
                    List<Workshop> shiftWorkshops = fs.getWorkshopList();
                    // add overridden workshop to shift's workshops,
                    shiftWorkshops.add(w);
                    // update change in shift object,
                    fs.setWorkshopList(shiftWorkshops);
                    // add to updated shift list with updated workshop list.
                    updatedFacilitatorShifts.add(fs);
                }
            }

            // Updating the guest speaker shifts
            for(GuestSpeakerShift gss : getGuestSpeakerShiftList()){

                // if match found with workshop's shift,
                if(w.getGuestSpeakerShift().equals(gss)){
                    // remove shift from updated shift list,
                    updatedGuestSpeakerShifts.remove(gss);
                    // shift's workshops (before updating)
                    List<Workshop> shiftWorkshops = gss.getWorkshopList();
                    // add overridden workshop to shift's workshops,
                    shiftWorkshops.add(w);
                    // update change in shift object,
                    gss.setWorkshopList(shiftWorkshops);
                    // add to updated shift list with updated workshop list.
                    updatedGuestSpeakerShifts.add(gss);
                }
            }
        }
        
        //override old shift lists with updated versions
        this.facilitatorShiftList = updatedFacilitatorShifts;
        this.guestSpeakerShiftList = updatedGuestSpeakerShifts;
    }

    public <V extends Shift> int calculateVariance(List<V> shiftList) {
        HashMap<Staff, Long> hoursByStaff = new HashMap<>();
        long totalMinutes = 0;

        for (Shift shift : shiftList) {
            long shiftDuration = shift.getTotalWorkshopMinutes()/60;
            Staff staff = shift.getStaff();

            hoursByStaff.put(staff, hoursByStaff.getOrDefault(staff, (long) 0) + shiftDuration);
            totalMinutes += shiftDuration;
        }

        long mean = totalMinutes / hoursByStaff.keySet().size();
        long variance = 0;

        /*for (long hours : hoursByStaff.values()) {
            variance += (hours - mean) ^ 2;
        }*/

        /*for (long hours : hoursByStaff.values()) {
            variance += (hours - mean) ^ 2;
        }*/

        for (Map.Entry<Staff, Long> entry : hoursByStaff.entrySet()) {

            long temp =0;
            temp += (entry.getValue() - mean);
            temp=temp*temp;
            variance += temp;

        }

        variance=variance/(hoursByStaff.size());

        return (int) Math.round(variance);
    }

    public int calculateFacilitatorVariance() {
        return calculateVariance(facilitatorShiftList);
    }

    public int calculateGuestSpeakerVariance() {
        return calculateVariance(guestSpeakerShiftList);
    }

/*
    private String getDate(LocalDate date) {

        SimpleDateFormat simpleDateformat = new SimpleDateFormat("E"); // the day of the week abbreviated
        //System.out.println(simpleDateformat.format(java.sql.Date.valueOf(date)));

        String temp = simpleDateformat.format(java.sql.Date.valueOf(date)).toUpperCase();
        //return(simpleDateformat.format(java.sql.Date.valueOf(date)).toUpperCase());

        //Remove fullstop that appears at end
        String temp2 = StringUtils.substring(temp , 0, temp.length() - 1);
        return(temp2);

    }*/

}
