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

/**
 * Created by clebeaupin on 28/09/15.
 */

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CharsetProvider extends java.nio.charset.spi.CharsetProvider {
    private static final String ISO6937_NAME = "ISO-6937";
    private static final String[] ISO6937_ALIASES = new String[] { "ISO-6937-2" };

    private Charset iso6937Charset = new Iso6937Charset(ISO6937_NAME, ISO6937_ALIASES);
    private List charsets;

    public CharsetProvider() {
        this.charsets = Arrays.asList(new Object[] { iso6937Charset });
    }

    /**
     * {@inheritDoc}
     */
    public Charset charsetForName(String charsetName) {
        charsetName = charsetName.toUpperCase(Locale.US);

        for (Iterator iter = charsets.iterator(); iter.hasNext();) {
            Charset charset = (Charset) iter.next();

            // Check the main name
            if (charset.name().equals(charsetName))
                return charset;

            // Check aliases
            if (charset.aliases().contains(charsetName))
                return charset;
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator charsets() {
        return charsets.iterator();
    }
}