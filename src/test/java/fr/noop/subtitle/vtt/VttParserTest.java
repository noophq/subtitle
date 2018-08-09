package fr.noop.subtitle.vtt;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import fr.noop.subtitle.exception.InvalidTimeRangeException;
import fr.noop.subtitle.model.SubtitleParsingException;

public class VttParserTest {

    private VttParser vttParser = new VttParser("utf-8");

    @Test
    public void testCompleteTimecode() throws IOException, SubtitleParsingException, InvalidTimeRangeException {
        FileInputStream is = new FileInputStream("src/test/resources/vtt/will.vtt");
        VttObject vttObject = vttParser.parse(is, false);

        Assert.assertEquals(8, vttObject.getCues().size());
    }
    
    @Test
    public void testHourlessTimecode() throws IOException, SubtitleParsingException, InvalidTimeRangeException {
        FileInputStream is = new FileInputStream("src/test/resources/vtt/will-no-hour.vtt");
        VttObject vttObject = vttParser.parse(is, false);

        Assert.assertEquals(9, vttObject.getCues().size());
    }
}
