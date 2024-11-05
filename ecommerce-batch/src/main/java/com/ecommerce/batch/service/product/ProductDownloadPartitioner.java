package com.ecommerce.batch.service.product;

import com.ecommerce.batch.domain.file.PartitionedFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductDownloadPartitioner implements Partitioner {
    private final ProductService productService;
    private final PartitionedFileRepository partitionedFileRepository;

    @Override
    @NonNull
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<String> productIds = productService.getProductIds().stream()
                .sorted()
                .toList();
        int minIdx = 0;
        int maxIdx = productIds.size() - 1;

        Map<String, ExecutionContext> result = new HashMap<>();

        int targetSize = (maxIdx - minIdx) / gridSize + 1;

        int number = 0;
        int start = minIdx;
        int end = start + targetSize - 1;

        while (start <= maxIdx) {
            ExecutionContext context = new ExecutionContext();
            String partitionKey = "partition" + number;
            result.put(partitionKey, context);

            if (end >= maxIdx) {
                end = maxIdx;
            }
            context.putString("minId", productIds.get(start));
            context.putString("maxId", productIds.get(end));
            try {
                String FILE_PREFIX = "partition" + number + "_";
                String FILE_SUFFIX = ".csv";
                File partitionFile = partitionedFileRepository.putFile(partitionKey, FILE_PREFIX, FILE_SUFFIX);
                context.put("file", partitionFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            start += targetSize;
            end += targetSize;
            number++;
        }

        return result;
    }
}
