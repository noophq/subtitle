package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.SubtitleParsingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;

public class VttParserTest {

    private VttParser vttParser = new VttParser("utf-8");

    @Test
    public void test() throws IOException, SubtitleParsingException {
        FileInputStream is = new FileInputStream("src/test/resources/vtt/test.vtt");
        VttObject vttObject = vttParser.parse(is);

        Assert.assertEquals(12, vttObject.getCues().size());
    }
}
