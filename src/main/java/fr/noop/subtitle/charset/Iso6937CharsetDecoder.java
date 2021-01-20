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
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.text.Normalizer;

/**
 * Created by clebeaupin on 28/09/15.
 */
public class Iso6937CharsetDecoder extends CharsetDecoder {
    public Iso6937CharsetDecoder(Iso6937Charset cs) {
        super(cs, 1.0f, 1.0f);
    }

    protected CoderResult decodeLoop(ByteBuffer in, CharBuffer out) {
        try {
            while (in.hasRemaining()) {
                // Convert to int to get unsigned byte
                int by = (in.get() & 0xff);
                char ch = (char) by;

                // Check if it's a composed char
                if ((by >= 0xc1 && by <= 0xc8) ||
                        (by >= 0xca && by <= 0xcb) ||
                        (by >= 0xcd && by <= 0xcf)
                ) {
                    // This is an accent
                    String decomposed = String.format("%s%s", (char) in.get(), (char) Iso6937CharsetMapping.decode(by));
                    String composed = Normalizer.normalize(decomposed, Normalizer.Form.NFC);
                    out.put(composed);
                    continue;
                }

                // This is not a composed char
                out.put((char) Iso6937CharsetMapping.decode(by));
            }
        } catch (BufferOverflowException x) { return CoderResult.OVERFLOW; }

        return CoderResult.UNDERFLOW;
    }

}