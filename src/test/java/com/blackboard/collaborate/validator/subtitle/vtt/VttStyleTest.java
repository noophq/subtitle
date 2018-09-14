/*
 * Title: VttStyleTest
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by jdvorak on 01/02/2017.
 */
public class VttStyleTest {

    private void testParserPri(String css, int errors) {
        CountingValidationListener listener = new CountingValidationListener();

        System.out.print("TESTING: " + css);
        try (SubtitleReader reader = new SubtitleReader(new StringReader(css))) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            reporter.addValidationListener(listener);

            VttStyle style = new VttStyle(reporter, null);

            style.parse(reader);
            listener.exactAssert("", errors);
        }
        catch (AssertionError e) {
            System.out.println(" ...ERROR");
            throw e;
        } catch (IOException e) {
            System.out.println(" ...ERROR");
            Assert.fail(e.getMessage());
        }
        System.out.println(" ...OK");
    }
    
    @Test
    public void testOkStyle1() {
        testParserPri("::cue { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle2() {
        testParserPri("::cue(.class1.class2) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle3() {
        testParserPri("::cue(tag) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle4() {
        testParserPri("::cue(#id) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle5() {
        testParserPri("::cue(tag#id) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle6() {
        testParserPri("::cue(.class.class) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle7() {
        testParserPri("::cue(tag.class) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle8() {
        testParserPri("::cue(tag#id.class1.class2) { some-style: somevalue; some-other:othervalue; }", 0);
    }

    @Test
    public void testOkStyle9() {
        testParserPri("::cue(tag#id.class1.class2[voice=\"someone\"]) { some-style: somevalue; some-other:othervalue; /* abcdef */ }", 0);
    }

    @Test
    public void testOkStyle10() {
        testParserPri("::cue { font: 26px Arial, Helvetica, sans-serif; }", 0);
    }
    
    @Test
    public void testOkStyle11() {
        testParserPri("::cue([lang=\"en-US\"]) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle12() {
        testParserPri("::cue(:lang(en)) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle13() {
        testParserPri("::cue(:past) { some-style: somevalue; }", 0);
    }

    @Test
    public void testOkStyle14() {
        testParserPri("::cue-region { color: yellow; }", 0);
    }


    @Test
    public void testOkStyle15() {
        testParserPri("::cue { color:\n yellow; \n}\n", 0); // new lines
    }
    
    @Test
    public void testOkStyle16() {
        testParserPri("::cue { color:\n yellow; \n}\n\nsome ignored text", 0); // new lines
    }
    
    @Test
    public void testErrorStyle1() {
        testParserPri("::cuex { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle2() {
        testParserPri("::cue() { some-style: somevalue; }", 1);
    }

    @Test
    public void testErrorStyle3() {
        testParserPri("::cue(..class1.class2) { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle4() {
        testParserPri("::cue(ta&g#) { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle5() {
        testParserPri("::cue(tag#i!d) { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle6() {
        testParserPri("::cue(tag#.class) { some-style:: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle7() {
        testParserPri("::cue(tag#id.class1.class2) { some-style: somevalue; some-other; }", 3);
    }

    @Test
    public void testErrorStyle8() {
        testParserPri("::cue(tag#id.cl/* abcdef */ass1.class2[voice=\"someone\"]) { some-style: somevalue; some-other:othervalue; }", 2);
    }

    @Test
    public void testErrorStyle9() {
        testParserPri("::cue { font: 26px Ari/* abcdef al, Helvetica, sans-serif; }", 4);
    }
    
    @Test
    public void testErrorStyle10() {
        testParserPri("::cue(:past)) { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle11() {
        testParserPri("::cue((:past) { some-style: somevalue; }", 2);
    }

    @Test
    public void testErrorStyle12() {
        testParserPri("::cue-region {{ color: yellow; }", 1);
    }

    @Test
    public void testErrorStyle13() {
        testParserPri("::cue { color: yellow; })", 1);
    }

    @Test
    public void testErrorStyle14() {
        testParserPri("::cue { newlines: \n\ninvalid; }", 2);
    }

    // new lines break
    @Test
    public void testErrorStyle15() {
        testParserPri(":::cue { color: yellow; })", 2);
    }

    @Test
    public void testErrorStyle16() {
        testParserPri("::cue ( color: yellow; }", 2);
    }

    @Test
    public void testErrorStyle17() {
        testParserPri("::cue ( color:\n yellow; }", 2);
    }

    @Test
    public void testErrorStyle18() {
        testParserPri("::c\nue ( color: yellow; }", 2);
    }
}
