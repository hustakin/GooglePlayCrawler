package com.google.play.crawler.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fanjie
 * @date 2013-10-21
 */
public class Constants {

	public static String getCurrentTime() {
		Date now = new Date(System.currentTimeMillis());
		return parseTime(now);
	}

	public static String parseTime(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = sdf.format(date);
		return dateStr;
	}

	public static String parseTime2(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		String dateStr = sdf.format(date);
		return dateStr;
	}

	public static String parseTime3(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String dateStr = sdf.format(date);
		return dateStr;
	}

	public static String parseTimeDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(date);
		return dateStr;
	}
}
