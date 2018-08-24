package com.hotmart.subtitle.util;

import java.math.BigDecimal;

public class VttUtils {

	private static final String TIME_FORMAT = "%02d:%02d:%02d.%03d";

	public static String formatTime(BigDecimal seconds) {
		int whole = seconds.intValue();
		BigDecimal frac = seconds.subtract(new BigDecimal(whole));
		
		int f = frac.multiply(new BigDecimal(1000)).intValue();
		
		int m = whole / 60;
		int s = whole % 60;
		
		int h = m / 60;
		m = m % 60;
		
		return String.format(TIME_FORMAT, h,m,s,f);
	}
	
	public static BigDecimal parseTime(String time) {
		String[] parts = time.split("\\.");
		
		long value = 0;
		for (String group : parts[0].split(":")) {
			value = value * 60 + Long.parseLong(group);
		}
		
		Double timeDouble = ((double) value * 1000 + Long.parseLong(parts[1]))/1000;

		return new BigDecimal(timeDouble.toString());
	}
}
