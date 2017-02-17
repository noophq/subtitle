package com.blackboard.collaborate.csl.validators.subtitle.srt;

import com.blackboard.collaborate.csl.validators.subtitle.util.TestUtils;
import org.junit.Test;

import java.io.IOException;

public class SrtParserTest {

    @Test
    public void testFiles() throws IOException {
        TestUtils.testFolder("src/test/resources/srt");

//        SrtObject srtObject = (SrtObject) srtParser.parse(is);
//        Assert.assertEquals(2, srtObject.getCuesCount());
    }
}
