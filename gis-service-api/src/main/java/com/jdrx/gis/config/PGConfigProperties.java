package com.jdrx.gis.config;

import com.zaxxer.hikari.HikariConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by daququ on 2019/8/22.
 */
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class PGConfigProperties extends HikariConfig {
}
