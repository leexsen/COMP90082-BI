package au.org.thebigissue.rostering.errors;

/** Exception for handling any errors in importing the excel worksheets */
public class InfeasibleException extends RuntimeException{

    public InfeasibleException(final String message) {
        super(message);
    }

}
