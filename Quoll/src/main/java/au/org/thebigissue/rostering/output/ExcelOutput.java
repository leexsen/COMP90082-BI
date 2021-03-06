package au.org.thebigissue.rostering.output;

import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ExcelOutput {

    private static int sheet;

    public static void setSheet(int foundSheet) {

        sheet = foundSheet;

    }

    public static void Output(Roster roster, String rootName, String outputDirectory, String excelFile) throws Exception {

        System.out.println("Excel output started!");

        //Generate new Excel file
        //Workbook workbook = new XSSFWorkbook();

        FileInputStream fis = new FileInputStream(excelFile);
        Workbook workbook = new XSSFWorkbook(fis);

        // System.out.println("Sheet name: "+workbook.getSheetName(sheet)+" sheet index: "+sheet);

        int facilitatorRow;
        int facilitatorColumn;
        int guestRow;
        int guestColumn;
        String sheetName;

        //For every Workshop
        for (Workshop workshop : roster.getWorkshopList()) {

            //System.out.println("Made it!"+column);

            // Get the sheet name
            sheetName = workshop.getSheetName();

            //Get the read in cell position for Facilitator
            facilitatorRow = workshop.getRowFacilitator();
            facilitatorColumn = workshop.getColumnFacilitator();

            //Get the Facilitator name
            String facilitator = workshop.getStaffString();

            //System.out.println("Read in Facilitator:"+workbook.getSheetAt(sheet).getRow(row+OFFSET).getCell(column).getStringCellValue());
            //System.out.println("Read in Guest:"+workbook.getSheetAt(sheet).getRow(row+OFFSET).getCell(column+1).getStringCellValue());

            //Write it
            Cell facilitatorCell =  workbook.getSheet(sheetName).getRow(facilitatorRow).getCell(facilitatorColumn);

            if (facilitatorCell == null) {
                facilitatorCell = workbook.getSheet(sheetName).getRow(facilitatorRow).createCell(facilitatorColumn);
            }
            facilitatorCell.setCellValue(facilitator);

            //System.out.println("Made it again!"+column);

            //Get the read in cell position for Guest
            guestRow = workshop.getRowGuest();
            guestColumn = workshop.getColumnGuest();

            //Get the Guest speaker's name
            String guest = workshop.getGuestString();


            //Write it
            Cell guestCell = workbook.getSheet(sheetName).getRow(guestRow).getCell(guestColumn);

            if (guestCell == null) {
                guestCell = workbook.getSheet(sheetName).getRow(guestRow).createCell(guestColumn);
            }
            guestCell.setCellValue(guest);

        }
        //Close the file
        FileOutputStream fileOut = new FileOutputStream(outputDirectory+"/" + rootName + "_Excel.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();


    }
}
