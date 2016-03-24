/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle;

import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.model.SubtitleWriter;
import org.apache.commons.cli.*;

import org.apache.commons.lang3.ArrayUtils;

import java.io.*;

public class Convert {
    private Options options = new Options();

    private enum ConvertFormat {
        TTML(new String[] {"xml"}),
        SAMI(new String[] {"smi"}),
        VTT(new String[] {"vtt"}),
        SRT(new String[] {"srt"}),
        STL(new String[] {"stl"});

        private String[] availableExtensions;

        ConvertFormat(String[] availableExtensions) {
            this.availableExtensions = availableExtensions;
        }

        public String[] getAvailableExtensions() {
            return this.availableExtensions;
        }

        public static ConvertFormat getEnum(String extension) {
            for(ConvertFormat v : values())
                if (ArrayUtils.contains(v.getAvailableExtensions(), extension)) {
                    return v;
                }
            throw new IllegalArgumentException();
        }
    }

    private enum ConvertParser {
        SAMI(ConvertFormat.SAMI, "fr.noop.subtitle.sami.SamiParser", true),
        VTT(ConvertFormat.VTT, "fr.noop.subtitle.vtt.VttParser", true),
        SRT(ConvertFormat.SRT, "fr.noop.subtitle.srt.SrtParser", true),
        STL(ConvertFormat.STL, "fr.noop.subtitle.stl.StlParser", false);

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
            for(ConvertParser v : values())
                if (v.getFormat() == format) {
                    return v;
                }
            throw new IllegalArgumentException();
        }
    }

    private enum ConvertWriter {
        SAMI(ConvertFormat.SAMI, "fr.noop.subtitle.sami.SamiWriter", true),
        VTT(ConvertFormat.VTT, "fr.noop.subtitle.vtt.VttWriter", true),
        SRT(ConvertFormat.SRT, "fr.noop.subtitle.srt.SrtWriter", true),
        TTML(ConvertFormat.TTML, "fr.noop.subtitle.ttml.TtmlWriter", false);

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

            // Build parser for input file
            SubtitleParser subtitleParser = null;

            try {
                subtitleParser = this.buildParser(inputFilePath, inputCharset);
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
                inputSubtitle = subtitleParser.parse(is, !disableStrictMode);
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
                writer = this.buildWriter(outputFilePath, outputCharset);
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

    private SubtitleParser buildParser(String filePath, String charset) throws IOException {
        String ext = this.getFileExtension(filePath);

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

    private SubtitleWriter buildWriter(String filePath, String charset) throws IOException {
        String ext = this.getFileExtension(filePath);

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

    private String getFileExtension(String filePath) throws IOException {
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
