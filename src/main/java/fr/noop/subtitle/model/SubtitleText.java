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

/**
 * Created by clebeaupin on 11/10/15.
 */
public interface SubtitleText {
    /**
     *
     * @return the text
     */
    public String toString();

    /**
     *
     * @return true if there is no text
     */
    public boolean isEmpty();
}
