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

import fr.noop.subtitle.model.SubtitleLine;
import fr.noop.subtitle.model.SubtitleText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 06/10/15.
 */
public class SubtitleTextLine implements SubtitleLine {
    List<SubtitleText> texts;

    public SubtitleTextLine() {
        this.texts = new ArrayList<>();
    }

    public SubtitleTextLine(List<SubtitleText> texts) {
        this.texts = texts;
    }

    public List<SubtitleText> getTexts() {
        return this.texts;
    }

    public void addText(SubtitleText text) {
        this.texts.add(text);
    }

    public boolean isEmpty() {
        return this.toString().isEmpty();
    }

    @Override
    public String toString() {
        String[] texts = new String[this.texts.size()];

        for (int i=0; i<texts.length; i++) {
            texts[i] = this.texts.get(i).toString();
        }

        return String.join("\n", texts);
    }
}
