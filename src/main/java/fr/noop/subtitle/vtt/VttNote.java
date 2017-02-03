package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.ValidationReporter;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttNote {
    private String note;
    private ValidationReporter reporter;

    public VttNote(ValidationReporter reporter) {
        this.reporter = reporter;
    }

    public void parse(StringBuilder bld) {
        String n = bld.toString();
        if (n.indexOf(VttParser.ARROW) >= 0) {
            reporter.notifyError("'" + VttParser.ARROW + "' found inside comment block");
        }
        else {
            note = n;
        }
    }

    public String toString() {
        return "NOTE " + note;
    }
}
