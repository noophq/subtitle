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

import java.security.InvalidParameterException;
import java.time.LocalTime;

/**
 * Created by clebeaupin on 22/09/15.
 */
public class SubtitleTimeCode implements Comparable<SubtitleTimeCode> {
    private final int MS_HOUR = 3600000;
    private final int MS_MINUTE = 60000;
    private final int MS_SECOND = 1000;
    private int hour;
    private int minute;
    private int second;
    private int millisecond;

    public SubtitleTimeCode(int hour, int minute, int second, int millisecond) {
        this.setHour(hour);
        this.setMinute(minute);
        this.setSecond(second);
        this.setMillisecond(millisecond);
    }

    public SubtitleTimeCode(LocalTime time) {
        this(time.getHour(), time.getMinute(), time.getSecond(), 0);
    }

    /**
     *
     * @param time Time in milliseconds
     */
    public SubtitleTimeCode(long time) {
        this.hour = (int) (time/MS_HOUR);
        this.minute = (int) ((time-(this.hour*MS_HOUR))/MS_MINUTE);
        this.second = (int) ((time-(this.hour*MS_HOUR+this.minute*MS_MINUTE))/MS_SECOND);
        this.millisecond = (int) (time-(this.hour*MS_HOUR+this.minute*MS_MINUTE+this.second*MS_SECOND));
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
            throw new InvalidParameterException("Hour value must be greater or equal to 0");
        }

        this.hour = hour;
    }

    public int getMinute() {
        return this.minute;
    }

    public void setMinute(int minute) {
        if (minute < 0 || minute > 59) {
            throw new InvalidParameterException("Minute value must be between 0 and 59");
        }

        this.minute = minute;
    }

    public int getSecond() {
        return this.second;
    }

    public void setSecond(int second) {
        if (second < 0 || second > 59) {
            throw new InvalidParameterException("A second value must be between 0 and 59");
        }

        this.second = second;
    }

    public int getMillisecond() {
        return this.millisecond;
    }

    public void setMillisecond(int millisecond) {
        if (millisecond < 0 || millisecond > 999) {
            throw new InvalidParameterException("A Millisecond value must be between 0 and 999");
        }

        this.millisecond = millisecond;
    }

    /**
     *
     * @return Time in milliseconds
     */
    public long getTime() {
        return this.hour*MS_HOUR+this.minute*MS_MINUTE+this.second*MS_SECOND+this.getMillisecond();
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
