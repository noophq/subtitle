package fr.noop.subtitle.model;

/**
 * Created by jdvorak on 30/01/2017.
 */
public interface ValidationReporter {

    void notifyWarning(String msg);

    void notifyError(String msg);

}
