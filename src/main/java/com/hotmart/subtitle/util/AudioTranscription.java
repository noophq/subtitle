package com.hotmart.subtitle.util;

import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import lombok.Getter;

@Getter
public class AudioTranscription {

	private Set<AudioTranscriptionChunk> chunks;
	
	public AudioTranscription() {
		chunks = new ConcurrentSkipListSet<>(new Comparator<AudioTranscriptionChunk>() {
			@Override
			public int compare(AudioTranscriptionChunk o1, AudioTranscriptionChunk o2) {
				return o1.getStartTime().compareTo(o2.getStartTime());
			}
		});
	}

	public void addChunk(AudioTranscriptionChunk chunk) {
		chunks.add(chunk);
	}
}
