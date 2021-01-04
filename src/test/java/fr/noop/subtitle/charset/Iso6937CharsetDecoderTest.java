/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.charset;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Created by clebeaupin on 29/09/15.
 */
public class Iso6937CharsetDecoderTest {
    public CharsetDecoder tested;

    @Before
    public void setUp() throws Exception {
        Charset charset = new Iso6937Charset("ISO-6937", new String[] {});
        tested = charset.newDecoder();
    }

    public String decodeBytes(byte[] bytes) throws UnsupportedEncodingException {
        ByteBuffer bb = ByteBuffer.wrap(bytes);

        try {
            CharBuffer cb = tested.decode(bb);
            return String.valueOf(cb);
        } catch (CharacterCodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }

    public String decode(String in) throws UnsupportedEncodingException {
        return decodeBytes(in.getBytes());
    }

    @Test
    public void testAscii() throws UnsupportedEncodingException {
        assertEquals("abcdefghijklmnopqrstuvwxyz", decode("abcdefghijklmnopqrstuvwxyz"));
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", decode("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    public void testGraveAccent() throws UnsupportedEncodingException {
        assertEquals("À", decodeBytes(DatatypeConverter.parseHexBinary("c141")));
        assertEquals("È", decodeBytes(DatatypeConverter.parseHexBinary("c145")));
        assertEquals("Ì", decodeBytes(DatatypeConverter.parseHexBinary("c149")));
        assertEquals("Ò", decodeBytes(DatatypeConverter.parseHexBinary("c14f")));
        assertEquals("Ù", decodeBytes(DatatypeConverter.parseHexBinary("c155")));
        assertEquals("à", decodeBytes(DatatypeConverter.parseHexBinary("c161")));
        assertEquals("è", decodeBytes(DatatypeConverter.parseHexBinary("c165")));
        assertEquals("ì", decodeBytes(DatatypeConverter.parseHexBinary("c169")));
        assertEquals("ò", decodeBytes(DatatypeConverter.parseHexBinary("c16f")));
        assertEquals("ù", decodeBytes(DatatypeConverter.parseHexBinary("c175")));
    }

    @Test
    public void testAcuteAccent() throws UnsupportedEncodingException {
        assertEquals("Á", decodeBytes(DatatypeConverter.parseHexBinary("c241")));
        assertEquals("Ć", decodeBytes(DatatypeConverter.parseHexBinary("c243")));
        assertEquals("É", decodeBytes(DatatypeConverter.parseHexBinary("c245")));
        assertEquals("Í", decodeBytes(DatatypeConverter.parseHexBinary("c249")));
        assertEquals("Ĺ", decodeBytes(DatatypeConverter.parseHexBinary("c24c")));
        assertEquals("Ń", decodeBytes(DatatypeConverter.parseHexBinary("c24e")));
        assertEquals("Ó", decodeBytes(DatatypeConverter.parseHexBinary("c24f")));
        assertEquals("Ŕ", decodeBytes(DatatypeConverter.parseHexBinary("c252")));
        assertEquals("Ś", decodeBytes(DatatypeConverter.parseHexBinary("c253")));
        assertEquals("Ú", decodeBytes(DatatypeConverter.parseHexBinary("c255")));
        assertEquals("Ý", decodeBytes(DatatypeConverter.parseHexBinary("c259")));
        assertEquals("Ź", decodeBytes(DatatypeConverter.parseHexBinary("c25a")));
        assertEquals("á", decodeBytes(DatatypeConverter.parseHexBinary("c261")));
        assertEquals("ć", decodeBytes(DatatypeConverter.parseHexBinary("c263")));
        assertEquals("é", decodeBytes(DatatypeConverter.parseHexBinary("c265")));
        assertEquals("í", decodeBytes(DatatypeConverter.parseHexBinary("c269")));
        assertEquals("ĺ", decodeBytes(DatatypeConverter.parseHexBinary("c26c")));
        assertEquals("ń", decodeBytes(DatatypeConverter.parseHexBinary("c26e")));
        assertEquals("ó", decodeBytes(DatatypeConverter.parseHexBinary("c26f")));
        assertEquals("ŕ", decodeBytes(DatatypeConverter.parseHexBinary("c272")));
        assertEquals("ś", decodeBytes(DatatypeConverter.parseHexBinary("c273")));
        assertEquals("ú", decodeBytes(DatatypeConverter.parseHexBinary("c275")));
        assertEquals("ý", decodeBytes(DatatypeConverter.parseHexBinary("c279")));
        assertEquals("ź", decodeBytes(DatatypeConverter.parseHexBinary("c27a")));
    }

    @Test
    public void testCircumflexAccent() throws UnsupportedEncodingException {
        assertEquals("Â", decodeBytes(DatatypeConverter.parseHexBinary("c341")));
        assertEquals("Ĉ", decodeBytes(DatatypeConverter.parseHexBinary("c343")));
        assertEquals("Ê", decodeBytes(DatatypeConverter.parseHexBinary("c345")));
        assertEquals("Ĝ", decodeBytes(DatatypeConverter.parseHexBinary("c347")));
        assertEquals("Ĥ", decodeBytes(DatatypeConverter.parseHexBinary("c348")));
        assertEquals("Î", decodeBytes(DatatypeConverter.parseHexBinary("c349")));
        assertEquals("Ĵ", decodeBytes(DatatypeConverter.parseHexBinary("c34a")));
        assertEquals("Ô", decodeBytes(DatatypeConverter.parseHexBinary("c34f")));
        assertEquals("Ŝ", decodeBytes(DatatypeConverter.parseHexBinary("c353")));
        assertEquals("Û", decodeBytes(DatatypeConverter.parseHexBinary("c355")));
        assertEquals("Ŵ", decodeBytes(DatatypeConverter.parseHexBinary("c357")));
        assertEquals("Ŷ", decodeBytes(DatatypeConverter.parseHexBinary("c359")));
        assertEquals("â", decodeBytes(DatatypeConverter.parseHexBinary("c361")));
        assertEquals("ĉ", decodeBytes(DatatypeConverter.parseHexBinary("c363")));
        assertEquals("ê", decodeBytes(DatatypeConverter.parseHexBinary("c365")));
        assertEquals("ĝ", decodeBytes(DatatypeConverter.parseHexBinary("c367")));
        assertEquals("ĥ", decodeBytes(DatatypeConverter.parseHexBinary("c368")));
        assertEquals("î", decodeBytes(DatatypeConverter.parseHexBinary("c369")));
        assertEquals("ĵ", decodeBytes(DatatypeConverter.parseHexBinary("c36a")));
        assertEquals("ô", decodeBytes(DatatypeConverter.parseHexBinary("c36f")));
        assertEquals("ŝ", decodeBytes(DatatypeConverter.parseHexBinary("c373")));
        assertEquals("û", decodeBytes(DatatypeConverter.parseHexBinary("c375")));
        assertEquals("ŵ", decodeBytes(DatatypeConverter.parseHexBinary("c377")));
        assertEquals("ŷ", decodeBytes(DatatypeConverter.parseHexBinary("c379")));
    }

    @Test
    public void testTildeAccent() throws UnsupportedEncodingException {
        assertEquals("Ã", decodeBytes(DatatypeConverter.parseHexBinary("c441")));
        assertEquals("Ĩ", decodeBytes(DatatypeConverter.parseHexBinary("c449")));
        assertEquals("Ñ", decodeBytes(DatatypeConverter.parseHexBinary("c44e")));
        assertEquals("Õ", decodeBytes(DatatypeConverter.parseHexBinary("c44f")));
        assertEquals("Ũ", decodeBytes(DatatypeConverter.parseHexBinary("c455")));
        assertEquals("ã", decodeBytes(DatatypeConverter.parseHexBinary("c461")));
        assertEquals("ĩ", decodeBytes(DatatypeConverter.parseHexBinary("c469")));
        assertEquals("ñ", decodeBytes(DatatypeConverter.parseHexBinary("c46e")));
        assertEquals("õ", decodeBytes(DatatypeConverter.parseHexBinary("c46f")));
        assertEquals("ũ", decodeBytes(DatatypeConverter.parseHexBinary("c475")));
    }

    @Test
    public void testMacronAccent() throws UnsupportedEncodingException {
        assertEquals("Ā", decodeBytes(DatatypeConverter.parseHexBinary("c541")));
        assertEquals("Ē", decodeBytes(DatatypeConverter.parseHexBinary("c545")));
        assertEquals("Ī", decodeBytes(DatatypeConverter.parseHexBinary("c549")));
        assertEquals("Ō", decodeBytes(DatatypeConverter.parseHexBinary("c54f")));
        assertEquals("Ū", decodeBytes(DatatypeConverter.parseHexBinary("c555")));
        assertEquals("ā", decodeBytes(DatatypeConverter.parseHexBinary("c561")));
        assertEquals("ē", decodeBytes(DatatypeConverter.parseHexBinary("c565")));
        assertEquals("ī", decodeBytes(DatatypeConverter.parseHexBinary("c569")));
        assertEquals("ō", decodeBytes(DatatypeConverter.parseHexBinary("c56f")));
        assertEquals("ū", decodeBytes(DatatypeConverter.parseHexBinary("c575")));
    }

    @Test
    public void testBreveAccent() throws UnsupportedEncodingException {
        assertEquals("Ă", decodeBytes(DatatypeConverter.parseHexBinary("c641")));
        assertEquals("Ğ", decodeBytes(DatatypeConverter.parseHexBinary("c647")));
        assertEquals("Ŭ", decodeBytes(DatatypeConverter.parseHexBinary("c655")));
        assertEquals("ă", decodeBytes(DatatypeConverter.parseHexBinary("c661")));
        assertEquals("ğ", decodeBytes(DatatypeConverter.parseHexBinary("c667")));
        assertEquals("ŭ", decodeBytes(DatatypeConverter.parseHexBinary("c675")));
    }

    @Test
    public void testDotAccent() throws UnsupportedEncodingException {
        assertEquals("Ċ", decodeBytes(DatatypeConverter.parseHexBinary("c743")));
        assertEquals("Ė", decodeBytes(DatatypeConverter.parseHexBinary("c745")));
        assertEquals("Ġ", decodeBytes(DatatypeConverter.parseHexBinary("c747")));
        assertEquals("İ", decodeBytes(DatatypeConverter.parseHexBinary("c749")));
        assertEquals("Ż", decodeBytes(DatatypeConverter.parseHexBinary("c75a")));
        assertEquals("ċ", decodeBytes(DatatypeConverter.parseHexBinary("c763")));
        assertEquals("ė", decodeBytes(DatatypeConverter.parseHexBinary("c765")));
        assertEquals("ġ", decodeBytes(DatatypeConverter.parseHexBinary("c767")));
        assertEquals("ż", decodeBytes(DatatypeConverter.parseHexBinary("c77a")));
    }

    @Test
    public void testUmlautAccent() throws UnsupportedEncodingException {
        assertEquals("Ä", decodeBytes(DatatypeConverter.parseHexBinary("c841")));
        assertEquals("Ë", decodeBytes(DatatypeConverter.parseHexBinary("c845")));
        assertEquals("Ï", decodeBytes(DatatypeConverter.parseHexBinary("c849")));
        assertEquals("Ö", decodeBytes(DatatypeConverter.parseHexBinary("c84f")));
        assertEquals("Ü", decodeBytes(DatatypeConverter.parseHexBinary("c855")));
        assertEquals("Ÿ", decodeBytes(DatatypeConverter.parseHexBinary("c859")));
        assertEquals("ä", decodeBytes(DatatypeConverter.parseHexBinary("c861")));
        assertEquals("ë", decodeBytes(DatatypeConverter.parseHexBinary("c865")));
        assertEquals("ï", decodeBytes(DatatypeConverter.parseHexBinary("c869")));
        assertEquals("ö", decodeBytes(DatatypeConverter.parseHexBinary("c86f")));
        assertEquals("ü", decodeBytes(DatatypeConverter.parseHexBinary("c875")));
        assertEquals("ÿ", decodeBytes(DatatypeConverter.parseHexBinary("c879")));
    }

    @Test
    public void testRingAccent() throws UnsupportedEncodingException {
        assertEquals("Å", decodeBytes(DatatypeConverter.parseHexBinary("ca41")));
        assertEquals("Ů", decodeBytes(DatatypeConverter.parseHexBinary("ca55")));
        assertEquals("å", decodeBytes(DatatypeConverter.parseHexBinary("ca61")));
        assertEquals("ů", decodeBytes(DatatypeConverter.parseHexBinary("ca75")));
    }

    @Test
    public void testCedillaAccent() throws UnsupportedEncodingException {
        assertEquals("Ç", decodeBytes(DatatypeConverter.parseHexBinary("cb43")));
        assertEquals("Ģ", decodeBytes(DatatypeConverter.parseHexBinary("cb47")));
        assertEquals("Ķ", decodeBytes(DatatypeConverter.parseHexBinary("cb4b")));
        assertEquals("Ļ", decodeBytes(DatatypeConverter.parseHexBinary("cb4c")));
        assertEquals("Ņ", decodeBytes(DatatypeConverter.parseHexBinary("cb4e")));
        assertEquals("Ŗ", decodeBytes(DatatypeConverter.parseHexBinary("cb52")));
        assertEquals("Ş", decodeBytes(DatatypeConverter.parseHexBinary("cb53")));
        assertEquals("Ţ", decodeBytes(DatatypeConverter.parseHexBinary("cb54")));
        assertEquals("ç", decodeBytes(DatatypeConverter.parseHexBinary("cb63")));
        assertEquals("ģ", decodeBytes(DatatypeConverter.parseHexBinary("cb67")));
        assertEquals("ķ", decodeBytes(DatatypeConverter.parseHexBinary("cb6b")));
        assertEquals("ļ", decodeBytes(DatatypeConverter.parseHexBinary("cb6c")));
        assertEquals("ņ", decodeBytes(DatatypeConverter.parseHexBinary("cb6e")));
        assertEquals("ŗ", decodeBytes(DatatypeConverter.parseHexBinary("cb72")));
        assertEquals("ş", decodeBytes(DatatypeConverter.parseHexBinary("cb73")));
        assertEquals("ţ", decodeBytes(DatatypeConverter.parseHexBinary("cb74")));
    }

    @Test
    public void testDoubleAcuteAccent() throws UnsupportedEncodingException {
        assertEquals("Ő", decodeBytes(DatatypeConverter.parseHexBinary("cd4f")));
        assertEquals("Ű", decodeBytes(DatatypeConverter.parseHexBinary("cd55")));
        assertEquals("ő", decodeBytes(DatatypeConverter.parseHexBinary("cd6f")));
        assertEquals("ű", decodeBytes(DatatypeConverter.parseHexBinary("cd75")));
    }

    @Test
    public void testOgonekAccent() throws UnsupportedEncodingException {
        assertEquals("Ą", decodeBytes(DatatypeConverter.parseHexBinary("ce41")));
        assertEquals("Ę", decodeBytes(DatatypeConverter.parseHexBinary("ce45")));
        assertEquals("Į", decodeBytes(DatatypeConverter.parseHexBinary("ce49")));
        assertEquals("Ų", decodeBytes(DatatypeConverter.parseHexBinary("ce55")));
        assertEquals("ą", decodeBytes(DatatypeConverter.parseHexBinary("ce61")));
        assertEquals("ę", decodeBytes(DatatypeConverter.parseHexBinary("ce65")));
        assertEquals("į", decodeBytes(DatatypeConverter.parseHexBinary("ce69")));
        assertEquals("ų", decodeBytes(DatatypeConverter.parseHexBinary("ce75")));
    }

    @Test
    public void testCaronAccent() throws UnsupportedEncodingException {
        assertEquals("Č", decodeBytes(DatatypeConverter.parseHexBinary("cf43")));
        assertEquals("Ď", decodeBytes(DatatypeConverter.parseHexBinary("cf44")));
        assertEquals("Ě", decodeBytes(DatatypeConverter.parseHexBinary("cf45")));
        assertEquals("Ľ", decodeBytes(DatatypeConverter.parseHexBinary("cf4c")));
        assertEquals("Ň", decodeBytes(DatatypeConverter.parseHexBinary("cf4e")));
        assertEquals("Ř", decodeBytes(DatatypeConverter.parseHexBinary("cf52")));
        assertEquals("Š", decodeBytes(DatatypeConverter.parseHexBinary("cf53")));
        assertEquals("Ť", decodeBytes(DatatypeConverter.parseHexBinary("cf54")));
        assertEquals("Ž", decodeBytes(DatatypeConverter.parseHexBinary("cf5a")));
        assertEquals("č", decodeBytes(DatatypeConverter.parseHexBinary("cf63")));
        assertEquals("ď", decodeBytes(DatatypeConverter.parseHexBinary("cf64")));
        assertEquals("ě", decodeBytes(DatatypeConverter.parseHexBinary("cf65")));
        assertEquals("ľ", decodeBytes(DatatypeConverter.parseHexBinary("cf6c")));
        assertEquals("ň", decodeBytes(DatatypeConverter.parseHexBinary("cf6e")));
        assertEquals("ř", decodeBytes(DatatypeConverter.parseHexBinary("cf72")));
        assertEquals("š", decodeBytes(DatatypeConverter.parseHexBinary("cf73")));
        assertEquals("ť", decodeBytes(DatatypeConverter.parseHexBinary("cf74")));
        assertEquals("ž", decodeBytes(DatatypeConverter.parseHexBinary("cf7a")));
    }

    @Test
    public void testSpecialChars() throws UnsupportedEncodingException {
        assertEquals("¤", decodeBytes(DatatypeConverter.parseHexBinary("a8")));
        assertEquals("®", decodeBytes(DatatypeConverter.parseHexBinary("d2")));
        assertEquals("©", decodeBytes(DatatypeConverter.parseHexBinary("d3")));
        assertEquals("¬", decodeBytes(DatatypeConverter.parseHexBinary("d6")));
        assertEquals("¦", decodeBytes(DatatypeConverter.parseHexBinary("d7")));
        assertEquals("ª", decodeBytes(DatatypeConverter.parseHexBinary("e3")));
        assertEquals("\u00ad", decodeBytes(DatatypeConverter.parseHexBinary("ff")));
    }
}