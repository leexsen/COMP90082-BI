package au.org.thebigissue.rostering.input;

import au.org.thebigissue.rostering.errors.ImporterException;
import au.org.thebigissue.rostering.output.ExcelOutput;
import au.org.thebigissue.rostering.solver.entities.FacilitatorShift;
import au.org.thebigissue.rostering.solver.entities.GuestSpeakerShift;
import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;
import au.org.thebigissue.rostering.solver.variables.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

//Code by Robert Sharp (186477) and Andre Simmonds

/**
 * Generates the input data for the roster.
 */

//We were told by the client that we should assume the Excel worksheet was 100% accurate
//So the error checking is inadequate
//This needs to be fixed. It can be done easily would only require a few hours coding
//Basically before reading in data, do a verification of cell type. If cell type is inappropriate
//Throw a new custom Exception (make a class such as TypeMismatchException) which would be caught by
//The callable and a popup displayed. The e String fed in would contain information about the cell location
//Next the actual reading in would be a try catch structure. Within the catch, a new Exception would be
//thrown, something like UnknownCellException, and this e would contain the original e as well as cell
//location information.

//The original code was written by Robert in a rush (late stage of sprint 2) and so needs to be refactored.
//Examples of refactoring needed is replacing the large number of strings with some kind of hashmap.

//Also needed is a generic cell reading in method.

public class RosteringImporter {

    private boolean FACILITATOR = false;
    private boolean GUEST = !FACILITATOR;

    // tracks the courses that do not need a guest speaker
    ArrayList<String> nonGuestCourses;

    private final String COURSE_SHEET_NAME = "Workshops";

    private final String STAFF_SHEET_NAME = "Facilitators | GuestSpeakers";

    private final String WORKSHOP_SHEET_NAME = "Melbourne";

    //Row where start date of rostering period is recorded
    private final int START_DATE_ROW = 2;

    //Column where start date is recorded
    private final int START_DATE_COLUMN = 15;

    //Week dates
    //private LocalDate startDate;
    //private LocalDate endDate;

    //For the start of data (bookings)
    private final int WORKSHOP_DATA_START = 7;

    //For the start of data (courses)
    private final int COURSES_DATA_START = 2;

    //Max num of specific unavailabilities per staff member
    private static final int NUM_SPECIFIC_UNAVAIL = 6;

    private final String WORKSHOP_TYPE = "Workshop ";

    private final String GUEST_REQUIREMENT = "Guest Speaker Required";

    private final String FIRST_NAME = "First Name";

    private final String LAST_NAME = "Last Name";

    private final String TYPE = "Type";

    private final String GUEST_COLUMN_NAME = "Guest Speaker";

    private final String FACILITATOR_COLUMN_NAME = "Facilitator";
    private final String OFFICE_STAFF_COLUMN_NAME = "Office Staff";

    private final String SCHOOL = "School";

    private final String WORKSHOP = "Workshop";

    private final String LOCATION = "Location";

    private final String STARTTIME = "Time Begin";

    private final String ENDTIME = "Time End";

    private final String MONTH = "Month";

    private final String JAN = "January";
    private final String FEB = "February";
    private final String MAR = "March";
    private final String APR = "April";
    private final String MAY = "May";
    private final String JUN = "June";
    private final String JUL = "July";
    private final String AUG = "August";
    private final String SEP = "September";
    private final String OCT = "October";
    private final String NOV = "November";
    private final String DEC = "December";

    //No year in speadsheet
    //private final String YEAR = "Year";

    private final String DATE = "Date";

    //FIXME - why can't this be read in?
    private final int TOTAL_SHEET_NUMBER = 7;

    private final String RELIABILITY = "Reliable";
    private final String TRAINED = "Trained";

    private final String OFFSITE = "Offsite Location";
    private final String LEVEL = "Level";
    private final String PAX = "Pax";
    private final String CONTACTNAME = "Contact Name";
    private final String EMAIL = "Email";
    private final String PHONE = "Phone 1";
    private final String ID = "ID";

    private final String MON_AV_FROM = "Monday Available From";
    private final String MON_AV_UNTIL = "Monday Available Until";

    private final String TUE_AV_FROM = "Tuesday Available From";
    private final String TUE_AV_UNTIL = "Tuesday Available Until";

    private final String WED_AV_FROM = "Wednesday Available From";
    private final String WED_AV_UNTIL = "Wednesday Available Until";

    private final String THU_AV_FROM = "Thursday Available From";
    private final String THU_AV_UNTIL = "Thursday Available Until";

    private final String FRI_AV_FROM = "Friday Available From";
    private final String FRI_AV_UNTIL = "Friday Available Until";

    private final String SAT_AV_FROM = "Saturday Available From";
    private final String SAT_AV_UNTIL = "Saturday Available Until";

    private final String SUN_AV_FROM = "Sunday Available From";
    private final String SUN_AV_UNTIL = "Sunday Available Until";



    private final String KEY_MON_AV_FROM = "MON_AV_FROM";
    private final String KEY_MON_AV_UNTIL = "MON_AV_UNTIL";

    private final String KEY_TUE_AV_FROM = "TUE_AV_FROM";
    private final String KEY_TUE_AV_UNTIL = "TUE_AV_UNTIL";

    private final String KEY_WED_AV_FROM = "WED_AV_FROM";
    private final String KEY_WED_AV_UNTIL = "WED_AV_UNTIL";

    private final String KEY_THU_AV_FROM = "THU_AV_FROM";
    private final String KEY_THU_AV_UNTIL = "THU_AV_UNTIL";

    private final String KEY_FRI_AV_FROM = "FRI_AV_FROM";
    private final String KEY_FRI_AV_UNTIL = "FRI_AV_UNTIL";

    private final String KEY_SAT_AV_FROM = "SAT_AV_FROM";
    private final String KEY_SAT_AV_UNTIL = "SAT_AV_UNTIL";

    private final String KEY_SUN_AV_FROM = "SUN_AV_FROM";
    private final String KEY_SUN_AV_UNTIL = "SUN_AV_UNTIL";

    private final String SPEC_UNAVAIL = "Specific Unavailability 1 Start Date";

    private int year;

    // Shift lists, needed when manually overriding workshops
    List<FacilitatorShift> facilitatorShifts;
    List<GuestSpeakerShift> guestSpeakerShifts;

    // List of workshops that are manually overridden, needed to update the associated shift objects in roster
    List<Workshop> overriddenWorkshops = new ArrayList<>();

    private String excelFile;

    //Needed because some columns contain both numeric and string
    //Source: https://stackoverflow.com/questions/1072561/how-can-i-read-numeric-strings-in-excel-cells-as-string-not-numbers
    private String getCellAsString(Cell cell, XSSFFormulaEvaluator evaluator) {

        String string = null;

        evaluator.evaluate(cell);

        DataFormatter objDefaultFormat = new DataFormatter();

        string = objDefaultFormat.formatCellValue(cell, evaluator);

        return string;

    }


    public Roster createRoster(LocalDate rosterStartDate, LocalDate rosterEndDate, String excelFile) throws IOException {

        this.excelFile = excelFile;

        this.nonGuestCourses = noGuestCourses();

        Roster roster = new Roster();
        roster.setId(0L);

        // reads the first date and last date of the period to roster from worksheet
        // (TODO replaced by input from GUI eventually)
        importWeekDates();

        year = rosterStartDate.getYear();

        AvailabilityImporter availabilityImporter = new AvailabilityImporter(excelFile);
        availabilityImporter.importAvailability(rosterStartDate);

        ArrayList<Staff> facilitators = availabilityImporter.getFacilitatorList();
        ArrayList<Staff> guestSpeakers = availabilityImporter.getGuestspeakerList();

        /*
        // need as many dummy guest speakers as there are courses that don't require a guest speaker
        String[] noGuestCourseList = getNoGuestCourseList();
        ArrayList<Staff> dummyGuests = dummyList(noGuestCourseList);
        // add dummy guests to the guestSpeakers list
        guestSpeakers.addAll(dummyGuests);
        //System.out.println("***************************************************************************\n" +
        // Arrays.toString(guestSpeakers.toArray()));
        */

        roster.setShiftList(facilitators, guestSpeakers, rosterStartDate, rosterEndDate);
        facilitatorShifts = roster.getFacilitatorShiftList();
        guestSpeakerShifts = roster.getGuestSpeakerShiftList();

        roster.setWorkshopList(importWorkshops(rosterStartDate, rosterEndDate, roster));

        //updating shifts in roster for the workshops that were overridden
        if(overriddenWorkshops.size() > 0)
            roster.updateOverrideShifts(overriddenWorkshops);

        return roster;
    }

    // This imports the facilitators first and last names and their availability
    private ArrayList<Staff> importStaff(Boolean staffType, LocalDate rosterStartDate) {

        String[] availabilityTags = {
                KEY_MON_AV_FROM,
                KEY_MON_AV_UNTIL,
                KEY_TUE_AV_FROM,
                KEY_TUE_AV_UNTIL,
                KEY_WED_AV_FROM,
                KEY_WED_AV_UNTIL,
                KEY_THU_AV_FROM,
                KEY_THU_AV_UNTIL,
                KEY_FRI_AV_FROM,
                KEY_FRI_AV_UNTIL,
                KEY_SAT_AV_FROM,
                KEY_SAT_AV_UNTIL,
                KEY_SUN_AV_FROM,
                KEY_SUN_AV_UNTIL
        };


        int firstNameColumnIndex = -1;
        int lastNameColumnIndex = -1;
        int typeColumnIndex = -1;
        int monAvFromIndex = -1;
        int specUnavailIndex = -1;
        int reliabilityColumnIndex = -1;
        int trainedColumnIndex = -1;

        ArrayList<Staff> staffList = new ArrayList<Staff>();

        int staffSheetIndex = -1;

        FileInputStream fis = null;

        try {

            fis = new FileInputStream(excelFile);
            //fis = new FileInputStream(BIR_FILE_PATH);
            Workbook workbook = new XSSFWorkbook(fis);

            for (int i = 0; i < TOTAL_SHEET_NUMBER; i++) {

                if (workbook.getSheetName(i).equals(STAFF_SHEET_NAME)) {

                    staffSheetIndex = i;
                    break;

                }

            }

            //FIXME -- Error for can't find the staff sheet
            if (staffSheetIndex == -1) {

                //throw new Exception();

            }

            Sheet facilitatorSheet = workbook.getSheetAt(staffSheetIndex);

            Row firstRow = facilitatorSheet.getRow(0);
            Iterator cellIterator = firstRow.cellIterator();

            while (cellIterator.hasNext()) {

                //FIXME -- Why does it need to be cast?
                Cell cell = (Cell) cellIterator.next();

                if (cell.getStringCellValue().equals(FIRST_NAME)) {
                    firstNameColumnIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(LAST_NAME)) {
                    lastNameColumnIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(TYPE)) {
                    typeColumnIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(RELIABILITY)) {
                    reliabilityColumnIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(TRAINED)) {
                    trainedColumnIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(MON_AV_FROM)) {
                    monAvFromIndex = cell.getColumnIndex();
                } else if (cell.getStringCellValue().equals(SPEC_UNAVAIL)) {
                    specUnavailIndex = cell.getColumnIndex();

                }


            }
            //FIXME -- How do this better? Custom exception?
            if ((typeColumnIndex == -1) || (firstNameColumnIndex == -1) || (lastNameColumnIndex == -1)
                    || (monAvFromIndex == -1) || (specUnavailIndex == -1)) {

                //throw new Exception();


            }

            //Go through the rows
            Iterator rowIterator = facilitatorSheet.rowIterator();

            String cellContent;

            //FIXME -- Why is this neccessary?
            rowIterator.next();

            //FIXME -- Should we break once we reach a blank cell?
            while (rowIterator.hasNext()) {

                //FIXME -- Why is there a cast?
                Row currentRow = (Row) rowIterator.next();

                //FIXME -- Do some error checking
                cellContent = currentRow.getCell(typeColumnIndex).getStringCellValue();

                //FIXME -- Better way to get ending?
                if (cellContent.equals("")) {break;}



                //String id = currentRow.getCell(firstNameColumnIndex).getStringCellValue() +
                //        currentRow.getCell(lastNameColumnIndex).getStringCellValue();

                String firstName = currentRow.getCell(firstNameColumnIndex).getStringCellValue();

                String lastName = currentRow.getCell(lastNameColumnIndex).getStringCellValue();

                int dayIndex;
                int dayCount;

                Availability availability = new Availability();
                DataFormatter formatter = new DataFormatter();


                for (dayIndex=1; dayIndex<=7; dayIndex++) {

                    double availableFromValue,availableToValue;
                    // flag for checking if staff member has a specific unavailability on this day
                    boolean unavailable = false;

                    // assign a date to day
                    //LocalDate dayDate = startDate.plusDays(dayIndex-1);
                    LocalDate dayDate = rosterStartDate.plusDays(dayIndex-1);

                    // iterate over all specific unavailabilities
                    for(int i = 0; i < NUM_SPECIFIC_UNAVAIL; i++){

                        String unavailStart = formatter.formatCellValue(currentRow.getCell(specUnavailIndex+3*i));
                        String unavailEnd = formatter.formatCellValue(currentRow.getCell(specUnavailIndex+3*i+1));

                        // if no specific unavailability input in this slot, skip it
                        if(unavailStart.equals("")){
                            continue;
                        }
                        // if start is defined, but no end date defined
                        else {
                            if(unavailEnd.equals("")){
                                //TODO exception / error state - no ending to an unavailability
                            }
                        }

                        //String unavailReason = formatter.formatCellValue(currentRow.getCell(specUnavailIndex+3*i+2));
                        int currentMonth = dayDate.getMonth().getValue();
                        String firstDateNum = unavailStart.split("/")[0];

                        LocalDate unavailStartDate;
                        LocalDate unavailEndDate;

                        // reversed date format from excel file
                        if(currentMonth == Integer.parseInt(firstDateNum)){
                            unavailStartDate = parseDateReversed(unavailStart);
                            unavailEndDate = parseDateReversed(unavailEnd);
                        }
                        // standard date format from excel file
                        else{
                            unavailStartDate = parseDate(unavailStart);
                            unavailEndDate = parseDate(unavailEnd);
                        }

                        //end date falls before start date error
                        if(unavailStartDate.isAfter(unavailEndDate)){
                            //TODO exception / error state for start date falling after end date
                        }
                        if(isUnavailableDate(unavailStartDate, unavailEndDate, dayDate)){
                            unavailable = true;
                            break;
                        }
                    }

                    // if unavailability matches day's date, set availability to 0
                    if(unavailable){
                        availableFromValue = 0;
                        availableToValue = 0;
                    }
                    // otherwise, check weekly availability
                    else {
                        availableFromValue = currentRow.getCell(
                                monAvFromIndex + 2*dayIndex -2).getNumericCellValue();
                        availableToValue = currentRow.getCell(
                                monAvFromIndex + 2*dayIndex -1).getNumericCellValue();
                    }

                    // staff member presumed unavailable all day
                    if (availableFromValue == 0 || availableToValue == 0) {
                        continue;
                    }

                    LocalTime availableFrom = getTime(availableFromValue);
                    LocalTime availableTo = getTime(availableToValue);


                    //if ((timeFromHourFrom!=0)&&(timeFromHourFrom!=0)) {

                    DayOfWeek day = DayOfWeek.of(dayIndex);

                    //FIXME -- oh no!
                    availability.update(day, availableFrom, availableTo);
                }

                String[] trained;
                String trainedCourses = currentRow.getCell(trainedColumnIndex).getStringCellValue();
                trained = trainedCourses.split(",");

                if ((cellContent.equalsIgnoreCase(FACILITATOR_COLUMN_NAME) || cellContent.equalsIgnoreCase(OFFICE_STAFF_COLUMN_NAME))&&staffType==FACILITATOR) {

                    //FIXME -- should we check to ensure it is unique?

                    if (cellContent.equalsIgnoreCase(OFFICE_STAFF_COLUMN_NAME)) {
                        staffList.add(new Facilitator(firstName, lastName, availability, 6, trained, true));
                    } else {
                        staffList.add(new Facilitator(firstName, lastName, availability, 6, trained, false));
                    }

                }

                if ((cellContent.equalsIgnoreCase(GUEST_COLUMN_NAME))&&(staffType==GUEST)) {

                    int reliability = (int) currentRow.getCell(reliabilityColumnIndex).getNumericCellValue();

                    // default value if cell is empty (i.e. 0)
                    if (reliability == 0) {
                        reliability = 2;
                    }

                    //FIXME -- should we check to ensure it is unique?
                    staffList.add(new GuestSpeaker(firstName, lastName, availability, 6, trained, reliability));

                }

            }

        }

        //FIXME, println redundant?
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("No file!");

        }

        //FIXME - what is IO?
        catch (IOException e) {

            System.out.println("Oops!");

        }


        for (Staff staff : staffList) {

        }
        return staffList;

    }

    /** returns whether a date is within a specific unavailability range */
    private boolean isUnavailableDate(LocalDate start, LocalDate end, LocalDate day){
        if(day.isBefore(end) && day.isAfter(start)){
            return true;
        }
        if(day.equals(start) || day.equals(end)){
            return true;
        }
        return false;
    }

    private LocalTime getTime(double timeValue) {

        DataFormatter df = new DataFormatter();

        // 0x14 "h:mm"; see  https://poi.apache.org/apidocs/dev/org/apache/poi/ss/usermodel/BuiltinFormats.html
        String timeString = df.formatRawCellContents(timeValue, 0x14, "hh:mm");


        LocalTime time = LocalTime.parse(timeString);

        return time;

    }

    private void importWeekDates() {
        int workshopSheetIndex = -1;

        FileInputStream fis = null;

        try {
            //fis = new FileInputStream(BIR_FILE_PATH);
            fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);

            for (int i = 0; i < TOTAL_SHEET_NUMBER; i++) {


                if (workbook.getSheetName(i).equals(WORKSHOP_SHEET_NAME)) {

                    //FIXME -- put in its own function
                    //Set the sheet for output to Excel later
                    ExcelOutput.setSheet(i);

                    workshopSheetIndex = i;
                    break;

                }

            }


            //FIXME -- Error for can't find the staff sheet
            if (workshopSheetIndex == -1) {

                //throw new Exception();

            }


            /*Sheet workshopSheet = workbook.getSheetAt(workshopSheetIndex);

            DataFormatter formatter = new DataFormatter();

            // Retrieving date from workshop sheet
            Row dateRow = workshopSheet.getRow(START_DATE_ROW - 1); //Subtract 1 because starts at zero
            Cell startDateCell = dateRow.getCell(START_DATE_COLUMN - 1);
            Cell endDateCell = dateRow.getCell(START_DATE_COLUMN); //End date is in the adjacent cell
            // first as a string,
            String startDateStr = formatter.formatCellValue(startDateCell);
            String endDateStr = formatter.formatCellValue(endDateCell);
            // then parse to a local date object
            startDate = parseDate(startDateStr);
            endDate = parseDate(endDateStr);*/
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private List<Workshop> importWorkshops(LocalDate rosterStartDate, LocalDate rosterEndDate, Roster roster) {
        List<Workshop> workshopList = new ArrayList<>();

        //Create a dictionary for months

        int i;

        int workshopSheetIndex = -1;

        FileInputStream fis = null;

        try {

            //fis = new FileInputStream(BIR_FILE_PATH);
            fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);

            //Source: https://stackoverflow.com/questions/1072561/how-can-i-read-numeric-strings-in-excel-cells-as-string-not-numbers
            XSSFFormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook)workbook);


            for (i = 0; i < TOTAL_SHEET_NUMBER; i++) {



                if (workbook.getSheetName(i).equals(WORKSHOP_SHEET_NAME)) {

                    workshopSheetIndex = i;
                    break;

                }

            }



            //FIXME -- Error for can't find the staff sheet
            if (workshopSheetIndex == -1) {

                //throw new Exception();

            }

            Sheet workshopSheet = workbook.getSheetAt(workshopSheetIndex);

            // Processing the rest of workshop sheet data
            Row firstRow = workshopSheet.getRow(WORKSHOP_DATA_START-1);
            Iterator cellIterator = firstRow.cellIterator();

            int dateColumn = -1;
            int startTimeColumn = -1;
            int locationColumn = -1;
            int workshopColumn = -1;
            int schoolColumn = -1;
            int endTimeColumn = -1;
            int facilitatorColumn = -1;
            int guestSpeakerColumn = -1;

            int offsiteColumnIndex = -1;
            int levelColumnIndex = -1;
            int paxColumnIndex = -1;
            int contactNameColumnIndex = -1;
            int emailColumnIndex = -1;
            int phoneColumnIndex = -1;
            int idColumnIndex = -1;


            //Find the Columns
            while (cellIterator.hasNext()) {

                //FIXME -- Why does it need to be cast?
                Cell cell = (Cell) cellIterator.next();

                String cellData;

                int column;

                CellType type = cell.getCellTypeEnum();
                if (type == CellType.STRING) {


                    cellData = cell.getStringCellValue();
                    column = cell.getColumnIndex();




                    if (cellData.equals(SCHOOL)) {

                        schoolColumn = column;

                    }

                    if (cellData.equals(WORKSHOP)) {

                        workshopColumn = column;

                    }

                    if (cellData.equals(LOCATION)) {

                        locationColumn = column;

                    }

                    if (cellData.equals(DATE)) {

                        dateColumn = column;

                    }


                    if (cellData.equals(STARTTIME)) {


                        startTimeColumn = column;

                    }

                    if (cellData.equals(ENDTIME)) {

                        endTimeColumn = column;

                    }

                    if (cellData.equals(FACILITATOR_COLUMN_NAME)) {

                        facilitatorColumn = column;

                    }

                    if (cellData.equals(GUEST_COLUMN_NAME)) {

                        guestSpeakerColumn = column;

                    }

                    if (cellData.equals(OFFSITE)) {
                        offsiteColumnIndex = column;
                    }

                    if (cellData.equals(LEVEL)) {
                        levelColumnIndex = column;
                    }

                    if (cellData.equals(PAX)) {
                        paxColumnIndex = column;
                    }
                    if (cellData.equals(CONTACTNAME)) {
                        contactNameColumnIndex = column;
                    }
                    if (cellData.equals(EMAIL)) {
                        emailColumnIndex = column;
                    }
                    if (cellData.equals(PHONE)) {
                        phoneColumnIndex = column;
                    }
                    if (cellData.equals(ID)) {
                        idColumnIndex = column;
                    }
                }
            }


            if ((dateColumn == -1)||(startTimeColumn == -1)||(locationColumn == -1)|| (facilitatorColumn ==-1) ||
                    (guestSpeakerColumn == -1) || (workshopColumn == -1)||(schoolColumn == -1)||
                    ( offsiteColumnIndex == -1) ||( levelColumnIndex == -1) ||( paxColumnIndex == -1)){

                throw new ImporterException("One of the columns in the Bookings sheet was not found.");

            }

            //Start pulling data

            Iterator rowIterator = workshopSheet.iterator();


            //Skip the header
            int rowskip = WORKSHOP_DATA_START-1;

            //FIXME -- sort out after excel finalised
            rowskip++;

            while(rowIterator.hasNext() && rowskip > 0) {

                rowskip--;
                rowIterator.next();

            }

            while(rowIterator.hasNext()) {


                String school = null;
                String course = null;
                String location = null;
                String facilitator = null;
                String guestSpeaker = null;
                long id;
                LocalDate date;
                LocalTime startTime;
                LocalTime endTime;

                String offsite = null;
                String level = null;
                String pax = null;
                String contactName = null;
                String email = null;
                String phone = null;

                String workshop = null;

                boolean facilitatorOnly = false;

                String tempMonth = null;

                //FIXME -- Why the cast?
                Row row = (Row)rowIterator.next();

                int rowIndex = row.getRowNum();

                //FIXME -- error detection if put number in string column? or missing values?
                school = row.getCell(schoolColumn).getStringCellValue();


                //FIXME -- better way to break out of the loop when you run out of data?
                if (school.equals("")) {break;}

                course = row.getCell(workshopColumn).getStringCellValue();

                location = row.getCell(locationColumn).getStringCellValue();


                /*DataFormatter formatter = new DataFormatter();
                String dateString = formatter.formatCellValue(row.);
                System.out.println(dateString);
                date = parseDate(dateString);*/

                Date tmpdate = row.getCell(dateColumn).getDateCellValue();

                date = tmpdate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();


                //FIXME -- Prevent the dates from going in
                //LocalDate rosterStartDate, LocalDate rosterEndDate
                if (date.isBefore(rosterStartDate) || date.isAfter(rosterEndDate) )
                {continue;}

                startTime = getTime(row.getCell(startTimeColumn).getNumericCellValue());

                endTime = getTime(row.getCell(endTimeColumn).getNumericCellValue());

                facilitator = row.getCell(facilitatorColumn).getStringCellValue();

                guestSpeaker = row.getCell(guestSpeakerColumn).getStringCellValue();

                workshop = row.getCell(workshopColumn).getStringCellValue();


                //String tempID = row.getCell(idColumnIndex).getStringCellValue();
                double tempID = row.getCell(idColumnIndex).getNumericCellValue();

                id = (long) tempID;


                offsite = getCellAsString(row.getCell(offsiteColumnIndex), objFormulaEvaluator);
                level = getCellAsString(row.getCell(levelColumnIndex), objFormulaEvaluator);
                pax = getCellAsString(row.getCell(paxColumnIndex), objFormulaEvaluator);
                contactName = getCellAsString(row.getCell(contactNameColumnIndex), objFormulaEvaluator);
                email = getCellAsString(row.getCell(emailColumnIndex), objFormulaEvaluator);
                phone = getCellAsString(row.getCell(phoneColumnIndex), objFormulaEvaluator);

                facilitatorOnly = isNonGuestWorkshop(course);


                // adding workshop normally (no manual override)
                if(facilitator.equals("") && guestSpeaker.equals("")) {
                    workshopList.add(new Workshop(id, school, course, location, offsite, level, pax, contactName,
                            email, phone, workshop, rowIndex, facilitatorColumn, rowIndex, guestSpeakerColumn,
                            date, startTime, endTime, facilitatorOnly, roster));
                }
                // overriding facilitator and guest speaker for this workshop
                else{
                    //need to assign the correct shifts to the workshop
                    FacilitatorShift overrideFS = null;
                    GuestSpeakerShift overrideGSS = null;

                    // find the correct shift for the facilitator
                    for(FacilitatorShift fs : facilitatorShifts){
                        // Hack on staff name (to capture those without a last name on file)
                        if(fs.getDate().equals(date) &&
                                (fs.getStaffName().equals(facilitator) || fs.getStaffName().equals(facilitator + " "))){
                            overrideFS = fs;
                            break;
                        }
                    }
                    // find the correct shift for the guest speaker
                    for(GuestSpeakerShift gss : guestSpeakerShifts) {
                        if (gss.getDate().equals(date) &&
                                (gss.getStaffName().equals(guestSpeaker) ||
                                        gss.getStaffName().equals(guestSpeaker + " "))) {
                            overrideGSS = gss;
                            break;
                        }
                    }

                    //workshop with the shifts already assigned
                    Workshop overriddenWorkshop = new Workshop(id, school, course, location, offsite, level, pax,
                            contactName, email, phone, workshop,
                            rowIndex, facilitatorColumn, rowIndex, guestSpeakerColumn,
                            overrideFS, overrideGSS, date, startTime, endTime);
                    // add to workshop list
                    workshopList.add(overriddenWorkshop);
                    // add to overridden workshop list
                    overriddenWorkshops.add(overriddenWorkshop);
                }
                //FIXME -- This should be read in
                //id2++;
            }

        }

        //FIXME, println redundant?
        catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("No file!");

        }

        //FIXME - what is IO?
        catch (IOException e) {

            System.out.println("Oops!");

        }

        return workshopList;

    }

    /** Takes a date string in d/M/yy format and parses it to a LocalDate object,
     *  with format yyyy-MM-dd.
     * @param startDateStr: date as a string
     * @return date as LocalDate object
     */
    private LocalDate parseDate(String startDateStr) {
        // needed to parse from d/M/yy format to local date object
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("d/M/yy");
        return LocalDate.parse(startDateStr,dTF);

    }

    /** Takes a date string in M/d/yy format and parses it to a LocalDate object,
     *  with format yyyy-MM-dd.
     * @param startDateStr: date as a string
     * @return date as LocalDate object
     */
    private LocalDate parseDateReversed(String startDateStr) {
        // needed to parse from d/M/yy format to local date object
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("M/d/yy");
        return LocalDate.parse(startDateStr,dTF);

    }

    /** returns list of names of all courses that do not require a guest speaker */
    private ArrayList<String> noGuestCourses(){

        ArrayList<String> noGuestCourses = new ArrayList<>();

        int courseSheetIndex = -1;

        FileInputStream fis = null;

        try {

            //fis = new FileInputStream(BIR_FILE_PATH);
            fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);

            //Source: https://stackoverflow.com/questions/1072561/how-can-i-read-numeric-strings-in-excel-cells-as-string-not-numbers
            XSSFFormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);

            for (int i = 0; i < TOTAL_SHEET_NUMBER; i++) {


                if (workbook.getSheetName(i).equals(COURSE_SHEET_NAME)) {

                    courseSheetIndex = i;
                    break;

                }

            }

            if (courseSheetIndex == -1) {
                throw new ImporterException("Workshop Sheet could not be found.");
            }

            Sheet courseSheet = workbook.getSheetAt(courseSheetIndex);

            // Processing the rest of workshop sheet data
            Row firstRow = courseSheet.getRow(COURSES_DATA_START - 1);
            Iterator cellIterator = firstRow.cellIterator();

            int courseNameCol = -1;
            int guestSpeakerCol = -1;
            //Find the Column
            while (cellIterator.hasNext()) {

                Cell cell = (Cell) cellIterator.next();

                String cellData;

                int column;

                CellType type = cell.getCellTypeEnum();
                if (type == CellType.STRING) {


                    cellData = cell.getStringCellValue();
                    column = cell.getColumnIndex();

                    if (cellData.equals(WORKSHOP_TYPE)) {
                        courseNameCol = column;

                    }
                    else if(cellData.equals(GUEST_REQUIREMENT)){
                        guestSpeakerCol = column;
                    }

                }
            }

            if (courseNameCol == -1) {
                throw new ImporterException("Workshop Column in the Workshop Sheet could not be found.");
            }

            if(guestSpeakerCol == -1){
                throw new ImporterException("Guest Speaker Requirement Column in the Workshop Sheet" +
                        " could not be found.");
            }

            Iterator rowIterator = courseSheet.iterator();


            // Skip the header
            int rowskip = COURSES_DATA_START - 1;

            //FIXME -- sort out after excel finalised
            rowskip++;

            while (rowIterator.hasNext() && rowskip > 0) {

                rowskip--;
                rowIterator.next();

            }

            while (rowIterator.hasNext()) {
                String course;

                Row row = (Row) rowIterator.next();

                course = row.getCell(courseNameCol).getStringCellValue();

                Boolean guestRequirement = row.getCell(guestSpeakerCol).getBooleanCellValue();

                if(!guestRequirement){
                    noGuestCourses.add(course);
                }
            }

            return noGuestCourses;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String[] getNoGuestCourseList() {

        List<Workshop> workshopList = new ArrayList<>();
        ArrayList<String> noGuestCourseList = new ArrayList<>();

        //Create a dictionary for months

        int i;

        int workshopSheetIndex = -1;

        FileInputStream fis = null;

        try {

            //fis = new FileInputStream(BIR_FILE_PATH);
            fis = new FileInputStream(excelFile);
            Workbook workbook = new XSSFWorkbook(fis);

            //Source: https://stackoverflow.com/questions/1072561/how-can-i-read-numeric-strings-in-excel-cells-as-string-not-numbers
            XSSFFormulaEvaluator objFormulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);

            for (i = 0; i < TOTAL_SHEET_NUMBER; i++) {


                if (workbook.getSheetName(i).equals(WORKSHOP_SHEET_NAME)) {

                    workshopSheetIndex = i;
                    break;

                }

            }

            if (workshopSheetIndex == -1) {
                // TODO need to find sheet by City name & include this in the exception message
                throw new ImporterException("Bookings Sheet could not be found.");
            }

            Sheet workshopSheet = workbook.getSheetAt(workshopSheetIndex);

            // Processing the rest of workshop sheet data
            Row firstRow = workshopSheet.getRow(WORKSHOP_DATA_START - 1);
            Iterator cellIterator = firstRow.cellIterator();

            int workshopColumn = -1;

            //Find the Column
            while (cellIterator.hasNext()) {

                Cell cell = (Cell) cellIterator.next();

                String cellData;

                int column;

                CellType type = cell.getCellTypeEnum();
                if (type == CellType.STRING) {

                    cellData = cell.getStringCellValue();
                    column = cell.getColumnIndex();

                    if (cellData.equals(WORKSHOP)) {

                        workshopColumn = column;

                    }

                }
            }

            if (workshopColumn == -1) {
                throw new ImporterException("Workshop Column in the Booking Sheet could not be found.");
            }

            Iterator rowIterator = workshopSheet.iterator();


            // Skip the header
            int rowskip = WORKSHOP_DATA_START - 1;

            //FIXME -- sort out after excel finalised
            rowskip++;

            while (rowIterator.hasNext() && rowskip > 0) {

                rowskip--;
                rowIterator.next();

            }

            while (rowIterator.hasNext()) {
                String course;
                Row row = (Row) rowIterator.next();

                course = row.getCell(workshopColumn).getStringCellValue();

                // stops looking for courses when it hits empty cell
                if(course.equals("")){
                    break;
                }

                if(nonGuestCourses.contains(course)){
                    noGuestCourseList.add(course);
                }
            }

            return noGuestCourseList.toArray(new String[noGuestCourseList.size()]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // generates n dummy guests and returns them in a list
    private ArrayList<Staff> dummyList(String[] courseList){
        ArrayList<Staff> dummyGuests = new ArrayList<>();
        for(int i = 0 ; i < courseList.length ; i++){
            dummyGuests.add(new DummyGuest(courseList));
        }
        return dummyGuests;
    }

    // used in DRL rule to find if a course name matches on of those in nonGuest-course list
    public boolean isNonGuestWorkshop(String course){
        if(nonGuestCourses.contains(course)){
            return true;
        }
        return false;
    }

}
