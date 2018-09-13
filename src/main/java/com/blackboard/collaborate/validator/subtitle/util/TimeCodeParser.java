/*
 * Title: TimeCodeParser
 * Copyright (c) 2018. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper class to parse srt/vtt timecodes.
 */
public class TimeCodeParser {
    // group1 - hours
    // group2 - minutes
    // group3 - seconds
    // group4 - decimal separator
    // group5 - milliseconds
    private static final Pattern pattern = Pattern.compile("^(?:(\\d+):)?(\\d{2}):(\\d{2})([\\.,])(\\d{3})$");

    private static final String SHORT_TIME_CODE_SRT = "%02d:%02d,%03d";
    private static final String LONG_TIME_CODE_SRT = "%d:%02d:%02d,%03d";

    private static final String SHORT_TIME_CODE_VTT = "%02d:%02d.%03d";
    private static final String LONG_TIME_CODE_VTT = "%d:%02d:%02d.%03d";

    /**
     * Match srt timecode.
     */
    public static boolean matchesSrt(String timeCodeString) {
        Matcher matcher = pattern.matcher(timeCodeString);
        return matcher.matches() && ",".equals(matcher.group(4));
    }

    /**
     * Match vtt timecode.
     */
    public static boolean matchesVtt(String timeCodeString) {
        Matcher matcher = pattern.matcher(timeCodeString);
        return matcher.matches() && ".".equals(matcher.group(4));
    }

    /**
     * Parse srt timecode.
     * @param reporter
     * @param timeCodeString
     * @param subtitleOffset Offset in millis
     * @return
     */
    public static SubtitleTimeCode parseSrt(ValidationReporter reporter, String timeCodeString, int subtitleOffset) {
        return parse(reporter, ",", timeCodeString, subtitleOffset);
    }

    /**
     * Parse vtt timecode.
     * @param reporter
     * @param timeCodeString
     * @param subtitleOffset Offset in millis
     * @return
     */
    public static SubtitleTimeCode parseVtt(ValidationReporter reporter, String timeCodeString, int subtitleOffset) {
        return parse(reporter, ".", timeCodeString, subtitleOffset);
    }

    /**
     * Parse srt or vtt timecode.
     * @param reporter
     * @param separator Decimal separator
     * @param timeCodeString
     * @param subtitleOffset Offset in millis
     * @return
     */
    private static SubtitleTimeCode parse(ValidationReporter reporter, String separator, String timeCodeString, int subtitleOffset) {
        Matcher matcher = pattern.matcher(timeCodeString);
        if (!matcher.matches()) {
            reporter.notifyError("Invalid time value: " + timeCodeString);
            return null;
        }

        try {
            int hours = 0;
            if (matcher.group(1) != null) {
                hours = Integer.parseInt(matcher.group(1));
            }
            int mins = Integer.parseInt(matcher.group(2));
            if (mins > 59) {
                reporter.notifyError("Invalid time format: " + timeCodeString);
                return null;
            }
            int seconds = Integer.parseInt(matcher.group(3));
            if (seconds > 59) {
                reporter.notifyError("Invalid time format: " + timeCodeString);
                return null;
            }
            String sep = matcher.group(4);
            if (!sep.equals(separator)) {
                reporter.notifyError("Invalid time format: " + timeCodeString);
                return null;
            }
            int millis = Integer.parseInt(matcher.group(5)); // TODO: millis always alligned with 0 to 3 ciphers???
            return new SubtitleTimeCode((3600L * hours + 60 * mins + seconds) * 1000 + millis + subtitleOffset);
        } catch (NumberFormatException e) {
            // should not happen, because regex extracts only valid numbers
            reporter.notifyError("Invalid time format: " + timeCodeString);
            return null;
        }
    }

    public static String formatSrt(SubtitleTimeCode timeCode) {
        if (timeCode.getHour() == 0) {
            return String.format(SHORT_TIME_CODE_SRT, timeCode.getMinute(), timeCode.getSecond(), timeCode.getMillisecond());
        } else {
            return String.format(LONG_TIME_CODE_SRT, timeCode.getHour(), timeCode.getMinute(), timeCode.getSecond(), timeCode.getMillisecond());
        }
    }

    public static String formatVtt(SubtitleTimeCode timeCode) {
        if (timeCode.getHour() == 0) {
            return String.format(SHORT_TIME_CODE_VTT, timeCode.getMinute(), timeCode.getSecond(), timeCode.getMillisecond());
        } else {
            return String.format(LONG_TIME_CODE_VTT, timeCode.getHour(), timeCode.getMinute(), timeCode.getSecond(), timeCode.getMillisecond());
        }
    }
}
