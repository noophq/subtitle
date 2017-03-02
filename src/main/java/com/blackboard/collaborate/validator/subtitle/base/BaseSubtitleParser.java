/*
 * Title: BaseSubtitleParser
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

import com.blackboard.collaborate.validator.subtitle.model.SubtitleObject;
import com.blackboard.collaborate.validator.subtitle.model.SubtitleParser;
import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;
import com.blackboard.collaborate.validator.subtitle.util.SubtitleReader;

import java.io.IOException;

/**
 * Created by jdvorak on 20.1.2017.
 */
//@Slf4j
public abstract class BaseSubtitleParser implements SubtitleParser {
    protected final ValidationReporter reporter;
    protected final SubtitleReader reader; // the reader

    public BaseSubtitleParser(ValidationReporter reporter, SubtitleReader reader) {
        this.reporter = reporter;
        this.reader = reader;
    }

    @Override
    public SubtitleObject parse() throws IOException {
        return parse(0,-1, true);
    }

    @Override
    public SubtitleObject parse(boolean strict) throws IOException {
        return parse(0,-1, strict);
    }

    @Override
    public SubtitleObject parse(int subtitleOffset, boolean strict) throws IOException {
        return parse(subtitleOffset,-1, strict);
    }


    @Override
    public void notifyWarning(String msg) {
        reporter.notifyWarning(msg);
    }

    @Override
    public void notifyError(String msg) {
        reporter.notifyError(msg);
    }
}
