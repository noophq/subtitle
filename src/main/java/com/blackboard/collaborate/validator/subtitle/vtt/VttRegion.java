/*
 * Title: VttRegion
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

import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttRegion {
    private static final Pattern REGION_PATTERN = Pattern.compile("(\\S+):(\\S+)");

    private String id;
    private float width = -1f;
    private int lines = -1;
    private boolean scrollUp = false;

    private float[] viewportanchor;
    private float[] regionanchor;
    private final ValidationReporter reporter;

    public VttRegion(ValidationReporter reporter) {
        this.reporter = reporter;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    private void setWidth(float v) {
        width = v;
    }

    private void setLines(int i) {
        lines = i;
    }

    private void setViewPortAnchor(float[] anchor) {
        viewportanchor = anchor;
    }

    private void setRegionAnchor(float[] anchor) {
        regionanchor = anchor;
    }

    private void setScrollUp(boolean up) {
        scrollUp = up;
    }

    public void parse(StringBuilder regionText) {
        // delete the REGION identifier
        int end = regionText.indexOf(VttParser.REGION_START);
        regionText.delete(0, end + VttParser.REGION_START.length());

        Matcher m = REGION_PATTERN.matcher(regionText);
        while (m.find()) {
            String name = m.group(1);
            String value = m.group(2);

            switch (name) {
                case "id":
                    if (value.contains(VttParser.ARROW)) {
                        reporter.notifyWarning("Invalid region " + name + ": " + value);
                    }
                    setId(value);
                    break;
                case "width":
                    try {
                        setWidth(VttParser.parsePercentage(value));
                    } catch (NumberFormatException e) {
                        reporter.notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "lines":
                    try {
                        setLines(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        reporter.notifyWarning("Invalid region " + name + ": " + value);
                    }
                    break;
                case "viewportanchor":
                    float[] vret = parseFloatCouple(value);
                    if (vret != null) {
                        setViewPortAnchor(vret);
                    }
                    break;
                case "regionanchor":
                    float[] aret = parseFloatCouple(value);
                    if (aret != null) {
                        setRegionAnchor(aret);
                    }
                    break;
                case "scroll":
                    if (!value.equals("up")) {
                        reporter.notifyWarning("Invalid region " + name + " value: " + value);
                    } else {
                        setScrollUp(true);
                    }
                    break;
                default:
                    reporter.notifyWarning("Unknown region setting: " + name + ":" + value);
                    break;
            }
        }
    }

    private float[] parseFloatCouple(String value) {
        String parts[] = value.split(",");
        if (parts.length == 2) {
            float[] ret = new float[2];
            try {
                ret[0] = VttParser.parsePercentage(parts[0]);
                ret[1] = VttParser.parsePercentage(parts[1]);
                return ret;
            } catch (NumberFormatException e) {
                reporter.notifyWarning("Invalid region setting: " + value);
            }
        } else {
            reporter.notifyWarning("Invalid region setting: " + value);
        }
        return null;
    }


    public String toString() {
        StringBuilder bld = new StringBuilder(VttParser.REGION_START).append("\n");

        bld.append("id:").append(id).append("\n");
        if (width > 0) {
            bld.append("width:").append(width * 100).append("%\n");
        }
        if (lines > 0) {
            bld.append("lines:").append(lines).append("\n");
        }
        if (scrollUp) {
            bld.append("scroll:up\n");
        }
        if (viewportanchor != null) {
            bld.append("viewportanchor:");
            bld.append(viewportanchor[0] * 100);
            bld.append("%,");
            bld.append(viewportanchor[1] * 100);
            bld.append("%\n");
        }
        if (regionanchor != null) {
            bld.append("regionanchor:");
            bld.append(regionanchor[0] * 100);
            bld.append("%,");
            bld.append(regionanchor[1] * 100);
            bld.append("%\n");
        }

        return bld.toString();
    }
}
