/*
 * Title: VttCueTest
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
 * Created by jdvorak on 20.1.2017.
 */
public class VttCueTest {

    private void testCueText(String text, int errors) {
        System.out.print("TESTING: " + text);

        CountingValidationListener listener = new CountingValidationListener();

        try (SubtitleReader reader = new SubtitleReader(new StringReader(text))) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            reporter.addValidationListener(listener);
            VttCue cue = new VttCue(reporter, null);

            cue.parseCueText(reader);

            listener.exactAssert("", errors);

        } catch (AssertionError e) {
            System.out.println(" ...ERROR");
            throw e;
        } catch (IOException e) {
            System.out.println(" ...ERROR");
            Assert.fail(e.getMessage());
        }

        System.out.println(" ...OK");
    }

    // missing '</v>' tag
    @Test
    public void testCueText1() {
        testCueText("<v Bill>plain text<b>bold text</b> end text", 0);
    }

    // disclosed '</b' tag
    @Test
    public void testCueText2() {
        testCueText("<v Bill>plain text<b>bold text</b end text", 2);
    }

    // disclosed '<b' tag
    @Test
    public void testCueText3() {
        testCueText("<v Bill>plain text<b bold text</b> end text", 2);
    }

    // invalid char '>'
    @Test
    public void testCueText4() {
        testCueText("<v Bill>plain text b> bold text</b> end text", 2);
    }

    // entities OK
    @Test
    public void testCueText5() {
        testCueText("<v Bill>plain &lt; &gt; &nbsp; end text</v>", 0);
        testCueText("<v Bil&lt;l>plain &lt; &gt; &nbsp; end text</v>", 0);
    }

    // invalid entity &nb
    // missing ; in entity
    @Test
    public void testCueText6() {
        testCueText("plain &lt; &gt; &nb", 1);
        testCueText("plain &lt &gt;", 1);
    }

    // v without annotation
    // lang without annotation
    @Test
    public void testCueText7() {
        testCueText("<v>no voice in v", 1);
        testCueText("<lang>no language in lang</lang>", 1);
    }

    // tags with annotation
    @Test
    public void testCueText8() {
        testCueText("<b annotation>a</b>", 1);
        testCueText("<rt annotation>a</rt>", 2);
        testCueText("<ruby annotation>a</ruby>", 1);
    }

    // b inside ruby/rt
    @Test
    public void testCueText9() {
        testCueText("<ruby>text<rt>wef<b>wef</b>we</rt></ruby>", 1);
    }

    // timestamp in cue text
    @Test
    public void testCueText10() {
        testCueText("start text <01:01:01.234> late text", 0);
    }

    @Test
    public void testCueText11() {
        testCueText("start text <lang.loud en>English</lang>", 0);
    }

    // invalid nesting
    @Test
    public void testCueText12() {
        testCueText("start text <lang.loud en>English<lang en>English2</lang></lang>", 1);
    }

    // invalid nesting
    @Test
    public void testCueText13() {
        testCueText("start text <b>bla bla <i>blab la<b>b2</b>erwfwfw</i>wwefwe</b>wfwefwe", 1);
    }

    // youtube
    @Test
    public void testCueText14() {
        testCueText("two<00:00:02.190><c> roads</c><00:00:02.580><c> diverged</c><00:00:03.330><c> in</c><c.colorE5E5E5><00:00:03.629><c> a</c><00:00:03.750><c> yellow</c><00:00:03.780><c> wood</c><00:00:04.080><c> and</c></c>", 7);
    }

    @Test
    public void testCueText15() {
        testCueText("aaa<aa&amp;bb>xxx</aa&amp;bb>", 1);
    }
}
