/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.util;


/**
 * Created by clebeaupin on 22/09/15.
 */
public class SubtitleTimeCode implements Comparable<SubtitleTimeCode> {
	private static final int MAX_HOUR = 24;
    private static final int MS_HOUR = 3600000;
    private static final int MS_MINUTE = 60000;
    private static final int MS_SECOND = 1000;

    private long milliseconds;

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond, int offset) {
    	milliseconds = hour * MS_HOUR + minute * MS_MINUTE + second * MS_SECOND + millisecond + offset;
        if (milliseconds < 0) {
            throw new IllegalArgumentException("Cannot create a timecode before time zero (check your offset) !");
        }
    }

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond) {
    	this(hour, minute, second, millisecond, 0);
    }

//    public SubtitleTimeCode(LocalTime time) {
//        this(time.getHour(), time.getMinute(), time.getSecond(), 0, 0);
//    }

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
        return (int) (getTime() - toCompare.getTime());
    }

    /**
     *
     * @param toSubtract Time code to substract to the current object
     * @return TimeCode the new time code
     */
    public SubtitleTimeCode subtract(SubtitleTimeCode toSubtract) {
        // FIXME: Throws exception if frame rate are not equals
        return new SubtitleTimeCode(this.getTime() - toSubtract.getTime());
    }
}
