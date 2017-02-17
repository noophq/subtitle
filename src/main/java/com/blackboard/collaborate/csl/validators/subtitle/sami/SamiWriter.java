/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.sami;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiWriter implements SubtitleWriter {
    private Charset charset; // Charset used to encode file

    public SamiWriter(Charset charset) {
        this.charset = charset;
    }

    @Override
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException {
        try (Writer writer = new OutputStreamWriter(os, charset)) {
            // Start SAMI
            writer.write("<SAMI>\n");

            // Write header
            writeHeader(subtitleObject, writer);

            // Write cues
            writeCues(subtitleObject, writer);

            // End SAMI
            writer.write("</SAMI>\n");
        }
    }

    private void writeHeader(SubtitleObject subtitleObject, Writer writer) throws IOException {
        // Start HEAD
        writer.write("<Head>\n");

        if (subtitleObject.hasProperty(SubtitleObject.Property.TITLE)) {
            // Write title
            writer.write("  <Title>");
            writer.write(subtitleObject.getProperty(SubtitleObject.Property.TITLE).toString());
            writer.write("</Title>\n");
        }

        // End HEAD
        writer.write("</Head>\n");
    }

    private void writeCues(SubtitleObject subtitleObject, Writer writer) throws IOException {
        // Start BODY
        writer.write("<Body>\n");

        for (SubtitleCue cue : subtitleObject.getCues()) {
            // Write Start time

            writer.write("  <SYNC Start=");
            writer.write(String.format("%d", cue.getStartTime().getTime()));
            writer.write(">\n");

            // Write text
            writer.write("    <P>");
            writer.write(cue.getText());
            writer.write("\n");
        }

        // End BODY
        writer.write("</Body>\n");
    }
}
