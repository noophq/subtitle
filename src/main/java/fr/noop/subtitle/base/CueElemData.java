package fr.noop.subtitle.base;

import java.util.Arrays;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueElemData implements CueData {
    private String tagName;
    public int position;
    public String voice;
    public String[] classes;


    public CueElemData(String wholeName) {
        int voiceStartIndex = wholeName.indexOf(" ");
        if (voiceStartIndex == -1) {
            voice = "";
        } else {
            voice = wholeName.substring(voiceStartIndex).trim();
            wholeName = wholeName.substring(0, voiceStartIndex);
        }
        String[] nameAndClasses = wholeName.split("\\.");
        tagName = nameAndClasses[0];

        if (nameAndClasses.length > 1) {
            classes = Arrays.copyOfRange(nameAndClasses, 1, nameAndClasses.length);
        } else {
            classes = new String[0];
        }
    }

    @Override
    public String getTag() {
        return tagName;
    }

    public String startElem() {
        StringBuilder bld = new StringBuilder();
        bld.append('<').append(tagName);
        for (String cls : classes) {
            bld.append('.').append(cls);
        }
        if (voice.length() > 0) {
            bld.append(' ').append(voice);
        }
        bld.append('>');
        return bld.toString();
    }

    public String endElem() {
        return "</" + tagName + '>';
    }

    @Override
    public String content() {
        return "";
    }

    public String toString() {
        return "";
    }
}
