package com.blackboard.collaborate.csl.validators.subtitle.base;

import lombok.NonNull;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CueElemData implements CueData {
    private final String tagName;
    public int position;
    public final String annotation;
    public final String[] classes;


    public CueElemData(@NonNull String tagName, @NonNull String[] classes, @NonNull String annotation) {
        this.tagName = tagName;
        this.classes = classes;
        this.annotation = annotation;
    }

    @Override
    public String getTag() {
        return tagName;
    }

    @Override
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

    @Override
    public String endElem() {
        return "</" + tagName + '>';
    }

    @Override
    public String content() {
        return "";
    }
}
