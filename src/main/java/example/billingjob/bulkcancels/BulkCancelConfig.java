package example.billingjob.bulkcancels;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
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
public class BulkCancelConfig {

    @Bean
    public Job bulkCancelJob(JobRepository jobRepository, Step bulkCancelStep1) {
        return new JobBuilder("BulkCancelJob", jobRepository)
                .start(bulkCancelStep1)
                .build();
    }

    @Bean
    public Step bulkCancelStep1(JobRepository jobRepository, JdbcTransactionManager transactionManager,
                                FlatFileItemReader<BulkCancelIdsDto> bulkCancelFileReader,
                                FlatFileItemWriter<BulkCancelResultDto> bulkCancelFileWriter,
                                BulkCancelService bulkCancelService) {

        return new StepBuilder("bulkCancelStep1", jobRepository)
                .<BulkCancelIdsDto, BulkCancelResultDto>chunk(10, transactionManager)
                .reader(bulkCancelFileReader)
                .processor(bulkCancelService)
                .writer(bulkCancelFileWriter)
                .build();
    }


    @Bean
    @StepScope
    public FlatFileItemReader<BulkCancelIdsDto> bulkCancelFileReader(@Value("#{jobParameters['bulk.cancel.input.file']}") String filePath) {

        return new FlatFileItemReaderBuilder<BulkCancelIdsDto>()
                .name("bulkCancelFileReader")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("merchantId", "transactionId")
                .targetType(BulkCancelIdsDto.class)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<BulkCancelResultDto> bulkCancelFileWriter(@Value("#{jobParameters['bulk.cancel.output.file']}") String filePath) {

        return new FlatFileItemWriterBuilder<BulkCancelResultDto>()
                .name("bulkCancelFileWriter")
                .resource(new FileSystemResource(filePath))
                .delimited()
                .names("merchantId", "transactionId", "status")
                .build();
    }

}

