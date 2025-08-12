package com.order.ordersystem.product.dto;

import com.order.ordersystem.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResDto {
    private Long id;
    private String productName;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;

    public ProductResDto fromEntity(Product product){
        return ProductResDto.builder()
                .id(product.getId())
                .productName(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .imagePath(product.getImagePath())
                .stockQuantity(product.getStockQuantity())
                .build();
    }
}
