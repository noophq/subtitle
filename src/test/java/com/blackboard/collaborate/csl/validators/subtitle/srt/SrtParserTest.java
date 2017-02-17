package com.blackboard.collaborate.csl.validators.subtitle.srt;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleParsingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SrtParserTest {

    private SrtParser srtParser = new SrtParser(StandardCharsets.UTF_8);

    @Test
    public void test() throws IOException, SubtitleParsingException {
        FileInputStream is = new FileInputStream("src/test/resources/srt/no-eof-nl.srt");
        SrtObject srtObject = (SrtObject) srtParser.parse(is);

        Assert.assertEquals(2, srtObject.getCuesCount());
    }
}
