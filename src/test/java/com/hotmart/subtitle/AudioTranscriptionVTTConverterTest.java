package com.hotmart.subtitle;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.hotmart.subtitle.util.AudioTranscription;
import com.hotmart.subtitle.util.AudioTranscriptionChunk;
import com.hotmart.subtitle.util.AudioTranscriptionVTTConverter;

public class AudioTranscriptionVTTConverterTest {
	
	private AudioTranscriptionVTTConverter vttConverterService;
	
	@Before
	public void setup() {
		vttConverterService = new AudioTranscriptionVTTConverter(20);
	}
	
	@Test
	public void format_shouldNotBreakLine() {
		AudioTranscription transcription = new AudioTranscription();
		AudioTranscriptionChunk chunk = new AudioTranscriptionChunk();
		chunk.setStartTime(BigDecimal.ZERO);
		chunk.setEndTime(BigDecimal.TEN);
		transcription.addChunk(chunk);
		
		chunk.setContent(Arrays.asList("acabou","tudo"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nacabou tudo\n\n");
		
		chunk.setContent(Arrays.asList("sem","quebrar","linha"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nsem quebrar linha\n\n");
	}
	
	@Test
	public void format_shouldBreakLine() {
		AudioTranscription transcription = new AudioTranscription();
		AudioTranscriptionChunk chunk = new AudioTranscriptionChunk();
		chunk.setStartTime(BigDecimal.ZERO);
		chunk.setEndTime(BigDecimal.TEN);
		transcription.addChunk(chunk);
		
		chunk.setContent(Arrays.asList("fala","pessoal","para","gente","encurtar","aqui","o","nosso","caminho"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nfala pessoal para gente\nencurtar aqui o nosso caminho\n\n");
		
		chunk.setContent(Arrays.asList("entra","no","nosso","sentimento"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nentra no nosso\nsentimento\n\n");
		
		chunk.setContent(Arrays.asList("vai","quebrar","linha","sim"));
		assertThat(vttConverterService.format(transcription)).isEqualTo("WEBVTT\n\n1\n00:00:00.000 --> 00:00:10.000\nvai quebrar linha\nsim\n\n");
	}
}
