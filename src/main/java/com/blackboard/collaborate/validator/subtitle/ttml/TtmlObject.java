/*
 * Title: TtmlObject
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.ttml;

import com.blackboard.collaborate.validator.subtitle.base.BaseSubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleCue;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleLine;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleText;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleRegion;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleStyle;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleStyledText;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class TtmlObject  extends BaseSubtitleObject {
    // Style signature => style id
    private final Map<String, String> styleMapping = new HashMap<>();

    // Style id => style object
    private final Map<String, SubtitleStyle> styles = new HashMap<>();

    // Region signature => region Id
    private final Map<String, String> regionMapping = new HashMap<>();

    // Regions id => region object
    private final Map<String, SubtitleRegion> regions = new HashMap<>();

    public TtmlObject() {
        super();
    }

    public TtmlObject(SubtitleObject subtitleObject) {
        super();

        for (Map.Entry<Property, Object> entry : subtitleObject.getProperties()) {
            this.setProperty(entry.getKey(), entry.getValue());
        }

        int cueIndex = 1;
        for (SubtitleCue cue : subtitleObject.getCues()) {
            TtmlCue ttmlCue = new TtmlCue(cue);

            // Register cue region
            SubtitleRegion region = ttmlCue.getRegion();
            String regionSignature = this.buildRegionSignature(region);

            if (!this.regionMapping.containsKey(regionSignature)) {
                // Region is not registered
                // Build a new region id
                String regionId = String.format("region-%d", this.regions.size()+1);
                this.regionMapping.put(regionSignature, regionId);
                this.regions.put(regionId, new SubtitleRegion(region));
            }

            // Register cue styles
            for (SubtitleLine line : ttmlCue.getLines()) {
                for (SubtitleText text : line.getTexts()) {
                    if (!(text instanceof SubtitleStyledText)) {
                        // No style applied on this text
                        continue;
                    }

                    // Register text style
                    SubtitleStyle style = ((SubtitleStyledText) text).getStyle();
                    String styleSignature = this.buildStyleSignature(style);

                    if (this.styleMapping.containsKey(styleSignature)) {
                        // Style already registered
                        continue;
                    }

                    // Style is not registered
                    // Build a new style id
                    String styleId = String.format("style-%d", this.styles.size()+1);
                    this.styleMapping.put(styleSignature, styleId);
                    this.styles.put(styleId, new SubtitleStyle(style));
                }
            }

            // Set cue id
            ttmlCue.setId(String.format("cue-%d", cueIndex++));
            this.addCue(ttmlCue);
        }
    }

    private String buildRegionSignature(SubtitleRegion region) {
        return String.format("%d-%d-%d-%d-%s",
                (int) (region.getX()*100),
                (int) (region.getY()*100),
                (int) (region.getWidth()*100),
                (int) (region.getHeight()*100),
                region.getVerticalAlign());
    }

    private String buildStyleSignature(SubtitleStyle style) {
        return String.format("%s-%s-%s-%s-%s-%s",
                style.getProperty(SubtitleStyle.Property.DIRECTION),
                style.getProperty(SubtitleStyle.Property.TEXT_ALIGN),
                style.getProperty(SubtitleStyle.Property.COLOR),
                style.getProperty(SubtitleStyle.Property.FONT_STYLE),
                style.getProperty(SubtitleStyle.Property.FONT_WEIGHT),
                style.getProperty(SubtitleStyle.Property.TEXT_DECORATION));
    }

    public String getStyleId(SubtitleStyle style) {
        String styleSignature = this.buildStyleSignature(style);
        return styleMapping.get(styleSignature);
    }

    public String getRegionId(SubtitleRegion region) {
        String regionSignature = this.buildRegionSignature(region);
        return regionMapping.get(regionSignature);
    }

    public Map<String, SubtitleStyle> getStyles() {
        return this.styles;
    }


    /**
     * Add a new style
     *
     * @param styleId Unique id of the new added style
     * @param style New added style
     */
    public void setStyle(String styleId, SubtitleStyle style) {
        this.styleMapping.put(this.buildStyleSignature(style), styleId);
        this.styles.put(styleId, style);
    }

    public Map<String, SubtitleRegion> getRegions() {
        return this.regions;
    }

    /**
     * Add a new region
     *
     * @param regionId Unique id of the new added region
     * @param region New added region
     */
    public void setRegion(String regionId, SubtitleRegion region) {
        this.regionMapping.put(this.buildRegionSignature(region), regionId);
        this.regions.put(regionId, region);
    }
}
