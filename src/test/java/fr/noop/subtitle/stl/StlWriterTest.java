package fr.noop.subtitle.stl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import org.junit.*;

import fr.noop.subtitle.model.SubtitleWriterWithTimecode;
import fr.noop.subtitle.srt.SrtObject;
import fr.noop.subtitle.srt.SrtParser;
import fr.noop.subtitle.util.SubtitleTimeCode;

public class StlWriterTest {
    private StlObject source;
    private StlObject tested;
    private StlGsi testedGsi;
    private StlTti testedTti;
    private StlObject tested2;
    private StlGsi tested2Gsi;
    private StlTti tested2Tti;
    private SubtitleTimeCode sourceTimeCode;
    private SubtitleTimeCode newTimeCode;
    private SubtitleTimeCode firstTimeCode;

    @Before
    public void setUp() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/stl/test.stl");
        OutputStream os = new FileOutputStream("src/test/resources/stl/tested.stl");
        StlParser stlParser = new StlParser();
        StlWriter stlWriter = new StlWriter();

        source = stlParser.parse(is);
        ((SubtitleWriterWithTimecode) stlWriter).setTimecode("01:00:00:00");
        stlWriter.write(source, os);
        sourceTimeCode = source.getGsi().getTcp();
        newTimeCode = new SubtitleTimeCode(1, 0, 0, 0);

        InputStream isTested = new FileInputStream("src/test/resources/stl/tested.stl");
        tested = stlParser.parse(isTested);
        testedGsi = tested.getGsi();
        testedTti = tested.getTtis().get(0);

        // test from srt to stl
        InputStream isSrt = new FileInputStream("src/test/resources/srt/no-eof-nl.srt");

        SrtParser srtParser = new SrtParser("utf-8");
        SrtObject srtSource = srtParser.parse(isSrt);
        OutputStream stlOs = new FileOutputStream("src/test/resources/stl/tested2.stl");
        stlWriter.write(srtSource, stlOs);

        InputStream isTested2 = new FileInputStream("src/test/resources/stl/tested2.stl");
        tested2 = stlParser.parse(isTested2);
        tested2Gsi = tested2.getGsi();
        tested2Tti = tested2.getTtis().get(0);
        firstTimeCode = new SubtitleTimeCode(1, 2, 17, 440);
    }

    @Test
    public void testGsiTcp() throws Exception {
        assertNotEquals(sourceTimeCode.getTime(), testedGsi.getTcp().getTime());
        assertEquals(newTimeCode.getTime(), testedGsi.getTcp().getTime());
        assertEquals(newTimeCode.getTime(), tested2Gsi.getTcp().getTime());
    }

    @Test
    public void testTtiTci() throws Exception {
        //first source cue is empty -> not writed
        SubtitleTimeCode sourceTimeCodeConverted = source.getTtis().get(1).getTci().convertFromStart(newTimeCode, sourceTimeCode);
        assertEquals(sourceTimeCodeConverted.getTime(), testedTti.getTci().getTime());
        assertEquals(firstTimeCode.getTime(), tested2Tti.getTci().getTime());
    }

    @Test
    public void testGsiLc() throws Exception {
        assertEquals(LanguageCode.Lc.FRENCH, testedGsi.getLc());
        assertEquals(LanguageCode.Lc.FRENCH, tested2Gsi.getLc());
    }

    @Test
    public void testGsiOpt() throws Exception {
        assertEquals("TEST", testedGsi.getOpt());
    }
}
