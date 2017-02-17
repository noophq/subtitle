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

import java.io.IOException;

/**
 * Created by clebeaupin on 02/10/15.
 */
public interface SubtitleParser {
    SubtitleObject parse() throws IOException;

    SubtitleObject parse(boolean strict) throws IOException;

    SubtitleObject parse(int subtitleOffset, boolean strict) throws IOException;

    SubtitleObject parse(int subtitleOffset, int maxDuration, boolean strict) throws IOException;

    void notifyWarning(String msg);

    void notifyError(String msg);

}
