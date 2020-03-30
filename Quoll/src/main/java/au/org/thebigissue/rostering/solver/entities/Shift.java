package au.org.thebigissue.rostering.solver.entities;

import au.org.thebigissue.rostering.output.PDFOutput;
import au.org.thebigissue.rostering.solver.AbstractPersistable;
import au.org.thebigissue.rostering.solver.variables.Facilitator;
import au.org.thebigissue.rostering.solver.variables.GuestSpeaker;
import au.org.thebigissue.rostering.solver.variables.Staff;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Super class for a shift for a staff member (facilitators and guest speakers)
 * For OptaPlanner, a Shift is created for each staff member, for each day in the rostering period, with start and
 * end times based on the staff member's availability.
 * Only Shifts that are allocated at least one Workshop should be output.
 */
public abstract class Shift extends AbstractPersistable {

    // staff member
    private Staff staff;

    // shift date
    private LocalDate date;

    // shift start and end times
    private LocalDateTime shiftStart;
    private LocalDateTime shiftEnd;



    // time spent in workshops, in minutes
    private long utilisedTime;
    // shift duration in minutes
    private long shiftDuration;

    // utilisation - must be an int, for OptaPlanner scoring
    private int utilisation;

    // a shift starts this many minutes before the first workshop starts
    private static final long START_BUFFER = 30;
    // a shift ends this many minutes after the last workshop ends
    private static final long END_BUFFER = 30;


    // list of workshops belonging to the shift
    // shadow variable, updated by OptaPlanner when allocating workshops
    private List<Workshop> workshopList;

    // required by OptaPlanner
    public Shift() {}

    /**
     * @param id        unique ID (required by OptaPlanner)
     * @param staff     the staff member (facilitator or guest speaker)
     * @param date      shift date
     */
    //public Shift(long id, String staffName, LocalDate date) {
    public Shift(long id, Staff staff, LocalDate date) {
        this.id = id;
        this.staff = staff;
        this.date = date;

        workshopList = new ArrayList<>();
        utilisedTime = 0;
    }


    /**
     * @return the number of workshops in the shift
     */
    public int getNumWorkshops() {
        return workshopList.size();
    }

    public void updateUtilisedTime() {
        utilisedTime = 0;
        for (Workshop workshop : workshopList) {
            LocalDateTime startTime = workshop.getStartDateTime();
            LocalDateTime endTime = workshop.getEndDateTime();
            utilisedTime += startTime.until(endTime, ChronoUnit.MINUTES);
        }
    }

    /**
     * Calculates the "utilisation" of the staff member in that shift: workshop time / total shift time
     * @return utilisation
     */
    public void updateUtilisation() {
        updateShiftTimes();
        updateUtilisedTime();
        if (shiftDuration != 0) {
//            utilisation = (int) (utilisedTime / shiftDuration) * 100;
            long totalWorkshopMinute=getTotalWorkshopMinutes();
            double temp=((double) totalWorkshopMinute / (double) shiftDuration) * 100;
            utilisation = (int) temp;
        } else {
            utilisation = 0;
        }
    }

    /**
     * Checks if this is an "empty shift".
     * An empty shift means that no workshops were allocated by OptaPlanner,
     * so it is not actually a shift and will not be outputted.
     *
     * @return true if empty shift
     */
    private boolean isEmptyShift() {
        return workshopList.size() == 0;
    }

    /**
     * Updates the shift start and end times, based on the first and last workshops in the shift.
     */
    private void updateShiftTimes() {

        // make sure times are not null
        if (isEmptyShift()) {
            return;
        }

        // find the earliest shift and last shift
        shiftStart = null;
        shiftEnd = null;

        for (Workshop workshop : workshopList) {
            LocalDateTime startTime = workshop.getStartDateTime().minusMinutes(START_BUFFER);
            if (shiftStart == null || startTime.isBefore(shiftStart)) {
                shiftStart = startTime;
            }

            LocalDateTime endTime = workshop.getEndDateTime().	plusMinutes(END_BUFFER);
            if (shiftEnd == null || endTime.isAfter(shiftEnd)) {
                shiftEnd = endTime;
            }
        }

        shiftDuration = shiftStart.until(shiftEnd, ChronoUnit.MINUTES);

    }

    /**
     * Prints the shift times and each of the workshops within the shift.
     * For displaying in the terminal.
     */
    public void printShift() {

        // ignore empty shifts
        if (isEmptyShift()) {
            return;
        }

        // update the shift times
        updateShiftTimes();

        // format the dates and times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String dateString = date.format(formatter);
        String startTime = shiftStart.toLocalTime().toString();
        String endTime = shiftEnd.toLocalTime().toString();

        System.out.println("\n");

        // print the heading for the shift, including start and end time
        System.out.format("%-10s%-18s%-17s%s", staff.getName(), dateString, startTime + " - " +
                endTime, "\n");

        // print the workshops within the shift
        System.out.println("---------------------------------------------------");
        for (Workshop workshop : workshopList) {
            workshop.printWorkshop();
        }

    }


    //TODO
    public ArrayList<String> getShiftInfo() {

        ArrayList<String> list=new ArrayList<String>();
        // ignore empty shifts
        if (isEmptyShift()) {
            return null;
        }

        // update the shift times
        updateShiftTimes();

        // format the dates and times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String dateString = date.format(formatter);
        String startTime = shiftStart.toLocalTime().toString();
        String endTime = shiftEnd.toLocalTime().toString();

        System.out.println("\n");

        // print the heading for the shift, including start and end time
        System.out.format("%-10s%-18s%-17s%s", staff.getName(), dateString, startTime + " - " +
                endTime, "\n");

        // name, dateString, time, location
        list.add(staff.getName());
        list.add(dateString);
        list.add(startTime+"-"+endTime);
        list.add(workshopList.get(0).getLocation());

        return list;
    }


    //-------------------------modify the code the generate new PDF here---------------------
    /**
     * Prints the shift times and each of the workshops within the shift.
     * For displaying in the terminal.
     */
    public void printShiftToPDF(String saveDirectory) {

        // ignore empty shifts
        if (isEmptyShift()) {
            return;
        }

        // update the shift times
        updateShiftTimes();

        // format the dates and times
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("eee, dd/MM/yyyy", new Locale("en_AU"));
        String dateString = date.format(formatter);
        String startTime = shiftStart.toLocalTime().toString();
        String endTime = shiftEnd.toLocalTime().toString();

        System.out.println("\n");

        String facilitatorShiftTitle=String.format("%-10s%-18s%-17s",staff.getName(), dateString, startTime + " - " +
                endTime);

        // print the heading for the shift, including start and end time
        System.out.format("%-10s%-18s%-17s%s", staff.getName(), dateString, startTime + " - " +
                endTime, "\n");

        // print the workshops within the shift
        System.out.println("---------------------------------------------------");
        for (Workshop workshop : workshopList) {
            workshop.printWorkshop();
        }

        try {
            PDFOutput.printForSpecifiedRolesProcedure(workshopList,facilitatorShiftTitle,saveDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    //-------------------------end of modify the code the generate new PDF here---------------------


    public long getTotalWorkshopMinutes(){

        long totalMinutes=0;

        for (Workshop ws:workshopList){
            long duration = ws.getStartDateTime().until(ws.getEndDateTime(), ChronoUnit.MINUTES);
            totalMinutes=totalMinutes+duration;
        }

        return totalMinutes;
    }


    // required by OptaPlanner
    public List<Workshop> getWorkshopList() {
        return workshopList;
    }

    public void setWorkshopList(List<Workshop> workshopList) {
        this.workshopList = workshopList;
    }

    public LocalDate getDate() {
        return date;
    }


    public String getStaffName() {
        return staff.getName();
    }


    public Staff getStaff() {
        return staff;
    }

    // needed because drl file can't cast Staff to a subclass
    /*public Facilitator getFacilitator() {
        if (staff instanceof Facilitator) {
            return (Facilitator) staff;
        } else {
            return null;
        }
    }*/

    /*public GuestSpeaker getGuestSpeaker() {
        if (staff instanceof GuestSpeaker) {
            return (GuestSpeaker) staff;
        } else {
            return null;
        }
    }*/

    public int getUtilisation() {
        updateUtilisation();
        return utilisation;
    }



    public long getDuration() {
        updateShiftTimes();
        return shiftDuration;
    }

    public long getUtilisedTime() {
        return utilisedTime;
    }



    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Shift)){
            return false;
        }
        if(((Shift)obj).getDate() == this.getDate()){
            if(((Shift)obj).getStaffName().equals(this.getStaffName())){
                return true;
            }
        }
        return false;
    }

    public boolean isLast(Workshop workshop1) {
        updateShiftTimes();
        LocalDateTime lastShiftTime = null;

        for (Workshop workshop2 : workshopList) {
            LocalDateTime startTime = workshop2.getStartDateTime().minusMinutes(START_BUFFER);
            if (lastShiftTime == null || startTime.isAfter(lastShiftTime)) {
                lastShiftTime = startTime;
            }
        }
        if (lastShiftTime.equals(workshop1.getStartDateTime())) {
            return true;
        }
        return false;

    }
}
