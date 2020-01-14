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
 * Created by clebeaupin on 12/10/15.
 */
@SuppressWarnings("serial")
public class SubtitleParsingException extends Exception {
	private int lineError;
    public SubtitleParsingException(String message) {
        super(message);
    }
    
    public SubtitleParsingException(String message, int lineError) {
        super(message);
        this.lineError = lineError;
    }

	public int getLineError() {
		return lineError;
	}
}
