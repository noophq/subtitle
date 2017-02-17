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

/**
 * Created by clebeaupin on 12/10/15.
 */
public class SubtitleParsingException extends Exception {
    private int line;
    private int column;

    public SubtitleParsingException(String message, int line, int column) {
        super(message);

        this.line = line;
        this.column = column;
    }

    public SubtitleParsingException(String message) {
        this(message, -1, -1);
    }

    public SubtitleParsingException(ValidationIssue issue) {
        this(issue.getMessage(), issue.getLine(), issue.getColumn());
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }
}
