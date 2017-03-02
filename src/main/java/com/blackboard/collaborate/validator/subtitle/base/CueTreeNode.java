/*
 * Title: CueTreeNode
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueTreeNode {
    private CueData data;
    private CueTreeNode parent;
    private final List<CueTreeNode> children;

    public CueTreeNode() {
        this(null);
    }

    public CueTreeNode(CueData data) {
        this.data = data;
        children = new ArrayList<>();
    }

    public void add(CueTreeNode child) {
        children.add(child);
        child.setParent(this);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public CueTreeNode getParent() {
        return parent;
    }

    protected void setParent(CueTreeNode parent) {
        this.parent = parent;
    }

    public int getChildCount() {
        return children.size();
    }

    public void setData(CueData data) {
        this.data = data;
    }

    public CueData getData() {
        return this.data;
    }

    public String toString() {
        StringBuilder bld = new StringBuilder();
        toStringPri(bld, false);
        return bld.toString();
    }

    public String toStyledString() {
        StringBuilder bld = new StringBuilder();
        toStringPri(bld, true);
        return bld.toString();
    }

    protected void toStringPri(StringBuilder bld, boolean withStyles) {

        if (data != null && withStyles) {
            bld.append(data.startElem());
        }
        if (data != null) {
            bld.append(data.content());
        }
        for (CueTreeNode node : children) {
            node.toStringPri(bld, withStyles);
        }
        if (data != null && withStyles) {
            bld.append(data.endElem());
        }
    }

    /**
     * Return first node with given tag name in parent chain.
     * @param tagName The tag name
     * @return null or node with given tag name
     */
    public CueTreeNode findParentByTag(String tagName) {
        if (tagName == null) {
            return null;
        }
        CueTreeNode node = this;
        while (node != null && !tagName.equals(node.getTag())) {
            node = node.getParent();
        }
        return node;
    }

    /**
     *
     * @return True if this node has some sub tags
     */
    public boolean hasSubTags() {
        for (CueTreeNode child : children) {
            if (child.getData() instanceof CueElemData) { // TODO: think of better design to avoid 'instanceof'
                return true;
            }
        }
        return false;
    }

    public String getTag() {
        if (data == null) {
            return null;
        }
        return data.getTag();
    }
}
