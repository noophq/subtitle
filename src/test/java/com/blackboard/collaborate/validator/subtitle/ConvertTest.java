/*
 * Title: ConvertTest
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by jdvorak on 14/02/2017.
 */
public class ConvertTest {

    private void convert(String inputFile, String outputExt, boolean strict) {
        File tmpFile;

        try {
            tmpFile = File.createTempFile("subtitleTest_", outputExt);
            tmpFile.deleteOnExit();
        } catch (IOException e) {
            return; // cannot perform the test
        }

        String outputFile = tmpFile.getAbsolutePath();
        System.out.println(inputFile);
        System.out.println(outputFile);
        String[] args = new String[] { "-i", inputFile, "-o", outputFile, !strict ? "-dsm" : "" };

        Convert.main(args);
    }

    @Test
    public void testSrt2VttConvert() {
        convert("src/test/resources/srt/standard.srt", ".vtt", true);
    }

    @Test
    public void testVtt2SrtConvert() {
        convert("src/test/resources/vtt/comments.vtt", ".srt", true);
    }

    @Test
    public void testVtt2VttConvert1() {
        convert("src/test/resources/vtt/comments.vtt", ".vtt", true);
    }

    @Test
    public void testVtt2VttConvert2() {
        convert("src/test/resources/vtt/Collab-recording_CR.vtt", ".vtt", true);
    }

    @Test
    public void testVtt2VttConvert3() {
        convert("src/test/resources/vtt/css_styles.vtt", ".vtt", false);
    }
}
