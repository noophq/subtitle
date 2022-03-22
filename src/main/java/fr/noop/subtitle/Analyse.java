package fr.noop.subtitle;

import fr.noop.subtitle.model.SubtitleParser;
import fr.noop.subtitle.model.SubtitleObject;
import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.model.SubtitleObject.Property;
import fr.noop.subtitle.util.SubtitleFrameRate.FrameRate;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;
import org.apache.commons.cli.*;
import org.apache.commons.io.input.BOMInputStream;

public class Analyse {
    private Options options = new Options();

    private void configureOptions() {
        this.options.addOption("h", "help", false, "print help");

        // Input subtitle file
        this.options.addOption(Option.builder("i")
                .required()
                .longOpt("input-file")
                .hasArg()
                .desc("Input file")
                .build());

        // // Output analysis report file
        this.options.addOption(Option.builder("o")
                .required()
                .longOpt("output-analysis-report-file")
                .hasArg()
                .desc("Output analysis report for input file")
                .build());
    }

    public Analyse() {
        this.configureOptions();
    }

    private String formatTimecode(SubtitleObject inputSubtitle, SubtitleTimeCode timeCode) {
        if (inputSubtitle.hasProperty(Property.FRAME_RATE)) {
            float frameRate = (float) inputSubtitle.getProperty(Property.FRAME_RATE);
            return timeCode.formatWithFramerate(frameRate);
        } else {
            return timeCode.toString();
        }
    }

    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("subtitle-analysis", this.options);
    }

    public JSONObject getProperties(SubtitleObject inputSubtitle) {
        JSONObject obj = new JSONObject();

        if (inputSubtitle.hasProperty(Property.FRAME_RATE)) {
            FrameRate frameRateEnum = FrameRate.getEnumFromFloat((float) inputSubtitle.getProperty(Property.FRAME_RATE));
            obj.put("frame_rate_numerator", frameRateEnum.getFrameRateNumerator());
            obj.put("frame_rate_denominator", frameRateEnum.getFrameRateDenominator());
        }
        if (inputSubtitle.hasProperty(Property.START_TIMECODE_PRE_ROLL)) {
            obj.put("start_timecode", formatTimecode(inputSubtitle, (SubtitleTimeCode) inputSubtitle.getProperty(Property.START_TIMECODE_PRE_ROLL)));
        }
        if (inputSubtitle.getCues().size() > 0) {
            obj.put("first_cue", formatTimecode(inputSubtitle, (SubtitleTimeCode) inputSubtitle.getCues().get(0).getStartTime()));
        }

        return obj;
    }

    private void run(String[] args) {
        // Create the parser
        CommandLineParser parser = new DefaultParser();
        try {
            // Parse the command line to get options
            CommandLine line = parser.parse(this.options, args);

            // Get options
            String fileAnalysed = line.getOptionValue("i");
            String reportFile = line.getOptionValue("o");

            // Build parser for input file
            SubtitleParser subtitleParser = null;

            try {
                subtitleParser = new Convert().buildParser(fileAnalysed, "utf-8");
            } catch(IOException e) {
                System.out.println(String.format("Unable to build parser for file %s: %s", fileAnalysed, e.getMessage()));
                System.exit(1);
            }

            InputStream is = null;
            BOMInputStream bom = null;

            // Open input file
            try {
                is = new FileInputStream(fileAnalysed);
                bom = new BOMInputStream(is);
            } catch(IOException e) {
                System.out.println(String.format("Input file %s does not exist: %s", fileAnalysed, e.getMessage()));
                System.exit(1);
            }

            // Parse input file
            SubtitleObject inputSubtitle = null;

            try {
                inputSubtitle = subtitleParser.parse(bom, true);
            } catch (IOException e) {
                System.out.println(String.format("Unable ro read input file %s: %s", fileAnalysed, e.getMessage()));
                System.exit(1);
            } catch (SubtitleParsingException e) {
                System.out.println(String.format("Unable to parse input file %s;: %s", fileAnalysed, e.getMessage()));
                System.exit(1);
            }

            // Get subtitle properties
            JSONObject obj = getProperties(inputSubtitle);

            // Write output file
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(reportFile))){
                obj.write(writer);
            } catch (IOException e) {
                System.out.println(String.format("Unable to write output file %s: %s", reportFile, e.getMessage()));
                System.exit(1);
            }
        } catch (ParseException exp) {
            this.printHelp();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Analyse analyse = new Analyse();
        analyse.run(args);
    }
}
