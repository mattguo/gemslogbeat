package com.mattguo.gemslogbeat.config;

public interface EntryFilter {
    String regex();
    String field();
    String addTag();
    String hasTag();
    String hasProp();
    //TODO private String typecast
    //TODO private String condition
}
