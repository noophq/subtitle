/*
 * Title: VttWriter
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleWriter;
import com.blackboard.collaborate.validator.subtitle.util.TimeCodeParser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttWriter implements SubtitleWriter {
    private final Writer writer;

    public VttWriter(OutputStream outputStream, Charset charset) {
        this.writer = new OutputStreamWriter(outputStream, charset);
    }

    @Override
    public void write(SubtitleObject subtitleObject) throws IOException {
        // Write header
        writer.write(VttParser.WEBVTT_TAG);
        writer.write("\n\n");

        // Write regions
        for (Object obj : subtitleObject.getObjects()) {
            if (obj instanceof SubtitleCue) {
                writeCue((SubtitleCue) obj);
            } else {
                writer.write(obj.toString());
            }
            writer.write("\n");
        }
    }

    private void writeCue(SubtitleCue cue) throws IOException {
        if (cue.getId() != null) {
            // Write number of subtitle
            writer.write(cue.getId());
            writer.write("\n");
        }

        // Write Start time and end time
        writer.write(TimeCodeParser.formatVtt(cue.getStartTime()));
        writer.write(" ");
        writer.write(VttParser.ARROW);
        writer.write(" ");
        writer.write(TimeCodeParser.formatVtt(cue.getEndTime()));

        // TODO: write VTT cue settings if any
        Iterable<Map.Entry<String, String>> settings = cue.getSettings();
        if (settings != null) {
            for (Map.Entry<String, String> entry : settings) {
                writer.write(' ');
                writer.write(entry.getKey());
                writer.write(":");
                writer.write(entry.getValue());
            }
        }

        writer.write("\n");

        // Write text
        writer.write(cue.getText());
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
