/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleParser;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleParsingException;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleWriter;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Convert {
    private Options options = new Options();

    private enum ConvertFormat {
        TTML(new String[] { "xml" }),
        SAMI(new String[] { "smi" }),
        VTT(new String[] { "vtt" }),
        SRT(new String[] { "srt" }),
        STL(new String[] { "stl" });

        private String[] availableExtensions;

        ConvertFormat(String[] availableExtensions) {
            this.availableExtensions = availableExtensions;
        }

        public String[] getAvailableExtensions() {
            return this.availableExtensions;
        }

        public static ConvertFormat getEnum(String extension) {
            for(ConvertFormat v : values()) {
                for(String ext : v.getAvailableExtensions()) {
                    if (ext.equals(extension)) {
                        return v;
                    }
                }
            }
            throw new IllegalArgumentException();
        }
    }

    private enum ConvertParser {
        SAMI(ConvertFormat.SAMI, "SamiParser", true),
        VTT(ConvertFormat.VTT, "VttParser", true),
        SRT(ConvertFormat.SRT, "SrtParser", true),
        STL(ConvertFormat.STL, "StlParser", false);

        private ConvertFormat format;
        private String className;
        private boolean charsetConstructor;

        ConvertParser(ConvertFormat format, String className, boolean charsetConstructor) {
            this.format = format;
            this.className = className;
            this.charsetConstructor = charsetConstructor;
        }

        public ConvertFormat getFormat() {
            return this.format;
        }

        public String getClassName() {
            return this.className;
        }

        /**
         * @return True if the parser constructor takes a charset as argument
         */
        public boolean hasCharsetConstructor() {
            return this.charsetConstructor;
        }

        public static ConvertParser getEnum(ConvertFormat format) {
            for(ConvertParser v : values()) {
                if (v.getFormat() == format) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }

    private enum ConvertWriter {
        SAMI(ConvertFormat.SAMI, "SamiWriter", true),
        VTT(ConvertFormat.VTT, "VttWriter", true),
        SRT(ConvertFormat.SRT, "SrtWriter", true),
        TTML(ConvertFormat.TTML, "TtmlWriter", false);

        private ConvertFormat format;
        private String className;
        private boolean charsetConstructor;

        ConvertWriter(ConvertFormat format, String className, boolean charsetConstructor) {
            this.format = format;
            this.className = className;
            this.charsetConstructor = charsetConstructor;
        }

        public ConvertFormat getFormat() {
            return this.format;
        }

        public String getClassName() {
            return this.className;
        }

        /**
         * @return True if the parser constructor takes a charset as argument
         */
        public boolean hasCharsetConstructor() {
            return this.charsetConstructor;
        }

        public static ConvertWriter getEnum(ConvertFormat format) {
            for(ConvertWriter v : values())
                if (v.getFormat() == format) {
                    return v;
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

        // Input file
        this.options.addOption(Option.builder("o")
                .required()
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

        // Output charset option
        this.options.addOption(Option.builder("dsm")
                .required(false)
                .longOpt("disable-strict-mode")
                .desc("Disable strict mode")
                .build());

        // Output charset option
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

    public static void stream(InputStream is, String inputExtension, String inputCharset,
                              boolean disableStrictMode, int offset, int duration, OutputStream os,
                              String outputExtension, String outputCharset
    ) throws IOException, SubtitleParsingException {
        SubtitleParser parser = buildParser( "dummy." + inputExtension, inputCharset, offset, duration );
        SubtitleObject subtitle = parser.parse(is, offset, duration, !disableStrictMode);
        SubtitleWriter writer = buildWriter( "dummy." + outputExtension, outputCharset );
        writer.write(subtitle, os);
    }

    /**
     * Run convert command line
     */
    public void run(String[] args) {
        // Create the parser
        CommandLineParser parser = new DefaultParser();


        try {
            // Parse the command line to get options
            CommandLine line = parser.parse(this.options, args);

            if (line.hasOption('h')) {
                this.printHelp();
                System.exit(1);
            }

            // Get options
            String inputFilePath = line.getOptionValue("i");
            String outputFilePath = line.getOptionValue("o");
            String inputCharset = line.getOptionValue("ic", "utf-8");
            String outputCharset = line.getOptionValue("oc", "utf-8");
            boolean disableStrictMode = line.hasOption("disable-strict-mode");
            int subtitleOffset = Integer.parseInt(line.getOptionValue("so", "0"));
            int maxDuration = Integer.parseInt(line.getOptionValue("sd", "-1"));

            // Build parser for input file
            SubtitleParser subtitleParser = null;

            try {
                subtitleParser = buildParser(inputFilePath, inputCharset, subtitleOffset, maxDuration);
            } catch(IOException e) {
                System.out.println(String.format("Unable to build parser for file %s: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            }

            InputStream is = null;

            // Open input file
            try {
                 is = new FileInputStream(inputFilePath);
            } catch(IOException e) {
                System.out.println(String.format("Input file %s does not exist: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Parse input file
            SubtitleObject inputSubtitle = null;

            try {
                inputSubtitle = subtitleParser.parse(is, subtitleOffset, maxDuration, !disableStrictMode);
            } catch (IOException e) {
                System.out.println(String.format("Unable ro read input file %s: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            } catch (SubtitleParsingException e) {
                System.out.println(String.format("Unable to parse input file %s;: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Build writer for the output file
            SubtitleWriter writer = null;

            try {
                writer = buildWriter(outputFilePath, outputCharset);
            } catch(IOException e) {
                System.out.println(String.format("Unable to build writer for file %s: %s", outputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Create output file
            OutputStream os = null;

            try {
                os = new FileOutputStream(outputFilePath);
            } catch(IOException e) {
                System.out.println(String.format("Unable to create output file %s: %s", outputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Write output file
            try {
                writer.write(inputSubtitle, os);
            } catch (IOException e) {
                System.out.println(String.format("Unable to write output file %s: %s", outputFilePath, e.getMessage()));
                System.exit(1);
            }
        }
        catch(ParseException exp) {
            this.printHelp();
            System.exit(1);
        }
    }

    public static SubtitleParser buildParser(String filePath, String charset, int offset, int duration) throws IOException {
        String ext = getFileExtension(filePath);

        // Get subtitle parser class
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext);
        ConvertParser convertParser = ConvertParser.getEnum(convertFormat);

        // Instantiate parser class
        try {
            Class<?> parserClass = Class.forName(convertParser.getClassName());

            if (convertParser.hasCharsetConstructor()) {
                return (SubtitleParser) parserClass.getConstructor(String.class).newInstance(charset);
            } else {
                return (SubtitleParser) parserClass.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new IOException(String.format("Unable to instantiate class %s", convertParser.getClassName()));
        }
    }

    private static SubtitleWriter buildWriter(String filePath, String charset) throws IOException {
        String ext = getFileExtension(filePath);

        // Get subtitle writer class
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext);
        ConvertWriter convertWriter = ConvertWriter.getEnum(convertFormat);

        // Instantiate writer class
        try {
            Class<?> writerClass = Class.forName(convertWriter.getClassName());

            if (convertWriter.hasCharsetConstructor()) {
                return (SubtitleWriter) writerClass.getConstructor(String.class).newInstance(charset);
            } else {
                return (SubtitleWriter) writerClass.getConstructor().newInstance();
            }
        } catch (Exception e) {
            throw new IOException(String.format("Unable to instantiate class %s", convertWriter.getClassName()));
        }
    }



    private static String getFileExtension(String filePath) throws IOException {
        String ext = null;

        int i = filePath.lastIndexOf('.');

        if (i > 0) {
            ext = filePath.substring(i+1);
        }

        if (ext == null) {
            throw new IOException("Unable to get file extension");
        }

        return ext;
    }

    public static void main(String[] args) {
        Convert convert = new Convert();
        convert.run(args);
    }
}
