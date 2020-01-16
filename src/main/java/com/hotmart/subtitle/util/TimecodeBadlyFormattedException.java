package com.hotmart.subtitle.util;

import fr.noop.subtitle.model.SubtitleParsingException;

public class TimecodeBadlyFormattedException extends SubtitleParsingException {

	private static final long serialVersionUID = 1L;

	public TimecodeBadlyFormattedException(String message, int errorLine) {
		super(message, errorLine);
	}
	
}
