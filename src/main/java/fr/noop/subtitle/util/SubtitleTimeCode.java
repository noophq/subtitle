/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.util;

import java.time.LocalTime;

/**
 * Created by clebeaupin on 22/09/15.
 */
public class SubtitleTimeCode implements Comparable<SubtitleTimeCode> {
	private static final int MAX_HOUR = 24;
    private static final int MS_HOUR = 3600000;
    private static final int MS_MINUTE = 60000;
    private static final int MS_SECOND = 1000;

    private int hour;
    private int minute;
    private int second;
    private int millisecond;

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond, int offset) {
    	int newTime = hour * MS_HOUR + minute * MS_MINUTE + second * MS_SECOND + millisecond + offset;
        if (newTime < 0) {
            throw new IllegalArgumentException("Cannot create a timecode before time zero (check your offset) !");
        }
        int newHour = (int) ((newTime / MS_HOUR) % MAX_HOUR);
        int minuteOffsetRest = newTime % MS_HOUR;
    	int newMinute = (int) (minuteOffsetRest / MS_MINUTE);
    	int secondOffsetRest = minuteOffsetRest % MS_MINUTE;
    	int newSecond = (int) (secondOffsetRest / MS_SECOND);
    	int newMillisecond = secondOffsetRest % MS_SECOND;

    	this.setHour(newHour);
        this.setMinute(newMinute);
        this.setSecond(newSecond);
        this.setMillisecond(newMillisecond);
    }

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond) {
    	this(hour, minute, second, millisecond, 0);
    }

    public SubtitleTimeCode(LocalTime time) {
        this(time.getHour(), time.getMinute(), time.getSecond(), 0, 0);
    }

    /**
     *
     * @param time Time in milliseconds
     */
    public SubtitleTimeCode(long time) {
        this.hour = (int) (time/MS_HOUR);
        this.minute = (int) ((time - (this.hour * MS_HOUR)) / MS_MINUTE);
        this.second = (int) ((time - (this.hour * MS_HOUR + this.minute * MS_MINUTE)) / MS_SECOND);
        this.millisecond = (int) (time - (this.hour * MS_HOUR + this.minute * MS_MINUTE + this.second * MS_SECOND));
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", this.hour, this.minute, this.second, this.millisecond);
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        if (hour < 0) {
            throw new IllegalArgumentException("Hour value must be greater or equal to 0");
        }

        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Minute value must be between 0 and 59");
        }

        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        if (second < 0 || second > 59) {
            throw new IllegalArgumentException("A second value must be between 0 and 59");
        }

        this.second = second;
    }

    public int getMillisecond() {
        return this.millisecond;
    }

    public void setMillisecond(int millisecond) {
        if (millisecond < 0 || millisecond > 999) {
            throw new IllegalArgumentException("A Millisecond value must be between 0 and 999");
        }

        this.millisecond = millisecond;
    }

    /**
     *
     * @return Time in milliseconds
     */
    public long getTime() {
        return this.hour * MS_HOUR + this.minute * MS_MINUTE + this.second * MS_SECOND + this.getMillisecond();
    }

    public int compareTo(SubtitleTimeCode toCompare) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if (this.getTime() == toCompare.getTime()) {
            return EQUAL;
        } else if (this.getTime() > toCompare.getTime()) {
            return AFTER;
        } else {
            return BEFORE;
        }
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
