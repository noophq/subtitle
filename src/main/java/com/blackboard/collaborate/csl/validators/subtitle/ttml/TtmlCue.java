/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.ttml;

import com.blackboard.collaborate.csl.validators.subtitle.base.BaseSubtitleCue;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.csl.validators.subtitle.model.SubtitleRegionCue;
import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleRegion;

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
