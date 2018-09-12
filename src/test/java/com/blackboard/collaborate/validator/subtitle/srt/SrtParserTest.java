/*
 * Title: SrtParserTest
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

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;
import com.blackboard.collaborate.validator.subtitle.util.TestUtils;
import com.blackboard.collaborate.validator.subtitle.vtt.CountingValidationListener;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SrtParserTest {
    @Test
    public void testParseTimecodeShort() {
        final String timeCode = "01:10,678";
        ValidationReporterImpl reporter = new ValidationReporterImpl(null);
        SrtCue cue = new SrtCue(reporter, null);
        SubtitleTimeCode tc = cue.parseTimeCode(timeCode, 0);
        assertNotNull(tc);
        assertEquals(timeCode, cue.formatTimeCode(tc));
    }

    @Test
    public void testParseTimecodeLong() {
        final String timeCode = "3333:55:10,234";
        ValidationReporterImpl reporter = new ValidationReporterImpl(null);
        SrtCue cue = new SrtCue(reporter, null);
        SubtitleTimeCode tc = cue.parseTimeCode(timeCode, 0);
        assertNotNull(tc);
        assertEquals(timeCode, cue.formatTimeCode(tc));
    }

    private void testTimeCode(SrtCue cue, String timeCodeString, int hr, int min, int sec, int ms) {
        SubtitleTimeCode tc = cue.parseTimeCode(timeCodeString, 0);
        assertNotNull(tc);
        assertEquals(hr, tc.getHour());
        assertEquals(min, tc.getMinute());
        assertEquals(sec, tc.getSecond());
        assertEquals(ms, tc.getMillisecond());
        assertEquals(timeCodeString, cue.formatTimeCode(tc));
    }

    @Test
    public void testFormatTimecode() {
        CountingValidationListener listener = new CountingValidationListener();
        ValidationReporterImpl reporter = new ValidationReporterImpl(null);
        reporter.addValidationListener(listener);

        SrtCue cue = new SrtCue(reporter, null);

        testTimeCode(cue, "00:10,000", 0, 0, 10, 0);
        testTimeCode(cue, "00:03,009", 0, 0, 3, 9);
        testTimeCode(cue, "02:13,880", 0, 2, 13, 880);
        testTimeCode(cue, "1:27:10,200", 1, 27, 10, 200);
        testTimeCode(cue, "2:27:10,200", 2, 27, 10, 200);
        testTimeCode(cue, "3333:27:10,200", 3333, 27, 10, 200);
    }

    @Test
    public void testFiles() throws IOException {
        TestUtils.testFolder("src/test/resources/srt");
    }
}
