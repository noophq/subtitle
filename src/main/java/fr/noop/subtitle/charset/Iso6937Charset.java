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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.List;

/**
 * Created by clebeaupin on 28/09/15.
 */
public class Iso6937Charset extends Charset {
    private static final List CONTAINED = Arrays.asList(new String[] { "US-ASCII" });

    /**
     * @param canonicalName The name as defined in java.nio.charset.Charset
     * @param aliases The aliases as defined in java.nio.charset.Charset
     */
    public Iso6937Charset(String canonicalName, String[] aliases) {
        super(canonicalName, aliases);
    }

    public boolean contains(final Charset cs) {
        return CONTAINED.contains(cs.name());
    }


    public CharsetDecoder newDecoder() {
        return new Iso6937CharsetDecoder(this);
    }


    public CharsetEncoder newEncoder() {
        return new Iso6937CharsetEncoder(this);
    }
}