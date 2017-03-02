/*
 * Title: SrtParserTest
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.srt;

import com.blackboard.collaborate.validator.subtitle.util.TestUtils;
import org.junit.Test;

import java.io.IOException;

public class SrtParserTest {

    @Test
    public void testFiles() throws IOException {
        TestUtils.testFolder("src/test/resources/srt");

//        SrtObject srtObject = (SrtObject) srtParser.parse(is);
//        Assert.assertEquals(2, srtObject.getCuesCount());
    }
}
