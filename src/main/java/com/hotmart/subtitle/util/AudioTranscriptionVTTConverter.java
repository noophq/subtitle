package com.hotmart.subtitle.util;

import java.util.StringJoiner;

public class AudioTranscriptionVTTConverter {

	private Integer maxCharCount;
	
	public AudioTranscriptionVTTConverter(Integer maxCharCount) {
		this.maxCharCount = maxCharCount;
	}

	public String format(AudioTranscription transcription) {
		StringBuilder srt = new StringBuilder("WEBVTT\n\n");
		int counter = 1;
		for (AudioTranscriptionChunk cue : transcription.getChunks()) {
			srt.append(counter++ + "\n");
			srt.append(VttUtils.formatTime(cue.getStartTime()) + " --> " + VttUtils.formatTime(cue.getEndTime()) + "\n");
			
			StringJoiner topLine = new StringJoiner(" ").setEmptyValue("");
			StringJoiner bottomLine = new StringJoiner(" ").setEmptyValue("");
			
			int wordCount = cue.getContent().size();
			
			StringJoiner current = topLine;
			for (int i = 0; i < wordCount; i++) {
				String word = cue.getContent().get(i);
				
				//== guarantees it is the same instance
				if(current == topLine && !(topLine.toString().length() + word.length() < maxCharCount || i < wordCount/2)) {
					current = bottomLine;
				}
				current.add(word);
			}
			
			srt.append(topLine + "\n");
			if(bottomLine.length() != 0) {
				srt.append(bottomLine + "\n");
			}
			srt.append("\n");
		}
		
		return srt.toString();
	}

}
