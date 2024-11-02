package com.ecommerce.batch.service.product;

import com.ecommerce.batch.domain.product.Product;
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

    public void save(Product product) {
        String sql = "insert into products(product_id, seller_id, category, product_name, "
                + "sales_start_date, sales_end_date, product_status, brand, manufacturer, "
                + "sales_price, stock_quantity, created_at, updated_at) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sql,
                product.getProductId(),
                product.getSellerId(),
                product.getCategory(),
                product.getProductName(),
                product.getSalesStartDate(),
                product.getSalesEndDate(),
                product.getProductStatus(),
                product.getBrand(),
                product.getManufacturer(),
                product.getSalesPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

}
