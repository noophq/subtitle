/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.util;

import fr.noop.subtitle.model.SubtitleText;

/**
 * Created by clebeaupin on 06/10/15.
 */
public class SubtitlePlainText implements SubtitleText {
    private String text; // Text to display

    public SubtitlePlainText(String text) {
        this.text = text;
    }

    @Override
    public boolean isEmpty() {
        return this.text.isEmpty();
    }

    @Override
    public String toString() {
        return this.text;
    }
}
