package au.org.thebigissue.rostering.gui;

import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**This code is largely taken from online tutorials
 * It captures the System.out.println and updates the console
 * It also counts the number of statements with DEBUG and updates the progress bar
 */

//Source: https://www.codejava.net/java-se/swing/redirect-standard-output-streams-to-jtextarea

public class ConsoleUpdater extends OutputStream {

    private ListView textArea;

    private Stage primaryStage;

    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private int count=0;

    private int progressCount = 0;

    private int PROGRESSCOUNTMAX = 100;

    //This countmeter was determined by trail and error, however, it doesn't give an accurate progress
    //since there is no way to know how many steps are needed to find an optimal solution before hand
    private final int COUNTMETER = 800;

    private int adjustedCount;

    private ProgressBar progressBar;

    public ConsoleUpdater(ListView area, Stage primaryStage, ProgressBar progressBar, long timeSetting) {
        this.textArea = area;
        this.primaryStage = primaryStage;
        this.progressBar = progressBar;
        this.adjustedCount = COUNTMETER * (int) timeSetting;
    }

    //Source: https://stackoverflow.com/questions/48589410/replicating-console-functionality-with-a-listview
    private void addText() throws IOException {
        String text = buffer.toString("UTF-8");

        buffer.reset();

        if (text.contains("DEBUG")||(text.length()==0)) {
            count++;
            //if (count>COUNTMETER) {
            if (count>adjustedCount) {
                progressCount++;
                progressBar.setProgress((double)progressCount/PROGRESSCOUNTMAX);
                count = 0;
            }
            return;
        }

        //This code used to put all System.out.println that didn't contain DEBUG to the console
        /*
        //Adding text to console
        Platform.runLater( () -> {textArea.getItems().add(text);
        List<String> items = textArea.getItems();
        int index = items.size();
        textArea.scrollTo(index - 1);
                }
            );*/

    }

    @Override
    public void write(int b) throws IOException {
        if (b == '\n') {
            addText();
        } else {
            buffer.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int bound = off + len;
        for (int i = off; i < bound; i++) {
            if (b[i] == '\n') {
                buffer.write(b, off, i - off);
                addText();
                off = i + 1;
            }
        }
        assert(off <= bound);
        buffer.write(b, off, bound - off);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void flush() throws IOException {
        // outputs all currently buffered data as a new cell, without receiving
        // a newline as otherwise is required for that
        addText();
    }

    @Override
    public void close() throws IOException {
        flush();
        buffer.close();
    }
}
