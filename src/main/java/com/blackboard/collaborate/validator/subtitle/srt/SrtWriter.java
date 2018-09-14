/*
 * Title: SrtWriter
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.srt;

import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleWriter;
import com.blackboard.collaborate.validator.subtitle.util.TimeCodeParser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Created by clebeaupin on 02/10/15.
 */
public class SrtWriter implements SubtitleWriter {
    private final Writer writer;

    public SrtWriter(OutputStream outputStream, Charset charset) {
        this.writer = new OutputStreamWriter(outputStream, charset);
    }

    @Override
    public void write(SubtitleObject subtitleObject) throws IOException {
        int subtitleIndex = 1;

        for (SubtitleCue cue : subtitleObject.getCues()) {
            // Write number of subtitle cue
            writer.write(String.format("%d", subtitleIndex++));
            writer.write("\n");

            // Write Start time and end time
            writer.write(TimeCodeParser.formatSrt(cue.getStartTime()));
            writer.write(" ");
            writer.write(SrtCue.ARROW);
            writer.write(" ");
            writer.write(TimeCodeParser.formatSrt(cue.getEndTime()));
            writer.write("\n");

            // Write text
            writer.write(cue.getText());
            // Write emptyline
            writer.write("\n");
        }
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
