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
import com.blackboard.collaborate.validator.subtitle.model.SubtitleRegionCue;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleRegion;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class TtmlCue extends BaseSubtitleCue implements SubtitleRegionCue {
    private SubtitleRegion region;

    public TtmlCue(SubtitleCue cue) {
        super(cue);

        if (cue instanceof SubtitleRegionCue) {
            this.setRegion(new SubtitleRegion(((SubtitleRegionCue) cue).getRegion()));
        }
    }

    public void setRegion(SubtitleRegion region) {
        this.region = region;
    }

    @Override
    public SubtitleRegion getRegion() {
        return this.region;
    }
}
