package com.ecommerce.batch.jobconfig.product.download;

import com.ecommerce.batch.domain.file.PartitionedFileRepository;
import com.ecommerce.batch.domain.product.Product;
import com.ecommerce.batch.dto.product.download.ProductDownloadCsvRow;
import com.ecommerce.batch.service.product.ProductDownloadPartitioner;
import com.ecommerce.batch.util.FileUtils;
import com.ecommerce.batch.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamWriter;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class ProductDownloadJobConfiguration {

    @Bean
    public Job productDownloadJob(
            JobRepository jobRepository,
            Step productDownloadPartitionStep,
            Step productFileMergeStep,
            JobExecutionListener jobExecutionListener
    ) {
        return new JobBuilder("productDownloadJob", jobRepository)
                .start(productDownloadPartitionStep)
                .next(productFileMergeStep)
                .listener(jobExecutionListener)
                .build();
    }

    @Bean
    public Step productDownloadPartitionStep(
            JobRepository jobRepository,
            Step productPagingStep,
            ProductDownloadPartitioner productDownloadPartitioner,
            PartitionHandler productDownloadPartitionHandler
    ) {
        return new StepBuilder("productDownloadPartitionStep", jobRepository)
                .partitioner(productPagingStep.getName(), productDownloadPartitioner)
                .partitionHandler(productDownloadPartitionHandler)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @JobScope
    public TaskExecutorPartitionHandler productDownloadPartitionHandler(
            @Qualifier("productExecutor") TaskExecutor taskExecutor,
            Step productPagingStep,
            @Value("#{jobParameters['gridSize']}") int gridSize
    ) {
        TaskExecutorPartitionHandler handler = new TaskExecutorPartitionHandler();
        handler.setTaskExecutor(taskExecutor);
        handler.setStep(productPagingStep);
        handler.setGridSize(gridSize);
        return handler;
    }

    @Bean
    public Step productPagingStep(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            ItemReader<Product> productPagingReader,
            ItemProcessor<Product, ProductDownloadCsvRow> productDownloadProcessor,
            ItemWriter<ProductDownloadCsvRow> productCsvWriter,
            StepExecutionListener stepExecutionListener,
            @Qualifier("productExecutor") TaskExecutor taskExecutor
    ) {
        return new StepBuilder("productPagingStep", jobRepository)
                .<Product, ProductDownloadCsvRow>chunk(1000, platformTransactionManager)
                .reader(productPagingReader)
                .processor(productDownloadProcessor)
                .writer(productCsvWriter)
                .allowStartIfComplete(true)
                .listener(stepExecutionListener)
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Product> productPagingReader(
            @Value("#{stepExecutionContext['minId']}") String minId,
            @Value("#{stepExecutionContext['maxId']}") String maxId,
            DataSource dataSource,
            PagingQueryProvider productPagingQueryProvider
    ) {
        return new JdbcPagingItemReaderBuilder<Product>()
                .dataSource(dataSource)
                .name("productPagingReader")
                .queryProvider(productPagingQueryProvider)
                .parameterValues(Map.of("minId", minId, "maxId", maxId))
                .pageSize(1000)
                .beanRowMapper(Product.class)
                .build();
    }

    @Bean
    public SqlPagingQueryProviderFactoryBean productPagingQueryProvider(DataSource dataSource) {
        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();

        String sql = "select product_id, seller_id, category, product_name, sales_start_date, sales_end_date, "
                + "product_status, brand, manufacturer, sales_price, stock_quantity, created_at, updated_at";

        sqlPagingQueryProviderFactoryBean.setSelectClause(sql);
        sqlPagingQueryProviderFactoryBean.setFromClause("from products");
        sqlPagingQueryProviderFactoryBean.setSortKey("product_id");
        sqlPagingQueryProviderFactoryBean.setWhereClause("product_id >= :minId AND product_id <= :maxId");
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);

        return sqlPagingQueryProviderFactoryBean;
    }

    @Bean
    public ItemProcessor<Product, ProductDownloadCsvRow> productDownloadProcessor() {
        return ProductDownloadCsvRow::from;
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamWriter<ProductDownloadCsvRow> productCsvWriter(
            @Value("#{stepExecutionContext['file']}") File file
    ) {
        List<String> columns = ReflectionUtils.getFieldNames(ProductDownloadCsvRow.class);

        FlatFileItemWriter<ProductDownloadCsvRow> productCsvWriter = new FlatFileItemWriterBuilder<ProductDownloadCsvRow>()
                .name("productCsvWriter")
                .resource(new FileSystemResource(file))
                .delimited()
                .names(columns.toArray(String[]::new))
                .build();

        return new SynchronizedItemStreamWriterBuilder<ProductDownloadCsvRow>()
                .delegate(productCsvWriter)
                .build();
    }

    @Bean
    public Step productFileMergeStep(
            JobRepository jobRepository,
            Tasklet productFileMergeTasklet,
            PlatformTransactionManager transactionManager,
            StepExecutionListener stepExecutionListener
    ) {
        return new StepBuilder("productFileMergeStep", jobRepository)
                .tasklet(productFileMergeTasklet, transactionManager)
                .listener(stepExecutionListener)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @JobScope
    public Tasklet productFileMergeTasklet(
            @Value("#{jobParameters['outputFilePath']}") String path,
            PartitionedFileRepository partitionedFileRepository
    ) {
        return (contribution, chunkContext) -> {
            FileUtils.mergeFiles(
                    String.join(",", ReflectionUtils.getFieldNames(ProductDownloadCsvRow.class)),
                    partitionedFileRepository.getFiles(),
                    new File(path)
            );

            return RepeatStatus.FINISHED;
        };
    }
}
