package com.ecommerce.batch.domain.product;

import lombok.Getter;

@Getter
public enum ProductStatus {
    AVAILABLE("판매 중"),
    OUT_OF_STOCK("품절"),
    DISCONTINUED("판매 종료");

    final String desc;

    ProductStatus(String desc) {
        this.desc = desc;
    }
}
