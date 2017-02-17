package com.blackboard.collaborate.csl.validators.subtitle.vtt;

import com.blackboard.collaborate.csl.validators.subtitle.model.ValidationReporter;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttNote {
    private String note = "";
    private final ValidationReporter reporter;

    public VttNote(ValidationReporter reporter) {
        this.reporter = reporter;
    }

    public void parse(StringBuilder noteText) {
        // delete the REGION identifier
        int end = noteText.indexOf(VttParser.NOTE_START);
        noteText.delete(0, end + VttParser.NOTE_START.length());

        String n = noteText.toString().trim();
        if (n.contains(VttParser.ARROW)) {
            reporter.notifyError("'" + VttParser.ARROW + "' found inside comment block");
        } else {
            note = n;
        }
    }

    public String toString() {
        return VttParser.NOTE_START + "\n" + note + "\n";
    }
}
