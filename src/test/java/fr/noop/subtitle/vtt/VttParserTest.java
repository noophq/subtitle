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

import fr.noop.subtitle.model.SubtitleParsingException;
import fr.noop.subtitle.util.SubtitleTimeCode;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class VttParserTest {

    @Test
    public void testFormatTimecode() throws SubtitleParsingException {
		VttParser parser = new VttParser(StandardCharsets.UTF_8);
		VttCue cue = new VttCue(parser, null);

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

	private void testVttFile(String file, int errors) {
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

        listener.checkAssert(errors);
	}

	private void testFolder(String dir) throws IOException {
		Path testDir = Paths.get(dir);

		Properties props = new Properties();
		try (InputStream is = new FileInputStream(Paths.get(testDir.toString(), "vtt.properties").toString())) {
			props.load(is);
		}

		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(testDir)) {
			for (Path path : directoryStream) {
			    Path fileName = path.getFileName();
			    String fileStr = fileName.toString();
			    if (fileStr.endsWith(".vtt")) {
                    System.out.println("File: " + fileStr);
                    String errStr = props.getProperty(fileStr);
                    int errors = Integer.parseInt(errStr);
                    testVttFile(path.toString(), errors);
                }
			}
		}
	}

    @Test
    public void testFiles() throws IOException {
        testFolder("src/test/resources/vtt");
    }

//	@Test
//	public void testStyle() throws IOException {
//		testVttFile("src/test/resources/vtt/css_styles.vtt", 6);
//	}
}