package fr.noop.subtitle.exception;

/**
 * Created by leonardo on 25/01/17.
 *
 * Exception that will be used in case the number of minutes, seconds or milliseconds are invalid.
 *
 * Eg.: minutes or seconds not between 0 and 59. Milliseconds not between 0 and 999
 */
public class InvalidTimeRangeException extends Exception {

    public InvalidTimeRangeException(String message){
        super(message);
    }

}
