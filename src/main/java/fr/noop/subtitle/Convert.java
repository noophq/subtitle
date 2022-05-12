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
import fr.noop.subtitle.model.SubtitleWriterWithFrameRate;
import fr.noop.subtitle.model.SubtitleWriterWithHeader;
import fr.noop.subtitle.model.SubtitleWriterWithTimecode;
import fr.noop.subtitle.model.SubtitleWriterWithDsc;
import fr.noop.subtitle.model.SubtitleWriterWithOffset;

import org.apache.commons.cli.*;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.ArrayUtils;

import java.io.*;
import java.nio.file.*;

public class Convert {
    private Options options = new Options();

    private enum ConvertFormat {
        TTML(new String[] {"xml"}),
        SAMI(new String[] {"smi"}),
        VTT(new String[] {"vtt"}),
        SRT(new String[] {"srt"}),
        STL(new String[] {"stl"}),
        ASS(new String[] {"ass"});

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
        SAMI(ConvertFormat.SAMI, "fr.noop.subtitle.sami.SamiWriter", true, false, false, false, false, false),
        VTT(ConvertFormat.VTT, "fr.noop.subtitle.vtt.VttWriter", true, false, false, false, false, false),
        SRT(ConvertFormat.SRT, "fr.noop.subtitle.srt.SrtWriter", true, false, false, false, false, false),
        TTML(ConvertFormat.TTML, "fr.noop.subtitle.ttml.TtmlWriter", false, false, false, false, false, false),
        STL(ConvertFormat.STL, "fr.noop.subtitle.stl.StlWriter", false, false, true, true, true, true),
        ASS(ConvertFormat.ASS, "fr.noop.subtitle.ass.AssWriter", true, true, true, true, false, false);

        private ConvertFormat format;
        private String className;
        private boolean charsetConstructor;
        private boolean withHeader;
        private boolean withFrameRate;
        private boolean withTimecode;
        private boolean withDsc;
        private boolean withOffset;

        ConvertWriter(ConvertFormat format, String className, boolean charsetConstructor, boolean withHeader, boolean withFrameRate, boolean withTimecode, boolean withDsc, boolean withOffset) {
            this.format = format;
            this.className = className;
            this.charsetConstructor = charsetConstructor;
            this.withHeader = withHeader;
            this.withFrameRate = withFrameRate;
            this.withTimecode = withTimecode;
            this.withDsc = withDsc;
            this.withOffset = withOffset;
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

        public boolean withHeader() {
            return this.withHeader;
        }

        public boolean withFrameRate() {
            return this.withFrameRate;
        }

        public boolean withTimecode() {
            return this.withTimecode;
        }

        public boolean withDsc() {
            return this.withDsc;
        }

        public boolean withOffset() {
            return this.withOffset;
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

        // Output timecode option
        this.options.addOption(Option.builder("otc")
                .required(false)
                .longOpt("output-timecode")
                .hasArg()
                .desc("Output timecode")
                .build());

        // Output charset option
        this.options.addOption(Option.builder("dsm")
                .required(false)
                .longOpt("disable-strict-mode")
                .desc("Disable strict mode")
                .build());

        // Input header file
        this.options.addOption(Option.builder("hf")
                .required(false)
                .longOpt("header-file")
                .hasArg()
                .desc("Input header file")
                .build());

        // Output frame rate option
        this.options.addOption(Option.builder("ofr")
                .required(false)
                .longOpt("output-framerate")
                .hasArg()
                .desc("Output frame rate")
                .build());

        // Output display standard code option
        this.options.addOption(Option.builder("dsc")
                .required(false)
                .longOpt("output-dsc")
                .hasArg()
                .desc("Output display standard code")
                .build());

        // Output offset option
        this.options.addOption(Option.builder("off")
                .required(false)
                .longOpt("output-offset")
                .hasArg()
                .desc("Output offset timecode")
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
            String outputTimecode = line.getOptionValue("otc");
            String headerFilePath = line.getOptionValue("hf");
            String outputFrameRate = line.getOptionValue("ofr");
            String outputDsc = line.getOptionValue("dsc");
            String outputOffset = line.getOptionValue("off");
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
            BOMInputStream bom = null;

            // Open input file
            try {
                 is = new FileInputStream(inputFilePath);
                 bom = new BOMInputStream(is);
            } catch(IOException e) {
                System.out.println(String.format("Input file %s does not exist: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Parse input file
            SubtitleObject inputSubtitle = null;

            try {
                inputSubtitle = subtitleParser.parse(bom, !disableStrictMode);
            } catch (IOException e) {
                System.out.println(String.format("Unable ro read input file %s: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            } catch (SubtitleParsingException e) {
                System.out.println(String.format("Unable to parse input file %s;: %s", inputFilePath, e.getMessage()));
                System.exit(1);
            }

            // Parser header file
            String headerText = null;
            try {
                if (headerFilePath != null) {
                    headerText = new String(Files.readAllBytes(Paths.get(headerFilePath)));
                }
            } catch(IOException e) {
                System.out.println(String.format("Header file %s does not exist: %s", headerFilePath, e.getMessage()));
                System.exit(1);
            }

            // Build writer for the output file
            SubtitleWriter writer = null;

            try {
                writer = this.buildWriter(outputFilePath, outputCharset, headerText, outputFrameRate, outputTimecode, outputDsc, outputOffset);
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

    public SubtitleParser buildParser(String filePath, String charset) throws IOException {
        String ext = this.getFileExtension(filePath);

        // Get subtitle parser class
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext.toLowerCase());
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

    private SubtitleWriter buildWriter(String filePath, String charset, String headerText, String frameRate, String timecode, String dsc, String offset) throws IOException {
        String ext = this.getFileExtension(filePath);

        // Get subtitle writer class
        ConvertFormat convertFormat = ConvertFormat.getEnum(ext);
        ConvertWriter convertWriter = ConvertWriter.getEnum(convertFormat);

        // Instantiate writer class
        try {
            Class<?> writerClass = Class.forName(convertWriter.getClassName());
            SubtitleWriter instance = null;
            if (convertWriter.hasCharsetConstructor()) {
                instance = (SubtitleWriter) writerClass.getConstructor(String.class).newInstance(charset);
            } else {
                instance = (SubtitleWriter) writerClass.getConstructor().newInstance();
            }
            if (convertWriter.withFrameRate()) {
                ((SubtitleWriterWithFrameRate) instance).setFrameRate(frameRate);
            }
            if (convertWriter.withTimecode()) {
                ((SubtitleWriterWithTimecode) instance).setTimecode(timecode);
            }
            if (convertWriter.withHeader()) {
                ((SubtitleWriterWithHeader) instance).setHeaderText(headerText);
            }
            if (convertWriter.withDsc()) {
                ((SubtitleWriterWithDsc) instance).setDsc(dsc);
            }
            if (convertWriter.withOffset()) {
                ((SubtitleWriterWithOffset) instance).setOffset(offset);
            }
            return instance;
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
