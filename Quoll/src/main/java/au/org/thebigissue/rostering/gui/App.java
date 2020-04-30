package au.org.thebigissue.rostering.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;

/**Code that creates all the Buttons, header tabs etc. for the GUI
 * Written by Robert Sharp 186477
 * 27 th October 2019
 */

//import javafx.embed.swing.SwingFXUtils;

//Could be extended by displaying the stage of the rostering process once it has started (eg. "importing..."
//"outputting" as well as displaying the estimated time remaining
//This would only take a few hours to implement and would require the RosteringApp to change from a static
//to an object with methods "getCurrentStage" which the watcher Callable in created by the controller would
//ping. The other method would be to implement the observer pattern which is done using properties.

public class App extends Application {

    private static final int GUI_HEIGHT = 640;
    private static final int GUI_WIDTH = 800;
    private static final int CONSOLE_WIDTH = 600;
    private static final int CONSOLE_HEIGHT = 180;
    private static final int VBOX_SPACING = 10;
    private static final int SEPARATOR_HEIGHT = 50;
    private static final int LABEL_WIDTH = 300;
    private static final int ROSTER_BUTTON_WIDTH = 100;
    private static final int ROSTER_BUTTON_HEIGHT = 50;


    //private static final TextArea console = new TextArea("Big Issue Roster Application"+"\n");
    private static ListView<String> console;

    //The following variables have been moved to controller and should be deleted to finish refactoring process
    private static String inputExcelPath = null;

    private static String outputDirectoryPath = null;

    private static String customWordTemplatePath = null;

    private static String timeSetting = "10";

    private static String fileName = "config.txt";

    private static boolean WordSetting = false;
    private static boolean PDFSetting = false;
    private static boolean ExcelSetting = false;
    private static boolean StandardSetting = false;
    private static boolean ErrorSetting = false;


    //The following code is too long and should have been split up such as a different method for every tab
    @Override
    public void start(Stage primaryStage) throws Exception{

        //This can print out System.out.println information
        console = new ListView<>();

        //Controller handles the logic
        Controller controller = new Controller(primaryStage);

        //Labels
        Text statusLabel = new Text("Stage: ");
        Text timeLabel = new Text("Expected time left: ");

        Text status = new Text("Waiting");
        Text time = new Text("30 seconds");

        GridPane gridStatus = new GridPane();
        gridStatus.setPadding(new Insets(0, 10, 10, 10));
        gridStatus.setMinSize(100, 40);
        gridStatus.setVgap(10);
        gridStatus.setHgap(10);

        gridStatus.add(statusLabel, 0, 0);
        gridStatus.add(status, 1, 0);
        gridStatus.add(timeLabel, 0, 1);
        gridStatus.add(time, 1, 1);

        gridStatus.setHalignment(statusLabel, HPos.RIGHT);
        gridStatus.setHalignment(timeLabel, HPos.RIGHT);

        gridStatus.setAlignment(Pos.CENTER);

        gridStatus.setVisible(false);

        //Stop executor
        //This is to ensure that when click X button the Java quits properly
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                //event.consume();
                controller.stopExecutor();
                System.exit(0);

            }
        });

        VBox root = new VBox();

        primaryStage.setTitle("Big Issue Roster Application");

        // Load the image
        File imageFile = new File("GUIheader.png");

        //System.out.println(imageFile.exists());

        Image image = new Image(imageFile.toURI().toString());

        //System.out.println(image.getHeight());

        //System.out.println(image.getWidth());

        // Loads the image from a file
        //BufferedImage imageFile = ImageIO.read(getClass().getResource("/GUIheader.png"));

        //Image image = SwingFXUtils.toFXImage(imageFile, null);

        //Quite button

        Button qButton = new Button("Quite");

        //Stops the threads with quiting, needed for mac otherwise the .jar won't quit properly
        qButton.setOnAction(e -> {
            controller.stopExecutor();
            System.exit(0);
        });

        // HBox of control buttons
        HBox anchorHbox = new HBox();
        //Enable the following code to have a quite button
        //anchorHbox.getChildren().addAll(qButton);

        // Anchor the controls

        TabPane tabPane = new TabPane();


        //Code was taken from
        //https://stackoverflow.com/questions/37721760/add-buttons-to-tabs-and-tab-area-javafx
        //To handle adding a quit button to the tab
        //However, in the end we didn't put that quite button in
        //So effectively the code makes no difference

        AnchorPane anchor = new AnchorPane();
        anchor.getChildren().addAll(tabPane, anchorHbox);
        AnchorPane.setTopAnchor(anchorHbox, 3.0);
        AnchorPane.setRightAnchor(anchorHbox, 5.0);
        AnchorPane.setTopAnchor(tabPane, 1.0);
        AnchorPane.setRightAnchor(tabPane, 1.0);
        AnchorPane.setLeftAnchor(tabPane, 1.0);
        AnchorPane.setBottomAnchor(tabPane, 1.0);


        // Save Configuration buttons

        Button saveButton = new Button("Save Config");
        Button saveButton2 = new Button("Save Config");

        saveButton.setOnAction(e -> {
            controller.saveConfig();
        });

        saveButton2.setOnAction(e -> {
            controller.saveConfig();
        });

        // Checkboxes for file settings

        HBox checkboxHBox = new HBox();

        CheckBox checkBoxWord = new CheckBox("Word Output");
        CheckBox checkBoxPDF = new CheckBox("PDF Output");
        CheckBox checkBoxExcel = new CheckBox("Update Excel File");

        checkboxHBox.getChildren().addAll(checkBoxWord, checkBoxPDF, checkBoxExcel);
        checkboxHBox.setAlignment(Pos.CENTER);
        checkboxHBox.setSpacing(5);

        //Set the action for clicking the checkboxes
        checkBoxWord.setOnAction(e -> {
            controller.toggleWordSetting();
            checkBoxWord.setSelected(controller.getWordSetting());
        });
        checkBoxPDF.setOnAction(e -> {
            controller.togglePDFSetting();
            checkBoxPDF.setSelected(controller.getPDFSetting());
        });
        checkBoxExcel.setOnAction(e -> {
            controller.toggleExcelSetting();
            checkBoxExcel.setSelected(controller.getExcelSetting());
        });

        //Checkboxes for console output
        HBox checkboxHBoxOutput = new HBox();

        //These checkboxes were hidden in the final GUI
        CheckBox checkBoxStandard = new CheckBox("Output standard messages");
        CheckBox checkBoxError = new CheckBox("Output error messages");

        checkboxHBoxOutput.getChildren().addAll(checkBoxStandard, checkBoxError);
        checkboxHBoxOutput.setAlignment(Pos.CENTER);
        checkboxHBoxOutput.setSpacing(5);

        checkBoxStandard.setOnAction(e -> {
            controller.toggleStandardSetting();
            checkBoxStandard.setSelected(controller.getStandardSetting());
        });

        checkBoxError.setOnAction(e -> {
            controller.toggleErrorSetting();
            checkBoxError.setSelected(controller.getErrorSetting());
        });

        // Rostering button

        Button rosterButton = new Button("Roster");

        rosterButton.setMinWidth(ROSTER_BUTTON_WIDTH);
        rosterButton.setMinHeight(ROSTER_BUTTON_HEIGHT);

        controller.setRosterButton(rosterButton);

        // Create the application header
        ImageView imageView = new ImageView();

        imageView.setImage(image);

        root.getChildren().add(imageView);

        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        //imageView.setImage(image);

        //root.getChildren().add(imageView);

        root.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        // Create the tabs

        Tab tab1 = new Tab("Main", new Label("Main tab"));
        Tab tab2 = new Tab("File options"  , new Label("File options tab"));
        Tab tab3 = new Tab("Advanced options"  , new Label("Advanced options tab"));
        Tab tab4 = new Tab("Console"  , new Label("Console for messages"));

        tab1.closableProperty().set(false);
        tab2.closableProperty().set(false);
        tab3.closableProperty().set(false);
        tab4.closableProperty().set(false);

        // Set it floating so can see black behind
        tabPane.getStyleClass().add("floating");

        VBox vbox = new VBox();

        vbox.setSpacing(10);

        vbox.setAlignment(Pos.CENTER);

        Separator separator1 = new Separator();
        separator1.setMinHeight(50);
        Separator separator2 = new Separator();
        separator2.setMinHeight(50);
        Separator separator3 = new Separator();
        separator3.setMinHeight(50);

        Text startDateLabel = new Text("Start date:");

        Text startDateStatus = new Text("Invalid dates!");

        DatePicker startDatePicker = new DatePicker();

        Text endDateLabel = new Text("End date:");

        Text endDateStatus = new Text("Invalid dates!");

        endDateStatus.setVisible(false);
        startDateStatus.setVisible(false);

        DatePicker endDatePicker = new DatePicker();


        // action event
        EventHandler<ActionEvent> dateEvent = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent e)
            {

                controller.setStartDate(startDatePicker.getValue());
                controller.setEndDate(endDatePicker.getValue());

            }
        };


        startDatePicker.setOnAction(dateEvent);
        endDatePicker.setOnAction(dateEvent);

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setMinSize(100, 100);
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(startDateLabel, 0, 0);
        grid.add(startDatePicker, 1, 0);
        grid.add(startDateStatus, 2, 0);
        grid.add(endDateLabel, 0, 1);
        grid.add(endDatePicker, 1, 1);
        grid.add(endDateStatus, 2, 1);


        grid.setAlignment(Pos.CENTER);

        grid.setHalignment(startDateLabel, HPos.RIGHT);
        grid.setHalignment(endDateLabel, HPos.RIGHT);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(0);
        progressBar.setDisable(true);

        vbox.getChildren().addAll(separator1, grid, separator2, gridStatus, progressBar, rosterButton, separator3);

        //Start file options

        VBox fileOptionsVBox = new VBox();

        fileOptionsVBox.setSpacing(10);

        fileOptionsVBox.setAlignment(Pos.CENTER);

        Separator separatorFO1 = new Separator();
        separatorFO1.setMinHeight(50);
        Separator separatorFO2 = new Separator();
        separatorFO2.setMinHeight(50);
        Separator separatorFO3 = new Separator();
        separatorFO3.setMinHeight(50);


        //Input Excel File
        Text inputExcelLabel = new Text("Input Excel File:");

        FileChooser excelChooser = new FileChooser();

        TextField inputExcelInfo = new TextField("No file selected");

        inputExcelInfo.setMinWidth(LABEL_WIDTH);
        inputExcelInfo.setMaxWidth(LABEL_WIDTH);
        inputExcelInfo.setDisable(true);

        Button excelChooserButton = new Button("Select File");
        excelChooserButton.setOnAction(e -> {
            File selectedFile = excelChooser.showOpenDialog(primaryStage);
            if (!(selectedFile==null)) {
                inputExcelInfo.setText(selectedFile.getAbsolutePath());
                //inputExcelPath = selectedFile.getAbsolutePath();
                controller.setInputExcelPath(selectedFile.getAbsolutePath());
            }
        });

        //Output directory
        Text outputDirectoryLabel = new Text("Output directory:");

        DirectoryChooser outputChooser = new DirectoryChooser();

        TextField outputDirectoryInfo = new TextField("No file selected");

        outputDirectoryInfo.setMinWidth(LABEL_WIDTH);
        outputDirectoryInfo.setMaxWidth(LABEL_WIDTH);
        outputDirectoryInfo.setDisable(true);

        Button outputChooserButton = new Button("Select Directory");
        outputChooserButton.setOnAction(e -> {
            File selectedDirectory = outputChooser.showDialog(primaryStage);
            if (!(selectedDirectory==null)) {
                outputDirectoryInfo.setText(selectedDirectory.getAbsolutePath());
                //outputDirectoryPath = selectedDirectory.getAbsolutePath();
                controller.setOutputDirectoryPath(selectedDirectory.getAbsolutePath());
            }
        });


        GridPane gridFO = new GridPane();
        gridFO.setPadding(new Insets(10, 10, 10, 10));
        gridFO.setMinSize(100, 100);
        gridFO.setVgap(10);
        gridFO.setHgap(10);

        gridFO.add(inputExcelLabel, 0, 0);
        gridFO.add(excelChooserButton, 1, 0);
        gridFO.add(inputExcelInfo, 2, 0);
        gridFO.add(outputDirectoryLabel, 0, 1);
        gridFO.add(outputChooserButton, 1, 1);
        gridFO.add(outputDirectoryInfo, 2, 1);
        gridFO.setAlignment(Pos.CENTER);

        gridFO.setHalignment(inputExcelLabel, HPos.RIGHT);
        gridFO.setHalignment(outputDirectoryLabel, HPos.RIGHT);

        fileOptionsVBox.getChildren().addAll(separatorFO1, gridFO, checkboxHBox, separatorFO2,saveButton, separatorFO3);

        //End file options

        //Start options

        VBox advancedOptionsVBox = new VBox();

        advancedOptionsVBox.setSpacing(10);

        advancedOptionsVBox.setAlignment(Pos.CENTER);

        Separator separatorAO1 = new Separator();
        separatorAO1.setMinHeight(50);
        Separator separatorAO2 = new Separator();
        separatorAO2.setMinHeight(50);
        Separator separatorAO3 = new Separator();
        separatorAO3.setMinHeight(50);

        Text customWordTemplateLabel = new Text("Custom Word Template File:");

        FileChooser wordChooser = new FileChooser();

        TextField customWordTemplateInfo = new TextField("No file selected");

        customWordTemplateInfo.setMinWidth(LABEL_WIDTH);
        customWordTemplateInfo.setMaxWidth(LABEL_WIDTH);
        customWordTemplateInfo.setDisable(true);

        Button wordChooserButton = new Button("Select File");
        wordChooserButton.setOnAction(e -> {
            File selectedFile = wordChooser.showOpenDialog(primaryStage);
            if (!(selectedFile==null)) {
                customWordTemplateInfo.setText(selectedFile.getAbsolutePath());
                //customWordTemplatePath = selectedFile.getAbsolutePath();
                controller.setCustomWordTemplatePath(selectedFile.getAbsolutePath());
            }
        });

        Text timeSettingLabel = new Text("Time setting:");

        // Time options
        String[] options = controller.getComboOptions();

        // Create a combo box
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(options));

        comboBox.setOnAction(e -> {
            //timeSetting=(String)comboBox.getValue();
            //controller.setTimeSetting(Integer.parseInt((String)comboBox.getValue()));
            controller.setTimeSetting(comboBox.getValue());
        });

        //comboBox.getSelectionModel().select(timeSetting);

        GridPane gridAO = new GridPane();
        gridAO.setPadding(new Insets(10, 10, 10, 10));
        gridAO.setMinSize(100, 100);
        gridAO.setVgap(10);
        gridAO.setHgap(10);

        gridAO.add(customWordTemplateLabel, 0, 0);
        gridAO.add(wordChooserButton, 1, 0);
        gridAO.add(customWordTemplateInfo, 2, 0);
        gridAO.add(timeSettingLabel, 0, 1);
        gridAO.add(comboBox, 1, 1);

        gridAO.setAlignment(Pos.CENTER);

        gridAO.setHalignment(customWordTemplateLabel, HPos.RIGHT);
        gridAO.setHalignment(timeSettingLabel, HPos.RIGHT);

        //Output options here
        //advancedOptionsVBox.getChildren().addAll(separatorAO1, gridAO, checkboxHBoxOutput, separatorAO2, saveButton2, separatorAO3);
        advancedOptionsVBox.getChildren().addAll(separatorAO1, gridAO, separatorAO2, saveButton2, separatorAO3);

        //End options

        //Start console

        VBox consoleVBox = new VBox();

        consoleVBox.setSpacing(10);

        consoleVBox.setAlignment(Pos.CENTER);

        Separator separatorC1 = new Separator();
        separatorC1.setMinHeight(50);
        Separator separatorC2 = new Separator();
        separatorC2.setMinHeight(50);
        Separator separatorC3 = new Separator();
        separatorC3.setMinHeight(50);

        console.setMinWidth(CONSOLE_WIDTH);
        console.setMinHeight(CONSOLE_HEIGHT);
        console.setMaxWidth(CONSOLE_WIDTH);

        Button messageButton = new Button("Clear messages");
        messageButton.setOnAction(e -> {

            console.getItems().clear();

        });

        consoleVBox.getChildren().addAll(separatorC1, console, messageButton, separatorC3);

        //End console

        tab1.setContent(vbox);
        tab2.setContent(fileOptionsVBox);
        tab3.setContent(advancedOptionsVBox);
        tab4.setContent(consoleVBox);

        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);
        tabPane.getTabs().add(tab4);

        //root.getChildren().add(tabPane);
        root.getChildren().add(anchor);

        rosterButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {

                controller.tryRoster();

            }
        });

        controller.loadValues();

        controller.setProgressBar(progressBar);
        controller.setConsole(console);

        //Set the loaded values
        wordChooser.setInitialFileName(controller.getCustomWordTemplatePath());
        customWordTemplateInfo.setText(controller.getCustomWordTemplatePath());

        excelChooser.setInitialFileName(controller.getInputExcelPath());
        inputExcelInfo.setText(controller.getInputExcelPath());

        outputChooser.setInitialDirectory(new File(controller.getOutputDirectoryPath()));
        outputDirectoryInfo.setText(controller.getOutputDirectoryPath());

        checkBoxWord.setSelected(controller.getWordSetting());
        checkBoxPDF.setSelected(controller.getPDFSetting());
        checkBoxExcel.setSelected(controller.getExcelSetting());
        checkBoxStandard.setSelected(controller.getStandardSetting());
        checkBoxError.setSelected(controller.getErrorSetting());

        //comboBox.getSelectionModel().select(Integer.toString(controller.getTimeSetting()));
        comboBox.getSelectionModel().select(controller.getTimeSettingKey());

        //Show the GUI
        primaryStage.setScene(new Scene(root, GUI_WIDTH, GUI_HEIGHT));
        primaryStage.setHeight(GUI_HEIGHT);
        primaryStage.setResizable(false);

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
