package com.blackboard.collaborate.csl.validators.subtitle.base;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueElemData implements CueData {
    private String tagName;
    public int position;
    public String annotation;
    public String[] classes;


    public CueElemData(String tagName, String[] classes, String annotation) {
        this.tagName = tagName;
        this.classes = classes;
        this.annotation = annotation;
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
        if (annotation.length() > 0) {
            bld.append(' ').append(annotation);
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
}
