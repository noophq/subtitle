package com.blackboard.collaborate.csl.validators.subtitle.base;


/**
 * Created by jdvorak on 19.1.2017.
 */
public interface CueData {
    String getTag();

    String startElem();

    String endElem();

    /**
     * Plain text content.
     * @return
     */
    String content();
}
