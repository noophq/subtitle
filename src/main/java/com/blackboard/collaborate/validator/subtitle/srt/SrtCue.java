/*
 * Title: SrtCue
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.srt;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SrtCue extends BaseSubtitleCue {
    static final String ARROW = "-->";
    private static final Pattern CUE_TIME_PATTERN = Pattern.compile("^\\s*(\\S+)\\s+" + ARROW + "\\s+(\\S+)\\s*$");


    private final ValidationReporter reporter;

    public SrtCue(ValidationReporter reporter) {
        this.reporter = reporter;
    }

    int parseCueId(String textLine, int lastCueNumber) {
        // First textLine is the cue number
        int cueNumber;
        try {
            cueNumber = Integer.parseInt(textLine);
            if (cueNumber != lastCueNumber + 1) {
                reporter.notifyWarning("Out of order cue number: " + textLine);
            }
        } catch (NumberFormatException e) {
            reporter.notifyError("Unable to parse cue number: " + textLine);
            cueNumber = lastCueNumber;
        }

        setId(textLine);
        return cueNumber;
    }

    boolean parseCueHeader(String textLine, int subtitleOffset) {
        Matcher m = CUE_TIME_PATTERN.matcher(textLine);
        if (!m.matches()) {
            reporter.notifyError("Timecode '" + textLine + "' is badly formated");
            return false;
        }

        setStartTime(parseTimeCode(m.group(1), subtitleOffset));
        setEndTime(parseTimeCode(m.group(2), subtitleOffset));
        return true;
    }

    // duplicity in VTT
    protected SubtitleTimeCode parseTimeCode(String timeCodeString, int subtitleOffset) {
        long value = 0;
        String[] parts = timeCodeString.split(",", 2); // 00:04:20,375
        if (parts.length > 2) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
            return null;
        }
        String[] subparts = parts[0].split(":");
        if (subparts.length > 3) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
            return null;
        }
        try {
            for (String subpart : subparts) {
                value = value * 60 + Long.parseLong(subpart);
            }

            long stamp = value * 1000 + subtitleOffset;
            if (parts.length > 1) {
                stamp += Long.parseLong(parts[1]);
            }

            return new SubtitleTimeCode(stamp);

        } catch (NumberFormatException e) {
            reporter.notifyError("Invalid time format: " + timeCodeString);
        } catch (IllegalArgumentException e) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
        }
        return null;
    }
}
