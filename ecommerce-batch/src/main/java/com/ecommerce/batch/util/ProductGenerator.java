package com.ecommerce.batch.util;

import com.ecommerce.batch.domain.product.ProductStatus;
import com.ecommerce.batch.dto.product.upload.ProductUploadCsvRow;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Random;

@Slf4j
public class ProductGenerator {
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        String csvFilePath = "data/random_products.csv";
        int recordCount = 10000000;

        /*
          try () 안에 선언하면 try-with-resources 문법으로 인해 리소스가 자동으로 close 된다. -> finally 불필요
          - 리소스 관리가 필요한 객체(FileWriter, CSVPrinter 등)를 다룰 때는 반드시
            try-with-resources 문법(try () 안에 선언)을 사용하는 것이 좋다.
         */
        try (
                FileWriter out = new FileWriter(csvFilePath);
                CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.builder()
                        .setHeader(ReflectionUtils.getFieldNames(ProductUploadCsvRow.class).toArray(new String[0]))
                        .build())
        ) {
            for (int i = 0; i < recordCount; i++) {
                printer.printRecord(generateRecord());

                if (i % 100000 == 0) {
                    log.info("Generated {} records", i);
                }
            }
        } catch (IOException ioException) {
            log.error("[ProductGenerator] IO Exception >>> {}", ioException.getMessage());
        }
    }

    private static Object[] generateRecord() {
        ProductUploadCsvRow productRow = randomProductRow();
        return new Object[]{
                productRow.getSellerId(),
                productRow.getCategory(),
                productRow.getProductName(),
                productRow.getSalesStartDate(),
                productRow.getSalesEndDate(),
                productRow.getProductStatus(),
                productRow.getBrand(),
                productRow.getManufacturer(),
                productRow.getSalesPrice(),
                productRow.getStockQuantity()
        };
    }

    private static ProductUploadCsvRow randomProductRow() {
        String[] CATEGORIES = {"가전", "가구", "패션", "식품", "화장품", "서적", "스포츠", "완구", "음악", "디지털"};
        String[] PRODUCT_NAMES = {"TV", "소파", "셔츠", "햇반", "스킨케어 세트", "소설", "축구공", "레고", "기타", "스마트폰"};
        String[] BRANDS = {"삼성", "LG", "나이키", "아모레퍼시픽", "현대", "BMW", "롯데", "스타벅스", "도미노", "맥도날드"};
        String[] MANUFACTURERS = {"삼성전자", "LG전자", "나이키코리아", "아모레퍼시픽", "현대자동차", "BMW코리아", "롯데제과", "스타벅스코리아", "도미노피자", "맥도날드코리아"};
        String[] STATUSES = Arrays.stream(ProductStatus.values())
                .map(Enum::name)
                .toArray(String[]::new);

        return ProductUploadCsvRow.of(
                randomSellerId(),
                randomChoice(CATEGORIES),
                randomChoice(PRODUCT_NAMES),
                randomDate(2020, 2023),
                randomDate(2024, 2026),
                randomChoice(STATUSES),
                randomChoice(BRANDS),
                randomChoice(MANUFACTURERS),
                randomSalesPrice(),
                randomStockQuantity()
        );
    }

    private static long randomSellerId() {
        return RANDOM.nextLong(1, 101);
    }

    private static int randomSalesPrice() {
        return RANDOM.nextInt(10000, 500001);
    }

    private static int randomStockQuantity() {
        return RANDOM.nextInt(1, 1001);
    }

    private static String randomDate(int startYear, int endYear) {
        int year = RANDOM.nextInt(startYear, endYear + 1);
        int month = RANDOM.nextInt(1, 13);
        int day = RANDOM.nextInt(1, 29);
        return LocalDate.of(year, month, day).toString();
    }

    private static String randomChoice(String[] array) {
        return array[RANDOM.nextInt(array.length)];
    }
}
