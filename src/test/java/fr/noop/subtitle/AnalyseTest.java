package fr.noop.subtitle;

import fr.noop.subtitle.stl.StlParser;
import fr.noop.subtitle.model.SubtitleObject;

import org.junit.Assert;
import org.junit.Test;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;

public class AnalyseTest {
    @Test
    public void test() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/stl/test.stl");
        StlParser stlParser = new StlParser();
        SubtitleObject stl = stlParser.parse(is);

        Analyse analyse = new Analyse();
        JSONObject report = analyse.getProperties(stl);

        Assert.assertEquals(25, report.get("frame_rate_numerator"));
        Assert.assertEquals("10:00:00:00", report.get("start_timecode"));
        Assert.assertEquals("10:00:06:08", report.get("first_cue"));
    }
}
