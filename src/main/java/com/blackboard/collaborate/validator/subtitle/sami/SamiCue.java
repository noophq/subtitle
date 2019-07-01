/*
 * Title: SamiCue
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.sami;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleLine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class SamiCue extends BaseSubtitleCue {
    @Getter
    private List<SubtitleLine> lines; // Lines composed of texts

    public SamiCue() {
        this.lines = new ArrayList<>();
    }

    @Override
    public String getText() {
        StringBuilder bld = new StringBuilder();

        for (SubtitleLine line : lines) {
            bld.append(line.toString()).append("\n");
        }

        return bld.toString();
    }

    public void addLine(SubtitleLine line) {
        this.lines.add(line);
    }
}
