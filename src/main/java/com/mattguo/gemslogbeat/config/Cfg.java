package com.mattguo.gemslogbeat.config;

import java.io.IOException;
import java.net.URL;

import org.cfg4j.provider.ConfigurationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Cfg {
    static Logger LOGGER = LoggerFactory.getLogger(Cfg.class);

    private static MyConfig cfg;

    public static MyConfig one() {
        return cfg;
    }

    private static ConfigurationProvider provider;

    static {
//        ConfigFilesProvider configFilesProvider = new ConfigFilesProvider() {
//            @Override
//            public Iterable<Path> getConfigFiles() {
//                return Lists.newArrayList(Paths.get("config.yaml"));
//            }
//        };
//        ClasspathConfigurationSource source = new ClasspathConfigurationSource(configFilesProvider);
//        provider = new ConfigurationProviderBuilder().withConfigurationSource(source).build();

        URL url = Resources.getResource("config.yaml");
        String yamlText;
        try {
            yamlText = Resources.toString(url, Charsets.UTF_8);

            Constructor constructor = new Constructor(MyConfig.class);//Car.class is root
            TypeDescription carDescription = new TypeDescription(MyConfig.class);
            carDescription.putListPropertyType("filters", EntryFilter.class);
            constructor.addTypeDescription(carDescription);
            Yaml yaml = new Yaml(constructor);
            cfg = (MyConfig)yaml.load(yamlText);

        } catch (IOException e) {
            LOGGER.error("Failed to load yaml config", e);
        }


//        final EntryFilter[] filters = provider.bind("cfg.filters", EntryFilter[].class);
//        cfg = new MyConfig() {
//            @Override
//            public EntryFilter[] filters() {
//                return filters;
//            }
//        };
    }
}
