package com.hotmart.subtitle.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class AudioTranscription {

	private List<AudioTranscriptionChunk> chunks;
	
	public AudioTranscription() {
		chunks = new ArrayList<>();
	}

	public void addChunk(AudioTranscriptionChunk chunk) {
		chunks.add(chunk);
	}
}
