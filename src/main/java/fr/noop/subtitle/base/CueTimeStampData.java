package fr.noop.subtitle.base;

import fr.noop.subtitle.util.SubtitleTimeCode;

/**
 * Created by jdvorak on 25.1.2017.
 */
public class CueTimeStampData implements CueData {
    private SubtitleTimeCode time;

    public CueTimeStampData(SubtitleTimeCode time) {
        this.time = time;
    }

    public SubtitleTimeCode getTime() {
        return time;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public String startElem() {
        return "<";
    }

    @Override
    public String endElem() {
        return ">";
    }

    @Override
    public String content() {
        return time == null ? "invalidTime" : time.toString();
    }
}
