/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.util;

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleText;

/**
 * Created by clebeaupin on 06/10/15.
 */
public class SubtitlePlainText implements SubtitleText {
    private final String text; // Text to display

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
