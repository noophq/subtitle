package com.blackboard.collaborate.csl.validators.subtitle.base;

/**
 * Created by jdvorak on 19.1.2017.
 */
public class CuePlainData implements CueData {
    private String content;

    public CuePlainData(String content) {
        this.content = content;
    }

    @Override
    public String getTag() {
        return null;
    }

    @Override
    public String startElem() {
        return "";
    }

    @Override
    public String endElem() {
        return "";
    }

    @Override
    public String content() {
        return content;
    }
}
