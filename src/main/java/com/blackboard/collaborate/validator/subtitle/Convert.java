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

import com.blackboard.collaborate.validator.subtitle.model.SubtitleException;
import com.blackboard.collaborate.validator.subtitle.model.ValidationIssue;
import lombok.extern.slf4j.Slf4j;
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


@Slf4j
public class Convert {
    private final Options options = new Options();

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
//        String inputCharset = line.getOptionValue("ic", StandardCharsets.UTF_8.name());
//        String outputCharset = line.getOptionValue("oc", StandardCharsets.UTF_8.name());
        boolean disableStrictMode = line.hasOption("disable-strict-mode");

        int subtitleOffset = Integer.parseInt(line.getOptionValue("so", "0"));
        int maxDuration = Integer.parseInt(line.getOptionValue("sd", "-1"));

        SubtitleConverter.Format inFormat = SubtitleConverter.Format.getEnum(getFileExtension(inputFilePath));
        SubtitleConverter.Format outFormat = SubtitleConverter.Format.getEnum(getFileExtension(outputFilePath));

//        Charset iCharset = Charset.forName(inputCharset);
//        Charset oCharset = Charset.forName(outputCharset);

        SubtitleConverter converter = new SubtitleConverter();
        try {
            converter.convert(new FileInputStream(inputFilePath), inFormat, new FileOutputStream(outputFilePath), outFormat, null, !disableStrictMode);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return 1; // IO error
        } catch (SubtitleException e) {
            System.err.println(e.getMessage());
            if (e.getIssueList() != null) {
                for (ValidationIssue err : e.getIssueList()) {
                    System.err.println(err);
                }
            }
            return 1; // error
        }
        return 0;
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
