package au.org.thebigissue.rostering.input;

import au.org.thebigissue.rostering.solver.entities.FacilitatorShift;
import au.org.thebigissue.rostering.solver.entities.GuestSpeakerShift;
import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

enum BookingColumnIndex {
    DAY(0, null),
    DATE(1, null),
    COLLINS(2, "Collins Street"),
    DWH(3, "DWH"),
    OTHER(4, "Other"),
    P123(5, "P123"),
    P456(6, "P456"),
    DHD(7, "DHD"),
    HHI(8, "HHI"),
    CSE(9, "CSE"),
    PE(10, "Pe"),
    DHDE(11, "DHDe"),
    DADE(12, "DADe"),
    HHIE(13, "HHIe"),
    CSEE(14, "CSEe"),
    TBIDEA(15, "TBIdea"),
    AH(16, "Ah"),
    C(17, "C"),
    FACIL(18, null),
    GUESTSPEAKER(19, null),
    LOCATION(20, null),
    PAX(21, null),
    LEVEL(22, null),
    SCHOOL(23, null),
    RET(24, null),
    CONTACTNAME(25, null),
    CONTACTEMAIL(26, null),
    CONTACTNUMBER(27, null),
    BILLABLEAMOUNT(28, null),
    COMMENTS(29, null);

    private static final BookingColumnIndex[] ENUMS = BookingColumnIndex.values();

    private int value;
    private String displayName;

    private BookingColumnIndex(int value, String displayName) {
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
     * Obtains an instance of BookingColumnIndex from an int value.
     * BookingColumnIndex is an enum representing the indexes of all columns in the bookings sheets.
     */
    public static BookingColumnIndex of(int i) {
        if (i < 0 || i >= ENUMS.length) {
            throw new IndexOutOfBoundsException("Invalid value for ColumnIndex: " + i);
        }
        return ENUMS[i];
    }

}

public class BookingImporter {
    private final String JAN_SHEET_NAME = "MJan";
    private final String FEB_SHEET_NAME = "MFeb";
    private final String MAR_SHEET_NAME = "MMar";
    private final String APR_SHEET_NAME = "MApr";
    private final String MAY_SHEET_NAME = "MMay";
    private final String JUN_SHEET_NAME = "MJun";
    private final String JUL_SHEET_NAME = "MJul";
    private final String AUG_SHEET_NAME = "MAug";
    private final String SEP_SHEET_NAME = "MSep";
    private final String OCT_SHEET_NAME = "MOct";
    private final String NOV_SHEET_NAME = "MNov";
    private final String DEC_SHEET_NAME = "MDec";
    private final String[] bookingNames = {JAN_SHEET_NAME, FEB_SHEET_NAME, MAR_SHEET_NAME, APR_SHEET_NAME, MAY_SHEET_NAME,
                                            JUN_SHEET_NAME, JUL_SHEET_NAME, AUG_SHEET_NAME, SEP_SHEET_NAME, OCT_SHEET_NAME,
                                            NOV_SHEET_NAME, DEC_SHEET_NAME};


    private List<Sheet> bookingSheets = new ArrayList<>();
    private DataFormatter formatter = new DataFormatter();

    public BookingImporter(String excelFileName) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(excelFileName));
        for (Sheet sheet : workbook) {
            for (String sheetName : bookingNames) {
                if (sheet.getSheetName().equals(sheetName)) {
                    bookingSheets.add(sheet);
                    break;
                }
            }
        }
        workbook.close();
    }

    public List<List<Workshop>> importBookings(LocalDate rosterStartDate, LocalDate rosterEndDate, Roster roster,
                                               List<FacilitatorShift> facilitatorShifts,
                                               List<GuestSpeakerShift> guestSpeakerShifts,
                                               List<Workshop> overriddenWorkshops) {
        List<Workshop> workshopList = new ArrayList<>();
        long id = 0;

        for (Sheet sheet: bookingSheets) {

            int day = -1;

            for (Row row : sheet) {

                // skip the first two rows -- heading
                if (row.getRowNum() <= 1)
                    continue;

                // go to next sheet if row is empty
                if (isEmptyRow(row))
                    break;

                // get day of the workshop
                if (day == -1 || !formatter.formatCellValue(row.getCell(BookingColumnIndex.DATE.getValue())).equals("")) {
                    day = (int) row.getCell(BookingColumnIndex.DATE.getValue()).getNumericCellValue();
                }

                LocalDate date = LocalDate.of(2019, getMonth(sheet), day);

                // only add workshops if they are within specified roster dates
                if (date.isBefore(rosterStartDate) || date.isAfter(rosterEndDate))
                    continue;

                // skip over rows that has no workshop
                if (getLocationIndex(row) == -1)
                    continue;

                //import data of the workshop
                String school = formatter.formatCellValue(row.getCell(BookingColumnIndex.SCHOOL.getValue()));
                String offsite = formatter.formatCellValue(row.getCell(BookingColumnIndex.LOCATION.getValue()));
                String level = formatter.formatCellValue(row.getCell(BookingColumnIndex.LEVEL.getValue()));
                String pax = formatter.formatCellValue(row.getCell(BookingColumnIndex.PAX.getValue()));
                String contactName = formatter.formatCellValue(row.getCell(BookingColumnIndex.CONTACTNAME.getValue()));
                String email = formatter.formatCellValue(row.getCell(BookingColumnIndex.CONTACTEMAIL.getValue()));
                String phone = formatter.formatCellValue(row.getCell(BookingColumnIndex.CONTACTNUMBER.getValue()));
                int rowIndex = row.getRowNum();
                int facilitatorColumn = BookingColumnIndex.FACIL.getValue();
                String facilitator = formatter.formatCellValue(row.getCell(facilitatorColumn));
                int guestSpeakerColumn = BookingColumnIndex.GUESTSPEAKER.getValue();
                String guestSpeaker = formatter.formatCellValue(row.getCell(guestSpeakerColumn));
                String course = getCourse(row);
                String workshop = course;
                int locationIndex = getLocationIndex(row);
                LocalTime startTime = getTime(formatter.formatCellValue(row.getCell(locationIndex)));
                LocalTime endTime = startTime.plusHours(1);
                String location = getLocation(row, locationIndex);
                boolean facilitatorOnly = false;
                String sheetName = sheet.getSheetName();

                // adding workshop normally (no manual override)
                if (facilitator.equals("") && guestSpeaker.equals("")) {
                    workshopList.add(new Workshop(id, school, course, location, offsite, level, pax, contactName,
                            email, phone, workshop, rowIndex, facilitatorColumn, rowIndex, guestSpeakerColumn,
                            date, startTime, endTime, facilitatorOnly, sheetName, roster));
                }
                // overriding facilitator and guest speaker for this workshop
                else {
                    //need to assign the correct shifts to the workshop
                    FacilitatorShift overrideFS = null;
                    GuestSpeakerShift overrideGSS = null;

                    // find the correct shift for the facilitator
                    for (FacilitatorShift fs : facilitatorShifts) {
                        // Hack on staff name (to capture those without a last name on file)
                        if (fs.getDate().equals(date) && fs.getStaffName().equals(facilitator)) {
                            overrideFS = fs;
                            break;
                        }
                    }
                    // find the correct shift for the guest speaker
                    for (GuestSpeakerShift gss : guestSpeakerShifts) {
                        if (gss.getDate().equals(date) && gss.getStaffName().equals(guestSpeaker)) {
                            overrideGSS = gss;
                            break;
                        }
                    }

                    //workshop with the shifts already assigned
                    Workshop overriddenWorkshop = new Workshop(id, school, course, location, offsite, level, pax,
                            contactName, email, phone, workshop,
                            rowIndex, facilitatorColumn, rowIndex, guestSpeakerColumn,
                            overrideFS, overrideGSS, date, startTime, endTime, sheetName, roster);
                    // add to workshop list
                    workshopList.add(overriddenWorkshop);
                    // add to overridden workshop list
                    overriddenWorkshops.add(overriddenWorkshop);
                }

                id++;

            }
        }

        List<List<Workshop>> workshops = new ArrayList<List<Workshop>>();
        workshops.add(workshopList);
        workshops.add(overriddenWorkshops);

        return workshops;

    }

    private LocalTime getTime(String timeString) {
        // DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h.mma");
        DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("h[.mm]a")
                .toFormatter(Locale.ENGLISH);
        LocalTime time = LocalTime.parse(timeString, timeFormatter);

        return time;
    }

    private String getCourse(Row row) {
        String course = null;
        for (int i = BookingColumnIndex.P123.getValue(); i <= BookingColumnIndex.C.getValue(); i++) {
            if (formatter.formatCellValue(row.getCell(i)).equals("1")) {
                course = BookingColumnIndex.of(i).getDisplayName();
            }
        }

        return course;
    }

    private int getLocationIndex(Row row) {
        int locationIndex = -1;
        for (int i = BookingColumnIndex.COLLINS.getValue(); i <= BookingColumnIndex.OTHER.getValue(); i++) {
            if (!formatter.formatCellValue(row.getCell(i)).equals("")) {
                locationIndex = i;
            }
        }

        return locationIndex;
    }

    private Month getMonth(Sheet sheet) {
        Month month;
        switch (sheet.getSheetName()) {
            case JAN_SHEET_NAME:
                month = Month.JANUARY;
                break;
            case FEB_SHEET_NAME:
                month = Month.FEBRUARY;
                break;
            case MAR_SHEET_NAME:
                month = Month.MARCH;
                break;
            case APR_SHEET_NAME:
                month = Month.APRIL;
                break;
            case MAY_SHEET_NAME:
                month = Month.MAY;
                break;
            case JUN_SHEET_NAME:
                month = Month.JUNE;
                break;
            case JUL_SHEET_NAME:
                month = Month.JULY;
                break;
            case AUG_SHEET_NAME:
                month = Month.AUGUST;
                break;
            case SEP_SHEET_NAME:
                month = Month.SEPTEMBER;
                break;
            case OCT_SHEET_NAME:
                month = Month.OCTOBER;
                break;
            case NOV_SHEET_NAME:
                month = Month.NOVEMBER;
                break;
            case DEC_SHEET_NAME:
                month = Month.DECEMBER;
                break;
            default:
                month = null;
        }
        return month;
    }

    private String getLocation(Row row, int locationIndex) {
        String location = null;
        if (locationIndex == BookingColumnIndex.OTHER.getValue()) {
            location = formatter.formatCellValue(row.getCell(BookingColumnIndex.LOCATION.getValue()));
        } else {
            location = BookingColumnIndex.of(locationIndex).getDisplayName();
        }

        return location;
    }

    private boolean isEmptyRow(Row row) {
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);

            if (cell != null && cell.getCellTypeEnum() != CellType.BLANK)
                return false;
        }
        return true;
    }
}
