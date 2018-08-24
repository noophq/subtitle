package com.hotmart.subtitle.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioTranscriptionChunk {

	private BigDecimal startTime;
	private BigDecimal endTime;
	
	private List<String> content = new ArrayList<>();

}
