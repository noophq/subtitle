/*
 * Title: SubtitleConverter
 * Copyright (c) 2019. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle;

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleException;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleWriter;
import com.blackboard.collaborate.validator.subtitle.model.ValidationIssue;
import com.blackboard.collaborate.validator.subtitle.model.ValidationListener;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.sami.SamiParser;
import com.blackboard.collaborate.validator.subtitle.sami.SamiWriter;
import com.blackboard.collaborate.validator.subtitle.srt.SrtParser;
import com.blackboard.collaborate.validator.subtitle.srt.SrtWriter;
import com.blackboard.collaborate.validator.subtitle.stl.StlParser;
import com.blackboard.collaborate.validator.subtitle.ttml.TtmlWriter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.vtt.VttParser;
import com.blackboard.collaborate.validator.subtitle.vtt.VttWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SubtitleConverter {

    public enum Format {
        TTML(new String[] { "xml" }),
        SAMI(new String[] { "smi" }),
        VTT(new String[] { "vtt" }),
        SRT(new String[] { "srt" }),
        STL(new String[] { "stl" });

        private final String[] availableExtensions;

        Format(String[] availableExtensions) {
            this.availableExtensions = availableExtensions;
        }

        public String[] getAvailableExtensions() {
            return this.availableExtensions;
        }

        public static Format getEnum(String extension) {
            for (Format v : values()) {
                for (String ext : v.getAvailableExtensions()) {
                    if (ext.equals(extension)) {
                        return v;
                    }
                }
            }
            throw new IllegalArgumentException("Unsupported extension");
        }
    }

    private enum ConvertParser {
        SAMI(Format.SAMI, SamiParser.class),
        VTT(Format.VTT, VttParser.class),
        SRT(Format.SRT, SrtParser.class),
        STL(Format.STL, StlParser.class);

        private final Format format;
        private final Class<? extends SubtitleParser> clazz;

        ConvertParser(Format format, Class<? extends SubtitleParser> clazz) {
            this.format = format;
            this.clazz = clazz;
        }

        public Format getFormat() {
            return this.format;
        }

        public Class<? extends SubtitleParser> getParserClass() {
            return clazz;
        }

        public static ConvertParser getEnum(Format format) {
            for (ConvertParser v : values()) {
                if (v.getFormat() == format) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unsupported format");
        }
    }

    private enum ConvertWriter {
        SAMI(Format.SAMI, SamiWriter.class),
        VTT(Format.VTT, VttWriter.class),
        SRT(Format.SRT, SrtWriter.class),
        TTML(Format.TTML, TtmlWriter.class);

        private final Format format;
        private final Class<? extends SubtitleWriter> clazz;

        ConvertWriter(Format format, Class<? extends SubtitleWriter> clazz) {
            this.format = format;
            this.clazz = clazz;
        }

        public Format getFormat() {
            return this.format;
        }

        public Class<? extends SubtitleWriter> getWriterClass() {
            return clazz;
        }

        public static ConvertWriter getEnum(Format format) {
            for (ConvertWriter v : values()) {
                if (v.getFormat() == format) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }


    /**
     * Converts subtitles from one format to another.
     * @param inStream subtitle data
     * @param inFormat subtitle format
     * @param outputStream stream to write converted subtitles
     * @param outFormat output subtitle format
     * @param listener optional listener to be notified about subtitle validation errors
     * @param strictMode If true, fail on validation errors
     * @throws SubtitleException
     */
    public void convert(InputStream inStream, Format inFormat, OutputStream outputStream, Format outFormat, ValidationListener listener, boolean strictMode)
            throws SubtitleException {
        final List<ValidationIssue> errors = new ArrayList<>();

        SubtitleObject subtitleObject;

        try (SubtitleReader reader = new SubtitleReader(inStream, StandardCharsets.UTF_8)) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            if (listener != null) {
                reporter.addValidationListener(listener);
            }
            reporter.addValidationListener(errors::add);

            SubtitleParser subtitleParser = buildParser(reporter, reader, inFormat);
            // Parse input file
            subtitleObject = subtitleParser.parse(0, -1, true);
        } catch (IOException e) {
            throw new SubtitleException("Error reading input stream", e);
        } catch (Exception e) {
            throw new SubtitleException("Error creating subtitle parser for " + inFormat, e);
        }

        if (strictMode && !errors.isEmpty()) {
            log.error("Subtitle contains errors: {}", errors.size());
            throw new SubtitleException("Subtitle contains errors", errors);
        }

        // Build writer for the output file if specified
        try (SubtitleWriter subWriter = buildWriter(outputStream, outFormat, StandardCharsets.UTF_8)) {
            subWriter.write(subtitleObject);
        } catch (IOException e) {
            throw new SubtitleException("Unable to write output", e);
        } catch (Exception e) {
            throw new SubtitleException("Unable to create subtitle writer for " + outFormat, e);
        }
    }

    private static SubtitleParser buildParser(ValidationReporter reporter, SubtitleReader reader, Format convertFormat)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Get subtitle parser class
        ConvertParser convertParser = ConvertParser.getEnum(convertFormat);

        // Instantiate parser class
        Class<? extends SubtitleParser> parserClass = convertParser.getParserClass();
        return parserClass.getConstructor(ValidationReporter.class, SubtitleReader.class).newInstance(reporter, reader);
    }

    private static SubtitleWriter buildWriter(OutputStream outputStream, Format convertFormat, Charset charset)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Get subtitle writer class
        ConvertWriter convertWriter = ConvertWriter.getEnum(convertFormat);

        // Instantiate writer class
        Class<? extends SubtitleWriter> writerClass = convertWriter.getWriterClass();
        return writerClass.getConstructor(OutputStream.class, Charset.class).newInstance(outputStream, StandardCharsets.UTF_8);
    }

}
