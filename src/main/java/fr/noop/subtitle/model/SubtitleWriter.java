/*
 *  This file is part of the noOp organization .
 *
 *  (c) Cyrille Lebeaupin <clebeaupin@noop.fr>
 *
 *  For the full copyright and license information, please view the LICENSE
 *  file that was distributed with this source code.
 *
 */

package fr.noop.subtitle.model;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by clebeaupin on 02/10/15.
 */
public interface SubtitleWriter {
    public void write(SubtitleObject subtitleObject, OutputStream os) throws IOException;
}
