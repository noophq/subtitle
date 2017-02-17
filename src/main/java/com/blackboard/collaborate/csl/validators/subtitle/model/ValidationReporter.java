package com.blackboard.collaborate.csl.validators.subtitle.model;

/**
 * Report validation issues.
 */
public interface ValidationReporter {
    /**
     * Report a warning.
     * @param msg The message.
     */
    void notifyWarning(String msg);

    /**
     * Report an error.
     * @param msg The message.
     */
    void notifyError(String msg);
}
