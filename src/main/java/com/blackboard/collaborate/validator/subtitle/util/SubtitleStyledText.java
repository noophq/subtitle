/*
 * Title: SubtitleStyledText
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

import com.blackboard.collaborate.validator.subtitle.model.SubtitleStyled;


/**
 * Created by clebeaupin on 12/10/15.
 */
public class SubtitleStyledText extends SubtitlePlainText implements SubtitleStyled {
    private final SubtitleStyle style;

    public SubtitleStyledText(String text, SubtitleStyle style) {
        super(text);
        this.style = style;
    }

    @Override
    public SubtitleStyle getStyle() {
        return this.style;
    }
}
