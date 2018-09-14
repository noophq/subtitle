/*
 * Title: StlParserTest
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.stl;


/**
 * Created by clebeaupin on 24/11/15.
 */
public class StlParserTest {
//    private StlObject tested;
//    private StlGsi testedGsi;
//    private StlTti testedTti;
//    private StlCue testedCue;

//    @Before
//    public void setUp() throws Exception {
//        InputStream is = new FileInputStream("src/test/resources/stl/test.stl");
//        StlParser stlParser = new StlParser();
//        tested = (StlObject) stlParser.parse(is);
//        testedGsi = tested.getGsi();
//        testedTti = tested.getTtis().get(1);
//
//        // First tti is not empty so it is not considered as a cue
//        testedCue = (StlCue) tested.getCue(0);
//    }
//
//    @Test
//    public void testGsiCpn() throws Exception {
//        assertEquals(StlGsi.Cpn.MULTILINGUAL, testedGsi.getCpn());
//    }
//
//    @Test
//    public void testGsiDfc() throws Exception {
//        assertEquals(StlGsi.Dfc.STL25, testedGsi.getDfc());
//    }
//
//    @Test
//    public void testGsiDsc() throws Exception {
//        assertEquals(StlGsi.Dsc.DSC_TELETEXT_LEVEL_1, testedGsi.getDsc());
//    }
//
//    @Test
//    public void testGsiCct() throws Exception {
//        assertEquals(StlGsi.Cct.LATIN, testedGsi.getCct());
//    }
//
//    @Test
//    public void testGsiOpt() throws Exception {
//        assertEquals("TEST", testedGsi.getOpt());
//    }
//
//    @Test
//    public void testGsiOet() throws Exception {
//        assertEquals("TEST EPISODE", testedGsi.getOet());
//    }
//
//    @Test
//    public void testGsiTpt() throws Exception {
//        assertEquals("TEST", testedGsi.getTpt());
//    }
//
//    @Test
//    public void testGsiTet() throws Exception {
//        assertEquals("EPISODE DE TEST", testedGsi.getTet());
//    }
//
//    @Test
//    public void testGsiTn() throws Exception {
//        assertEquals("C. Lebeaupin", testedGsi.getTn());
//    }
//
//    @Test
//    public void testGsiCd() throws Exception {
//        // Expected date: 2013-10-10
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(0);
//        cal.set(2013, Calendar.OCTOBER, 10, 0, 0, 0);
//        assertEquals(cal.getTime(), testedGsi.getCd());
//    }
//
//    @Test
//    public void testGsiRd() throws Exception {
//        // Expected date: 2013-10-17
//        Calendar cal = Calendar.getInstance();
//        cal.setTimeInMillis(0);
//        cal.set(2013, Calendar.OCTOBER, 17, 0, 0, 0);
//        assertEquals(cal.getTime(), testedGsi.getRd());
//    }
//
//    @Test
//    public void testGsiRn() throws Exception {
//        assertEquals(8224, testedGsi.getRn());
//    }
//
//    @Test
//    public void testGsiTnb() throws Exception {
//        assertEquals(13, testedGsi.getTnb());
//    }
//
//    @Test
//    public void testGsiTns() throws Exception {
//        assertEquals(13, testedGsi.getTns());
//    }
//
//    @Test
//    public void testGsiMnc() throws Exception {
//        assertEquals(40, testedGsi.getMnc());
//    }
//
//    @Test
//    public void testGsiMnr() throws Exception {
//        assertEquals(23, testedGsi.getMnr());
//    }
//
//    @Test
//    public void testGsiTcp() throws Exception {
//        assertEquals(36, testedGsi.getTcp().getTime());
//    }
//
//    @Test
//    public void testGsiTcf() throws Exception {
//        assertEquals(36, testedGsi.getTcf().getTime());
//    }
//
//    @Test
//    public void testGsiCo() throws Exception {
//        assertEquals("FRA", testedGsi.getCo());
//    }
//
//    @Test
//    public void testGsiPub() throws Exception {
//        assertEquals("TESTSUB", testedGsi.getPub());
//    }
//
//    @Test
//    public void testGsiEn() throws Exception {
//        assertEquals("TESTSUB 1.0.1", testedGsi.getEn());
//    }
//
//    @Test
//    public void testTtiSn() throws Exception {
//        assertEquals(2, testedTti.getSn());
//    }
//
//    @Test
//    public void testTtiTci() throws Exception {
//        assertEquals(36006320, testedTti.getTci().getTime());
//    }
//
//    @Test
//    public void testTtiTco() throws Exception {
//        assertEquals(36009360, testedTti.getTco().getTime());
//    }
//
//    @Test
//    public void testTtiVp() throws Exception {
//        assertEquals(20, testedTti.getVp());
//    }
//
//    @Test
//    public void testTtiJc() throws Exception {
//        assertEquals(StlTti.Jc.CENTER, testedTti.getJc());
//    }
//
//    @Test
//    public void testCueLines() throws Exception {
//        assertEquals(2, testedCue.getLines().size());
//        SubtitleLine line1 = testedCue.getLines().get(0);
//        SubtitleLine line2 = testedCue.getLines().get(1);
//        SubtitleStyledText text1 = (SubtitleStyledText) line1.getTexts().get(0);
//
//        // Test content
//        assertEquals("-Ellis Island,", line1.toString());
//        assertEquals("îlot de larmes et d'exil,", line2.toString());
//
//        // Test styles
//        assertEquals("cyan", text1.getStyle().getColor());
//    }
//
//    @Test
//    public void testCueRegion() throws Exception {
//        assertEquals(0, (int) testedCue.getRegion().getY());
//        assertEquals(9565, (int) (testedCue.getRegion().getHeight()*100));
//    }
//
//    @Test
//    public void testCueRegion2() throws Exception {
//        // Make sure that 2 cues having a different number of lines
//        // have equal regions
//        StlCue testedCue2 = (StlCue) tested.getCue(1);
//        assertEquals(testedCue.getRegion(), testedCue2.getRegion());
//    }
}