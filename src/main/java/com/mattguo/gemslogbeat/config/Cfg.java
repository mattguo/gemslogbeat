package com.mattguo.gemslogbeat.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.classpath.ClasspathConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class Cfg {
    static Logger LOGGER = LoggerFactory.getLogger(Cfg.class);

    private static MyConfig cfg;

    public static MyConfig one() {
        return cfg;
    }

    private static ConfigurationProvider provider;

    static {
        ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
            @Override
            public Iterable<Path> getConfigFiles() {
                return Lists.newArrayList(Paths.get("config.yaml"));
            }
        };
        ClasspathConfigurationSource source = new ClasspathConfigurationSource(configFilesProvider);
        provider = new ConfigurationProviderBuilder().withConfigurationSource(source).build();

        final EntryFilter[] filters = provider.bind("cfg.filters", EntryFilter[].class);
        cfg = new MyConfig() {
            @Override
            public EntryFilter[] filters() {
                return filters;
            }
        };
    }
}
