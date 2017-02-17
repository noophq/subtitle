/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.vtt;

import com.blackboard.collaborate.csl.validators.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTimeCode;
import com.blackboard.collaborate.csl.validators.subtitle.util.TestUtils;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VttParserTest {

    @Test
    public void testFormatTimecode() {
		CountingValidationListener listener = new CountingValidationListener();
		ValidationReporterImpl reporter = new ValidationReporterImpl(null);
		reporter.addValidationListener(listener);

		VttCue cue = new VttCue(reporter, null);

		SubtitleTimeCode tc = cue.parseTimeCode("00:10.000", 0);
		assertEquals(0, tc.getHour());
		assertEquals(0, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(0, tc.getMillisecond());

		tc = cue.parseTimeCode("00:13.000", 0);
		assertEquals(0, tc.getHour());
		assertEquals(0, tc.getMinute());
		assertEquals(13, tc.getSecond());
		assertEquals(0, tc.getMillisecond());

		tc = cue.parseTimeCode("02:13.880", 0);
		assertEquals(0, tc.getHour());
		assertEquals(2, tc.getMinute());
		assertEquals(13, tc.getSecond());
		assertEquals(880, tc.getMillisecond());

		tc = cue.parseTimeCode("1:27:10.200", 0);
		assertEquals(1, tc.getHour());
		assertEquals(27, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(200, tc.getMillisecond());

		tc = cue.parseTimeCode("02:27:10.200", 0);
		assertEquals(2, tc.getHour());
		assertEquals(27, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(200, tc.getMillisecond());

    }

    @Test
    public void testFiles() throws IOException {
        TestUtils.testFolder("src/test/resources/vtt");
    }

//	@Test
//	public void testHelper() throws IOException {
//		TestUtils.testFile("src/test/resources/vtt/regions.vtt", 1);
//	}
}