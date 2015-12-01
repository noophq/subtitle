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
        this.cues = new ArrayList<SubtitleCue>();
        this.properties = new HashMap<>();
    }

    public void addCue(SubtitleCue cue) {
        this.cues.add(cue);
    }

    public List<SubtitleCue> getCues() {
        return this.cues;
    }

    public void setCues(List<SubtitleCue> cues) {
        this.cues = cues;
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
    public Map<Property, Object> getProperties() {
        return this.properties;
    }
    public void setProperty(Property property, Object value) {
        this.properties.put(property, value);
    }


    public void setProperties(Map<Property, Object> properties) {
        this.properties = properties;
    }
}
