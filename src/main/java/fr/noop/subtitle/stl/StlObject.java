/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.stl;

import fr.noop.subtitle.base.BaseSubtitleObject;
import fr.noop.subtitle.stl.StlGsi.Dsc;
import fr.noop.subtitle.util.SubtitleRegion;
import fr.noop.subtitle.util.SubtitleRegion.VerticalAlign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 22/09/15.
 */
public class StlObject extends BaseSubtitleObject {
    private StlGsi gsi;
    List<StlTti> ttis = new ArrayList<>();

    public StlObject(StlGsi gsi) {
        this.setProperty(Property.TITLE, gsi.getOpt());
        this.setProperty(Property.FRAME_RATE, gsi.getDfc().getFrameRate());
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
        StlCue cue = new StlCue(tti, gsi);

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

        if (this.gsi.getDsc() == Dsc.DSC_TELETEXT_LEVEL_1 || this.gsi.getDsc() == Dsc.DSC_TELETEXT_LEVEL_2){
            if (tti.getVp() == 1){
                region.setVerticalAlign(VerticalAlign.TOP);
            }
        }

        cue.setRegion(region);

        // Add cue to stl object
        this.addCue(cue);
    }
}
