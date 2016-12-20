package com.mattguo.gemslogbeat;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public interface IDispatcher {
	final static DateTimeFormatter iso = ISODateTimeFormat.dateTime();
    // final static DateTimeFormatter iso = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");

	void open() throws Exception;
	void close();
	void onNewLine(String newLine, String host);
}
