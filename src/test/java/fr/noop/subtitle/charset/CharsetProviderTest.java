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

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Created by clebeaupin on 28/09/15.
 */
public class CharsetProviderTest {
    private CharsetProvider tested = new CharsetProvider();

    @Test
    public void testIso6937() throws Exception {
        Charset charset = tested.charsetForName("ISO-6937");
        assertNotNull("charset not found", charset);
        assertEquals(Iso6937Charset.class, charset.getClass());
        assertEquals(charset, tested.charsetForName("ISO-6937"));
        assertEquals(charset, tested.charsetForName("ISO-6937-2"));
    }

    @Test
    public void testNotHere() throws Exception {
        assertNull(tested.charsetForName("X-DOES-NOT-EXIST"));
    }

    @Test
    public void testIterator() throws Exception {
        Iterator iterator = tested.charsets();
        HashSet found = new HashSet();
        while (iterator.hasNext())
            found.add(iterator.next());
        assertEquals(1, found.size());
        Charset charset1 = tested.charsetForName("ISO-6937");
        assertTrue(found.contains(charset1));
    }
}