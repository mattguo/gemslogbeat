package com.mattguo.gemslogbeat;

import java.util.regex.Pattern;

public class Multiline {
	private Pattern startLinePattern;
	private StringBuilder sbPrev = new StringBuilder();
	
	public Multiline(String startLineRegex) {
		startLinePattern = Pattern.compile(startLineRegex);
	}
	
	public String addLine(String line) {
		boolean startLine = RegexUtil.find(line, startLinePattern);
		String ret = null;
		if (startLine) {
			if (sbPrev.length() > 0) {
				ret = sbPrev.toString();
				sbPrev.setLength(0);
			}
			sbPrev.append(line);
		} else {
			sbPrev.append("\n" + line);
		}
		
		return ret;
	}
	
	public String remainingString() {
		return sbPrev.length() > 0 ? sbPrev.toString() : null;
	}
}
