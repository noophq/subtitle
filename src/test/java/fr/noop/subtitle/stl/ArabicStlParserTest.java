/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.stl;

import static org.junit.Assert.*;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleLine;
import fr.noop.subtitle.util.SubtitleStyledText;
import org.junit.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by clebeaupin on 24/11/15.
 */
public class ArabicStlParserTest {
    private StlObject tested;
    private StlGsi testedGsi;
    private StlTti testedTti;
    private StlCue testedCue;

    @Before
    public void setUp() throws Exception {
        InputStream is = new FileInputStream("src/test/resources/stl/test_arabic.stl");
        StlParser stlParser = new StlParser();
        tested = stlParser.parse(is);
        testedGsi = tested.getGsi();
        testedTti = tested.getTtis().get(1);

        // First tti is not empty so it is not considered as a cue
        testedCue = (StlCue) tested.getCues().get(0);
    }

    @Test
    public void testGsiCpn() throws Exception {
        assertEquals(StlGsi.Cpn.UNITED_STATES, testedGsi.getCpn());
    }

    @Test
    public void testGsiDfc() throws Exception {
        assertEquals(StlGsi.Dfc.STL25, testedGsi.getDfc());
    }

    @Test
    public void testGsiDsc() throws Exception {
        assertEquals(StlGsi.Dsc.OPEN_SUBTITLING, testedGsi.getDsc());
    }

    @Test
    public void testGsiCct() throws Exception {
        assertEquals(StlGsi.Cct.LATIN_ARABIC, testedGsi.getCct());
    }

    @Test
    public void testGsiCd() throws Exception {
        // Expected date: 2013-10-10
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2016, Calendar.DECEMBER, 13, 0, 0, 0);
        assertEquals(cal.getTime(), testedGsi.getCd());
    }

    @Test
    public void testGsiRd() throws Exception {
        // Expected date: 2013-10-17
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(2017, Calendar.JANUARY, 11, 0, 0, 0);
        assertEquals(cal.getTime(), testedGsi.getRd());
    }

    @Test
    public void testGsiRn() throws Exception {
        assertEquals(12592, testedGsi.getRn());
    }

    @Test
    public void testGsiMnc() throws Exception {
        assertEquals(42, testedGsi.getMnc());
    }

    @Test
    public void testGsiMnr() throws Exception {
        assertEquals(11, testedGsi.getMnr());
    }

    @Test
    public void testGsiCo() throws Exception {
        assertEquals("USA", testedGsi.getCo());
    }

    @Test
    public void testTtiSn() throws Exception {
        assertEquals(2, testedTti.getSn());
    }

    @Test
    public void testTtiVp() throws Exception {
        assertEquals(11, testedTti.getVp());
    }

    @Test
    public void testTtiJc() throws Exception {
        assertEquals(StlTti.Jc.CENTER, testedTti.getJc());
    }

    @Test
    public void testCueLines() throws Exception {
        assertEquals(1, testedCue.getLines().size());
        SubtitleLine line1 = testedCue.getLines().get(0);
        SubtitleStyledText text1 = (SubtitleStyledText) line1.getTexts().get(0);
        // Test content
        assertEquals("هل أنت واثق أنه هنا", line1.toString());
    }

    @Test
    public void testCueRegion() throws Exception {
        assertEquals(0, (int) testedCue.getRegion().getY());
        assertEquals(10000, (int) (testedCue.getRegion().getHeight()*100));
    }

    @Test
    public void testCueRegion2() throws Exception {
        // Make sure that 2 cues having a different number of lines
        // have equal regions
        StlCue testedCue2 = (StlCue) tested.getCues().get(1);
        assertEquals(testedCue.getRegion(), testedCue2.getRegion());
    }
}