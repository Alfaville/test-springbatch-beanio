package com.example.batchfiles.config;

import com.example.batchfiles.handler.LocalDateTypeHandler;
import com.example.batchfiles.listener.JobNotificationListener;
import com.example.batchfiles.model.source.BaseLayout;
import com.example.batchfiles.model.source.complex.ComplexLayout;
import com.example.batchfiles.model.source.complex.ComplexLayoutDetail;
import com.example.batchfiles.model.source.complex.ComplexLayoutHeader;
import com.example.batchfiles.model.source.complex.ComplexLayoutTrailer;
import com.example.batchfiles.model.target.UniqueLayout;
import com.example.batchfiles.processor.ComplexLayoutItemProcessor;
import lombok.RequiredArgsConstructor;
import org.beanio.StreamFactory;
import org.beanio.builder.FixedLengthParserBuilder;
import org.beanio.builder.StreamBuilder;
import org.beanio.spring.BeanIOFlatFileItemReader;
import org.beanio.types.BigDecimalTypeHandler;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.nio.file.Paths;

@Profile({"complex"})
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
class ComplexBatchConfiguration extends BatchConfigurationForInheritance {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    protected BeanIOFlatFileItemReader<BaseLayout> complexReader() {
        StreamBuilder builder = new StreamBuilder("complex_layout")
                .format("fixedlength")
                .parser(new FixedLengthParserBuilder())
                .addRecord(ComplexLayout.class)
                .addRecord(ComplexLayoutHeader.class)
                .addRecord(ComplexLayoutDetail.class)
                .addRecord(ComplexLayoutTrailer.class)
                .addTypeHandler("currencyHandler", new BigDecimalTypeHandler())
                .addTypeHandler("dateHandler", new LocalDateTypeHandler())
                .readOnly()
                .strict()
                .ignoreUnidentifiedRecords();
        StreamFactory factory = StreamFactory.newInstance();
        factory.define(builder);


        final String path = System.getProperty("input.file.name");
        if(!Paths.get(path).toFile().exists()) {
            throw new IllegalArgumentException("File or path not found");
        }
        Resource resource = new FileSystemResource(path);
        BeanIOFlatFileItemReader beanIOFlatFileItemReader = new BeanIOFlatFileItemReader<>();
        beanIOFlatFileItemReader.setResource(resource);
        beanIOFlatFileItemReader.setStreamFactory(factory);
        beanIOFlatFileItemReader.setErrorHandler(new LoggingBeanReaderErrorHandler());
        beanIOFlatFileItemReader.setStreamName("complex_layout");
        return beanIOFlatFileItemReader;
    }

    @Bean
    protected ComplexLayoutItemProcessor complexProcessor() {
        return new ComplexLayoutItemProcessor();
    }

    @Bean
    protected Job complexJob(JobNotificationListener listener, @Qualifier("complexStep") Step step1) {
        return jobBuilderFactory.get("complexJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .preventRestart()
                .build();
    }

    @Bean
    protected Step complexStep() {
        return stepBuilderFactory.get("complexStep")
                .<BaseLayout, UniqueLayout> chunk(10)
                .reader(complexReader())
                .processor(complexProcessor())
                .writer(super.writerJpa())
                .build();
    }

}
