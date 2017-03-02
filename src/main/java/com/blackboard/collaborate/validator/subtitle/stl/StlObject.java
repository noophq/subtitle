/*
 * Title: StlObject
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.stl;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleRegion;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 22/09/15.
 */
public class StlObject extends BaseSubtitleObject {
    private StlGsi gsi;
    List<StlTti> ttis = new ArrayList<>();

    public StlObject(StlGsi gsi) {
        this.setProperty(SubtitleObject.Property.TITLE, gsi.getOpt());
        this.setProperty(SubtitleObject.Property.FRAME_RATE, gsi.getDfc().getFrameRate());
        this.gsi = gsi;
    }

    public StlGsi getGsi() {
        return this.gsi;
    }

    public void setGsi(StlGsi gsi) {
        this.gsi = gsi;
    }

    public List<StlTti> getTtis() {
        return this.ttis;
    }

    public void setTtis(List<StlTti> ttis) {
        this.ttis = ttis;
    }

    public void addTti(StlTti tti) {
        this.ttis.add(tti);

        // Create cue from tti
        StlCue cue = new StlCue(tti);

        // Do not create cue if tti text field is empty
        if (cue.isEmpty()) {
            return;
        }

        // Adjust start and end time depending on GSI Tcf
        cue.subtractTime(this.gsi.getTcf());

        // Create cue region
        // Use tti vertical position
        // and gsi maximum number of rows information
        // to build the region
        float rowHeight = 100.0f/((float) gsi.getMnr());

        // Adjust vp to align cues having 1, 2 or 3 rows
        int newVp = tti.getVp()+(2*(cue.getLines().size()-1));

        // Consider that all regions are rows taking 100% of the width
        SubtitleRegion region = new SubtitleRegion(0, 100.0f-((gsi.getMnr()-newVp)*rowHeight));
        cue.setRegion(region);

        // Add cue to stl object
        this.addCue(cue);
    }
}
