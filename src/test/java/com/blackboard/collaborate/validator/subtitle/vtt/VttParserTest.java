/*
 * Title: VttParserTest
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

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;
import com.blackboard.collaborate.validator.subtitle.util.TestUtils;
import com.blackboard.collaborate.validator.subtitle.util.TimeCodeParser;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class VttParserTest {

    private void testTimeCode(ValidationReporter reporter, String timeCodeString, int hr, int min, int sec, int ms) {
        SubtitleTimeCode tc = TimeCodeParser.parseVtt(reporter, timeCodeString, 0);
        assertNotNull(tc);
        assertEquals(hr, tc.getHour());
        assertEquals(min, tc.getMinute());
        assertEquals(sec, tc.getSecond());
        assertEquals(ms, tc.getMillisecond());
        assertEquals(timeCodeString, TimeCodeParser.formatVtt(tc));
    }

    private void testInvalidTimeCode(ValidationReporter reporter, String timeCodeString) {
        SubtitleTimeCode tc = TimeCodeParser.parseVtt(reporter, timeCodeString, 0);
        assertNull(tc);
    }

    @Test
    public void testInvalidTimeCode() {
        CountingValidationListener listener = new CountingValidationListener();
        ValidationReporterImpl reporter = new ValidationReporterImpl(null);
        reporter.addValidationListener(listener);

        testInvalidTimeCode(reporter, "65:10.200");
        testInvalidTimeCode(reporter, "59:60.200");
        testInvalidTimeCode(reporter, "59:59.1200");
    }

    @Test
    public void tesTimeCode() {
		CountingValidationListener listener = new CountingValidationListener();
		ValidationReporterImpl reporter = new ValidationReporterImpl(null);
		reporter.addValidationListener(listener);

        testTimeCode(reporter, "00:10.000", 0, 0, 10, 0);
        testTimeCode(reporter, "00:03.009", 0, 0, 3, 9);
        testTimeCode(reporter, "02:13.880", 0, 2, 13, 880);
        testTimeCode(reporter, "1:27:10.200", 1, 27, 10, 200);
        testTimeCode(reporter, "2:27:10.200", 2, 27, 10, 200);
        testTimeCode(reporter, "3333:27:10.200", 3333, 27, 10, 200);
    }

    @Test
    public void testFiles() throws IOException {
        TestUtils.testFolder("src/test/resources/vtt");
    }
}