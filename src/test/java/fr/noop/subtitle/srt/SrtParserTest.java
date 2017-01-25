package fr.noop.subtitle.srt;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.noop.subtitle.exception.InvalidTimeRangeException;
import fr.noop.subtitle.model.SubtitleParsingException;

public class SrtParserTest {

    private SrtParser srtParser = new SrtParser("utf-8");

    @Test
    public void test() throws IOException, SubtitleParsingException, InvalidTimeRangeException {
        FileInputStream is = new FileInputStream("src/test/resources/srt/no-eof-nl.srt");
        SrtObject srtObject = srtParser.parse(is);

        Assert.assertEquals(2, srtObject.getCues().size());
    }
}
