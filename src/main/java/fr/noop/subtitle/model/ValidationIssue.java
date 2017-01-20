package fr.noop.subtitle.model;

/**
 * Created by jdvorak on 20.1.2017.
 *
 * This will be sent to the client (?)
 */
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

    public ValidationIssue(Severity severity, String message, int line, int column) {
        this.message = message;
        this.severity = severity;
        this.line = line;
        this.column = column;
    }

    public ValidationIssue(Severity severity, String message) {
        this(severity, message, 0, 0);
    }

    public Severity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String toString() {
        return severity + ":" + message + "(" + line + ":" + column + ")";
    }
}
