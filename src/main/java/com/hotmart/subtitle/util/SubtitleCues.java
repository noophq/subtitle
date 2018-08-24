package com.hotmart.subtitle.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class SubtitleCues {

    private List<SubtitleCues.Cue> cues = new ArrayList<>();
    
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor(access=AccessLevel.PRIVATE)
    public static class Cue {
    	
		private BigDecimal startTime;
		private BigDecimal endTime;
		
		private String content;
 	}
}
