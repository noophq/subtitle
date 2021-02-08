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

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.text.Normalizer;
import java.util.HashMap;

/**
 * Created by clebeaupin on 28/09/15.
 */
public class Iso6937CharsetEncoder extends CharsetEncoder {

    public Iso6937CharsetEncoder(Iso6937Charset cs) {
        super(cs, 2.0f, 2.0f);
    }

    protected CoderResult encodeLoop(CharBuffer in, ByteBuffer out) {
        try {
            while (in.hasRemaining()) {
                char ch = in.get();

                // Try to decompose char
                String decomposed = Normalizer.normalize(String.valueOf(ch), Normalizer.Form.NFD);

                if (decomposed.length() == 2) {
                    // This is a composed char
                    // Store the accent at the first position
                    // Then the non accented character
                    //System.out.format(">>> %x %x", (byte) Iso6937CharsetMapping.encode(decomposed.charAt(1)), (byte) decomposed.charAt(0));
                    out.put((byte) Iso6937CharsetMapping.encode(decomposed.charAt(1)));
                    out.put((byte) decomposed.charAt(0));
                    continue;
                }

                // This is not composed char
                int chEncoded = Iso6937CharsetMapping.encode(ch);
                out.put((byte) chEncoded);
            }
        }
        catch (BufferOverflowException x) { return CoderResult.OVERFLOW; }
        return CoderResult.UNDERFLOW;
    }
}
