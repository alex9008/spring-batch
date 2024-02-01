package example.billingjob.errorcodemapping;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class ErrorMappingConfig {

    @Bean
    public Job errorMappingJob(JobRepository jobRepository, Step errorMappingStep1) {
        return new JobBuilder("ErrorMappingJob", jobRepository)
                .start(errorMappingStep1)
                .build();
    }

    @Bean
    public Job genericErrorCodesJob(JobRepository jobRepository, Step genericErrorCodesStep1) {
        return new JobBuilder("GenericErrorCodesJob", jobRepository)
                .start(genericErrorCodesStep1)
                .build();
    }

    @Bean
    public Step errorMappingStep1(JobRepository jobRepository, JdbcTransactionManager transactionManager,
                                  ItemReader<ErrorMappingDataDto> errorMappingDataFileReader,
                                  ItemProcessor<ErrorMappingDataDto, ErrorMappingSql> errorMappingDataProcessor,
                                  ItemWriter<ErrorMappingSql> errorMappingDataFileWriter) {
        return new StepBuilder("errorMappingStep", jobRepository)
                .<ErrorMappingDataDto, ErrorMappingSql>chunk(100, transactionManager)
                .reader(errorMappingDataFileReader)
                .processor(errorMappingDataProcessor)
                .writer(errorMappingDataFileWriter)
                .build();
    }

    @Bean
    public Step genericErrorCodesStep1(JobRepository jobRepository, JdbcTransactionManager transactionManager,
                                       ItemReader<GenericErrorCodesDto> genericErrorCodesDtoFlatFileItemReader,
                                       ItemProcessor<GenericErrorCodesDto, ErrorMappingSql> genericErrorCodesProcessor,
                                       ItemWriter<ErrorMappingSql> errorMappingDataFileWriter) {
        return new StepBuilder("genericErrorCodesStep", jobRepository)
                .<GenericErrorCodesDto, ErrorMappingSql>chunk(100, transactionManager)
                .reader(genericErrorCodesDtoFlatFileItemReader)
                .processor(genericErrorCodesProcessor)
                .writer(errorMappingDataFileWriter)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<ErrorMappingDataDto> errorMappingDataFileReader(@Value("#{jobParameters['input.file']}") String inputFile) {

        return new FlatFileItemReaderBuilder<ErrorMappingDataDto>()
                .name("errorMappingDataFileReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("processor", "gmErrorCode", "providerErrorCode")
                .targetType(ErrorMappingDataDto.class)
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemWriter<ErrorMappingSql> errorMappingDataFileWriter(@Value("#{jobParameters['output.file']}") String outputFile) {
        return new FlatFileItemWriterBuilder<ErrorMappingSql>()
                .name("errorMappingDataFileWriter")
                .resource(new FileSystemResource(outputFile))
                .delimited()
                .names("sql")
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<GenericErrorCodesDto> genericErrorCodesDtoFlatFileItemReader(@Value("#{jobParameters['input.file']}") String inputFile) {

        return new FlatFileItemReaderBuilder<GenericErrorCodesDto>()
                .name("genericErrorCodesDtoFlatFileItemReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("gmErrorCode")
                .targetType(GenericErrorCodesDto.class)
                .build();
    }
}
