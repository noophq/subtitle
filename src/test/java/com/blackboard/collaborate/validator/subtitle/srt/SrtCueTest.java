/*
 * Title: SrtCueTest
 * Copyright (c) 2018. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.srt;

import com.blackboard.collaborate.validator.subtitle.base.ValidationReporterImpl;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;
import com.blackboard.collaborate.validator.subtitle.vtt.CountingValidationListener;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by jdvorak on 20.1.2017.
 */
public class SrtCueTest {

    private void testCueText(String text, int errors) {
        System.out.print("TESTING: " + text);

        CountingValidationListener listener = new CountingValidationListener();

        try (SubtitleReader reader = new SubtitleReader(new StringReader(text))) {
            ValidationReporterImpl reporter = new ValidationReporterImpl(reader);
            reporter.addValidationListener(listener);
            SrtCue cue = new SrtCue(reporter, null);

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
        testCueText("plain text<b>bold text</b> end text", 0);
    }

    // disclosed '</b' tag
    @Test
    public void testCueText2() {
        testCueText("plain text<b>bold text</b end text", 2);
    }

    // disclosed '<b' tag
    @Test
    public void testCueText3() {
        testCueText("plain text<b bold text</b> end text", 2);
    }

    // invalid char '>'
    @Test
    public void testCueText4() {
        testCueText("plain text b> bold text</b> end text", 2);
    }

    // entities OK
    @Test
    public void testCueText5() {
        testCueText("plain &lt; &gt; &nbsp; end text", 0);
    }

    // invalid entity &nb
    // missing ; in entity
    @Test
    public void testCueText6() {
        testCueText("plain &lt; &gt; &nb", 1);
        testCueText("plain &lt &gt;", 1);
    }

    // timestamp in cue text
    @Test
    public void testCueText10() {
        testCueText("start text <01:01:01.234> late text", 2);
    }

    // invalid nesting
    @Test
    public void testCueText13() {
        testCueText("start text <b>bla bla <i>blab la<b>b2</b>erwfwfw</i>wwefwe</b>wfwefwe", 1);
    }
}
