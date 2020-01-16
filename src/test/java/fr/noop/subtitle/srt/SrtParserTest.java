package fr.noop.subtitle.srt;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import com.hotmart.subtitle.util.TimecodeBadlyFormattedException;

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
    
    @Test
    public void expectedTimecodeBadlyFormatedException() throws IOException, SubtitleParsingException, InvalidTimeRangeException {
        FileInputStream is = new FileInputStream("src/test/resources/srt/invalid_format.srt");
        int lineError = -1;
        
        try {
        	srtParser.parse(is);
        } catch (TimecodeBadlyFormattedException e) {
        	lineError = e.getLineError();
		}
        
        Assert.assertEquals(6, lineError);
    }
}
