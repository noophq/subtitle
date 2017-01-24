/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.vtt;

import fr.noop.subtitle.base.BaseSubtitleCue;
import fr.noop.subtitle.base.CueTreeNode;
import fr.noop.subtitle.util.SubtitleTextLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by clebeaupin on 11/10/15.
 */
public class VttCue extends BaseSubtitleCue {
    private static final String TAG_BOLD = "b";
    private static final String TAG_ITALIC = "i";
    private static final String TAG_UNDERLINE = "u";
    private static final String TAG_CLASS = "c";
    private static final String TAG_VOICE = "v";
    private static final String TAG_LANG = "lang";

    public static final int ANCHOR_NONE = 0;
    public static final int ANCHOR_START = 1;
    public static final int ANCHOR_MIDDLE = 2;
    public static final int ANCHOR_END = 3;


    private CueTreeNode tree;

    public void setTree(CueTreeNode tree) {
        this.tree = tree;
    }

    public CueTreeNode getTree() {
        return this.tree;
    }

    public void setSetting(String name, Object value) {
        // N/A
    }

}
