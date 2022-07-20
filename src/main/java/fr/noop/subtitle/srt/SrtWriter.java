/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.srt;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleWriterWithTimecode;
import fr.noop.subtitle.model.SubtitleWriterWithFrameRate;
import fr.noop.subtitle.model.SubtitleWriterWithOffset;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by clebeaupin on 02/10/15.
 */
public class SrtWriter implements SubtitleWriterWithTimecode, SubtitleWriterWithFrameRate, SubtitleWriterWithOffset {
    private String charset; // Charset used to encode file
    private String outputTimecode;
    private String outputFrameRate;
    private String outputOffset;

    public SrtWriter(String charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        try {
            int subtitleIndex = 0;
            SubtitleTimeCode startTimeCode = new SubtitleTimeCode(0);
            float frameRate = 25;

            if (subtitleObject.hasProperty(SubtitleObject.Property.START_TIMECODE_PRE_ROLL)){
                startTimeCode = (SubtitleTimeCode) subtitleObject.getProperty(SubtitleObject.Property.START_TIMECODE_PRE_ROLL);
            }
            if (subtitleObject.hasProperty(SubtitleObject.Property.FRAME_RATE)) {
                frameRate = (float) subtitleObject.getProperty(SubtitleObject.Property.FRAME_RATE);
            }

            for (SubtitleCue cue : subtitleObject.getCues()) {
                subtitleIndex++;

                // Write number of subtitle
                String number = String.format("%d\n", subtitleIndex);
                os.write(number.getBytes(this.charset));

                // Write Start time and end time
                SubtitleTimeCode startTC = cue.getStartTime().convertWithOptions(startTimeCode, outputTimecode, frameRate, outputFrameRate, outputOffset);
                SubtitleTimeCode endTC = cue.getEndTime().convertWithOptions(startTimeCode, outputTimecode, frameRate, outputFrameRate, outputOffset);

                String startToEnd = String.format("%s --> %s\n",
                        this.formatTimeCode(startTC),
                        this.formatTimeCode(endTC));
                os.write(startToEnd.getBytes(this.charset));

                // Write text
                String text = String.format("%s\n", cue.getText());
                os.write(text.getBytes(this.charset));

                // Write emptyline
                os.write("\n".getBytes(this.charset));
            }
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Encoding error in input subtitle");
        }
    }

    private String formatTimeCode(SubtitleTimeCode timeCode) {
        return String.format("%02d:%02d:%02d,%03d",
                timeCode.getHour(),
                timeCode.getMinute(),
                timeCode.getSecond(),
                timeCode.getMillisecond());
    }

    @Override
    public void setTimecode(String timecode) {
        this.outputTimecode= timecode;
    }

    @Override
    public void setFrameRate(String frameRate) {
        this.outputFrameRate = frameRate;
    }

    @Override
    public void setOffset(String offset) {
        this.outputOffset = offset;
    }
}
