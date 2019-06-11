package com.example.batchfiles.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.GenericApplicationContextFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing(modular = true)
class BatchModularConfiguration {

    @Bean
    protected ApplicationContextFactory visaJobL() {
        return new GenericApplicationContextFactory(SimpleBatchConfiguration.class);
    }

    @Bean
    protected ApplicationContextFactory masterJobL() {
        return new GenericApplicationContextFactory(ComplexBatchConfiguration.class);
    }

}