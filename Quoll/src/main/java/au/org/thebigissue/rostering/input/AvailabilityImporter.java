package au.org.thebigissue.rostering.input;

import au.org.thebigissue.rostering.solver.variables.Availability;
import au.org.thebigissue.rostering.solver.variables.Facilitator;
import au.org.thebigissue.rostering.solver.variables.GuestSpeaker;
import au.org.thebigissue.rostering.solver.variables.Staff;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

enum ColumnIndex {
    NAME(0, null),
    STAFF_CODE(1, null),
    MAX_SESSIONS(2, null),
    MON_AM(3, null),
    MON_PM(4, null),
    TUE_AM(5, null),
    TUE_PM(6, null),
    WED_AM(7, null),
    WED_PM(8, null),
    THU_AM(9, null),
    THU_PM(10, null),
    FRI_AM(11, null),
    FRI_PM(12, null),
    P123(13, "P123"),
    P456(14, "P456"),
    DHD(15, "DHD"),
    HHI(16, "HHI"),
    CSE(17, "CSE"),
    PE(18, "Pe"),
    DHDE(19, "DHDe"),
    DADE(20, "DADe"),
    HHIE(21, "HHIe"),
    CSEE(22, "CSEe"),
    TBIDEA(23, "TBIdea"),
    AH(24, "Ah"),
    C(25, "C");

    private static final ColumnIndex[] ENUMS = ColumnIndex.values();

    private int value;
    private String displayName;

    private ColumnIndex(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Obtains an instance of ColumnIndex from an int value.
     * ColumnIndex is an enum representing the indexes of all columns in the availability sheet.
     */
    public static ColumnIndex of(int i) {
        if (i < 0 || i >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid value for ColumnIndex: " + i);
        }
        return ENUMS[i];
    }
}

public class AvailabilityImporter {
    private final String AVAILABILITY_SHEET_NAME = "Master availability";
    private final String FACILITATOR_CODE = "F";
    private final String GUESTSPEAKER_CODE = "GS";

    private final String TRUE = "Y";
    private final String FALSE = "N";

    private String DEFAULT_AM_START_TIME = "08:00";
    private String DEFAULT_AM_END_TIME = "12:00";
    private String DEFAULT_PM_START_TIME = "12:00";
    private String DEFAULT_PM_END_TIME = "20:00";

    private Sheet availabilitySheet = null;

    private ArrayList<Staff> facilitatorList = new ArrayList<>();
    private ArrayList<Staff> guestspeakerList = new ArrayList<>();

    public AvailabilityImporter(String execlFileName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(execlFileName));
        for (Sheet sheet : workbook) {
            if (sheet.getSheetName().equals(AVAILABILITY_SHEET_NAME)) {
                availabilitySheet = sheet;
                break;
            }
        }
        workbook.close();
    }

    public ArrayList<Staff> getFacilitatorList() {
        return facilitatorList;
    }

    public ArrayList<Staff> getGuestspeakerList() {
        return guestspeakerList;
    }

    public void importAvailability(LocalDate rosterStartDate) {
        for (Row row : availabilitySheet) {

            // skip the first row -- heading
            if (row.getRowNum() == 0)
                continue;

            Availability availability = readAvailability(row);

            // skip this person if the person is unavailable for the whole week
            if (availability.unavailableAtAll())
                continue;

            String[] trainedCourses = readTrainedCourses(row);
            String firstName = row.getCell(ColumnIndex.NAME.getValue()).getStringCellValue();
            String staffCode = row.getCell(ColumnIndex.STAFF_CODE.getValue()).getStringCellValue();
            int maxSessions = (int) row.getCell(ColumnIndex.MAX_SESSIONS.getValue()).getNumericCellValue();

            if (staffCode.equals(FACILITATOR_CODE))
                facilitatorList.add(new Facilitator(firstName, "", availability, maxSessions, trainedCourses));
            else if (staffCode.equals(GUESTSPEAKER_CODE))
                guestspeakerList.add(new GuestSpeaker(firstName, "", availability, maxSessions, trainedCourses, 2));
        }
    }

    private Availability readAvailability(Row row) {
        Availability availability = new Availability();

        for (int i = ColumnIndex.MON_AM.getValue(); i <= ColumnIndex.FRI_AM.getValue(); i+=2) {
            Cell cell_am = row.getCell(i);
            Cell cell_pm = row.getCell(i + 1);

            if (cell_am == null || cell_pm == null)
                continue;

            String available_am = cell_am.getStringCellValue();
            String available_pm = cell_pm.getStringCellValue();

            LocalTime availableFrom;
            LocalTime availableUntil;

            if (available_am.equals(TRUE)) {
                availableFrom = LocalTime.parse(DEFAULT_AM_START_TIME);
                if (available_pm.equals(TRUE))
                    availableUntil = LocalTime.parse(DEFAULT_PM_END_TIME);
                else
                    availableUntil = LocalTime.parse(DEFAULT_AM_END_TIME);

            } else if (available_pm.equals(TRUE)) {
                availableFrom = LocalTime.parse(DEFAULT_PM_START_TIME);
                availableUntil = LocalTime.parse(DEFAULT_PM_END_TIME);

            } else
                continue;

            // make sure the dayIndex starts from 1 if it is Monday
            int dayIndex = (i - ColumnIndex.MON_AM.getValue()) / 2 + 1;
            DayOfWeek day = DayOfWeek.of(dayIndex);

            availability.update(day, availableFrom, availableUntil);
        }

        return availability;
    }

    private String[] readTrainedCourses(Row row) {
        ArrayList<String> trainedCourses = new ArrayList<>();

        for (int i = ColumnIndex.P123.getValue(); i <= ColumnIndex.C.getValue(); i++) {
            Cell cell  = row.getCell(i);

            if (cell == null)
                continue;

            String isTrained = cell.getStringCellValue();

            if (isTrained.equals(TRUE))
                trainedCourses.add(ColumnIndex.of(i).getDisplayName());
        }

        return trainedCourses.toArray(String[]::new);
    }
}
