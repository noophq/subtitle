package fr.noop.subtitle.vtt;

import java.io.LineNumberReader;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttRegion {
    private String id;
    private float width = -1;
    private int lines = -1;
    private boolean scrollUp = false;

    private int viewportanchor;
    private int regionanchor;

    public VttRegion() {

    }

    public String getId() {
        return id;
    }

    // FIXME - class should be immutable
    public void setId(String id) {
        this.id = id;
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
//        if (viewportanchor) {
//            bld.append("viewportanchor:xxx \n");
//        }
//        if (regionanchor) {
//            bld.append("regionanchor:xxx \n");
//        }

        return bld.toString();
    }
}
