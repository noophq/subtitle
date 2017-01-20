/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import static org.junit.Assert.assertEquals;

import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.model.ValidationIssue;
import fr.noop.subtitle.model.ValidationListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleTimeCode;

import java.nio.charset.StandardCharsets;

public class VttParserTest {

    @Test
    public void testFormatTimecode() throws SubtitleParsingException {
		VttParser parser = new VttParser(StandardCharsets.UTF_8);

		SubtitleTimeCode tc = parser.parseTimeCode("00:10.000", 0);
		assertEquals(0, tc.getHour());
		assertEquals(0, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(0, tc.getMillisecond());

		tc = parser.parseTimeCode("00:13.000", 0);
		assertEquals(0, tc.getHour());
		assertEquals(0, tc.getMinute());
		assertEquals(13, tc.getSecond());
		assertEquals(0, tc.getMillisecond());

		tc = parser.parseTimeCode("02:13.880", 0);
		assertEquals(0, tc.getHour());
		assertEquals(2, tc.getMinute());
		assertEquals(13, tc.getSecond());
		assertEquals(880, tc.getMillisecond());

		tc = parser.parseTimeCode("1:27:10.200", 0);
		assertEquals(1, tc.getHour());
		assertEquals(27, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(200, tc.getMillisecond());

		tc = parser.parseTimeCode("02:27:10.200", 0);
		assertEquals(2, tc.getHour());
		assertEquals(27, tc.getMinute());
		assertEquals(10, tc.getSecond());
		assertEquals(200, tc.getMillisecond());

    }

	@Test
	public void testCueText() {
		VttParser parser = new VttParser(StandardCharsets.UTF_8);
		parser.addValidationListener(new ValidationListener() {
			@Override
			public void onValidation(ValidationIssue event) {
				System.out.println(event.toString());
			}
		});
		StringBuilder bld = new StringBuilder("<v Bill>plain text<b>bold text</b> end text");

		try {
			CueTreeNode node = parser.parseCueTree(bld, 0);
			System.out.println(node.toStyledString());

		} catch (SubtitleParsingException e) {
			Assert.fail(e.getMessage());
		}
	}
}