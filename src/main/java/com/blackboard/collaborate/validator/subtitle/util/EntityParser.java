/*
 * Title: EntityParser
 * Copyright (c) 2018. Blackboard Inc. and its subsidiary companies.
 *
 * This program is based on noophq/subtitle.
 * Copyright (c) 2015-2016 Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 * This program is free software licensed under the GNU Lesser General Public License v3.
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */

package com.blackboard.collaborate.validator.subtitle.util;

import com.blackboard.collaborate.validator.subtitle.model.ValidationReporter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;


public class EntityParser {
    private static final Pattern entityPattern = Pattern.compile("^[A-Za-z][A-Za-z0-9]+$");

    private static final List<Integer> FORBIDDEN_CODES = Arrays.asList(
            0x00, 0x80, 0x82, 0x83, 0x84, 0x85, 0x86, 0x87, 0x88, 0x89,
            0x8A, 0x8B, 0x8C, 0x8E, 0x91, 0x92, 0x93, 0x94, 0x95, 0x96,
            0x97, 0x98, 0x99, 0x9A, 0x9B, 0x9C, 0x9E, 0x9F
    );

    public static void parse(ValidationReporter reporter, String entity) {
        if (entity.startsWith("#") && entity.length() > 1) {
            int code;
            if (entity.charAt(1) == 'x' || entity.charAt(1) == 'X') {
                try {
                    code = Integer.parseInt(entity.substring(2), 16);
                } catch (NumberFormatException e) {
                    reporter.notifyError("Invalid entity code: '&" + entity + ";'");
                    return;
                }
            } else {
                try {
                    code = Integer.parseInt(entity);
                } catch (NumberFormatException e) {
                    reporter.notifyError("Invalid entity code: '&" + entity + ";'");
                    return;
                }
            }
            if (Collections.binarySearch(FORBIDDEN_CODES, code) < 0 || (code >= 0xD800 && code <= 0xDFFF) || code > 0x10FFFF) {
                reporter.notifyError("Forbidden entity code: '&" + entity + ";'");
            }
        } else if (!entityPattern.matcher(entity).matches()) {
            reporter.notifyError("Invalid entity name: '&" + entity + ";'");
        }
    }
}
