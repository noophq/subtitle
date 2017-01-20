package fr.noop.subtitle.base;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueTreeNode {
    private CueData data;
    private CueTreeNode parent;
    private List<CueTreeNode> children;

    public CueTreeNode() {
        this(null);
    }

    public CueTreeNode(CueData data) {
        this.data = data;
        children = new ArrayList<CueTreeNode>();
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

//    public CueElemNode<T> getChild(int idx) {
//        return children.get(idx); // FIXME - check range
//    }

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
        if (isLeaf()) {
            if (data != null) {
                bld.append(data.toString());
            }
        }
        else {
            if (data != null && withStyles) {
                bld.append(data.startElem());
            }
            for (CueTreeNode node : children) {
                node.toStringPri(bld, withStyles);
            }
            if (data != null && withStyles) {
                bld.append(data.endElem());
            }
        }
    }
}
