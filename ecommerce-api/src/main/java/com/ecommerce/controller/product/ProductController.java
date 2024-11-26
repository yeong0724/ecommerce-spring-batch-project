package com.ecommerce.controller.product;

import com.ecommerce.dto.product.ProductResponse;
import com.ecommerce.dto.product.ProductResult;
import com.ecommerce.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/products")
@RestController
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/{productId}")
    public ProductResponse getProduct(@PathVariable String productId) {
        return ProductResponse.from(productService.findProduct(productId));
    }

    @GetMapping("")
    public Page<ProductResponse> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "productId,asc") String[] sort
    ) {
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        Page<ProductResult> productResultByPageable = productService.getAllProducts(pageable);

        return productResultByPageable.map(ProductResponse::from);
    }
}
