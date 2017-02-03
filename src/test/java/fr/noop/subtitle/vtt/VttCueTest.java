package fr.noop.subtitle.vtt;

import fr.noop.subtitle.model.SubtitleParsingException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

// FIXME - rename the class to VttCueTest

/**
 * Created by jdvorak on 20.1.2017.
 */
public class VttCueTest {

    private void testCueText(String text, int maxErrors) {
        VttParser parser = new VttParser(StandardCharsets.UTF_8);
        VttCue cue = new VttCue(parser, null);

        CountingValidationListener listener = new CountingValidationListener();
        parser.addValidationListener(listener);

        try {
            cue.parseCueTextTree(new StringBuilder(text));

        } catch (SubtitleParsingException e) {
            Assert.fail(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        listener.checkAssert(maxErrors);
    }

    // missing '</v>' tag
    @Test
    public void testCueText1() {
        testCueText("<v Bill>plain text<b>bold text</b> end text", 1);
    }

    // disclosed '</b' tag
    @Test
    public void testCueText2() {
        testCueText("<v Bill>plain text<b>bold text</b end text", 3);
    }

    // disclosed '<b' tag
    @Test
    public void testCueText3() {
        testCueText("<v Bill>plain text<b bold text</b> end text", 3);
    }

    // invalid char '>'
    @Test
    public void testCueText4() {
        testCueText("<v Bill>plain text b> bold text</b> end text", 3);
    }

    // entities OK
    @Test
    public void testCueText5() {
        testCueText("<v Bill>plain &lt; &gt; &nbsp; end text</v>", 0);
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
        testCueText("<v>no voice in v", 2);
        testCueText("<lang>no language in lang</lang>", 2);
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

    // timestamp in cue tesxt
    @Test
    public void testCueText10() {
        testCueText("start text <01:01:01.234> late text", 1);
    }

}
