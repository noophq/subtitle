/*
 * Title: ValidationIssue
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

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by jdvorak on 20.1.2017.
 *
 * This will be sent to the client (?)
 */
@Getter
@AllArgsConstructor
public class ValidationIssue {

    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }

    private Severity severity;
    private String message;
    private int line;
    private int column;

    public ValidationIssue(Severity severity, String message) {
        this(severity, message, 0, 0);
    }

    public String toString() {
        return severity + ": " + message + " (" + line + ":" + column + ")";
    }
}
