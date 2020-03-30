package au.org.thebigissue.rostering.errors;

/** Exception for handling any errors in importing the excel worksheets */
public class ImporterException extends RuntimeException{

    public ImporterException(final String message) {
        super(message);
    }

}
