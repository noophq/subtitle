package fr.noop.subtitle.srt;

import fr.noop.subtitle.model.SubtitleParsingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class SrtParserTest {

    private SrtParser srtParser = new SrtParser("utf-8");

    @Test
    public void test() throws IOException, SubtitleParsingException {
        FileInputStream is = new FileInputStream("src/test/resources/srt/no-eof-nl.srt");
        SrtObject srtObject = srtParser.parse(is);

        Assert.assertEquals(2, srtObject.getCues().size());
    }
}
