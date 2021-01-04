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

import java.util.HashMap;

/**
 * Created by clebeaupin on 29/09/15.
 */
public class Iso6937CharsetMapping {
    private static HashMap<Integer, Integer> encodingMapping = new HashMap();
    private static HashMap<Integer, Integer> decodingMapping = new HashMap();

    static {

        add(0xa4, 0xa8);
        add(0x2018, 0xa9);
        add(0x201C, 0xaa);
        add(0x2190, 0xac);
        add(0x2191, 0xad);
        add(0x2192, 0xae);
        add(0x2193, 0xaf);
        add(0xd7, 0xb4);
        add(0xf7, 0xb7);
        add(0x2019, 0xb9);
        add(0x201d, 0xba);
        add(0x300, 0xc1);
        add(0x301, 0xc2);
        add(0x302, 0xc3);
        add(0x303, 0xc4);
        add(0x304, 0xc5);
        add(0x306, 0xc6);
        add(0x307, 0xc7);
        add(0x308, 0xc8);
        add(0x30a, 0xca);
        add(0x327, 0xcb);
        add(0x30b, 0xcd);
        add(0x328, 0xce);
        add(0x30c, 0xcf);
        add(0x2015, 0xd0);
        add(0xb9, 0xd1);
        add(0xae, 0xd2);
        add(0xa9, 0xd3);
        add(0x2122, 0xd4);
        add(0x266a, 0xd5);
        add(0xac, 0xd6);
        add(0xa6, 0xd7);
        add(0x215b, 0xdc);
        add(0x215c, 0xdd);
        add(0x215d, 0xde);
        add(0x215e, 0xdf);
        add(0x2126, 0xe0);
        add(0xc6, 0xe1);
        add(0x110, 0xe2);
        add(0xaa, 0xe3);
        add(0x126, 0xe4);
        add(0x132, 0xe6);
        add(0x13d, 0xe7);
        add(0x141, 0xe8);
        add(0xd8, 0xe9);
        add(0x152, 0xea);
        add(0xba, 0xeb);
        add(0xde, 0xec);
        add(0x166, 0xed);
        add(0x14a, 0xee);
        add(0x149, 0xef);
        add(0x138, 0xf0);
        add(0xe6, 0xf1);
        add(0x111, 0xf2);
        add(0xf0, 0xf3);
        add(0x127, 0xf4);
        add(0x131, 0xf5);
        add(0x133, 0xf6);
        add(0x140, 0xf7);
        add(0x142, 0xf8);
        add(0xf8, 0xf9);
        add(0x153, 0xfa);
        add(0xdf, 0xfb);
        add(0xfe, 0xfc);
        add(0x167, 0xfd);
        add(0x14B, 0xfe);
        add(0xad, 0xff);
    }

    public static void add(int x, int y) {
        encodingMapping.put(x, y);
        decodingMapping.put(y, x);
    }

    public static int encode(int x) {
        if (!encodingMapping.containsKey(x)) {
            // Do not encode this char
            return x;
        }

        return encodingMapping.get(x);
    }

    public static int decode(int y) {
        if (!decodingMapping.containsKey(y)) {
            // Do not decode this char
            return y;
        }

        return decodingMapping.get(y);
    }

}