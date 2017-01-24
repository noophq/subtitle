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

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VttParserTest {

    static class CountingValidationListener implements ValidationListener {
        int count;

        @Override
        public void onValidation(ValidationIssue event) {
            count++;
            System.out.println(event.toString());
        }

        public int getCount() {
            return count;
        }
    }

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


	private void testCueText(String text, int maxErrors) {
		VttParser parser = new VttParser(StandardCharsets.UTF_8);
        CountingValidationListener listener = new CountingValidationListener();
		parser.addValidationListener(listener);

		try (LineNumberReader lnrd = new LineNumberReader(new StringReader(text))) {
			parser.setSource(lnrd);
			CueTreeNode node = parser.parseCueTextTree();
			System.out.println(node.toStyledString());

		} catch (SubtitleParsingException e) {
			Assert.fail(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
		String msg = "Error count exceeded: " + listener.getCount() + " <= " + maxErrors;
		Assert.assertTrue(msg, listener.getCount() <= maxErrors);
	}

	private void testCueFile(String file, int maxErrors) {
		VttParser parser = new VttParser(StandardCharsets.UTF_8);
        CountingValidationListener listener = new CountingValidationListener();
        parser.addValidationListener(listener);

		try (InputStream is = new FileInputStream(file)) {
			parser.parse(is);

		} catch (SubtitleParsingException e) {
			//Assert.fail(e.getMessage());
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}

        String msg = "Error count exceeded: " + listener.getCount() + " <= " + maxErrors;
        Assert.assertTrue(msg, listener.getCount() <= maxErrors);
	}

	@Test
	public void testCueText1() {
		testCueText("<v Bill>plain text<b>bold text</b> end text", 1);
	}

	@Test
	public void testCueText2() {
		testCueText("<v Bill>plain text<b>bold text</b end text", 3);
	}

	@Test
	public void testCueText3() {
		testCueText("<v Bill>plain text<b bold text</b> end text", 2);
	}

	@Test
	public void testCueText4() {
		testCueText("<v Bill>plain text b> bold text</b> end text", 2);
	}

	@Test
	public void testCueText5() {
		testCueText("<v Bill>plain &lt; &gt; &nbsp; end text</v>", 0);
	}

	@Test
	public void testCueText6() {
		testCueText("plain &lt; &gt; &nb", 1);
	}

	// @Test (expected = IllegalArgumentException.class)
	@Test
	public void testAllCueFiles() throws IOException {
		Path testDir = Paths.get("src/test/resources/vtt/webvtt-file-parsing");

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(testDir)) {
			for (Path path : directoryStream) {
			    Path fileName = path.getFileName();
			    if (fileName.toString().endsWith(".vtt")) {
                    System.out.println("File: " + fileName.toString());
                    testCueFile(path.toString(), Integer.MAX_VALUE);
                }
			}
		}
	}

    @Test
    public void testNulls() throws IOException {
        testCueFile("src/test/resources/vtt/webvtt-file-parsing/nulls.vtt", Integer.MAX_VALUE);
    }
}