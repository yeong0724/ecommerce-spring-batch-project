package com.ecommerce.service.product;

import com.ecommerce.domain.product.Product;
import com.ecommerce.dto.product.ProductResult;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ProductNotFoundException;
import com.ecommerce.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResult findProduct(String productId) {
        return ProductResult.from(findProductById(productId));
    }

    private Product findProductById(String productId) throws ProductNotFoundException {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Page<ProductResult> getAllProducts(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAll(pageable);

        return productsPage.map(ProductResult::from);
    }

    @Transactional
    public void decreaseStock(String productId, int quantity) throws InsufficientStockException {
        Product product = findProductById(productId);
        product.decreaseStock(quantity);
        productRepository.save(product);
    }

    @Transactional
    public void increaseStock(String productId, int quantity) throws ProductNotFoundException {
        Product product = findProductById(productId);
        product.increaseStock(quantity);
        productRepository.save(product);
    }
}
