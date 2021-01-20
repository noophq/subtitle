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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetEncoder;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Created by clebeaupin on 29/09/15.
 */
public class Iso6937CharsetEncoderTest {
    public CharsetEncoder tested;

    @Before
    public void setUp() throws Exception {
        Charset charset = new Iso6937Charset("ISO-6937", new String[] {});
        tested = charset.newEncoder();
    }

    public byte[] encodeToBytes(String in) throws UnsupportedEncodingException {
        CharBuffer cb = CharBuffer.wrap(in);

        try {
            ByteBuffer bb = tested.encode(cb);
            byte[] bytes = new byte[bb.remaining()];
            bb.get(bytes, 0, bb.remaining());
            return bytes;
        } catch (CharacterCodingException e) {
            throw new UnsupportedEncodingException(e.getMessage());
        }
    }

    public String encode(String in) throws UnsupportedEncodingException {
        return new String(encodeToBytes(in), "US-ASCII");
    }

    @Test
    public void testAscii() throws UnsupportedEncodingException {
        assertEquals("abcdefghijklmnopqrstuvwxyz", encode("abcdefghijklmnopqrstuvwxyz"));
        assertEquals("ABCDEFGHIJKLMNOPQRSTUVWXYZ", encode("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    }

    @Test
    public void testGraveAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c141"), encodeToBytes("À")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c145"), encodeToBytes("È")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c149"), encodeToBytes("Ì")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c14f"), encodeToBytes("Ò")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c155"), encodeToBytes("Ù")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c161"), encodeToBytes("à")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c165"), encodeToBytes("è")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c169"), encodeToBytes("ì")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c16f"), encodeToBytes("ò")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c175"), encodeToBytes("ù")));
    }

    @Test
    public void testAcuteAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c241"), encodeToBytes("Á")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c243"), encodeToBytes("Ć")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c245"), encodeToBytes("É")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c249"), encodeToBytes("Í")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c24c"), encodeToBytes("Ĺ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c24e"), encodeToBytes("Ń")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c24f"), encodeToBytes("Ó")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c252"), encodeToBytes("Ŕ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c253"), encodeToBytes("Ś")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c255"), encodeToBytes("Ú")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c259"), encodeToBytes("Ý")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c25a"), encodeToBytes("Ź")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c261"), encodeToBytes("á")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c263"), encodeToBytes("ć")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c265"), encodeToBytes("é")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c269"), encodeToBytes("í")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c26c"), encodeToBytes("ĺ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c26e"), encodeToBytes("ń")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c26f"), encodeToBytes("ó")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c272"), encodeToBytes("ŕ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c273"), encodeToBytes("ś")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c275"), encodeToBytes("ú")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c279"), encodeToBytes("ý")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c27a"), encodeToBytes("ź")));
    }

    @Test
    public void testCircumflexAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c341"), encodeToBytes("Â")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c343"), encodeToBytes("Ĉ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c345"), encodeToBytes("Ê")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c347"), encodeToBytes("Ĝ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c348"), encodeToBytes("Ĥ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c349"), encodeToBytes("Î")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c34a"), encodeToBytes("Ĵ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c34f"), encodeToBytes("Ô")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c353"), encodeToBytes("Ŝ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c355"), encodeToBytes("Û")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c357"), encodeToBytes("Ŵ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c359"), encodeToBytes("Ŷ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c361"), encodeToBytes("â")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c363"), encodeToBytes("ĉ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c365"), encodeToBytes("ê")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c367"), encodeToBytes("ĝ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c368"), encodeToBytes("ĥ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c369"), encodeToBytes("î")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c36a"), encodeToBytes("ĵ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c36f"), encodeToBytes("ô")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c373"), encodeToBytes("ŝ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c375"), encodeToBytes("û")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c377"), encodeToBytes("ŵ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c379"), encodeToBytes("ŷ")));
    }

    @Test
    public void testTildeAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c441"), encodeToBytes("Ã")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c449"), encodeToBytes("Ĩ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c44e"), encodeToBytes("Ñ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c44f"), encodeToBytes("Õ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c455"), encodeToBytes("Ũ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c461"), encodeToBytes("ã")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c469"), encodeToBytes("ĩ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c46e"), encodeToBytes("ñ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c46f"), encodeToBytes("õ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c475"), encodeToBytes("ũ")));
    }

    @Test
    public void testMacronAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c541"), encodeToBytes("Ā")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c545"), encodeToBytes("Ē")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c549"), encodeToBytes("Ī")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c54f"), encodeToBytes("Ō")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c555"), encodeToBytes("Ū")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c561"), encodeToBytes("ā")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c565"), encodeToBytes("ē")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c569"), encodeToBytes("ī")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c56f"), encodeToBytes("ō")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c575"), encodeToBytes("ū")));
    }

    @Test
    public void testBreveAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c641"), encodeToBytes("Ă")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c647"), encodeToBytes("Ğ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c655"), encodeToBytes("Ŭ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c661"), encodeToBytes("ă")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c667"), encodeToBytes("ğ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c675"), encodeToBytes("ŭ")));
    }

    @Test
    public void testDotAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c743"), encodeToBytes("Ċ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c745"), encodeToBytes("Ė")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c747"), encodeToBytes("Ġ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c749"), encodeToBytes("İ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c75a"), encodeToBytes("Ż")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c763"), encodeToBytes("ċ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c765"), encodeToBytes("ė")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c767"), encodeToBytes("ġ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c77a"), encodeToBytes("ż")));
    }

    @Test
    public void testUmlautAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c841"), encodeToBytes("Ä")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c845"), encodeToBytes("Ë")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c849"), encodeToBytes("Ï")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c84f"), encodeToBytes("Ö")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c855"), encodeToBytes("Ü")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c859"), encodeToBytes("Ÿ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c861"), encodeToBytes("ä")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c865"), encodeToBytes("ë")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c869"), encodeToBytes("ï")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c86f"), encodeToBytes("ö")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c875"), encodeToBytes("ü")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("c879"), encodeToBytes("ÿ")));
    }

    @Test
    public void testRingAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ca41"), encodeToBytes("Å")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ca55"), encodeToBytes("Ů")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ca61"), encodeToBytes("å")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ca75"), encodeToBytes("ů")));
    }

    @Test
    public void testCedillaAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb43"), encodeToBytes("Ç")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb47"), encodeToBytes("Ģ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb4b"), encodeToBytes("Ķ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb4c"), encodeToBytes("Ļ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb4e"), encodeToBytes("Ņ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb52"), encodeToBytes("Ŗ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb53"), encodeToBytes("Ş")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb54"), encodeToBytes("Ţ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb63"), encodeToBytes("ç")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb67"), encodeToBytes("ģ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb6b"), encodeToBytes("ķ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb6c"), encodeToBytes("ļ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb6e"), encodeToBytes("ņ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb72"), encodeToBytes("ŗ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb73"), encodeToBytes("ş")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cb74"), encodeToBytes("ţ")));
    }

    @Test
    public void testDoubleAcuteAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cd4f"), encodeToBytes("Ő")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cd55"), encodeToBytes("Ű")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cd6f"), encodeToBytes("ő")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cd75"), encodeToBytes("ű")));
    }

    @Test
    public void testOgonekAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce41"), encodeToBytes("Ą")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce45"), encodeToBytes("Ę")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce49"), encodeToBytes("Į")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce55"), encodeToBytes("Ų")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce61"), encodeToBytes("ą")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce65"), encodeToBytes("ę")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce69"), encodeToBytes("į")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ce75"), encodeToBytes("ų")));
    }

    @Test
    public void testCaronAccent() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf43"), encodeToBytes("Č")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf44"), encodeToBytes("Ď")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf45"), encodeToBytes("Ě")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf4c"), encodeToBytes("Ľ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf4e"), encodeToBytes("Ň")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf52"), encodeToBytes("Ř")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf53"), encodeToBytes("Š")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf54"), encodeToBytes("Ť")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf5a"), encodeToBytes("Ž")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf63"), encodeToBytes("č")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf64"), encodeToBytes("ď")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf65"), encodeToBytes("ě")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf6c"), encodeToBytes("ľ")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf6e"), encodeToBytes("ň")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf72"), encodeToBytes("ř")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf73"), encodeToBytes("š")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf74"), encodeToBytes("ť")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("cf7a"), encodeToBytes("ž")));
    }

    @Test
    public void testSpecialChars() throws UnsupportedEncodingException {
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("a8"), encodeToBytes("¤")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("d2"), encodeToBytes("®")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("d3"), encodeToBytes("©")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("d6"), encodeToBytes("¬")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("d7"), encodeToBytes("¦")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("e3"), encodeToBytes("ª")));
        assertTrue(Arrays.equals(DatatypeConverter.parseHexBinary("ff"), encodeToBytes("\u00ad")));
    }
}