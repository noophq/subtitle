/*
 * Title: SubtitleCue
 * Copyright (c) 2017. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.model;

import com.blackboard.collaborate.validator.subtitle.util.SubtitleTimeCode;

import java.util.Map;

/**
 * Created by clebeaupin on 11/10/15.
 */
public interface SubtitleCue {
    String getId();

    SubtitleTimeCode getStartTime();

    SubtitleTimeCode getEndTime();

    String getText();

    Iterable<Map.Entry<String, String>> getSettings();
}
