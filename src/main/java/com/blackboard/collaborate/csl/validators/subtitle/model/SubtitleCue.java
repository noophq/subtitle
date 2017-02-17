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

/**
 * Created by clebeaupin on 11/10/15.
 */
public interface SubtitleCue {
    public String getId();
    public SubtitleTimeCode getStartTime();
    public SubtitleTimeCode getEndTime();
    public List<SubtitleLine> getLines();
    public String getText();
}
