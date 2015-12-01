/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.ttml;

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleRegionCue;
import fr.noop.subtitle.util.SubtitleRegion;

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
