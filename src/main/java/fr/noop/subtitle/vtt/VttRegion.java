package fr.noop.subtitle.vtt;

import java.io.LineNumberReader;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttRegion {
    private String id;
    private float width;
    private int lines;
    private boolean scrollUp = false;

    private int viewportanchor;
    private int regionanchor;

    public VttRegion() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
