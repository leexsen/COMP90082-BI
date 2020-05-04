package au.org.thebigissue.rostering.solver;

import au.org.thebigissue.rostering.errors.InfeasibleException;
import au.org.thebigissue.rostering.gui.ConsoleUpdater;
import au.org.thebigissue.rostering.input.RosteringImporter;
import au.org.thebigissue.rostering.output.ExcelOutput;
import au.org.thebigissue.rostering.output.PDFOutput;
import au.org.thebigissue.rostering.output.WordOutput;
import au.org.thebigissue.rostering.solver.solution.Roster;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Contains the main function for the standalone rostering application.
 * Takes the workshops and other properties as input and outputs a solved roster.
 */
public class RosteringApp {

    private static String getRootName(LocalDate startDate, LocalDate endDate) {

        String name = null;

        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", new Locale("en_AU"));
        name = startDate.format(dayFormatter)+"-"+endDate.format(dayFormatter);

        return name;

    }

    public static void RunApp(LocalDate startDate, LocalDate endDate,
                              String excelFile, String outputDirectory, String templateFile,
                              int timeSetting,
                              boolean PDFSetting, boolean WordSetting, boolean ExcelSetting,
                              boolean StandardSetting, boolean ErrorSetting,
                              ListView<String> console, Stage primaryStage, ProgressBar progressBar,
                              String amFromSetting, String amToSetting, String pmFromSetting, String pmToSetting)
            throws IOException, Exception {

        //Capture the output
        PrintStream printStream = new PrintStream(new ConsoleUpdater(console, primaryStage, progressBar, timeSetting));

        if (StandardSetting) {
            System.setOut(printStream);
        }
        if (ErrorSetting) {
            System.setErr(printStream);
        }

        // Build the Solver
        // Build the Solver Factory
        SolverFactory<Roster> solverFactory = SolverFactory.createFromXmlResource(
                "au/org/thebigissue/rostering/solver/rosteringSolverConfig.xml");


        //SolverFactory<Roster> solverFactory = SolverFactory.createFromXmlResource(
        //        "rosteringSolverConfig.xml");


        //Change the time setting of the solver
        solverFactory.getSolverConfig().getTerminationConfig().setSecondsSpentLimit((long) timeSetting);


        //Build Solver
        Solver<Roster> solver = solverFactory.buildSolver();


        // Load the problem
        //Roster unsolvedRoster = new RosteringGenerator().createRoster();

        // Import the problem
        Roster unsolvedRoster = new RosteringImporter().createRoster(startDate, endDate, excelFile, amFromSetting, amToSetting, pmFromSetting, pmToSetting);

        // Solve the problem
        Roster solvedRoster = solver.solve(unsolvedRoster);

        // Display the result
        System.err.println("\nSolved roster:\n");

        // print roster to console
        //printResult(solvedRoster);

        // Display shifts
        /*for (FacilitatorShift shift : solvedRoster.getFacilitatorShiftList()) {
            shift.printShift();
        }
        for (GuestSpeakerShift shift : solvedRoster.getGuestSpeakerShiftList()) {
            shift.printShift();
        }*/

        //Generate the file name for export
        String rootName = getRootName(startDate, endDate);

        //Create the Excel file
        if (ExcelSetting) {
            ExcelOutput.Output(solvedRoster, rootName, outputDirectory, excelFile);
        }

        //Create the PDF
        if (PDFSetting) {
            PDFOutput.printGeneralRoster(solvedRoster.getWorkshopList());
            PDFOutput.printShiftAndWorkshops(solvedRoster.getFacilitatorShiftList(), true);
            PDFOutput.printShiftAndWorkshops(solvedRoster.getGuestSpeakerShiftList(), false);
        }

        //Create the director in a try block to avoid memory leaks
        try(ScoreDirector<Roster> director = solver.getScoreDirectorFactory()
                .buildScoreDirector()){

            //Need to set the working solution for the ScoreDirector
            director.setWorkingSolution(solvedRoster);

            //Indictment Map
            Map<Object, Indictment> map = director.getIndictmentMap();

            //Make the Word file
            if (WordSetting) {
                WordOutput.Output(solvedRoster, rootName, outputDirectory, templateFile, map);
            }

            //Store output regarding constraints
            String constraintOutput = printConstraints(director);

            Platform.runLater(() -> {
                        console.getItems().add(constraintOutput);
                        List<String> items = console.getItems();
                        int index = items.size();
                        console.scrollTo(index - 1);
                    }
            );
        }

        //Generate a popup if the roster is infeasible
        if (!(solvedRoster.getScore().isFeasible())) {

            throw new InfeasibleException("The roster is infeasible");

        }

    }

    /** Function which iterates through all constraints with a hard score breach and prints output
     *  to explain the nature of the breach.
     * @param scoreDirector
     * @return
     */
    private static String printConstraints(ScoreDirector<Roster> scoreDirector){
        String output = "";

            Collection<ConstraintMatchTotal> constraintMatchTotals = scoreDirector.getConstraintMatchTotals();

            for(ConstraintMatchTotal cmt : constraintMatchTotals){

                String constraintName = cmt.getConstraintName();
                //score related to the constraint
                HardSoftScore totalScore = (HardSoftScore) cmt.getScore();

                // only print details for constraint matches that make roster infeasible
                int hardScore = (int) totalScore.toLevelNumbers()[0];

                // ignore breaches of soft constraints
               if(hardScore >= 0){
                    continue;
                }

                output += ("RULE BREACHED: " + constraintName + " - # of breaches: " + (-hardScore) +"\n");

                for(ConstraintMatch cm : cmt.getConstraintMatchSet()){
                    List<Object> justificationList = cm.getJustificationList();
                    HardSoftScore score = (HardSoftScore) cm.getScore();
                    int iHardScore = (int) score.toLevelNumbers()[0];
                    output += ("" + (-iHardScore) + " conflict due to " + Arrays.toString(
                            justificationList.toArray()) + "\n");
                }
            }
    //System.out.println("variance f : " + solvedRoster.calculateFacilitatorVariance());
    return output;
    }

    // print function for testing
    /*private static void printResult(Roster roster) {
        System.out.format("%-18s%-17s%-60s%-8s%-18s%-13s%-15s%s", "Day", "Time",
                "School", "Course", "Location", "Facilitator", "Guest Speaker", "\n");
        for (Workshop workshop : roster.getWorkshopList()) {
            workshop.printWorkshop();


        }
    }*/
}


