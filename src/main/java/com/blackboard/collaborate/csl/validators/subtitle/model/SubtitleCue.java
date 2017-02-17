/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package com.blackboard.collaborate.csl.validators.subtitle.model;

import com.blackboard.collaborate.csl.validators.subtitle.util.SubtitleTimeCode;

import java.util.List;
import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public interface SubtitleCue {
    String getId();

    SubtitleTimeCode getStartTime();

    SubtitleTimeCode getEndTime();

    List<SubtitleLine> getLines();

    String getText();

    Iterable<Map.Entry<String, String>> getSettings();
}
