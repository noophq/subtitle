package com.hotmart.subtitle.util;

import fr.noop.subtitle.model.SubtitleParsingException;

public class TimecodeBadlyFormatedException extends SubtitleParsingException {

	private static final long serialVersionUID = 1L;

	public TimecodeBadlyFormatedException(String message, int errorLine) {
		super(message, errorLine);
	}
	
}
