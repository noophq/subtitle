/*
 * Title: ValidationReporter
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

/**
 * Report validation issues.
 */
public interface ValidationReporter {
    /**
     * Report a warning.
     * @param msg The message.
     */
    void notifyWarning(String msg);

    /**
     * Report an error.
     * @param msg The message.
     */
    void notifyError(String msg);
}
