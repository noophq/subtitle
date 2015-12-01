/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.model;

import java.util.List;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public interface SubtitleObject {
    // Properties
    public enum Property {
        TITLE,
        DESCRIPTION,
        COPYRIGHT,
        FRAME_RATE;
    }

    public boolean hasProperty(Property property);
    public Object getProperty(Property property);
    public Map<Property, Object> getProperties();
    public List<SubtitleCue> getCues();
}
