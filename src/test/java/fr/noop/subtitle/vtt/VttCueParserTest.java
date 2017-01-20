package fr.noop.subtitle.vtt;

import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.model.SubtitleParsingException;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Created by jdvorak on 20.1.2017.
 */
public class VttCueParserTest {
    @Test
    public void test() {
        VttParser parser = new VttParser(StandardCharsets.UTF_8);

        StringBuilder bld = new StringBuilder("plain text<b>bold text</b> end text");
        try {
            CueTreeNode node = parser.parseCueTree(bld);
            System.out.println(node.toStyledString());

        } catch (SubtitleParsingException e) {
            Assert.fail(e.getMessage());
        }
    }
}
