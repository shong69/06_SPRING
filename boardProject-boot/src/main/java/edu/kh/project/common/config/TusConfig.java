package edu.kh.project.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import me.desair.tus.server.TusFileUploadService;

@Configuration
@PropertySource("classpath:/config.properties")
public class TusConfig {
	
    @Value("${tus.data.path}")
    private String tusDataPath;

    @Value("${tus.data.expiration}")
    Long tusDataExpiration;

    @Bean
    public TusFileUploadService tus() {
        return new TusFileUploadService()
                .withStoragePath(tusDataPath)
                .withDownloadFeature()
                .withUploadExpirationPeriod(tusDataExpiration)
                .withThreadLocalCache(true)
                .withUploadURI("/tus/upload");
    }
}
