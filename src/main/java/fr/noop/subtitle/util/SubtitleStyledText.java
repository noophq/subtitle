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

import fr.noop.subtitle.model.SubtitleStyled;
import fr.noop.subtitle.model.SubtitleText;


/**
 * Created by clebeaupin on 12/10/15.
 */
public class SubtitleStyledText extends SubtitlePlainText implements SubtitleStyled {
    private SubtitleStyle style;

    public SubtitleStyledText(String text, SubtitleStyle style) {
        super(text);
        this.style = style;
    }

    public SubtitleStyle getStyle() {
        return this.style;
    }
}
