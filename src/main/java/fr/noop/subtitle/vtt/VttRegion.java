package fr.noop.subtitle.vtt;

import java.io.LineNumberReader;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttRegion {
    private String id;
    private float width = -1f;
    private int lines = -1;
    private boolean scrollUp = false;

    private float[] viewportanchor;
    private float[] regionanchor;

    public VttRegion() {
        // N/A
    }

    public String getId() {
        return id;
    }

    // FIXME - class should be immutable
    public void setId(String id) {
        this.id = id;
    }

    public void setWidth(float v) {
        width = v;
    }

    public void setLines(int i) {
        lines = i;
    }

    public void setViewPortAnchor(float[] anchor) {
        viewportanchor = anchor;
    }

    public void setRegionAnchor(float[] anchor) {
        regionanchor = anchor;
    }

    public void setScrollUp(boolean up) {
        scrollUp = up;
    }


    public String toString() {
        StringBuilder bld = new StringBuilder("REGION\n");

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
