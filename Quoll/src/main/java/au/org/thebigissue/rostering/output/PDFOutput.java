package au.org.thebigissue.rostering.output;


import au.org.thebigissue.rostering.gui.Controller;
import au.org.thebigissue.rostering.solver.entities.FacilitatorShift;
import au.org.thebigissue.rostering.solver.entities.GuestSpeakerShift;
import au.org.thebigissue.rostering.solver.entities.Workshop;
import au.org.thebigissue.rostering.solver.solution.Roster;
import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.utils.PDStreamUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFOutput {


    private static final String directory = Controller.outputDirectoryPath+"/Roster.pdf";
    private static final String dirFacilitator = Controller.outputDirectoryPath+"/Facilitator_Workshops.pdf";
    private static final String dirGuestSpeaker = "src/main/resources/RosterGuestSpeaker.pdf";
//    private static final String directoryForSpecifiedGuestSpeakerGroupByName = "src/main/resources/RosterGuestSpeakerGroupByName.pdf";
    private static final String dirGuestSpeakerGroupByName = "src/main/resources/Workshop_Sheet_For_Each_GuestSpeaker.pdf";
    private static final String dirGuestShift = "src/main/resources/GuestShift.pdf";
    private static final String dirGuestShiftAndWorkshop = Controller.outputDirectoryPath+"/GuestSpeaker_Workshops&Shifts.pdf";
    private static final PDFont fontBold = PDType1Font.HELVETICA_BOLD;

//    private static final String msgOne = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
    private static final String msgOne ="";
//    private static final String msgTwo = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
    private static final String msgTwo = "";



    /**
     * This will print results to a PDF table in the directory "src/main/resources/Roster.pdf"
     * Directory can be modified as a private variables
     *
     * @param roster the general roster
     * @throws IOException
     */
    public static void printGeneralRoster(Roster roster) throws IOException {
        PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        PDDocument mainDocument = new PDDocument();

        PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
//        PDImageXObject pdImage = PDImageXObject.createFromFile("src/main/resources/img/big_issue_logo.png",mainDocument);

        //Dummy Table
        float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
        float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
        float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

        boolean drawContent = true;
        float yStart = yStartNewPage;
        float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
        float yPosition = 425;

        BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

        // Table created by the above codes


        //write some text into the PDF
        //Begin the Content stream
        contentStream.beginText();

        //Setting the font to the Content stream
        contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
        //Setting the leading
        contentStream.setLeading(14.5f);
        //Setting the position for the line
        contentStream.newLineAtOffset(50, 500);

        String text = "TBIC GUEST SPEAKER ROSTER";

        //Adding text in the form of string
        contentStream.showText(text);
        contentStream.newLine();
        contentStream.newLine();
//        text = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
        contentStream.showText(msgOne);

        contentStream.newLine();
//        text = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
        contentStream.showText(msgTwo);

        //Ending the content stream
        contentStream.endText();



        Row<PDPage> headerRow = table.createRow(15f);
        Cell<PDPage> cell = headerRow.createCell(14, "Day");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "Time");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "School");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "Course");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "Location");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "Facilitator");
        //cell.setFont(fontBold);
        cell.setFontSize(16);
        cell = headerRow.createCell(14, "Guest Speaker");
        //cell.setFont(fontBold);
        cell.setFontSize(16);

        table.addHeaderRow(headerRow);


        for (Workshop workshop : roster.getWorkshopList()) {
            ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
            Row<PDPage> row = table.createRow(12);
            for (String content : rosterPDFContentForEachLine) {
                cell = row.createCell(14, content);
            }
        }


        table.draw();



        contentStream.close();
        mainDocument.addPage(myPage);

//        addFooter(mainDocument);

        //Now rearrange the page number
        PDDocument revertedDocument = revertPages(mainDocument);
        revertedDocument.save(directory);
        revertedDocument.close();


    }

    /**
     * This function prints the roster for each facilitator
     *
     * @param solvedRoster the roster you wish to print
     * @throws IOException
     */
    public static void printRosterFacilitator(Roster solvedRoster) throws IOException {

        File file = new File(dirFacilitator);
        if (file.exists()) {
            file.delete();
        }

        for (FacilitatorShift shift : solvedRoster.getFacilitatorShiftList()) {
            shift.printShiftToPDF(dirFacilitator);
        }

    }

    /**
     * This function prints the roster for each guest speaker
     *
     * @param solvedRoster the roster
     * @throws IOException
     */
    public static void printRosterGuestSpeakerGroupByName(Roster solvedRoster) throws IOException {

        File file = new File(dirGuestSpeakerGroupByName);
        if (file.exists()) {
            file.delete();
        }

        Map<String, ArrayList<GuestSpeakerShift>> GuestSpeakerShiftMap = new HashMap<String, ArrayList<GuestSpeakerShift>>();

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {

            String StaffName = shift.getStaffName();
            ArrayList<GuestSpeakerShift> list = GuestSpeakerShiftMap.get(StaffName);
            if (list == null) {
                ArrayList<GuestSpeakerShift> GuestSpeakerShiftList = new ArrayList<GuestSpeakerShift>();
                GuestSpeakerShiftMap.put(StaffName, GuestSpeakerShiftList);
                list = GuestSpeakerShiftMap.get(StaffName);
            }

            list.add(shift);

        }

        GuestSpeakerShiftMap.entrySet().forEach(entry -> {
            String GuestName = entry.getKey();
            ArrayList<GuestSpeakerShift> shiftArrayList = GuestSpeakerShiftMap.get(GuestName);
            List<Workshop> bigWorkshopList = new ArrayList<>();

            for (GuestSpeakerShift shift : shiftArrayList) {
//                shift.printShiftToPDF(directoryForSpecifiedGuestSpeakerGroupByName);
                List<Workshop> workShopList = shift.getWorkshopList();
                bigWorkshopList.addAll(workShopList);

                //TODO:print function here for the big list

            }

            try {
                printGuestSpeakerShiftAndWorkshop(bigWorkshopList, GuestName, dirGuestSpeakerGroupByName);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        /*

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShiftToPDF(directoryForSpecifiedGuestSpeaker);
        }*/

    }

    /**
     * This function prints the combination of the Guest speaker shifts and workshops into PDF outputs
     * for a specified guest speaker
     *
     * @param workshopList the lists of workshop of the specified guest speaker
     * @param guestName the name of the guest speaker
     * @param fileDrirectory the directory of the output file
     * @throws IOException
     */
    public static void printGuestSpeakerShiftAndWorkshop(List<Workshop> workshopList, String guestName, String fileDrirectory) throws IOException {

        File PDFFile = new File(fileDrirectory);

        if (!PDFFile.exists()) {
            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = new PDDocument();

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
//        PDImageXObject pdImage = PDImageXObject.createFromFile("src/main/resources/img/big_issue_logo.png",mainDocument);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes


            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            //Setting the leading
            contentStream.setLeading(14.5f);
            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = guestName;

            //Adding text in the form of string
            contentStream.showText(text);
            contentStream.newLine();
            contentStream.newLine();


//            text = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
            contentStream.showText(msgOne);

            contentStream.newLine();
//            text = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
            contentStream.showText(msgTwo);

            //Ending the content stream
            contentStream.endText();


        /*PDPageContentStream contents = new PDPageContentStream(mainDocument, myPage);

        contents.drawImage(pdImage,70,250);
        contents.close();*/

            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Workshop Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "School");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Course");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Facilitator");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Guest Speaker");
            //cell.setFont(fontBold);
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);

            for (Workshop workshop : workshopList) {
                ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                for (String content : rosterPDFContentForEachLine) {
                    cell = row.createCell(14, content);
                }
            }

            table.draw();

            contentStream.close();
            mainDocument.addPage(myPage);

//        addFooter(mainDocument);

        /*//Now rearrange the page number
        PDDocument revertedDocument=revertPages(mainDocument);
        revertedDocument.save(fileDrirectory);
        revertedDocument.close();*/
            mainDocument.save(fileDrirectory);
            mainDocument.close();

        } else {

            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = PDDocument.load(PDFFile);

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
//        PDImageXObject pdImage = PDImageXObject.createFromFile("src/main/resources/img/big_issue_logo.png",mainDocument);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes


            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            //Setting the leading
            contentStream.setLeading(14.5f);
            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = guestName;

            //Adding text in the form of string
            contentStream.showText(text);

            contentStream.newLine();
            contentStream.newLine();


//            text = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
            contentStream.showText(msgOne);

            contentStream.newLine();
//            text = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
            contentStream.showText(msgTwo);


            //Ending the content stream
            contentStream.endText();


        /*PDPageContentStream contents = new PDPageContentStream(mainDocument, myPage);

        contents.drawImage(pdImage,70,250);
        contents.close();*/


            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Workshop Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "School");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Course");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Facilitator");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Guest Speaker");
            //cell.setFont(fontBold);
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);


            for (Workshop workshop : workshopList) {
                ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                for (String content : rosterPDFContentForEachLine) {
                    cell = row.createCell(14, content);
                }
            }


            table.draw();


            contentStream.close();
            mainDocument.addPage(myPage);

//        addFooter(mainDocument);

            /*//Now rearrange the page number
            PDDocument revertedDocument=revertPages(mainDocument);
            revertedDocument.save(fileDrirectory);
            revertedDocument.close();*/

            mainDocument.save(fileDrirectory);

            mainDocument.close();

        }

    }


    /**
     * This function prints the shifts for guest speakers
     *
     * @param solvedRoster the roster
     * @throws IOException
     */
    public static void printRosterGuestSpeakerShift(Roster solvedRoster) throws IOException {

        File file = new File(dirGuestShift);
        if (file.exists()) {
            file.delete();
        }

        Map<String, ArrayList<GuestSpeakerShift>> GuestSpeakerShiftMap = new HashMap<String, ArrayList<GuestSpeakerShift>>();

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {

            String StaffName = shift.getStaffName();
            ArrayList<GuestSpeakerShift> list = GuestSpeakerShiftMap.get(StaffName);
            if (list == null) {
                ArrayList<GuestSpeakerShift> GuestSpeakerShiftList = new ArrayList<GuestSpeakerShift>();
                GuestSpeakerShiftMap.put(StaffName, GuestSpeakerShiftList);
                list = GuestSpeakerShiftMap.get(StaffName);
            }

            list.add(shift);

        }

        GuestSpeakerShiftMap.entrySet().forEach(entry -> {
            String GuestName = entry.getKey();
            ArrayList<GuestSpeakerShift> shiftArrayList = GuestSpeakerShiftMap.get(GuestName);
            List<ArrayList<String>> bigShiftInfoList = new ArrayList<ArrayList<String>>();

            for (GuestSpeakerShift shift : shiftArrayList) {
//                shift.printShiftToPDF(directoryForSpecifiedGuestSpeakerGroupByName);
                ArrayList<String> shiftInfoList = shift.getShiftInfo();
                if (shiftInfoList == null) {
                    continue;
                }
                for (String con : shiftInfoList) {
                    System.out.println(con);
                }
                bigShiftInfoList.add(shiftInfoList);

            }

            try {
                printGuestShift(bigShiftInfoList, GuestName, dirGuestShift);
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        /*

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShiftToPDF(directoryForSpecifiedGuestSpeaker);
        }*/

    }

    /**
     * This function prints the shifts and workshops for all guest speakers
     *
     * @param solvedRoster the roster
     * @throws IOException
     */
    public static void printGuestSpeakerShiftAndWorkshopsForAll(Roster solvedRoster) throws IOException {

        File file = new File(dirGuestShiftAndWorkshop);
        if (file.exists()) {
            file.delete();
        }

        Map<String, ArrayList<GuestSpeakerShift>> GuestSpeakerShiftMap = new HashMap<String, ArrayList<GuestSpeakerShift>>();

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {

            String StaffName = shift.getStaffName();
            ArrayList<GuestSpeakerShift> list = GuestSpeakerShiftMap.get(StaffName);
            if (list == null) {
                ArrayList<GuestSpeakerShift> GuestSpeakerShiftList = new ArrayList<GuestSpeakerShift>();
                GuestSpeakerShiftMap.put(StaffName, GuestSpeakerShiftList);
                list = GuestSpeakerShiftMap.get(StaffName);
            }

            list.add(shift);

        }

        GuestSpeakerShiftMap.entrySet().forEach(entry -> {
            String GuestName = entry.getKey();
            ArrayList<GuestSpeakerShift> shiftArrayList = GuestSpeakerShiftMap.get(GuestName);
            List<ArrayList<String>> bigShiftInfoList = new ArrayList<ArrayList<String>>();

            for (GuestSpeakerShift shift : shiftArrayList) {
//                shift.printShiftToPDF(directoryForSpecifiedGuestSpeakerGroupByName);
                ArrayList<String> shiftInfoList = shift.getShiftInfo();
                if (shiftInfoList == null) {
                    continue;
                }
                for (String con : shiftInfoList) {
                    System.out.println(con);
                }
                bigShiftInfoList.add(shiftInfoList);

            }

            try {
                printGuestShift(bigShiftInfoList, GuestName, dirGuestShiftAndWorkshop);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                printWorkshopForGuestSpeaker(shiftArrayList, dirGuestShiftAndWorkshop);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        /*

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShiftToPDF(directoryForSpecifiedGuestSpeaker);
        }*/

    }

    /**
     * This function prints guest speakers shifts for a given guest
     *
     * @param bigShiftInfoList the guest's shift info
     * @param guestName the guest name
     * @param directoryForGuestShift the directory for saving the file
     * @throws IOException
     */
    public static void printGuestShift(List<ArrayList<String>> bigShiftInfoList, String guestName, String directoryForGuestShift) throws IOException {

        File PDFFile = new File(directoryForGuestShift);


        if (!PDFFile.exists()) {
            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = new PDDocument();

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
//        PDImageXObject pdImage = PDImageXObject.createFromFile("src/main/resources/img/big_issue_logo.png",mainDocument);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes


            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            //Setting the leading
            contentStream.setLeading(14.5f);
            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = guestName;

            //Adding text in the form of string
            contentStream.showText(text);
            contentStream.newLine();
            contentStream.newLine();

//            text = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
            contentStream.showText(msgOne);

            contentStream.newLine();
//            text = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
            contentStream.showText(msgTwo);

            //Ending the content stream
            contentStream.endText();


            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Guest Name");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Shift Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);


            for (ArrayList<String> info : bigShiftInfoList) {
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                int i = 0;
                for (String content : info) {
                    i++;
                    cell = row.createCell(14, content);
                }
            }

            table.draw();

            contentStream.close();
            mainDocument.addPage(myPage);

            mainDocument.save(directoryForGuestShift);
            mainDocument.close();

        } else {

            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = PDDocument.load(PDFFile);

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);
//        PDImageXObject pdImage = PDImageXObject.createFromFile("src/main/resources/img/big_issue_logo.png",mainDocument);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes


            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);
            //Setting the leading
            contentStream.setLeading(14.5f);
            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = guestName;

            //Adding text in the form of string
            contentStream.showText(text);
            contentStream.newLine();
            contentStream.newLine();

//            text = "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  ";
            contentStream.showText(msgOne);

            contentStream.newLine();
//            text = "Phone Toni or Kat on 9214 8653, and let them know that you work on our program.";
            contentStream.showText(msgTwo);

            //Ending the content stream
            contentStream.endText();


            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Guest Name");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Shift Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);


            for (ArrayList<String> info : bigShiftInfoList) {
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                for (String content : info) {
                    cell = row.createCell(14, content);
                }
            }


            table.draw();

            contentStream.close();
            mainDocument.addPage(myPage);

//        addFooter(mainDocument);

        /*//Now rearrange the page number
        PDDocument revertedDocument=revertPages(mainDocument);
        revertedDocument.save(fileDrirectory);
        revertedDocument.close();*/
            mainDocument.save(directoryForGuestShift);
            mainDocument.close();

        }

    }

    /**
     * This function prints the workshop data for a specified guest speaker
     *
     * @param guestSpeakerShiftsList the shifts list for all guest speakers
     * @param dir
     * @throws IOException
     */
    public static void printWorkshopForGuestSpeaker(List<GuestSpeakerShift> guestSpeakerShiftsList, String dir) throws IOException {

        Map<String, ArrayList<GuestSpeakerShift>> GuestSpeakerShiftMap = new HashMap<String, ArrayList<GuestSpeakerShift>>();

        for (GuestSpeakerShift shift : guestSpeakerShiftsList) {

            String StaffName = shift.getStaffName();
            ArrayList<GuestSpeakerShift> list = GuestSpeakerShiftMap.get(StaffName);
            if (list == null) {
                ArrayList<GuestSpeakerShift> GuestSpeakerShiftList = new ArrayList<GuestSpeakerShift>();
                GuestSpeakerShiftMap.put(StaffName, GuestSpeakerShiftList);
                list = GuestSpeakerShiftMap.get(StaffName);
            }

            list.add(shift);

        }

        GuestSpeakerShiftMap.entrySet().forEach(entry -> {
//            System.out.println(entry.getKey() + " awsome man");
            String GuestName = entry.getKey();
            ArrayList<GuestSpeakerShift> shiftArrayList = GuestSpeakerShiftMap.get(GuestName);
            List<Workshop> bigWorkshopList = new ArrayList<>();

            for (GuestSpeakerShift shift : shiftArrayList) {
//                shift.printShiftToPDF(directoryForSpecifiedGuestSpeakerGroupByName);
                List<Workshop> workShopList = shift.getWorkshopList();
                bigWorkshopList.addAll(workShopList);

            }

            try {
                printGuestSpeakerShiftAndWorkshop(bigWorkshopList, GuestName, dir);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        /*

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShiftToPDF(directoryForSpecifiedGuestSpeaker);
        }*/

    }

    /**
     * prints shift info for guest speakers into PDF
     *
     * @param solvedRoster the roster
     * @throws IOException
     */
    public static void printGuestSpeaker(Roster solvedRoster) throws IOException {

        File file = new File(dirGuestSpeaker);
        if (file.exists()) {
            file.delete();
        }

        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShiftToPDF(dirGuestSpeaker);
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

            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = PDDocument.load(PDFFile);

            /*//revert the pages first then revert them back
            revertPages(mainDocument,directoryForSpecifiedFacilitator);*/

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes


            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = facilitatorShiftTitle;

            //Adding text in the form of string
            contentStream.showText(text);
//            contentStream.showText("-----------------------------------------");

            //Ending the content stream
            contentStream.endText();


            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "School");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Course");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Facilitator");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Guest Speaker");
            //cell.setFont(fontBold);
            cell.setFontSize(16);


            table.addHeaderRow(headerRow);

            for (Workshop workshop : workshopList) {
                ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                for (String content : rosterPDFContentForEachLine) {
                    cell = row.createCell(14, content);
                }
            }


            table.draw();


            contentStream.close();
            mainDocument.addPage(myPage);

            /*//Now rearrange the page number
            revertPages(mainDocument,directoryForSpecifiedFacilitator);*/
            mainDocument.save(saveDirectory);
            mainDocument.close();


        } else {

            PDPage myPage = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
            PDDocument mainDocument = new PDDocument();

            PDPageContentStream contentStream = new PDPageContentStream(mainDocument, myPage);

//            revertPages(mainDocument,directoryForSpecifiedFacilitator);

            //Dummy Table
            float margin = 50;
// starting y position is whole page height subtracted by top and bottom margin
            float yStartNewPage = myPage.getMediaBox().getHeight() - (2 * margin);
// we want table across whole page width (subtracted by left and right margin ofcourse)
            float tableWidth = myPage.getMediaBox().getWidth() - (2 * margin);

            boolean drawContent = true;
            float yStart = yStartNewPage;
            float bottomMargin = 70;
// y position is your coordinate of top left corner of the table
            float yPosition = 450;

            BaseTable table = new BaseTable(yPosition, yStartNewPage, bottomMargin, tableWidth, margin, mainDocument, myPage, true, drawContent);

            // Table created by the above codes

            //write some text into the PDF
            //Begin the Content stream
            contentStream.beginText();

            //Setting the font to the Content stream
            contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

            //Setting the position for the line
            contentStream.newLineAtOffset(50, 500);

            String text = facilitatorShiftTitle;

            //Adding text in the form of string
            contentStream.showText(text);
//            contentStream.showText("-----------------------------------------");
            //Ending the content stream
            contentStream.endText();


            Row<PDPage> headerRow = table.createRow(15f);
            Cell<PDPage> cell = headerRow.createCell(14, "Day");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Time");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "School");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Course");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Location");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Facilitator");
            //cell.setFont(fontBold);
            cell.setFontSize(16);
            cell = headerRow.createCell(14, "Guest Speaker");
            //cell.setFont(fontBold);
            cell.setFontSize(16);

            table.addHeaderRow(headerRow);

            for (Workshop workshop : workshopList) {
                ArrayList<String> rosterPDFContentForEachLine = workshop.getWorkshopData();
//            cont.showText(rosterPDFContentForEachLine);
//            cont.newLine();
                Row<PDPage> row = table.createRow(12);
                for (String content : rosterPDFContentForEachLine) {
                    cell = row.createCell(14, content);
                }
            }

            table.draw();

            contentStream.close();
            mainDocument.addPage(myPage);

            /*//Now rearrange the page number
            revertPages(mainDocument,directoryForSpecifiedFacilitator);*/

            mainDocument.save(saveDirectory);
            mainDocument.close();

        }

    }


    /**
     * this functions revert the pages order in oldDoc
     * @param oldDoc oldDoc = mainDocument; oldDoc is the old file you wish to revert the page order
     */
    public static PDDocument revertPages(PDDocument oldDoc) throws IOException {

        //Now rearrange the page number
        PDDocument newDoc = new PDDocument();
//        oldDoc = mainDocument;
        PDPageTree allPages = oldDoc.getDocumentCatalog().getPages();

        // Code to rearrange the list goes here
        // The PDF Document generated by the above codes is upside down and we need to rearrange them to make it pretty

        int i = 0;
        for (int curPageCnt = (allPages.getCount() - 1); curPageCnt >= 0; curPageCnt--) {
            newDoc.addPage(allPages.get(curPageCnt));
            i++;
        } // end for


        return newDoc;

        /*newDoc.save(directoryForPDFFile);
        newDoc.close();*/

    }

    /*//source: https://www.oodlestechnologies.com/blogs/How-to-Add-Footer-on-Each-Page-of-a-PDF-document-without-iText/
    public static void addFooter(PDDocument document) throws IOException {

// get all number of pages.

        int numberOfPages = document.getNumberOfPages();

        for (int i = 0; i < numberOfPages; i++) {
            PDPage fpage = document.getPage(i);

// content stream to write content in pdf page.
            PDPageContentStream contentStream = new PDPageContentStream(document, fpage, PDPageContentStream.AppendMode.APPEND, true);
            PDStreamUtils.write(contentStream, "Want to talk about work, or anything else?  You’re able to get 1 hour per week of counselling (for free!) as a guest speaker with The Big Issue Classroom.  Phone Toni or Kat on 9214 8653, and let them know that you work on our program.",
                    PDType1Font.HELVETICA, 10, 460, 50, new Color(102, 102, 102));//set stayle and size
            contentStream.close();

        }


    }*/


}
