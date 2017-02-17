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

import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleLine;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 06/10/15.
 */
public class SubtitleTextLine implements SubtitleLine {
    private final List<SubtitleText> texts;

    public SubtitleTextLine() {
        this.texts = new ArrayList<>();
    }

    public SubtitleTextLine(List<SubtitleText> texts) {
        this.texts = texts;
    }

    @Override
    public List<SubtitleText> getTexts() {
        return this.texts;
    }

    public void addText(SubtitleText text) {
        this.texts.add(text);
    }

    @Override
    public boolean isEmpty() {
        return this.toString().isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();

        for (SubtitleText text : texts) {
            bld.append(text.toString());
        }

        return bld.toString();
    }
}
