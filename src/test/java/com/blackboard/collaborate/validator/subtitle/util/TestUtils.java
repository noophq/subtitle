/*
 * Title: TestUtils
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.srt.SrtParser;
import com.blackboard.collaborate.validator.subtitle.vtt.CountingValidationListener;
import com.blackboard.collaborate.validator.subtitle.vtt.VttParser;
import org.junit.Assert;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Created by jdvorak on 06/02/2017.
 */
public final class TestUtils {
    private TestUtils() {
        // N/A
    }

    private static SubtitleParser parserForFile(ValidationReporter reporter, SubtitleReader reader, String file) {
        String ext = "";
        int i = file.lastIndexOf('.');
        if (i > 0) {
            ext = file.substring(i + 1);
        }

        switch (ext) {
            case "vtt":
                return new VttParser(reporter, reader);
            case "srt":
                return new SrtParser(reporter, reader);
            default:
                return null;
        }
    }

    public static void testFile(String file, int errors) {

        CountingValidationListener listener = new CountingValidationListener();

        try (SubtitleReader reader = new SubtitleReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            reporter.addValidationListener(listener);
            SubtitleParser parser = parserForFile(reporter, reader, file);
            if (parser == null) {
                return;
            }

            System.out.print("File: " + file);
            parser.parse();
            listener.exactAssert(file, errors);

        } catch (AssertionError e) {
            System.out.println(" ...ERROR");
            throw e;
        } catch (IOException e) {
            System.out.println(" ...ERROR");
            Assert.fail(e.getMessage());
        }

        System.out.println(" ...OK");
    }

    public static void testFolder(String dir) throws IOException {
        Path testDir = Paths.get(dir);

        Properties props = new Properties();
        try (InputStream is = new FileInputStream(Paths.get(testDir.toString(), "test.properties").toString())) {
            props.load(is);
        }

        props.stringPropertyNames().forEach(fileStr -> {
            String errStr = props.getProperty(fileStr);
            int errors = Integer.parseInt(errStr);
            Path path = testDir.resolve(fileStr);
            if (Files.exists(path)) {
                testFile(path.toString(), errors);
            }
        });
    }
}
