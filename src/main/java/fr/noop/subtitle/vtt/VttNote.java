package fr.noop.subtitle.vtt;

/**
 * Created by jdvorak on 23.1.2017.
 */
public class VttNote {
    private String note;

    public void setNote(String note) {
        this.note = note;
    }

    public String toString() {
        return "NOTE " + note;
    }
}
