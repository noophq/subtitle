/*
 * Title: SubtitleTimeCode
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;


import lombok.EqualsAndHashCode;

/**
 * Created by clebeaupin on 22/09/15.
 */
@EqualsAndHashCode(of = { "milliseconds" })
public class SubtitleTimeCode implements Comparable<SubtitleTimeCode> {
	//private static final long MAX_HOUR = 24L;
    private static final long MS_HOUR = 3600000L; // must be long to avoid integer overflows
    private static final long MS_MINUTE = 60000L; // --//--
    private static final long MS_SECOND = 1000L;  // --//--

    private final long milliseconds;

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond, int offset) {
    	milliseconds = hour * MS_HOUR + minute * MS_MINUTE + second * MS_SECOND + millisecond + offset;
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Cannot create a timecode before time zero (check your offset) !");
        }
    }

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond) {
    	this(hour, minute, second, millisecond, 0);
    }

    /**
     *
     * @param time Time in milliseconds
     */
    public SubtitleTimeCode(long time) {
        this.milliseconds = time;
    }

    @Override
    public String toString() {
        long hour = milliseconds / MS_HOUR;
        long minute = (milliseconds % MS_HOUR) / MS_MINUTE;
        long second = (milliseconds % MS_MINUTE) / MS_SECOND;
        long ms = milliseconds % MS_SECOND;
        return String.format("%02d:%02d:%02d.%03d", hour, minute, second, ms);
    }

    public int getHour() {
        return (int) (milliseconds / MS_HOUR);
    }

    public int getMinute() {
        return (int) ((milliseconds % MS_HOUR) / MS_MINUTE);
    }

    public int getSecond() {
        return (int) ((milliseconds % MS_MINUTE) / MS_SECOND);
    }

    public int getMillisecond() {
        return (int) (milliseconds % MS_SECOND);
    }

    /**
     *
     * @return Time in milliseconds
     */
    public long getTime() {
        return milliseconds;
    }

    @Override
    public int compareTo(SubtitleTimeCode toCompare) {
        return Long.signum(getTime() - toCompare.getTime());
    }

    public boolean isBefore(SubtitleTimeCode toCompare) {
        return compareTo(toCompare) < 0;
    }

    public boolean isAfter(SubtitleTimeCode toCompare) {
        return compareTo(toCompare) > 0;
    }

    /**
     *
     * @param toSubtract Time code to subtract from the current object
     * @return The resulting time code
     */
    public SubtitleTimeCode subtract(SubtitleTimeCode toSubtract) {
        return new SubtitleTimeCode(getTime() - toSubtract.getTime());
    }
}
