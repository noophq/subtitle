/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.base;

import fr.noop.subtitle.model.SubtitleCue;
import fr.noop.subtitle.model.SubtitleObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clebeaupin on 22/09/15.
 */
public abstract class BaseSubtitleObject implements SubtitleObject {
    private List<SubtitleCue> cues;
    private Map<Property, Object> properties;

    public BaseSubtitleObject() {
        this.cues = new ArrayList<>();
        this.properties = new HashMap<>();
    }

    public void addCue(SubtitleCue cue) {
        this.cues.add(cue);
    }

    public Iterable<SubtitleCue> getCues() {
        return this.cues;
    }

    public SubtitleCue getLastCue() {
        int idx = cues.size() - 1;
        if (idx < 0) {
            return null;
        }
        return cues.get(idx);
    }

    @Override
    public int getCuesCount() {
        return cues.size();
    }

    public SubtitleCue getCue(int idx) {
        return cues.get(idx);
    }


    @Override
    public Object getProperty(Property property) {
        return this.properties.get(property);
    }

    @Override
    public boolean hasProperty(Property property) {
        return (this.getProperty(property) != null);
    }

    @Override
    public Iterable<Map.Entry<Property, Object>> getProperties() {
        return this.properties.entrySet();
    }

    public void setProperty(Property property, Object value) {
        this.properties.put(property, value);
    }

}
