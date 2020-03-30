/*

Code by Robert Sharp 186477
Date: 16th October 2019

 */

package au.org.thebigissue.rostering.gui;

import au.org.thebigissue.rostering.errors.ImporterException;
import au.org.thebigissue.rostering.errors.InfeasibleException;
import au.org.thebigissue.rostering.solver.RosteringApp;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**Controller
 * Code by Robert Sharp 186477
 * The controller takes care of the logic of the GUI. It saves and loads from a config.txt
 * date: 27th October 2019
 */
public class Controller {


    private boolean running;
    private String inputExcelPath;
    public static String outputDirectoryPath;
    private String customWordTemplatePath;
    private int timeSetting;
    private boolean PDFSetting;
    private boolean WordSetting;
    private boolean ExcelSetting;
    private boolean StandardSetting; //For console
    private boolean ErrorSetting; //For console
    private ListView console;
    private Stage primaryStage;
    private ProgressBar progressBar;
    private LocalDate startDate;
    private LocalDate endDate;
    private String configFileName = "config.txt";
    private Button rosterButton;
    private int threadID = 0;
    private ExecutorService executor;

    private final double TIMEFACTOR = 2.0; //The factor max time is. So a factor of 2 means that threads
    //Will time out after they have executed for twice the time they should.
    private final int STEP = 200;
    private final int MILLISECONDS = 1000;

    private final int MINTIME = 10; //This is the minimum time for a roster. It should probably have been
    //set higher for the client.
    private final String DEFAULTTIME = "60 sec";

    //These are the labels for the combo box for setting the time of the roster
    private final String[] options = { "5 sec", "10 sec", "15 sec", "30 sec",
            "45 sec", "60 sec", "120 sec", "5 min", "10 min", "15 min", "30 min","1 hour","2 hours"};

    //This will map the labels for the combo box onto integer values
    private Map<String, Integer> timeDictionary = new HashMap<String, Integer>();

    //For number of blank lines to write to new config file
    private int CONFIGLINES = 9;

    /**stopExecutor
     * shuts down the executor. Without doing this, you can't close the the .jar on mac on exit
     */
    public void stopExecutor() {

        executor.shutdown();

    }

    /**getTimeSetting
     * returns the time setting (int)
     */
    public int getTimeSetting() {

        return timeSetting;

    }

    /**getPDFSetting
     * gets the setting for whether PDFs should be exported or not
     */
    public boolean getPDFSetting() {

        return PDFSetting;
    }

    /**getExcelSetting
     * gets the setting for whether a new Excel file should be created with guest and facilitator cells updated
     */
    public boolean getExcelSetting() {

        return ExcelSetting;

    }

    /**getStandardSetting
     * if this is enabled, all System.out.println will be printed to the console
     */
    public boolean getStandardSetting() {

        return StandardSetting;

    }

    /**getErrorSetting
     * if this is enabled, all System.out.println relating to errors only will be printed to the console
     */
    public boolean getErrorSetting() {

        return ErrorSetting;

    }

     /**getWordSetting
      *  if this is enabled, Word roster will be outputted
     */
    public boolean getWordSetting() {

        return WordSetting;
    }

    public String getInputExcelPath() {
        return inputExcelPath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }

    public String getCustomWordTemplatePath() {
        return customWordTemplatePath;
    }

    /**processDirectoryPath
     * This takes a String tempPath for a directory and checks if it exists and is a directory
     * if not, the current directory is set as whatever the .exe is being executed in ("user.dir")
     */
    public String processDirectoryPath(String tempPath) {

        if (!(tempPath.equals("null"))) {

            File dir = new File(tempPath);

            //Because the specified directory might not exist or might not be a directory
            if ((dir.exists()) && (dir.isDirectory())) {

                //System.out.println("Loaded:" + tempPath);
                return (tempPath);
            }
        }

        String currentDirectory = System.getProperty("user.dir");

        //Set the path to be empty
        return(currentDirectory);

    }

    /** processFilePath
     * similar to processDirectoryPath, except deals with files not directory
     */
    public String processFilePath(String tempPath) {

        if (!(tempPath.equals("null"))) {

            File file = new File(tempPath);

            //Because the specified directory might not exist or might not be a directory
            if ((file.exists()) && (file.isFile())) {

                //System.out.println("Loaded:" + tempPath);
                return (tempPath);

            }
        }

        //Set the path to be empty
        return("");

    }

    /**processSetting
     * in the event that a setting wasn't set but was saved, it would be saved as "null"
     * So this method will read in a string and if it is "null" then return false.
     */
    public boolean processSetting(String settingTemp) {

        if (!(settingTemp.equals("null"))) {
            if (settingTemp.equals("true")) {return(true);
            } else {return(false);}
        }

        else {

            return(false);

        }
    }

    //Load some default values
    public void loadValues() {

        int count = 0;

        timeDictionary.put(options[count++],5);
        timeDictionary.put(options[count++],10);
        timeDictionary.put(options[count++],15);
        timeDictionary.put(options[count++],30);
        timeDictionary.put(options[count++],45);
        timeDictionary.put(options[count++],60);
        timeDictionary.put(options[count++],120);
        timeDictionary.put(options[count++],5*60);
        timeDictionary.put(options[count++],10*60);
        timeDictionary.put(options[count++],15*60);
        timeDictionary.put(options[count++],30*60);
        timeDictionary.put(options[count++],60*60);
        timeDictionary.put(options[count++],2*60*60);

        //Check if config file exists, create otherwise
        File tempFile = new File(configFileName);
        boolean exists = tempFile.exists();
        if (!exists) {

            createBlankConfig();

        }

        //REUSE: reused code for reading in lines
        //Source: https://www.geeksforgeeks.org/different-ways-reading-text-file-java/

        List<String> lines = Collections.emptyList();
        try
        {
            lines =
                    Files.readAllLines(Paths.get(configFileName), StandardCharsets.UTF_8);

            //Load the information

            String inputExcelPathTemp = lines.get(0);
            String outputDirectoryPathTemp = lines.get(1);
            String customWordTemplatePathTemp = lines.get(2);

            String timeSettingTemp = lines.get(3);

            String WordSettingTemp = lines.get(4);
            String PDFSettingTemp = lines.get(5);
            String ExcelSettingTemp = lines.get(6);

            String StandardSettingTemp = lines.get(7);
            String ErrorSettingTemp = lines.get(8);

            //Set the loaded information
            customWordTemplatePath = processFilePath(customWordTemplatePathTemp);

            inputExcelPath = processFilePath(inputExcelPathTemp);
            outputDirectoryPath = processDirectoryPath(outputDirectoryPathTemp);

            WordSetting = processSetting(WordSettingTemp);
            PDFSetting = processSetting(PDFSettingTemp);
            ExcelSetting = processSetting(ExcelSettingTemp);
            StandardSetting = processSetting(StandardSettingTemp);
            ErrorSetting = processSetting(ErrorSettingTemp);

            if (!(timeSettingTemp.equals("null"))) {

                if (timeDictionary.containsKey(timeSettingTemp)) {

                    timeSetting = timeDictionary.get(timeSettingTemp);

                }

                else {setTimeSetting(DEFAULTTIME);}

                //timeSetting = Integer.parseInt(timeSettingTemp);

            } else {setTimeSetting(DEFAULTTIME);}

        }

        catch (IOException e)
        {

            e.printStackTrace();
        }


    }

    public String[] getComboOptions() {

        return (options);

    }


    public Controller(Stage primaryStage) {

        //Create executor. This handles the threads
        executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        this.primaryStage = primaryStage;

    }


    private boolean isEmpty(String path) {

        if (path.equals("")) {

            return true;

        }

        return false;
    }

    /**errorWord
     * This creates a popup if the Word Output could not be created because the template input file is not
     * specified
     */
    private boolean errorWord() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Cannot proceed create Word Output");
        alert.setContentText("Please ensure that the Word template file and output are specified");
        alert.showAndWait();

        return false;

    }

    //Like the Word warning but for Excel file
    private boolean errorExcel() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Cannot import information");
        alert.setContentText("Please ensure that the input Excel file is specified");
        alert.showAndWait();

        return false;

    }

    //This warning is shown if the user has selected a shorter time setting than the minimum
    //Why is it even possible to select such a combo setting? In future, triggering this warning
    //might depend on the number of days, for example, if rostering for a day is okay to use 30 seconds,
    //but for a week needs 120 seconds let's say
    private boolean warningTime(int time) {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Time setting increased");
        alert.setContentText("The rostering will continue with a time setting of "+time+" seconds");
        alert.showAndWait();

        return false;

    }

    //Need to specify the date
    private boolean errorNoDate() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Cannot create Roster");
        alert.setContentText("Please ensure that both the start and end date are specified");
        alert.showAndWait();

        return false;

    }

    //Warn the user if they're about to overwrite a file that already exists
    //private void overwriteFileWarning()

    //No output selected
    private void noOutputSelected() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Cannot create Roster");
        alert.setContentText("Please ensure that at least one form of output is selected");
        alert.showAndWait();

    }

    //Incorrect dates specified - if you put the end date before the beginning date
    private boolean errorIncorrectDate() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Cannot create Roster");
        alert.setContentText("The Roster start date must occur before the end date");
        alert.showAndWait();

        return false;

    }

    //Set the console. This will later be fed to the threads which will adjust the console.
    public void setConsole(ListView console) {

        this.console = console;

    }

    //Set the progressBar.
    public void setProgressBar(ProgressBar progressBar) {

        this.progressBar = progressBar;

    }

    //Set the Roster Button.
    public void setRosterButton(Button rosterButton) {

        this.rosterButton = rosterButton;

    }


    public void setStartDate(LocalDate startDate) {

        this.startDate = startDate;

    }

    public void setEndDate(LocalDate endDate) {

        this.endDate = endDate;

    }

    public void setInputExcelPath(String inputExcelPath) {

        this.inputExcelPath = inputExcelPath;

    }

    public void setOutputDirectoryPath(String outputDirectoryPath) {

        this.outputDirectoryPath = outputDirectoryPath;

    }

    /*public void setTimeSetting(int timeSetting) {

        this.timeSetting = timeSetting;

    }*/

    /**getTimeSettingKey
     * gets the key such as "30 sec" for a time setting of 30
     */
    public String getTimeSettingKey() {

        for (String key : options) {

            if (timeDictionary.get(key) == timeSetting) {

                return key;

            }

        }

        return DEFAULTTIME;

    }

    public void setTimeSetting(String key) {

        this.timeSetting = timeDictionary.get(key);

    }

    public void setCustomWordTemplatePath(String customWordTemplatePath) {

        this.customWordTemplatePath = customWordTemplatePath;

    }

    //Create blank config
    public void createBlankConfig() {

        try {

            PrintWriter writer = new PrintWriter(configFileName, "UTF-8");

            for (int i = 1; i<=CONFIGLINES; i++) {
                writer.println("null");
            }

            System.out.println("Configuration file created");

            writer.close();

        }

        catch (IOException e)
        {

            e.printStackTrace();

        }

    }



    //Save config
    public void saveConfig() {

        //Save the default values
        try {

            PrintWriter writer = new PrintWriter(configFileName, "UTF-8");

            writer.println(inputExcelPath);

            writer.println(outputDirectoryPath);

            writer.println(customWordTemplatePath);

            writer.println(timeSetting);

            writer.println(WordSetting);
            writer.println(PDFSetting);
            writer.println(ExcelSetting);

            writer.println(StandardSetting);
            writer.println(ErrorSetting);

            System.out.println("Configuration file saved");

            writer.close();

        }

        catch (IOException e)
        {

            e.printStackTrace();

        }

    }

    public void toggleWordSetting() {

        WordSetting = !WordSetting;

    }

    public void togglePDFSetting() {

        PDFSetting = !PDFSetting;

    }

    public void toggleExcelSetting() {

        ExcelSetting = !ExcelSetting;

    }

    public void toggleStandardSetting() {

        StandardSetting = !StandardSetting;

    }

    public void toggleErrorSetting() {

        ErrorSetting = !ErrorSetting;

    }

    private void unsuccessfulRoster() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Failure");
        alert.setHeaderText("The Roster was generated, but it is infeasible");
        alert.setContentText("Check the output directory for files");
        alert.showAndWait();

    }


    //Shown if the thread has timed out (ie. more than twice the intended time has elapsed with no result)
    private void timeOut() {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Rostering process has failed!");
        alert.setHeaderText("No Roster will be produced due to timeout");
        alert.setContentText("Something must be preventing the Rostering process from completing");
        alert.showAndWait();

    }

    //Popup when general error has caused the thread to abort
    private void errorNoRoster(Exception e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception!");
        alert.setHeaderText("No Roster will be produced due to an Exception");
        alert.setContentText(e.toString());
        alert.showAndWait();

    }

    //A roster has been produced but is infeasible. Alerts the client so they no to either manually realter
    //the roster after checking the console for more info, or, edit the input file and reroster (say with problem
    //bookings removed
    private void infeasibleRoster() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Roster is infeasible!");
        alert.setHeaderText("A roster will be produced however it is infeasible");
        alert.setContentText("Please check the console for more details and adjust the roster manually");
        alert.showAndWait();

    }

    //Message when the roster is feasible
    private void successfulRoster() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("The Roster was able to be successfully generated");
        alert.setContentText("Check the output directory for files");
        alert.showAndWait();

    }

    /**MyWatchCallable
     * This callable will watch the other callable and terminate it if it has gone on too long
     */
    public class MyWatcherCallable implements Callable<Boolean> {
        Future<Boolean> future;
        Controller controller;
        Double timeMax;

        public MyWatcherCallable(Future<Boolean> future, Controller controller, Double timeMax) {

            this.future = future;
            this.controller = controller;
            this.timeMax = timeMax;

        }

        public void enableRosterButton() {

            progressBar.setProgress(0);
            progressBar.setDisable(true);

            running = false;

            rosterButton.setDisable(false);

        }

        //public void run(){
        @Override
        public Boolean call() throws Exception {

            double time = 0.0;

            try {

                //While the thread hasn't finished, count time expired and compare it
                while(!future.isDone()) {

                    Thread.sleep(STEP);

                    time += ((double)STEP / MILLISECONDS);

                    //System.out.println(time + " Task is still not done... " + timeMax * TIMEFACTOR);

                    //Check if the amount of time has exceeded the intended time multiplied by a factor
                    if (time > (timeMax * TIMEFACTOR)) {

                        future.cancel(true);

                        Platform.runLater(() -> controller.timeOut());

                        enableRosterButton();

                        return false;

                    }
                }

                System.out.println("Result is:" + future.get());

                //If the other thread has finished successfully
                if (future.get()) {

                    //Create a popup to alert the user
                    //controller.successfulRoster();
                    Platform.runLater(() -> controller.successfulRoster());
                }
                else {

                    //Code for processing unsuccessful roster would go here
                    //instead though we just threw an exception if unsuccessful

                }

            }

            catch (InterruptedException e) {
                e.printStackTrace();
            }
            catch (ExecutionException e) {

                e.printStackTrace();
            }

            return true;

        }

    }


    /**This thread runs the RosterApp
     *
     */
    public class MyCallable implements Callable<Boolean> {
        int id;
        Controller controller;

        //public MyRunnable(int i) {
        public MyCallable(int i, Controller controller) {

            this.id = i;
            this.controller = controller;

        }

        public void disableRosterButton() {

            running = true;

            rosterButton.setDisable(true);

            progressBar.setDisable(false);
            progressBar.setProgress(0.1); //Show a little progress

        }

        public void enableRosterButton() {

            progressBar.setProgress(0);
            progressBar.setDisable(true);

            running = false;

            rosterButton.setDisable(false);

        }

        //public void run(){
        @Override
        public Boolean call() throws Exception {

            try {

                disableRosterButton();

                //System.out.println("Runnable started id:" + id);
                //System.out.println("Run: " + Thread.currentThread().getName());

                RosteringApp.RunApp(startDate, endDate, inputExcelPath, outputDirectoryPath, customWordTemplatePath, timeSetting,
                        PDFSetting, WordSetting, ExcelSetting, StandardSetting, ErrorSetting, console, primaryStage, progressBar);

                //System.out.println("#################");
                //System.out.println("FINISHED!!!!!!!!!");
                //System.out.println("#################");

                enableRosterButton();

                return true;

            }

            catch (ImporterException e) {

                Platform.runLater(() -> controller.errorNoRoster(e));

                enableRosterButton();

                return false;

            }

            catch (InfeasibleException e) {

                Platform.runLater(() -> controller.infeasibleRoster());

                enableRosterButton();

                return false;

            }

            catch (NullPointerException e) {

                Platform.runLater(() -> controller.errorNoRoster(e));

                enableRosterButton();

                return false;

            }

            catch (Exception e) {

                Platform.runLater(() -> controller.errorNoRoster(e));

                enableRosterButton();

                return false;

            }
        }
    }

    public void manualOverride() {

        //PDFSetting = false;
        ErrorSetting = true;
        StandardSetting = true;
        if (timeSetting < MINTIME) {

            timeSetting = MINTIME;
            warningTime(MINTIME);

        }

    }

    //This logic prevents attempt to roster without adequate information
    public void tryRoster() {

        manualOverride();

        boolean canRoster = true;

        boolean cannotCreateWord;

        cannotCreateWord = ((isEmpty(customWordTemplatePath)) || (isEmpty(outputDirectoryPath)));

        if ((WordSetting) && (cannotCreateWord)) {

            canRoster = errorWord();

        }

        boolean cannotInputExcel;

        cannotInputExcel = isEmpty(inputExcelPath);

        if (cannotInputExcel) {

            canRoster = errorExcel();

        }

        if ((startDate == null) || (endDate == null)) {

            canRoster = errorNoDate();

        }

        if ((startDate != null) && (endDate != null) && startDate.isAfter(endDate)) {

            canRoster = errorIncorrectDate();

        }

        //Check if at least one output is selected
        if ((getExcelSetting()==false) && (getWordSetting()==false) && (getPDFSetting()==false)) {

            noOutputSelected();
            canRoster = false;

        }

        if (canRoster) {

            //This one does stuff
            Future<Boolean> future = executor.submit(new MyCallable(threadID++, this));

            //This one checks for timeout
            Future<Boolean> watcher = executor.submit(new MyWatcherCallable(future,this, (double) timeSetting));

        }
    }
}
