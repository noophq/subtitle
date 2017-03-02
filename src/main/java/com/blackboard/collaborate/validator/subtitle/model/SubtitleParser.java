/*
 * Title: SubtitleParser
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
