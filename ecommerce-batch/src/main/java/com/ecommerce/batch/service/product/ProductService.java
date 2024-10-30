package com.ecommerce.batch.service.product;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final JdbcTemplate jdbcTemplate;

    public Long getCountProducts() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
    }
}
