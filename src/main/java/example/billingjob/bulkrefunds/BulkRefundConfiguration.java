package example.billingjob.bulkrefunds;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BulkRefundConfiguration {


    @Bean
    public Job refundJob(JobRepository jobRepository, Step refundJobStep3) {
        return new JobBuilder("RefundJob", jobRepository)
                .start(refundJobStep3)
                .build();
    }

    @Bean
    public Job replaceJob(JobRepository jobRepository, Step refundJobStep4) {
        return new JobBuilder("ReplaceJob", jobRepository)
                .start(refundJobStep4)
                .build();
    }


    @Bean
    public Step refundJobStep1(JobRepository jobRepository, JdbcTransactionManager transactionManager) {
        return new StepBuilder("filePreparation1", jobRepository)
                .tasklet(new RefundFilePreparationTasklet(), transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<RefundData> refundDataFileReader(@Value("#{jobParameters['input.file']}") String inputFile) {

        return new FlatFileItemReaderBuilder<RefundData>()
                .name("refundDataFileReader")
                .resource(new FileSystemResource(inputFile))
                .delimited()
                .names("id", "amount")
                .targetType(RefundData.class)
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<RefundData> refundDataTableWriter(DataSource dataSource) {
        String sql = "insert into REFUND_DATA values (:id, :amount)";
        return new JdbcBatchItemWriterBuilder<RefundData>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    public Step refundJobStep2(JobRepository jobRepository, JdbcTransactionManager transactionManager, ItemReader<RefundData> refundDataFileReader,
                      ItemWriter<RefundData> refundDataTableWriter) {
        return new StepBuilder("fileIngestion1", jobRepository)
                .<RefundData, RefundData>chunk(100, transactionManager)
                .reader(refundDataFileReader)
                .writer(refundDataTableWriter)
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<RefundData> refundDataTableReader(DataSource dataSource) {
        String sql = "select * from REFUND_DATA";
        return new JdbcCursorItemReaderBuilder<RefundData>().name("refundDataTableReader")
                .dataSource(dataSource)
                .sql(sql)
                .rowMapper(new DataClassRowMapper<>(RefundData.class))
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<FinalResultDto> refundDataFileWriter(@Value("#{jobParameters['output.file']}") String outputFile) {
        return new FlatFileItemWriterBuilder<FinalResultDto>()
                .resource(new FileSystemResource(outputFile))
                .name("refundDataFileWriter")
                .delimited()
                .names("refundData.id", "refundData.amount", "statusCode", "statusDescription", "externalReference")
                .build();
    }

    @Bean
    public Step refundJobStep3(JobRepository jobRepository, JdbcTransactionManager transactionManager, ItemReader<RefundData> refundDataFileReader, ItemProcessor<RefundData,
            FinalResultDto> refundDataProcessor, ItemWriter<FinalResultDto> refundDataFileWriter, RefundDataSkipListener refundSkipListener) {
        return new StepBuilder("reportGeneration1", jobRepository).
                <RefundData, FinalResultDto>chunk(100, transactionManager)
                .reader(refundDataFileReader)
                .processor(refundDataProcessor)
                .writer(refundDataFileWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(1000)
                .listener(refundSkipListener)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<RefundList> replaceElementsFileWriter(@Value("#{jobParameters['output.file']}") String outputFile) {
        return new FlatFileItemWriterBuilder<RefundList>()
                .resource(new FileSystemResource(outputFile))
                .name("replaceElementsFileWriter")
                .delimited()
                .names("refundData.id", "refundData.amount")
                .build();
    }

    @Bean
    public Step refundJobStep4(JobRepository jobRepository, JdbcTransactionManager transactionManager, ItemReader<RefundData> refundDataFileReader, ItemProcessor<RefundData, RefundList> replaceElementsProcessor, ItemWriter<RefundList> replaceElementsFileWriter) {
        return new StepBuilder("replaceElement", jobRepository)
                .<RefundData, RefundList>chunk(100, transactionManager)
                .reader(refundDataFileReader)
                .processor(replaceElementsProcessor)
                .writer(replaceElementsFileWriter)
                .build();
    }

    @Bean
    @StepScope
    public RefundDataSkipListener refundSkipListener(@Value("#{jobParameters['skip.file']}") String skippedFile) {
        return new RefundDataSkipListener(skippedFile);
    }
}
