/*
 * Title: CuePlainData
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
public class CuePlainData implements CueData {
    private final String content;

    public CuePlainData(@NonNull String content) {
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
