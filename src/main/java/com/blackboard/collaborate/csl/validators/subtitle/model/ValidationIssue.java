package com.blackboard.collaborate.csl.validators.subtitle.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by jdvorak on 20.1.2017.
 *
 * This will be sent to the client (?)
 */
@Getter
@AllArgsConstructor
public class ValidationIssue {

    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }

    private Severity severity;
    private String message;
    private int line;
    private int column;

    public ValidationIssue(Severity severity, String message) {
        this(severity, message, 0, 0);
    }

    public String toString() {
        return severity + ": " + message + " (" + line + ":" + column + ")";
    }
}
