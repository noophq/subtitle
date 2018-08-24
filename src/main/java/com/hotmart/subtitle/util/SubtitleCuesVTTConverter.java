package com.hotmart.subtitle.util;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hotmart.subtitle.util.SubtitleCues.Cue;

public class SubtitleCuesVTTConverter {
	
	public String format(SubtitleCues subtitleCues) {
		StringBuilder srt = new StringBuilder("WEBVTT\n\n");
		int counter = 1;
		for (Cue cue : subtitleCues.getCues()) {
			srt.append(counter++ + "\n");
			srt.append(VttUtils.formatTime(cue.getStartTime()) + " --> " + VttUtils.formatTime(cue.getEndTime()) + "\n");
			srt.append(cue.getContent());
			srt.append("\n\n");
		}
		
		return srt.toString();
	}

	public SubtitleCues parse(String subtitle) throws SubtitleCuesVTTException {
		try {
			SubtitleCues subtitleCues = new SubtitleCues();
			BufferedReader reader = new BufferedReader(new StringReader(subtitle));
			
			String line = reader.readLine(); //WEBVTT
			
			String timeRegex = "((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})(\\s-->\\s)((\\d{2}:)?\\d{2}:\\d{2}\\.\\d{3})";
			Pattern pattern = Pattern.compile(timeRegex);

			while((line = reader.readLine()) != null) {
				if(line.trim().matches("\\d+")) {
					line = reader.readLine();
				}
					
				Matcher matcher = pattern.matcher(line.trim());
				
				if(matcher.matches()) {
					subtitleCues.getCues().add(parseCue(reader, matcher));
				} 
			}
			
			return subtitleCues;
		} catch (Exception e) {
			throw new SubtitleCuesVTTException(e);
		}
	}

	private SubtitleCues.Cue parseCue(BufferedReader reader, Matcher matcher) {
		SubtitleCues.Cue cue = new SubtitleCues.Cue();

		cue.setStartTime(VttUtils.parseTime(matcher.group(1)));
		cue.setEndTime(VttUtils.parseTime(matcher.group(4)));

		StringBuilder content = new StringBuilder("");
		
		Iterator<String> it = reader.lines().iterator();
		
		int lines = 0;
		while(it.hasNext()) {
			String line = it.next();
			
			if(!line.isEmpty()) {
				if(lines == 0) {
					content.append(line);
				} else {
					content.append("\n" + line);
				}
				
				lines++;
			} else {
				break;
			}
		}
		
		cue.setContent(content.toString());
		
		return cue;
	}
}
