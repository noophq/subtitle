package com.blackboard.collaborate.csl.validators.subtitle.base;

import com.blackboard.collaborate.csl.validators.subtitle.model.ParsePositionProvider;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationIssue;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationListener;
import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationReporter;

/**
 * Implementation of ValidationReporter with listeners support.
 */
public class ValidationReporterImpl implements ValidationReporter {
    private ValidationListener[] listeners;
    private final ParsePositionProvider positionProvider;

    public ValidationReporterImpl(ParsePositionProvider positionProvider) {
        this.positionProvider = positionProvider;
    }

    public void addValidationListener(ValidationListener listener) {
        int len = 0;
        if (listeners != null) {
            len = listeners.length;
        }
        ValidationListener[] newListeners = new ValidationListener[len + 1];
        if (listeners != null) {
            System.arraycopy(listeners, 0, newListeners, 0, len);
        }
        newListeners[len] = listener;
        listeners = newListeners;
    }


    @Override
    public void notifyWarning(String msg) {
        if (listeners != null) {
            ValidationIssue issue = new ValidationIssue(ValidationIssue.Severity.WARNING, msg, getLineNumber(), getColumn());
            for (ValidationListener listener : listeners) {
                listener.onValidation(issue);
            }
        }
    }

    @Override
    public void notifyError(String msg) {
        ValidationIssue issue = new ValidationIssue(ValidationIssue.Severity.ERROR, msg, getLineNumber(), getColumn());
        if (listeners != null) {
            for (ValidationListener listener : listeners) {
                listener.onValidation(issue);
            }
        }
    }

    private int getLineNumber() {
        return (positionProvider == null) ? 0 : positionProvider.getLineNumber();
    }

    private int getColumn() {
        return (positionProvider == null) ? 0 : positionProvider.getColumn();
    }
}
