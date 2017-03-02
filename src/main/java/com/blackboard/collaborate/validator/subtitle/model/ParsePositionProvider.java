/*
 * Title: ParsePositionProvider
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
 * Provides line:column information of current parsing position.
 */
public interface ParsePositionProvider {
    /**
     * @return Line number.
     */
    int getLineNumber();

    /**
     * @return Column.
     */
    int getColumn();
}
