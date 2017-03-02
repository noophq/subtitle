/*
 * Title: CueElemData
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
