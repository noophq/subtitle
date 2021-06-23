/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.sami;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleWriter;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiWriter implements SubtitleWriter {
    private String charset; // Charset used to encode file

    public SamiWriter(String charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        try {
            // Start SAMI
            os.write(new String("<SAMI>\n").getBytes(this.charset));

            // Write header

            // Write cues
            this.writeCues(subtitleObject, os);

            // End SAMI
            os.write(new String("</SAMI>\n").getBytes(this.charset));
        } catch (UnsupportedEncodingException e) {
            throw new IOException("Encoding error in input subtitle");
        }
    }

    private void writeHeader(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        // Start HEAD
        os.write(new String("<Head>\n").getBytes(this.charset));

        if (subtitleObject.hasProperty(SubtitleObject.Property.TITLE)) {
            // Write title
            os.write(String.format("  <Title>%s</Title>\n",
                    subtitleObject.getProperty(SubtitleObject.Property.TITLE)
            ).getBytes(this.charset));
        }

        // End HEAD
        os.write(new String("</Head>\n").getBytes(this.charset));
    }

    private void writeCues(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        // Start BODY
        os.write(new String("<Body>\n").getBytes(this.charset));

        for (SubtitleCue cue : subtitleObject.getCues()) {
            // Write Start time

            os.write(String.format("  <SYNC Start=%d>\n", cue.getStartTime().getTime()).getBytes(this.charset));

            // Write text
            os.write(String.format("    <P>%s\n", cue.getText()).getBytes(this.charset));
        }

        // End BODY
        os.write(new String("</Body>\n").getBytes(this.charset));
    }
}
