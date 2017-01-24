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

import fr.noop.subtitle.base.BaseSubtitleObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttObject extends BaseSubtitleObject {
    private Map<String, VttRegion> regions;
    private List<VttStyle> styles;

    public VttObject() {
        super();

        regions = new HashMap<>();
        styles = new ArrayList<>();
    }

    public VttRegion getRegion(String id) {
        return regions.get(id);
    }

    public boolean addRegion(VttRegion vttRegion) {
        return regions.put(vttRegion.getId(), vttRegion) == null;
    }

    public List<VttStyle> getStyles() {
        return styles;
    }

    public VttCue getLastCue() {
        int idx = getCues().size() - 1;
        if (idx < 0) {
            return null;
        }
        return (VttCue) getCues().get(idx);
    }


}
