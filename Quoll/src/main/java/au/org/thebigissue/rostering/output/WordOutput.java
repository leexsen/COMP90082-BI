package au.org.thebigissue.rostering.output;

import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.Indictment;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * Code by Robert Sharp 186477
 * Date: 27th October 2019
 * Handles the output of roster in Word form
 * At the moment, does not output shifts
 * Reads in a template word file and replaces tags beginning with $ with values from the roster
 */

public class WordOutput {

    private static int HEADER_POS = 1;
    private static int STAFF_HEADER_POS = 2;
    private static int FEASIBLE_HEADER_POS = 2;
    private static int SWATCHNO = 9;

    private static String[] keys = {"$Date","$Time","$Guest","$Facilitator","$School","$Workshop", "$Location", "$Pax",
            "$Level","$Contact","$Phone","$email"};

    //This takes the bottom row and duplicates it
    //Note this means that tables cannot have a footer
    private static void addRow(int target, XWPFTable table) throws Exception {

        XWPFTableRow targetRow = table.getRow(target);

        CTRow ctrow = CTRow.Factory.parse(targetRow.getCtRow().newInputStream());
        XWPFTableRow newRow = new XWPFTableRow(ctrow, table);

        table.addRow(newRow);

    }

    //To overcome error in library
    //This was the only limitation found with the library
    //Basically without using the workaround given in the stack overflow thread, the tables did not update properly
    //Source:
    //https://stackoverflow.com/questions/53211749/cant-change-row-text-in-docx-file-once-row-is-added-to-table
    private static void commitTable(XWPFTable table) {

        int rowNumber = 0;
            for(
        XWPFTableRow row :table.getRows())

        {
            table.getCTTbl().setTrArray(rowNumber++, row.getCtRow());
        }

    }

    //Replace the row text
    //Replaces the tag with text
    //This is somewhat brittle
    //If the cell contained a spelling mistake, or different font, then only part of the cell would be read in
    //making a match impossible for tags such as $Mizpell4d and only possible for tags like $Spelling
    private static void replaceRowText(int targetRow, XWPFTable table, String tag, String text) {


        //System.out.println("key:" + tag +" result: "+text);

        //Source: https://stackoverflow.com/questions/22268898/replacing-a-text-in-apache-poi-xwpf

        /*
        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {*/

        XWPFTableRow row = table.getRows().get(targetRow);

        List<XWPFTableCell> cells = row.getTableCells();
        for (XWPFTableCell cell : cells) {

            //System.out.println(cell.getText());

            for (XWPFParagraph p : cell.getParagraphs()) {
                for (XWPFRun r : p.getRuns()) {

                    /*if (r.getText(0).equals("$")) {
                        System.out.println("We have a problem!");
                    }*/

                    //This is used for debugging
                    if ((r.getText(0)!=null)) {
                        //System.out.println("Not activated Text: "+r.getText(0));
                        //System.out.println("Not activated! key:" + tag +" result: "+text);
                    }

                    //Make sure not null and that run corresponds to the tag
                    if ((r.getText(0)!=null)&&(r.getText(0).equals(tag))) {
                        //System.out.println("To be Replaced Text: "+r.getText(0));
                        //System.out.println("Replaced! key:" + tag +" result: "+text);
                        r.setText(text, 0);
                    }
                }
            }
        }
    }

    //Replace the color of the row
    private static void changeRowColor(int targetRow, XWPFTable table, String color) {

        XWPFTableRow row = table.getRow(targetRow);

        List<XWPFTableCell> cells = row.getTableCells();

        for (XWPFTableCell cell : cells) {

            cell.getCTTc().addNewTcPr().addNewShd().setFill(color);
            //cell.setText("Changed!");

        }
    }


    //This gets the mapswatch from the template file
    //It needs to be refactored because it also prints the hex values of the colors in the table
    //This is a leftover from the early stage of coding and debugging
    private static HashMap<String, String[]> printColors(XWPFTable table) {

        String[] keySwatches = {
                "KEY_MON",

                "KEY_TUE",

                "KEY_WED",

                "KEY_THU",

                "KEY_FRI",

                "KEY_SAT",

                "KEY_SUN",

                "KEY_STAFF",

                "KEY_GUEST"
        };


        HashMap<String, String[]> mapSwatch = new HashMap<String, String[]>();

        int i;

        for (i=0; i<SWATCHNO; i++) {

            String key = keySwatches[i];

            String[] values = {"","",""};

            mapSwatch.put(key, values);

        }

        int row_index;

        for (row_index=0; row_index<3; row_index++) {

            XWPFTableRow row = table.getRow(row_index);

            List<XWPFTableCell> cells = row.getTableCells();

            int column_index = 0;

            for (XWPFTableCell cell : cells) {

                String color = cell.getColor();
                cell.setText(color);
                //System.out.println(color);

                String key = keySwatches[column_index];

                String[] values = mapSwatch.get(key);

                values[row_index] = color;

                mapSwatch.put(key, values);

                //System.out.println("key: " + key + " value: "+color);

                column_index++;

            }

        }

        commitTable(table);

        return(mapSwatch);

    }

    //This produces a set of staff
    //So basically we can do later "for every staff member in this set, lookup their workshops"
    private static LinkedHashSet<String> getStaff(Roster roster) {

        LinkedHashSet<String> staffSet = new LinkedHashSet<String>();
        //Set<String> dateSet = new HashSet<String>();

        //For every workshop

        for (Workshop workshop : roster.getWorkshopList()) {

            //Get the staff members name
            //Then add it to the set
            staffSet.add(workshop.getStaffString());
            //staffSet.add(workshop.getGuestString());

        }

        //Return the set
        return(staffSet);

    }

    //Same as above, except for guests.
    //Should be refactored into the same method with a boolean as input to choose what kind
    //of staff to return
    private static LinkedHashSet<String> getGuest(Roster roster) {

        LinkedHashSet<String> staffSet = new LinkedHashSet<String>();
        //Set<String> dateSet = new HashSet<String>();

        //For every workshop

        for (Workshop workshop : roster.getWorkshopList()) {

            //Get the staff members name
            //Then add it to the set
            //staffSet.add(workshop.getStaffString());
            staffSet.add(workshop.getGuestString());

        }

        //Return the set
        return(staffSet);

    }

    //Get a set of dates. This is so we can later print the workshops by day
    //private static Set<String> getDates(Roster roster) {
    private static LinkedHashSet<String> getDates(Roster roster) {

        LinkedHashSet<String> dateSet = new LinkedHashSet<String>();
        //Set<String> dateSet = new HashSet<String>();

        //For every workshop

        for (Workshop workshop : roster.getWorkshopList()) {

            //Get the date
            //Then add it to the set
            dateSet.add(workshop.getDateString());

        }

        //Return the set
        return(dateSet);

    }


    //Adds a table for each day in the roster
    public static void addRosterDateTable(XWPFDocument document, XWPFTable templateTable, Roster roster,
                                          String date, HashMap<String, String[]> mapSwatch, int position) throws Exception {

        String day = null;

        //Get the day
        for (Workshop workshop : roster.getWorkshopList()) {

            if (workshop.getDateString().equals(date)) {

                day = workshop.getDay();

            }

        }
        //Get the key

        String dayKey;

        dayKey = "KEY_"+day.toUpperCase();

        //Get the colors

        String colorHeader=mapSwatch.get(dayKey)[0];
        String colorRow1=mapSwatch.get(dayKey)[1];
        String colorRow2=mapSwatch.get(dayKey)[2];

        //Make the table
        //Source: http://apache-poi.1045710.n5.nabble.com/How-to-copy-a-table-and-modify-it-correctly-in-the-same-docx
        // -file-using-POI-td5716551.html
        CTTbl ctTbl = CTTbl.Factory.newInstance(); // Create a new CTTbl for the new table
        ctTbl.set(templateTable.getCTTbl()); // Copy the template table's CTTbl
        XWPFTable copiedTable = new XWPFTable(ctTbl, document); // Create a new table using the CTTbl upon

        //Change the header color
        changeRowColor(HEADER_POS, copiedTable, colorHeader);

        //Replace title text
        replaceRowText(0, copiedTable, "$Day", day);

        //For setting the rows
        int count = 0;

        boolean odd = true;

        //String[] keys = {"$Date","$Time","$Guest","$Facilitator","$School"};
        //String[] keys = {"$Date","$Time","$Guest","$Facilitator","$School","$Workshop", "$Location", "$Pax",
        //        "$Level","$Date2","$Time2","$email"};

        for (Workshop workshop : roster.getWorkshopList()) {

            //System.out.println("date: "+date);

            if (workshop.isDate(date)) {

                //System.out.println("Day: " + workshop.getWorkshopDictionary().get("$Day"));
                //System.out.println("Date: " + workshop.getWorkshopDictionary().get("$Date"));
                //System.out.println("");
                count++;

                //Copy a template row from last
                addRow(HEADER_POS + count, copiedTable);

                //Now replace that template row, copied row is new template row
                //replaceRowText(HEADER_POS + count, copiedTable, "$Time", workshop.getWorkshopDictionary().get("$Time"));

                for (String key : keys) {
                    replaceRowText(HEADER_POS + count, copiedTable, key, workshop.getWorkshopDictionary().get(key));
                }

                //Change color
                if (odd) {

                    changeRowColor(HEADER_POS + count, copiedTable, colorRow1);

                }

                else {

                    changeRowColor(HEADER_POS + count, copiedTable, colorRow2);
                }
                odd = !odd;
            }

        }

        //Remove the extra row at the end
        copiedTable.removeRow(HEADER_POS+count+1);

        //System.out.println(count);

        /*
        //Change the row colors
        for () {
            changeRowColor(HEADER_POS + 1, copiedTable, rowColor2);
            changeRowColor(HEADER_POS + 2, copiedTable, rowColor1);
        }*/

        commitTable(copiedTable);

        document.createParagraph();
        document.createTable();
        document.setTable(position, copiedTable);

    }

    //Same as above, except printing table for each staff member. Uses a boolean value for guest. The template table
    //fed in (templateTable) will be different depending on whether a guest list or staff member list is being printed
    public static void addRosterStaffTable(XWPFDocument document, XWPFTable templateTable, Roster roster,
                                          String name, HashMap<String, String[]> mapSwatch, int position, boolean isGuest) throws Exception {

        String colorHeader = null;
        String colorRow1 = null;
        String colorRow2 = null;

        //Get colors for table
        //Different colors used for staff versus guest for readibility
        if (isGuest) {
            colorHeader = mapSwatch.get("KEY_GUEST")[0];
            colorRow1 = mapSwatch.get("KEY_GUEST")[1];
            colorRow2 = mapSwatch.get("KEY_GUEST")[2];
        }
        else {
            colorHeader = mapSwatch.get("KEY_STAFF")[0];
            colorRow1 = mapSwatch.get("KEY_STAFF")[1];
            colorRow2 = mapSwatch.get("KEY_STAFF")[2];
        }

        //Make the table
        //Source: http://apache-poi.1045710.n5.nabble.com/How-to-copy-a-table-and-modify-it-correctly-in-the-same-docx
        // -file-using-POI-td5716551.html
        CTTbl ctTbl = CTTbl.Factory.newInstance(); // Create a new CTTbl for the new table
        ctTbl.set(templateTable.getCTTbl()); // Copy the template table's CTTbl
        XWPFTable copiedTable = new XWPFTable(ctTbl, document); // Create a new table using the CTTbl upon

        //Change the header color
        changeRowColor(STAFF_HEADER_POS, copiedTable, colorHeader);

        //Replace title text
        replaceRowText(0, copiedTable, "$Name", name);

        //Get the number of rows needed
        int count = 0;

        boolean odd = true;

        for (Workshop workshop : roster.getWorkshopList()) {

            if (workshop.isStaff(name)) {

                count++;

                //Copy a template row from last
                addRow(STAFF_HEADER_POS + count, copiedTable);

                for (String key : keys) {

                    replaceRowText(STAFF_HEADER_POS + count, copiedTable, key, workshop.getWorkshopDictionary().get(key));
                }

                //Change color
                if (odd) {

                    changeRowColor(STAFF_HEADER_POS + count, copiedTable, colorRow1);

                }

                else {

                    changeRowColor(STAFF_HEADER_POS + count, copiedTable, colorRow2);
                }
                odd = !odd;
            }

        }

        //Remove the extra row at the end
        copiedTable.removeRow(STAFF_HEADER_POS+count+1);

        commitTable(copiedTable);

        document.createParagraph();
        document.createTable();
        document.setTable(position, copiedTable);

    }

    //This prints a feasibility table for workshops only
    //Could be extended in future to print feasibility of shifts etc.
    //Uses hard coded red and green colors
    //In future another color swatch table could be added to the template document and read in
    public static void addFeasibleTable(XWPFDocument document, XWPFTable templateTable, Roster roster,
                                        List<FeasibleWorkshop> feasibilityList, int position) throws Exception {

        String colorFeasible = "93C47D"; //Green
        String colorInfeasible = "E06666"; //Red

        //Make the table
        //Source: http://apache-poi.1045710.n5.nabble.com/How-to-copy-a-table-and-modify-it-correctly-in-the-same-docx
        // -file-using-POI-td5716551.html
        CTTbl ctTbl = CTTbl.Factory.newInstance(); // Create a new CTTbl for the new table
        ctTbl.set(templateTable.getCTTbl()); // Copy the template table's CTTbl
        XWPFTable copiedTable = new XWPFTable(ctTbl, document); // Create a new table using the CTTbl upon

        int count = 0;

        //Add the data
        for (FeasibleWorkshop fwk : feasibilityList) {

            count++;

            Workshop wk = fwk.getWorkshop();
            String hardScore = Integer.toString(fwk.getHardScore());
            boolean feasible = fwk.isFeasible();

            //Copy a template row from last
            addRow(FEASIBLE_HEADER_POS + count, copiedTable);

            //Now replace that template row, copied row is new template row
            for (String key : keys) {
                replaceRowText(FEASIBLE_HEADER_POS + count, copiedTable, key, wk.getWorkshopDictionary().get(key));
            }

            //Replace the id
            replaceRowText(FEASIBLE_HEADER_POS + count, copiedTable, "$FullID", wk.toString());

            //Replace hard score
            replaceRowText(FEASIBLE_HEADER_POS + count, copiedTable, "$Hard", hardScore);

            //Change color
            if (feasible) {
                changeRowColor(FEASIBLE_HEADER_POS + count, copiedTable, colorFeasible);
            } else {
                changeRowColor(FEASIBLE_HEADER_POS + count, copiedTable, colorInfeasible);
            }

        }

        //Delete last row
        //Remove the extra row at the end
        copiedTable.removeRow(FEASIBLE_HEADER_POS+count+1);

        commitTable(copiedTable);

        document.createParagraph();
        document.createTable();
        document.setTable(position, copiedTable);

    }


    //Outputs the Roster as a Word document
    //In future could be extended to print shifts as well
    public static void Output(Roster roster, String rootName, String outputDirectory, String templateFile, Map<Object, Indictment> map) throws Exception {

        //Determine if it is feasible or not
        Boolean feasible = roster.getScore().isFeasible();

        //Hard constraint score for roster
        int totalHardScore = (int) roster.getScore().toLevelNumbers()[0];

        //Create feasibility data
        List<Workshop> wklist = roster.getWorkshopList();

        List<FeasibleWorkshop> feasibilityList = new ArrayList<FeasibleWorkshop>();

        for (Map.Entry<Object, Indictment> mapitem : map.entrySet()) {

            //Get index object
            Object tempObj = mapitem.getKey();

            //Get hardscore
            Indictment tempInd = mapitem.getValue();
            Score tempScore = tempInd.getScore();
            Number[] tempNumber = tempScore.toLevelNumbers();
            int hardScore = (int) tempNumber[0];

            // only print workshops with hard constraint breaches
            if(hardScore >= 0){
                continue;
            }

            //System.out.println("Obj "+tempObj);

            //If the map contains a workshop
            if (tempObj.toString().contains("Workshop")) {

                //Run through Workshop list
                for (Workshop wk : wklist) {

                    //System.out.println("Workshop "+wk);

                    if (tempObj == wk) {

                        if (hardScore < 0) {

                            System.out.println(tempObj + " " + hardScore + " red " + wk);

                            feasibilityList.add(new FeasibleWorkshop(wk,false, hardScore));

                        } else {

                            System.out.println(tempObj + " " + hardScore + " green " + wk);

                            feasibilityList.add(new FeasibleWorkshop(wk,true, hardScore));

                        }
                    }
                }
            }
        }

        int TABLE_BIG_ISSUE = 0;

        int TABLE_FEASIBLE = 1;

        int TABLE_DAY = 2;

        int TABLE_STAFF = 3;

        int TABLE_GUEST = 4;

        int TABLE_SWATCHES = 5;

        int START = 6;

        int STARTBREAK = 8;

        //Load the Document

        //XWPFDocument templateDocument = new XWPFDocument(new FileInputStream(FILE_NAME));
        XWPFDocument templateDocument = new XWPFDocument(new FileInputStream(templateFile));

        //Get the Swatches table
        XWPFTable tableSwatches = templateDocument.getTables().get(TABLE_SWATCHES);

        HashMap<String, String[]> mapSwatch = printColors(tableSwatches);

        //Get the set of staff
        LinkedHashSet<String> staffSet = getStaff(roster);

        //Get the set of guests
        LinkedHashSet<String> guestSet = getGuest(roster);

        //Get the set of dates
        LinkedHashSet<String> dateSet = getDates(roster);

        //Get first and last date
        Iterator dateIterator = dateSet.iterator();

        String firstDate = (String) dateIterator.next();
        String lastDate = firstDate; //If only one day with workshops
        while (dateIterator.hasNext()) {lastDate = (String) dateIterator.next();}

        //Get the Big Issue header table
        XWPFTable tableBigIssue = templateDocument.getTables().get(TABLE_BIG_ISSUE);

        //Replace the date
        replaceRowText(1, tableBigIssue, "$CustomDate",firstDate +" - "+ lastDate);

        //Replace the feasibility
        if (feasible) {
            replaceRowText(0, tableBigIssue, "$Feasible", "Feasible Roster");
        }
        else {
            replaceRowText(0, tableBigIssue, "$Feasible", "Infeasible Roster");
        }

        //Commit Table
        commitTable(tableBigIssue);

        //Get the feasibility table
        XWPFTable tableFeasible = templateDocument.getTables().get(TABLE_FEASIBLE);

        //Get the day table
        XWPFTable tableDay = templateDocument.getTables().get(TABLE_DAY);

        //Get the staff table
        XWPFTable tableStaff = templateDocument.getTables().get(TABLE_STAFF);

        //Get the guest table
        XWPFTable tableGuest = templateDocument.getTables().get(TABLE_GUEST);

        int position = START;

        //Only add feasibility table if roster has hard constraint breaches
        if(totalHardScore < 0){
            addFeasibleTable(templateDocument, tableFeasible, roster, feasibilityList, position++);
        }

        //For every date in the set
        for (String date: dateSet) {
            //Add first date on roster data
            addRosterDateTable(templateDocument, tableDay, roster, date, mapSwatch, position++
            );
        }

        //System.out.println("Printed days!");

        //For every staff in the set
        for (String staff: staffSet) {
            //Add first date on roster data
            addRosterStaffTable(templateDocument, tableStaff, roster, staff, mapSwatch, position++, false
            );
        }

        //System.out.println("Printed staff!");

        //For every staff in the set
        for (String staff: guestSet) {
            //Add first date on roster data
            addRosterStaffTable(templateDocument, tableGuest, roster, staff, mapSwatch, position++, true
            );
        }

        //System.out.println("Printed guests!");

        position = templateDocument.getPosOfTable(tableFeasible);
        templateDocument.removeBodyElement( position );

        position = templateDocument.getPosOfTable(tableDay);
        templateDocument.removeBodyElement( position );

        position = templateDocument.getPosOfTable(tableStaff);
        templateDocument.removeBodyElement( position );

        position = templateDocument.getPosOfTable(tableGuest);
        templateDocument.removeBodyElement( position );

        position = templateDocument.getPosOfTable(tableSwatches);
        templateDocument.removeBodyElement( position );

        System.out.println("Deleted tables!");

        List<XWPFParagraph> para = templateDocument.getParagraphs();

        int count = 0;

        for (XWPFParagraph p : para) {

            XWPFRun r = p.createRun();

            if (count>STARTBREAK) {

                r.addCarriageReturn();
                r.addBreak(BreakType.PAGE);
            }
            //r.setText("This is paragaph " + count);
            count++;
        }

        //Export the document
        templateDocument.write(new FileOutputStream( outputDirectory+"/" +rootName+"_Word.docx"));
        templateDocument.close();

    }

}
