/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.util.SubtitleTextLine;

/**
 * Created by clebeaupin on 14/10/15.
 */
public class VttLine extends SubtitleTextLine {
    private String voice;

    public String getVoice() {
        return this.voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
