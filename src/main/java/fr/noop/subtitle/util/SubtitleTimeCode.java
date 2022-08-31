
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

import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalTime;

import fr.noop.subtitle.util.SubtitleFrameRate.FrameRate;

/**
 * Created by clebeaupin on 22/09/15.
 */
public class SubtitleTimeCode implements Comparable<SubtitleTimeCode> {
    private final int MS_MAX = 86400000;
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
        if (time > MS_MAX) {
            time = time - MS_MAX;
        }
        this.setHour((int) (time / MS_HOUR));
        this.setMinute((int) ((time - (this.hour * MS_HOUR)) / MS_MINUTE));
        this.setSecond((int) ((time - (this.hour * MS_HOUR + this.minute * MS_MINUTE)) / MS_SECOND));
        this.setMillisecond((int) (time - (this.hour * MS_HOUR + this.minute * MS_MINUTE + this.second * MS_SECOND)));
    }

    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d.%03d", this.hour, this.minute, this.second, this.millisecond);
    }

    public String singleHourTimeToString() throws InvalidParameterException {
        if (this.hour > 9) {
            throw new InvalidParameterException("Hour value must be a single digit number");
        }
        return String.format("%01d:%02d:%02d.%02d", this.hour, this.minute, this.second, this.millisecond / 10);
    }

    public String formatWithFramerate(float frameRate) {
        float frameDuration = (1000 / frameRate);
        int frames = Math.round(this.millisecond / frameDuration);
        return String.format("%02d:%02d:%02d:%02d", this.hour, this.minute, this.second, frames);
    }

    public static SubtitleTimeCode fromStringWithFrames(String timeCodeString, float frameRate) throws IOException {
        int hour = Integer.parseInt(timeCodeString.substring(0, 2));
        int minute = Integer.parseInt(timeCodeString.substring(3, 5));
        int second = Integer.parseInt(timeCodeString.substring(6, 8));
        int frame = Integer.parseInt(timeCodeString.substring(9, 11));
        float frameDuration = (1000 / frameRate);
        int millisecond = Math.round(frame * frameDuration);
        return new SubtitleTimeCode(hour, minute, second, millisecond);
    }

    public int getHour() {
        return this.hour;
    }

    public void setHour(int hour) {
        if (hour < 0 || hour > 23) {
            throw new InvalidParameterException("Hour value must be between 0 and 23");
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

    public SubtitleTimeCode addOffset(SubtitleTimeCode toAdd) {
        return new SubtitleTimeCode(this.getTime() + toAdd.getTime());
    }

    public SubtitleTimeCode convertFromStart(SubtitleTimeCode newStartTimecode, SubtitleTimeCode originalStartTimecode) {
        long newStartTC = newStartTimecode.getTime();
        long origStartTC = originalStartTimecode.getTime();
        long difference = origStartTC - newStartTC;
        long newTC = this.getTime() - difference;
        return new SubtitleTimeCode(newTC);
    }

    public SubtitleTimeCode convertWithFrameRate(float originalFrameRate, float newFrameRate, SubtitleTimeCode startTimecode) throws IOException {
        if (needConforming(originalFrameRate, newFrameRate)) {
            SubtitleTimeCode init = this.subtract(startTimecode);
            long newTime = (long) ((float) init.getTime() * originalFrameRate / newFrameRate);
            return new SubtitleTimeCode(newTime).addOffset(startTimecode);
        } else {
            return this;
        }
    }

    private boolean needConforming(float originalFrameRate, float newFrameRate) {
        if (originalFrameRate != newFrameRate) {
            return Math.round(Math.abs(originalFrameRate - newFrameRate)) <= 1;
        } else {
            return false;
        }
    }

    public SubtitleTimeCode convertWithOptions(
        SubtitleTimeCode inputStartTC,
        String outputStartTC,
        float inputFrameRate,
        String outputFrameRate,
        String outputOffset
    ) throws IOException {
        SubtitleTimeCode converted = this;
        float outFrameRateFloat = inputFrameRate;
        if (outputFrameRate != null) {
            outFrameRateFloat = FrameRate.getEnum(outputFrameRate).getFrameRate();
            converted = converted.convertWithFrameRate(inputFrameRate, outFrameRateFloat, inputStartTC);
        }
        if (outputOffset != null) {
            SubtitleTimeCode offsetTimecode = SubtitleTimeCode.fromStringWithFrames(outputOffset, outFrameRateFloat);
            converted = converted.addOffset(offsetTimecode);
        }
        if (outputStartTC != null) {
            SubtitleTimeCode outputTC = SubtitleTimeCode.fromStringWithFrames(outputStartTC, outFrameRateFloat);
            converted = converted.convertFromStart(outputTC, inputStartTC);
        }
        return converted;
    }
}
