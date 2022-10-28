/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.model.SubtitleRegionCue;
import fr.noop.subtitle.util.SubtitleRegion;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttCue extends BaseSubtitleCue implements SubtitleRegionCue {
    private SubtitleRegion region;

    public void setRegion(SubtitleRegion region) {
        this.region = region;
    }

    @Override
    public SubtitleRegion getRegion() {
        return this.region;
    }
}
