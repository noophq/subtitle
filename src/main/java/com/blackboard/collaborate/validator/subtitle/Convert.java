/*
 * Title: Convert
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
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
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


public class Convert {
    private final Options options = new Options();

    private enum ConvertFormat {
        TTML(new String[] { "xml" }),
        SAMI(new String[] { "smi" }),
        VTT(new String[] { "vtt" }),
        SRT(new String[] { "srt" }),
        STL(new String[] { "stl" });

        private final String[] availableExtensions;

        ConvertFormat(String[] availableExtensions) {
            this.availableExtensions = availableExtensions;
        }

        public String[] getAvailableExtensions() {
            return this.availableExtensions;
        }

        public static ConvertFormat getEnum(String extension) {
            for (ConvertFormat v : values()) {
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
        SAMI(ConvertFormat.SAMI, SamiParser.class),
        VTT(ConvertFormat.VTT, VttParser.class),
        SRT(ConvertFormat.SRT, SrtParser.class),
        STL(ConvertFormat.STL, StlParser.class);

        private final ConvertFormat format;
        private final Class<? extends SubtitleParser> clazz;

        ConvertParser(ConvertFormat format, Class<? extends SubtitleParser> clazz) {
            this.format = format;
            this.clazz = clazz;
        }

        public ConvertFormat getFormat() {
            return this.format;
        }

        public Class<? extends SubtitleParser> getParserClass() {
            return clazz;
        }

        public static ConvertParser getEnum(ConvertFormat format) {
            for (ConvertParser v : values()) {
                if (v.getFormat() == format) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unsupported format");
        }
    }

    private enum ConvertWriter {
        SAMI(ConvertFormat.SAMI, SamiWriter.class),
        VTT(ConvertFormat.VTT, VttWriter.class),
        SRT(ConvertFormat.SRT, SrtWriter.class),
        TTML(ConvertFormat.TTML, TtmlWriter.class);

        private final ConvertFormat format;
        private final Class<? extends SubtitleWriter> clazz;

        ConvertWriter(ConvertFormat format, Class<? extends SubtitleWriter> clazz) {
            this.format = format;
            this.clazz = clazz;
        }

        public ConvertFormat getFormat() {
            return this.format;
        }

        public Class<? extends SubtitleWriter> getWriterClass() {
            return clazz;
        }

        public static ConvertWriter getEnum(ConvertFormat format) {
            for (ConvertWriter v : values()) {
                if (v.getFormat() == format) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    /**
     * Configure command line options
     */
    private void configureOptions() {
        this.options.addOption("h", "help", false, "print help");

        // Input file
        this.options.addOption(Option.builder("i")
                .required()
                .longOpt("input-file")
                .hasArg()
                .desc("Input file")
                .build());

        // Output file (if not specified, just validate the input)
        this.options.addOption(Option.builder("o")
                .required(false)
                .longOpt("output-file")
                .hasArg()
                .desc("Output file")
                .build());

        // Input charset option
        this.options.addOption(Option.builder("ic")
                .required(false)
                .longOpt("input-charset")
                .hasArg()
                .desc("Input charset")
                .build());

        // Output charset option
        this.options.addOption(Option.builder("oc")
                .required(false)
                .longOpt("output-charset")
                .hasArg()
                .desc("Output charset")
                .build());

        // Disable strict mode
        this.options.addOption(Option.builder("dsm")
                .required(false)
                .longOpt("disable-strict-mode")
                .desc("Disable strict mode")
                .build());

        // Subtitles offset
        this.options.addOption(Option.builder("so")
                .required(false)
                .longOpt("offset")
                .hasArg()
                .desc("Subtitles offset")
                .build());
    }

    public Convert() {
        this.configureOptions();
    }

    /**
     * Print help for the command line
     */
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("subtitle-convert", this.options);
    }

    /**
     * Run convert command line
     */
    public int run(String[] args) {
        // Create the parser
        CommandLineParser parser = new DefaultParser();
        CommandLine line;

        try {
            // Parse the command line to get options
            line = parser.parse(this.options, args);
        } catch (ParseException exp) {
            printHelp();
            return 1;
        }

        if (line.hasOption('h')) {
            printHelp();
            return 0;
        }

        // Get options
        String inputFilePath = line.getOptionValue("i");
        String outputFilePath = line.getOptionValue("o");
        String inputCharset = line.getOptionValue("ic", StandardCharsets.UTF_8.name());
        String outputCharset = line.getOptionValue("oc", StandardCharsets.UTF_8.name());
        boolean disableStrictMode = line.hasOption("disable-strict-mode");
        int subtitleOffset = Integer.parseInt(line.getOptionValue("so", "0"));
        int maxDuration = Integer.parseInt(line.getOptionValue("sd", "-1"));

        // Build parser for input file
        SubtitleParser subtitleParser;
        String ext = getFileExtension(inputFilePath);
        SubtitleObject subtitleObject;

        final List<ValidationIssue> errors = new ArrayList<>();

        ValidationListener listener = new ValidationListener() {
            @Override
            public void onValidation(ValidationIssue validationIssue) {
                errors.add(validationIssue);
            }
        };

        Charset iCharset = Charset.forName(inputCharset);
        Charset oCharset = Charset.forName(outputCharset);
        // Open input file
        try (SubtitleReader reader = new SubtitleReader(new FileInputStream(inputFilePath), iCharset)) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            reporter.addValidationListener(listener);
            subtitleParser = buildParser(reporter, reader, ext, subtitleOffset, maxDuration);

            // Parse input file
            subtitleObject = subtitleParser.parse(subtitleOffset, maxDuration, !disableStrictMode);
        } catch (IOException e) {
            System.err.println(String.format("Unable ro read input file %s: %s", inputFilePath, e.getMessage()));
            return 1;
        } catch (Exception e) {
            System.err.println(String.format("Unable to create subtitle parser: %s", ext));
            return 1;
        }

        if (!errors.isEmpty()) {
            System.err.println(String.format("Subtitle contains errors: %d", errors.size()));
            for (ValidationIssue err : errors) {
                System.err.println(err);
            }
            if (!disableStrictMode) {
                return 1; // error
            }
        }

        if (outputFilePath != null) {
            // Build writer for the output file if specified
            try (SubtitleWriter subWriter = buildWriter(outputFilePath, oCharset)) {
                subWriter.write(subtitleObject);
            } catch (IOException e) {
                System.err.println(String.format("Unable to write output file %s: %s", outputFilePath, e.getMessage()));
                return 1;
            } catch (Exception e) {
                System.err.println(String.format("Unable to create subtitle writer: %s", ext));
                return 1;
            }
        }
        return 0;
    }


    public static SubtitleParser buildParser(ValidationReporter reporter, SubtitleReader reader, String ext, int offset, int duration)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Get subtitle parser class
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext);
        ConvertParser convertParser = ConvertParser.getEnum(convertFormat);

        // Instantiate parser class
        Class<? extends SubtitleParser> parserClass = convertParser.getParserClass();
        return parserClass.getConstructor(ValidationReporter.class, SubtitleReader.class).newInstance(reporter, reader);
    }

    private static SubtitleWriter buildWriter(String outputFilePath, Charset oCharset)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, FileNotFoundException {
        // Get subtitle writer class

        String ext = getFileExtension(outputFilePath);
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext);
        ConvertWriter convertWriter = ConvertWriter.getEnum(convertFormat);

        // Instantiate writer class

        Class<? extends SubtitleWriter> writerClass = convertWriter.getWriterClass();
        return writerClass.getConstructor(OutputStream.class, Charset.class).newInstance(new FileOutputStream(outputFilePath), oCharset);
    }

    private static String getFileExtension(String filePath) {
        int i = filePath.lastIndexOf('.');
        return (i > 0) ? filePath.substring(i + 1) : "";
    }

    public static void main(String[] args) {
        Convert convert = new Convert();
        int ret = convert.run(args);
        if (ret != 0) {
            System.exit(ret);
        }
    }
}
