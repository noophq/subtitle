package fr.noop.subtitle.base;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueElemData implements CueData {
    private String elemName;

    public CueElemData(String name) {
        this.elemName = name;
    }

    @Override
    public String getTag() {
        return elemName;
    }

    public String startElem() {
        return '<' + elemName + '>';
    }

    public String endElem() {
        String endElem = elemName;
        int didx = endElem.indexOf('.');
        int sidx = endElem.indexOf(' ');
        if (didx > 0 || sidx > 0) {
            // cut styles, voices etc from elem name
            int idx = didx; // FIXME
            endElem = elemName.substring(0, idx);
        }

        return "</" + endElem + '>';
    }

    @Override
    public String content() {
        return "";
    }

    public String toString() {
        return "";
    }
}
