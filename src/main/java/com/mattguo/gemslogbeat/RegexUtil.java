package com.mattguo.gemslogbeat;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

public class RegexUtil {
    private static Pattern findGroupNamePattern = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

    public static String[] findGroupName(String regex) {
        List<String> names = Lists.newArrayList();
        Matcher matcher = findGroupNamePattern.matcher(regex);
        while(matcher.find()) {
            names.add(matcher.group(1));
        }
        String[] namesArray = new String[names.size()];
        return names.toArray(namesArray);
    }
    
    public static boolean find(String input, Pattern pattern) {
    	return pattern.matcher(input).find();
    }
}
