package au.org.thebigissue.rostering.output;


import au.org.thebigissue.rostering.gui.Controller;
import au.org.thebigissue.rostering.solver.entities.Shift;
import au.org.thebigissue.rostering.solver.entities.Workshop;
import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class PDFOutput {
    private static final String directory = Controller.outputDirectoryPath+"/Roster.pdf";
    private static final String dirFacilitator = Controller.outputDirectoryPath+"/Facilitator_Workshops.pdf";
    private static final String dirFacilitators = Controller.outputDirectoryPath+"/Facilitators/";
    private static final String dirGuestShiftAndWorkshop = Controller.outputDirectoryPath+"/GuestSpeaker_Workshops&Shifts.pdf";
    private static final String dirGuestShiftAndWorkshops = Controller.outputDirectoryPath+"/GuestSpeakers/";

//    private static final String msgOne = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
    private static final String msgOne = "";
//    private static final String msgTwo = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
    private static final String msgTwo = "";

    /**
     * This will print the final rosters to a PDF file
     * Directory can be modified as a private variables
     *
     * @param workshopList the final workshops
     * @throws IOException
     */
    public static void printGeneralRoster(List<Workshop> workshopList) throws IOException {
        String text = "BIGISSUE ROSTER";
        saveWorkshopsToPDF(null, workshopList, text, directory);
    }

    /**
     * This function creates a PDF object with pre-defined syntax
     *
     * @param mainDocument if not null, the function will write the new page at the bottom of existing file,
     *                     otherwise the function will create a new one.
     * @param text the header of the PDF file
     * @param action the lambda function that allows users to customize the table in the PDF file
     * @throws IOException
     */
    public static PDDocument createPDF(PDDocument mainDocument, String text, Consumer<BaseTable> action) throws IOException {
        PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));

        if (mainDocument == null)
            mainDocument = new PDDocument();

        PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

        float margin = 50;
        // starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
        // we want table across whole page width (subtracted by left and right margin of course)
        float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);
        float bottomMargin = 70;

        // y position is your coordinate of top left corner of the table
        float yPosition = 450;

        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, true);

        //write some text into the PDF
        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        //Setting the leading
        contentStream.setLeading(14.5f);
        //Setting the position for the line
        contentStream.newLineAtOffset(50, 500);

        //Adding text in the form of string
        contentStream.showText(text);
        contentStream.newLine();
        contentStream.newLine();

        contentStream.showText(msgOne);
        contentStream.newLine();
        contentStream.showText(msgTwo);

        //Ending the content stream
        contentStream.endText();

        // call users' lambda function of creating the table
        action.accept(table);

        table.draw();

        contentStream.close();
        mainDocument.addPage(myPage);

        return mainDocument;
    }

    /**
     * Save the workshops to a PDF file
     *
     * @param mainDocument the PDF object
     * @param workshopList the workshops to be written into a PDF file
     * @param text the header of the PDF file
     * @param filename the file name of the PDF file
     * @throws IOException
     */
    public static void saveWorkshopsToPDF(PDDocument mainDocument, List<Workshop> workshopList, String text, String filename) throws IOException {
        // create a PDF object with the customized table
        mainDocument = createPDF(mainDocument, text, pdfTable -> {
            BaseTable table = (BaseTable)pdfTable;

            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Day");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Workshop Time");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "School");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Course");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Location");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Facilitator");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Guest Speaker");
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);

            for (Workshop workshop : workshopList) {
                ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
                Row<PDPage> row = table.createRow(12);

                for (String content : rosterPDFContentForEachLine) {
                    row.createCell(14, content);
                }
            }
        });

        //Now rearrange the page number
        PDDocument revertedDocument = revertPages(mainDocument);
        revertedDocument.save(filename);
        revertedDocument.close();
        mainDocument.close();
    }

    /**
     * Save the shifts to a PDF file
     *
     * @param mainDocument the PDF object
     * @param shiftList the shifts to be written into a PDF file
     * @param text the header of the PDF file
     * @param filename the file name of the PDF file
     * @throws IOException
     */

    public static void saveShiftToPDF(PDDocument mainDocument, List<ArrayList<String>> shiftList, String text, String filename) throws IOException {
        mainDocument = createPDF(mainDocument, text, pdfTable -> {
            BaseTable table = (BaseTable)pdfTable;

            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Name");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Day");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Shift Time");
            cell.setFontSize(16);

            cell = headerRow.createCell(14, "Location");
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);

            for (ArrayList<String> info : shiftList) {
                Row<PDPage> row = table.createRow(12);

                for (String content : info)
                    row.createCell(14, content);

            }
        });

        mainDocument.save(filename);
        mainDocument.close();
    }

    /**
     * This function prints the shifts and workshops for all guest speakers
     *
     * @param shiftList the shifts of the roster
     * @param isForFacilitator true for creating the PDF file for faclitators, otherwise for guest speakers
     */
    public static void printShiftAndWorkshops(List<? extends Shift> shiftList, boolean isForFacilitator) {
        String fileForAllStaff, directoryForEachStaff;

        if (isForFacilitator) {
            fileForAllStaff = dirFacilitator;
            directoryForEachStaff = dirFacilitators;

        } else {
            fileForAllStaff = dirGuestShiftAndWorkshop;
            directoryForEachStaff = dirGuestShiftAndWorkshops;
        }

        File file = new File(fileForAllStaff);
        if (file.exists()) {
            file.delete();
        }

        File dir = new File(directoryForEachStaff);
        if (dir.exists()) {
            // delete all files in the directory
            for (String filename : dir.list()) {
                new File(dir.getPath(), filename).delete();
            }
        } else
            dir.mkdir();

        HashMap<String, ArrayList<Shift>> shiftMap = new HashMap<>();

        for (Shift shift : shiftList) {
            if (shift.getWorkshopList().size() == 0)
                continue;

            String StaffName = shift.getStaffName();
            ArrayList<Shift> list = shiftMap.get(StaffName);

            if (list == null) {
                list = new ArrayList<>();
                shiftMap.put(StaffName, list);
            }

            list.add(shift);
        }

        shiftMap.entrySet().forEach(entry -> {
            String name = entry.getKey();
            ArrayList<Shift> shiftArrayList = shiftMap.get(name);

            List<ArrayList<String>> finalShiftList = new ArrayList<>();
            List<Workshop> workshopList = new ArrayList<>();

            for (Shift shift : shiftArrayList) {
                workshopList.addAll(shift.getWorkshopList());

                ArrayList<String> shiftInfoList = shift.getShiftInfo();
                if (shiftInfoList != null)
                    finalShiftList.add(shiftInfoList);

            }

            try {
                // save shifts and workshops for all staff to a signal file
                printShift(finalShiftList, name, fileForAllStaff);
                printWorkshops(workshopList, name, fileForAllStaff);

                // save shifts and workshops to a different file for each staff
                printShift(finalShiftList, name, directoryForEachStaff + name + ".pdf");
                printWorkshops(workshopList, name, directoryForEachStaff + name + ".pdf");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * This function prints shifts for a given staff
     *
     * @param shiftList the staff's shift info
     * @param name the staff name
     * @param filename the directory for saving the file
     * @throws IOException
     */
    public static void printShift(List<ArrayList<String>> shiftList, String name, String filename) throws IOException {
        File PDFFile = new File(filename);

        if (PDFFile.exists()) {
            PDDocument mainDocument = PDDocument.load(PDFFile);
            saveShiftToPDF(mainDocument, shiftList, name, filename);

        } else {
            saveShiftToPDF(null, shiftList, name, filename);
        }

    }

    /**
     * This function prints the workshops of a guest speaker into PDF outputs
     *
     * @param workshopList the workshop list for the guest speaker
     * @param name the name of the guest speaker
     * @param filename
     * @throws IOException
     */
    public static void printWorkshops(List<Workshop> workshopList, String name, String filename) throws IOException {
        File PDFFile = new File(filename);

        if (PDFFile.exists()) {
            PDDocument mainDocument = PDDocument.load(PDFFile);
            saveWorkshopsToPDF(mainDocument, workshopList, name, filename);

        } else {
            saveWorkshopsToPDF(null, workshopList, name, filename);
        }
    }

    //If you want to make the table looks pretty for some reasons, please reference this external library
    // for future development. source:https://github.com/dhorions/boxable/wiki

    /**
     * this prints the facilitator shifts into PDF. It is a general procedure used by couple in class functions
     *
     * @param workshopList workshop lists for a specific facilitator
     * @param facilitatorShiftTitle the name of the facilitator
     * @param saveDirectory
     * @throws IOException
     */
    public static void printForSpecifiedRolesProcedure(List<Workshop> workshopList, String facilitatorShiftTitle, String saveDirectory) throws IOException {

        File PDFFile = new File(saveDirectory);

        if (PDFFile.exists()) {
            PDDocument mainDocument = PDDocument.load(PDFFile);
            saveWorkshopsToPDF(mainDocument, workshopList, facilitatorShiftTitle, saveDirectory);

        } else {
            saveWorkshopsToPDF(null, workshopList, facilitatorShiftTitle, saveDirectory);
        }

    }


    /**
     * this functions revert the pages order in oldDoc
     * @param oldDoc oldDoc = mainDocument; oldDoc is the old file you wish to revert the page order
     */
    public static PDDocument revertPages(PDDocument oldDoc) throws IOException {

        //Now rearrange the page number
        PDDocument newDoc = new PDDocument();
        PDPageTree allPages = oldDoc.getPages();
        int numberOfPages = oldDoc.getNumberOfPages();

        // Code to rearrange the list goes here
        // The PDF Document generated by the above codes is upside down and we need to rearrange them to make it pretty

        int currentPageIndex = numberOfPages - 1;
        for (int i = 0; i < numberOfPages; i++) {
            newDoc.addPage(allPages.get(currentPageIndex));
            currentPageIndex = (currentPageIndex + 1) % numberOfPages;
        }

        return newDoc;
    }
}
