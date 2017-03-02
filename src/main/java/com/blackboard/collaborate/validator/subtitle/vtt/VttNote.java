/*
 * Title: VttNote
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;

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
