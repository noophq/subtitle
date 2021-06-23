package fr.noop.subtitle.stl;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.Assert.*;
import org.junit.*;

import fr.noop.subtitle.model.SubtitleWriterWithTimecode;
import fr.noop.subtitle.util.SubtitleTimeCode;

public class StlWriterTest {
    private StlObject source;
    private StlObject tested;
    private StlGsi testedGsi;
    private StlTti testedTti;
    private SubtitleTimeCode sourceTimeCode;
    private SubtitleTimeCode newTimeCode;

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
    }

    @Test
    public void testGsiTcp() throws Exception {
        assertNotEquals(sourceTimeCode.getTime(), testedGsi.getTcp().getTime());
        assertEquals(newTimeCode.getTime(), testedGsi.getTcp().getTime());
    }

    @Test
    public void testTtiTci() throws Exception {
        //first source cue is empty -> not writed
        SubtitleTimeCode sourceTimeCodeConverted = source.getTtis().get(1).getTci().convertFromStart(newTimeCode, sourceTimeCode);
        assertEquals(sourceTimeCodeConverted.getTime(), testedTti.getTci().getTime());
    }

    @Test
    public void testGsiLc() throws Exception {
        assertEquals(LanguageCode.Lc.FRENCH, testedGsi.getLc());
    }

    @Test
    public void testGsiOpt() throws Exception {
        assertEquals("TEST", testedGsi.getOpt());
    }
}
