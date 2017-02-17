/*
 * Title: CueTimeStampData
 * Copyright: Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.base;

import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;

/**
 * Created by jdvorak on 25.1.2017.
 */
public class CueTimeStampData implements CueData {
    private final SubtitleTimeCode time;

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
