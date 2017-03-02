/*
 * Title: SamiWriter
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.sami;

import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiWriter implements SubtitleWriter {
    private final Writer writer;

    public SamiWriter(OutputStream outputStream, Charset charset) {
        this.writer = new OutputStreamWriter(outputStream, charset);
    }

    @Override
    public void write(SubtitleObject subtitleObject) throws IOException {
        // Start SAMI
        writer.write("<SAMI>\n");

        // Write header
        writeHeader(subtitleObject);

        // Write cues
        writeCues(subtitleObject);

        // End SAMI
        writer.write("</SAMI>\n");
    }

    private void writeHeader(SubtitleObject subtitleObject) throws IOException {
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

    private void writeCues(SubtitleObject subtitleObject) throws IOException {
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

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
