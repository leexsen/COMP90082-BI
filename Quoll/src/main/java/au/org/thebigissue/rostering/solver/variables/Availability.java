package au.org.thebigissue.rostering.solver.variables;

import au.org.thebigissue.rostering.solver.AbstractPersistable;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.HashMap;


public class Availability extends AbstractPersistable {

    // stores the staff member's regular availability
    private HashMap<DayOfWeek, LocalTime[]> weeklyAvailability = new HashMap<>();

    //private LocalDate[][] unavailability;

    public Availability() {

        EnumSet<DayOfWeek> daysOfWeek = EnumSet.allOf(DayOfWeek.class);

        for(DayOfWeek day : daysOfWeek) {
            weeklyAvailability.put(day, null);
        }

    }

    public void update(DayOfWeek day, LocalTime availableFrom, LocalTime availableUntil) {
        // adjust available times to account for shifts requiring 30 minutes before first workshop
        // and 30 minutes after last workshop
        availableFrom = availableFrom.plusMinutes(30);
        availableUntil = availableUntil.minusMinutes(30);
        weeklyAvailability.put(day, new LocalTime[] {availableFrom, availableUntil});
    }

    /**
     * Check if the person is available at a certain time.
     * @param dateTime date and time to check
     * @return true if unavailable
     */
    public boolean isUnavailable(LocalDateTime dateTime) {
        DayOfWeek day = dateTime.getDayOfWeek();
        LocalTime time = dateTime.toLocalTime();

        LocalTime[] timeRange = weeklyAvailability.get(day);

        if (timeRange == null) {
            return true;
        } else {
            LocalTime availableFrom = timeRange[0];
            LocalTime availableUntil = timeRange[1];

            return time.isBefore(availableFrom) || time.isAfter(availableUntil);

        }
    }


}
