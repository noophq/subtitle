/*
 * Title: SubtitlePlainText
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import com.blackboard.collaborate.validator.subtitle.model.SubtitleText;

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
