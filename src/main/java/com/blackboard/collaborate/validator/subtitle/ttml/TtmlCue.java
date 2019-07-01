/*
 * Title: TtmlCue
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.ttml;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleLine;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleRegionCue;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleRegion;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 11/10/15.
 */
@Getter
public class TtmlCue extends BaseSubtitleCue implements SubtitleRegionCue {
    @Setter
    private SubtitleRegion region;

    private List<SubtitleLine> lines; // Lines composed of texts

    public TtmlCue(SubtitleCue cue) {
        super(cue);

        this.lines = new ArrayList<>();
        if (cue instanceof SubtitleRegionCue) {
            this.setRegion(new SubtitleRegion(((SubtitleRegionCue) cue).getRegion()));
        }
    }

    @Override
    public String getText() {
        StringBuilder bld = new StringBuilder();

        for (SubtitleLine line : lines) {
            bld.append(line.toString()).append("\n");
        }

        return bld.toString();
    }
}
