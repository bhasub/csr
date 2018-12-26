package com.emc.ideaforce;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Configurations pertaining to ideaforce application
 */
@Configuration
public class IdeaforceConfiguration {

    private static final int MAX_FILE_UPLOAD_SIZE_IN_BYTES = 5 * 1024 * 1024;

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSizePerFile(MAX_FILE_UPLOAD_SIZE_IN_BYTES);
        return multipartResolver;
    }

}
