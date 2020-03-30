package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.input.MovableWorkshopFilter;
import au.org.thebigissue.rostering.solver.AbstractPersistable;
import au.org.thebigissue.rostering.solver.solution.Roster;
import au.org.thebigissue.rostering.solver.variables.GuestSpeaker;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A workshop session
 * Contains all details about the workshop, e.g. location, school, staff members, time and date
 */
@PlanningEntity(movableEntitySelectionFilter =
        MovableWorkshopFilter.class)
public class Workshop extends AbstractPersistable {

    // static information for each workshop; from input data
    private String school;
    private String course;
    private String location;
    private LocalDateTime startDateTime;
    private Duration duration;
    private LocalDateTime endDateTime;

    // descriptive information used for Word output
    private String offsite;
    private String level;
    private String pax;
    private String contactName;
    private String email;
    private String phone;
    private String workshop;
    private boolean facilitatorOnly;

    //For Excel output
    private int rowFacilitator;
    private int columnFacilitator;
    private int rowGuest;
    private int columnGuest;

    // planning variables - OptaPlanner allocates a facilitator and a guest speaker for each Workshop
    private FacilitatorShift facilitatorShift;
    private GuestSpeakerShift guestSpeakerShift;

    // override flag, sets workshop entities to be unmovable (not changeable) by Optaplanner
    private boolean overridden = false;

    private Roster roster;
    private List<FacilitatorShift> facilitatorShiftList;
    private List<GuestSpeakerShift> guestSpeakerShiftList;

    // required by OptaPlanner
    public Workshop() {}

    public Workshop(long id, String school, String course, String location,
                        String offsite, String level, String pax, String contactName, String email, String phone,
                            String workshop, int rowFacilitator, int columnFacilitator, int rowGuest, int columnGuest,
                                LocalDate date, LocalTime startTime, LocalTime endTime, boolean facilitatorOnly, Roster roster) {
        this.id = id;
        this.school = school;
        this.course = course;
        this.location = location;
        this.workshop = workshop;
        this.offsite=offsite;
        this.level=level;
        this.pax=pax;
        this.contactName=contactName;
        this.email=email;
        this.phone=phone;
        this.facilitatorOnly=facilitatorOnly;

        this.rowFacilitator = rowFacilitator;
        this.columnFacilitator = columnFacilitator;
        this.rowGuest = rowGuest;
        this.columnGuest = columnGuest;

        this.roster = roster;

        System.out.println("Normal: "+offsite + level + pax + contactName + email + phone);

        startDateTime = LocalDateTime.of(date, startTime);
        endDateTime = LocalDateTime.of(date, endTime);
        duration = Duration.between(startDateTime, endDateTime);

        // create lists of possible FacilitatorShifts and GuestSpeakerShifts based on the date
        // because cannot e.g. assign a Monday workshop to a Tuesday shift

        facilitatorShiftList = roster.getFacilitatorShiftList();
        ArrayList<FacilitatorShift> newFacilitatorShiftList = new ArrayList<>();
        for (FacilitatorShift shift : facilitatorShiftList) {
            if (shift.getDate().equals(this.startDateTime.toLocalDate())) {
                newFacilitatorShiftList.add(shift);
            }
        }
        facilitatorShiftList = newFacilitatorShiftList;

        guestSpeakerShiftList = roster.getGuestSpeakerShiftList();
        ArrayList<GuestSpeakerShift> newGuestSpeakerShiftList = new ArrayList<>();
        for (GuestSpeakerShift shift : guestSpeakerShiftList) {
            if (shift.getDate().equals(this.startDateTime.toLocalDate())) {
                newGuestSpeakerShiftList.add(shift);
            }
        }
        guestSpeakerShiftList = newGuestSpeakerShiftList;

    }

    // constructor for overridden workshop, assigns the correct shifts
    public Workshop(long id, String school, String course, String location,
                        String offsite, String level, String pax, String contactName, String email, String phone,
                            String workshop, int rowFacilitator, int columnFacilitator, int rowGuest, int columnGuest,
                            FacilitatorShift overrideFS, GuestSpeakerShift overrideGSS,
                                LocalDate date, LocalTime startTime, LocalTime endTime) {
        this.id = id;
        this.school = school;
        this.course = course;
        this.location = location;
        this.workshop = workshop;
        this.offsite=offsite;
        this.level=level;
        this.pax=pax;
        this.contactName=contactName;
        this.email=email;
        this.phone=phone;
        this.facilitatorOnly=false;

        this.rowFacilitator = rowFacilitator;
        this.columnFacilitator = columnFacilitator;
        this.rowGuest = rowGuest;
        this.columnGuest = columnGuest;

        System.out.println("Overridden: "+offsite + level + pax + contactName + email + phone);

        this.facilitatorShift = overrideFS;
        this.guestSpeakerShift = overrideGSS;
        startDateTime = LocalDateTime.of(date, startTime);
        endDateTime = LocalDateTime.of(date, endTime);
        duration = Duration.between(startDateTime, endDateTime);
        // flag that workshop should not be changed by Optaplanner
        overridden = true;
    }

    // old constructor used in RosteringGenerator
    public Workshop(long id, String school, String course, String location, int year, int month, int dayOfMonth,
                    int startTimeHr, int startTimeMin, int durationInMinutes) {
        this.id = id;
        this.school = school;
        this.course = course;
        this.location = location;
        startDateTime = LocalDateTime.of(year, month, dayOfMonth, startTimeHr, startTimeMin);
        duration = Duration.ofMinutes(durationInMinutes);
        endDateTime = startDateTime.plus(duration);
    }

    /**
     * Formats and prints the workshop details, for use when running in command line
     */
    public void printWorkshop() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String date = startDateTime.toLocalDate().format(formatter);

        String startTime = startDateTime.toLocalTime().toString();
        String endTime = endDateTime.toLocalTime().toString();

        String facilitatorName = "NONE";
        String guestSpeakerName = "NONE";

        if (facilitatorShift != null) {
            facilitatorName = facilitatorShift.getStaffName();
        }

        if (guestSpeakerShift != null) {
            guestSpeakerName = guestSpeakerShift.getStaffName();
        }

        System.out.format("%-18s%-17s%-60s%-8s%-18s%-13s%-15s%s",date, startTime + " - " +
                        endTime, school, course, location, facilitatorName,
                guestSpeakerName,"\n");
    }

    /**
     * Used for printing PDF documentation
     *
     * @return info for workshops line by lines
     */
    public String printPDFWorkshop() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String date = startDateTime.toLocalDate().format(formatter);

        String startTime = startDateTime.toLocalTime().toString();
        String endTime = endDateTime.toLocalTime().toString();

        String formattedOutput = String.format("%-18s%-17s%-40s%-8s%-18s%-13s%-15s", date, startTime + " - " +
                        endTime, school, course, location, facilitatorShift.getStaffName(),
                guestSpeakerShift.getStaffName());

//        System.out.print(formattedOutput);

        return formattedOutput;
    }


    /**
     * Used for printing Workshop information into Word table
     *
     * @return info for workshops as a hashmap
     */
    public HashMap<String, String> getWorkshopDictionary() {

        HashMap<String, String> result= new HashMap<String, String>();

        //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        //String date = startDateTime.toLocalDate().format(formatter);

        DateTimeFormatter conciseFormatter = DateTimeFormatter.ofPattern("eee, dd MMM", new Locale("en_AU"));

        String date = startDateTime.toLocalDate().format(conciseFormatter);

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("eee", new Locale("en_AU"));
        String day = startDateTime.toLocalDate().format(dayFormatter);

        String startTime = startDateTime.toLocalTime().toString();
        String endTime = endDateTime.toLocalTime().toString();

        result.put("$Day",day);
        result.put("$Date",date);
        result.put("$Time",startTime + " - " +
                endTime);
        result.put("$School",school);
        result.put("$Course",course);
        result.put("$Location",location);
        result.put("$Facilitator",facilitatorShift.getStaffName());
        result.put("$Guest",guestSpeakerShift.getStaffName());

        /*String[] keys = {"$Date","$Time","$Guest","$Facilitator","$School","$Workshop", "$Location", "$Pax",
                "$Level","$ContactName","$ContactNo","$email"};*/

        result.put("$Workshop", workshop);

        result.put("$Pax", pax);
        result.put("$Level", level);
        //System.out.println("$ContactName: "+contactName);
        //System.out.println("$ContactNo: "+phone);
        result.put("$Contact", contactName);
        result.put("$Phone", phone);
        result.put("$email", email);

        //Rather than show "other" for location, put that offiste location in
        if (location.equals("Other")) {
            result.put("$Location", offsite);
        }
        else {
            result.put("$Location", location);
        }

        return result;
    }


    /**
     * Used for printing PDF documentation into table
     *
     * @return info for workshops line by lines
     */
    public ArrayList<String> getWorkshopData() {

        ArrayList<String> result= new ArrayList<String>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String date = startDateTime.toLocalDate().format(formatter);

        String startTime = startDateTime.toLocalTime().toString();
        String endTime = endDateTime.toLocalTime().toString();

        result.add(date);
        result.add(startTime + " - " +
                endTime);
        result.add(school);
        result.add(course);
        result.add(location);
        if (facilitatorShift!=null) {
            result.add(facilitatorShift.getStaffName());
        }else {
            result.add("default facilitator");
        }

        if (guestSpeakerShift!=null){
            result.add(guestSpeakerShift.getStaffName());
        }else {
            result.add("default guestSpeaker");
        }

        return result;
    }


    /**
     * Checks if another workshop will run at any point at the same time with another
     * Note: the final minute of the workshop is excluded, i.e. a workshop ending at 10:00 is not considered to overlap
     * with a workshop starting at 10:00
     *
     * @param other other workshop
     * @return true if the other workshop overlaps with this one
     */
    public boolean hasOverlap(Workshop other) {
        return startDateTime.isBefore(other.getEndDateTime()) && endDateTime.isAfter(other.getStartDateTime());
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }
    public LocalDate getDate() {
        return startDateTime.toLocalDate();
    }

    @PlanningVariable(valueRangeProviderRefs = {"facilitatorShiftRange"})
    public FacilitatorShift getFacilitatorShift() {
        return facilitatorShift;
    }

    public void setFacilitatorShift(FacilitatorShift facilitatorShift) { this.facilitatorShift = facilitatorShift; }

    @ValueRangeProvider(id = "facilitatorShiftRange")
    public List<FacilitatorShift> getFacilitatorShiftList() {
        return facilitatorShiftList;
    }

    @PlanningVariable(valueRangeProviderRefs = {"guestSpeakerShiftRange"})
    public GuestSpeakerShift getGuestSpeakerShift() {
        return guestSpeakerShift;
    }
    public void setGuestSpeakerShift(GuestSpeakerShift guestSpeakerShift) { this.guestSpeakerShift = guestSpeakerShift; }


    @ValueRangeProvider(id = "guestSpeakerShiftRange")
    public List<GuestSpeakerShift> getGuestSpeakerShiftList() {
        return guestSpeakerShiftList;
    }

    /*public int getGuestSpeakerReliability() {
        GuestSpeaker guestSpeaker = guestSpeakerShift.getGuestSpeaker();
        return guestSpeaker.getReliability();
    }*/

    /*public int getReliabilityDifference(Workshop other) {
        int reliabilityThis = getGuestSpeakerReliability();
        int reliabilityOther = other.getGuestSpeakerReliability();
        return reliabilityOther - reliabilityThis;
    }*/


    public String getCourse() {
        return course;
    }
    public String getLocation() {
        return location;
    }
    public String getOffsite() {
        return offsite;
    }
    public boolean getOverridden(){ return overridden; }

    public String getDateString() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("en_AU"));
        String date = startDateTime.toLocalDate().format(formatter);
        return(date);

    }

    public String getDay() {

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("eee", new Locale("en_AU"));
        String day = startDateTime.toLocalDate().format(dayFormatter);

        return(day);

    }

    public String getStaffString() {

        return(facilitatorShift.getStaffName());

    }

    public String getGuestString() {

        return(guestSpeakerShift.getStaffName());

    }

    public boolean isDay(String targetDay) {

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("eee", new Locale("en_AU"));
        String day = startDateTime.toLocalDate().format(dayFormatter);

        if (day.equals(targetDay))
        {
        return true;}

        else {return false;}

    }

    public boolean isStaff(String targetName) {

        String name1 = facilitatorShift.getStaffName();
        String name2 = guestSpeakerShift.getStaffName();

        if ((name1.equals(targetName))||(name2.equals(targetName))) {
            return true;
        }

        return false;
    }

    public boolean isDate(String targetDate) {

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("en_AU"));
        String date = startDateTime.toLocalDate().format(dayFormatter);

        if (date.equals(targetDate))
        {
            return true;}

        else {return false;}

    }

    public boolean getFacilitatorOnly() {return facilitatorOnly;}

    public int getColumnGuest() {
        return columnGuest;
    }

    public int getRowGuest() {
        return rowGuest;
    }

    public int getColumnFacilitator() {
        return columnFacilitator;
    }

    public int getRowFacilitator() {
        return rowFacilitator;
    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-ID" + id;
    }

}
