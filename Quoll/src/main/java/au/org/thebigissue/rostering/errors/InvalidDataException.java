package au.org.thebigissue.rostering.errors;

/** Exception for handling any errors when reading in data from the excel worksheets */
public class InvalidDataException extends RuntimeException{

    public InvalidDataException(final String message) {
        super(message);
    }

}