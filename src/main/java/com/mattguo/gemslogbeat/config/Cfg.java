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
        URL url = Resources.getResource("config.yaml");
        String yamlText;
        try {
            yamlText = Resources.toString(url, Charsets.UTF_8);

            Constructor constructor = new Constructor(MyConfig.class);//Car.class is root
            TypeDescription myConfigDescription = new TypeDescription(MyConfig.class);
            myConfigDescription.putListPropertyType("filters", EntryFilter.class);
            myConfigDescription.putMapPropertyType("es", ElasticSearchConfig.class, ElasticSearchConfig.class);
            TypeDescription filterDescription = new TypeDescription(EntryFilter.class);
            filterDescription.putMapPropertyType("latency", LatencyCheck.class, LatencyCheck.class);
            constructor.addTypeDescription(myConfigDescription);
            Yaml yaml = new Yaml(constructor);
            cfg = (MyConfig)yaml.load(yamlText);

        } catch (IOException e) {
            LOGGER.error("Failed to load yaml config", e);
        }
    }
}
