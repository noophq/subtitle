/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleWriter;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttWriter implements SubtitleWriter {
    private Charset charset; // Charset used to encode file

    public VttWriter(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        VttObject vtt = (VttObject) subtitleObject;

        try (Writer writer = new OutputStreamWriter(os, charset)) {
            // Write header
            writer.write("WEBVTT\n\n");

            // Write regions
            for (Object obj : vtt.getObjects()) {
                if (obj instanceof SubtitleCue) {
                    writeCue(writer, (SubtitleCue) obj);
                }
                else {
                    writer.write(obj.toString());
                }
                writer.write("\n");
            }
        }
    }

    private void writeCue(Writer writer, SubtitleCue cue) throws IOException {
        if (cue.getId() != null) {
            // Write number of subtitle
            writer.write(cue.getId());
            writer.write("\n");
        }

        // Write Start time and end time
        writer.write(this.formatTimeCode(cue.getStartTime()));
        writer.write(" --> ");
        writer.write(this.formatTimeCode(cue.getEndTime()));

        // FIXME - write VTT cue settings if any
//        VttCue vttCue = (VttCue) cue;
//        writer.write(vttCue.));

        writer.write("\n");

        // Write text
        writer.write(cue.getText());
        writer.write("\n");
    }

    private String formatTimeCode(SubtitleTimeCode timeCode) {
        return String.format("%02d:%02d:%02d.%03d",
                timeCode.getHour(),
                timeCode.getMinute(),
                timeCode.getSecond(),
                timeCode.getMillisecond());
    }
}
