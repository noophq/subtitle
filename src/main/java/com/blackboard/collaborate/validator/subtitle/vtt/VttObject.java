/*
 * Title: VttObject
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.vtt;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttObject extends BaseSubtitleObject {
    private final Map<String, VttRegion> regions;
    private final List<VttStyle> styles;

    private final List<Object> objects;

    public VttObject() {
        super();

        regions = new HashMap<>();
        styles = new ArrayList<>();
        objects = new ArrayList<>();
    }

    @Override
    public void addCue(SubtitleCue cue) {
        objects.add(cue);
        super.addCue(cue);
    }

    public Iterable<VttRegion> getRegions() {
        return regions.values();
    }

    public VttRegion getRegion(String id) {
        return regions.get(id);
    }

    public boolean addRegion(VttRegion vttRegion) {
        objects.add(vttRegion);
        return regions.put(vttRegion.getId(), vttRegion) == null;
    }

    public Iterable<VttStyle> getStyles() {
        return styles;
    }

    public void addStyles(VttStyle vttStyle) {
        objects.add(vttStyle);
        styles.add(vttStyle);
    }

    public void addNote(VttNote vttNote) {
        objects.add(vttNote);
    }

    @Override
    public Iterable<?> getObjects() {
        return objects;
    }
}
