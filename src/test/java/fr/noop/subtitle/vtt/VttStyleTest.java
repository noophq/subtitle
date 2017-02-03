package fr.noop.subtitle.vtt;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * Created by jdvorak on 01/02/2017.
 */
public class VttStyleTest {
    public static final String[] CSS_OK = new String[]{
            "::cue { some-style: somevalue; }",
            "::cue(.class1.class2) { some-style: somevalue; }",
            "::cue(tag) { some-style: somevalue; }",
            "::cue(#id) { some-style: somevalue; }",
            "::cue(tag#id) { some-style: somevalue; }",
            "::cue(.class.class) { some-style: somevalue; }",
            "::cue(tag.class) { some-style: somevalue; }",
            "::cue(tag#id.class1.class2) { some-style: somevalue; some-other:othervalue; }",
            "::cue(tag#id.class1.class2[voice=\"someone\"]) { some-style: somevalue; some-other:othervalue; /* abcdef */ }",
            "::cue { font: 26px Arial, Helvetica, sans-serif; }",

            "::cue([lang=\"en-US\"]) { some-style: somevalue; }",
            "::cue(:lang(en)) { some-style: somevalue; }",
            "::cue(:past) { some-style: somevalue; }",
            "::cue-region { color: yellow; }",
    };

    public static final String[] CSS_ERR = new String[]{
            "::cuex { some-style: somevalue; }",
            "::cue() { some-style: somevalue; }",
            "::cue(..class1.class2) { some-style: somevalue; }",
            "::cue(ta&g#) { some-style: somevalue; }",
            "::cue(tag#i!d) { some-style: somevalue; }",
            "::cue(tag#.class) { some-style:: somevalue; }",
            "::cue(tag#id.class1.class2) { some-style: somevalue; some-other; }",
            "::cue(tag#id.cl/* abcdef */ass1.class2[voice=\"someone\"]) { some-style: somevalue; some-other:othervalue; }",
            "::cue { font: 26px Ari/* abcdef al, Helvetica, sans-serif; }",

            "::cue(:past)) { some-style: somevalue; }",
            "::cue((:past) { some-style: somevalue; }",
            "::cue-region {{ color: yellow; }",
            "::cue { color: yellow; })",
            ":::cue { color: yellow; })",
            ":::cue ( color: yellow; }",
            ":::cue ( color:\n yellow; }",
            ":::c\nue ( color: yellow; }",
    };


//    @Test
//    public void testTokenizer() {
//        StringTokenizer st = new StringTokenizer(str, "{}:,;()# =", true);
//
//        while (st.hasMoreElements()) {
//            System.out.println(st.nextElement());
//        }
//    }
//
//    @Test
//    public void testScanner() {
//        try (Scanner scan = new Scanner(new StringReader(str))) {
//            scan.useDelimiter(Pattern.compile("\\s*[{}()#:=;]\\s*|\\s*/\\*\\s*|\\s*\\*/\\s*"));
//
//            while (scan.hasNext()) {
//                System.out.println("'" + scan.next() + "'");
//                //scan.skip("\\s*/\\*\\s*|\\s*\\*/\\s*")
//            }
//        }
//    }



    private void testParserPri(String[] styles, int errs) {
        VttParser parser = new VttParser(StandardCharsets.UTF_8);
        VttCue cue = new VttCue(parser, null);

        CountingValidationListener listener = new CountingValidationListener();
        parser.addValidationListener(listener);

        VttStyle style = new VttStyle(parser, null);

        for (String css : styles) {
            System.out.println(css);
            listener.reset();
            style.parse(new StringBuilder(css));
            listener.checkAssert(errs);
        }
    }

    @Test
    public void testOkStyles() {
        testParserPri(CSS_OK, 0);
    }

    @Test
    public void testErrorStyles() {
        testParserPri(CSS_ERR, 100);
    }

}
