package com.order.ordersystem.product.domain;

import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.product.dto.ProductUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private int price;
    private int stockQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String imagePath;

    public void updateUrl(String url){
        this.imagePath=url;
    }

    public void updateProduct(ProductUpdateDto productUpdateDto) {
        this.name = productUpdateDto.getName();
        this.price = productUpdateDto.getPrice();
        this.category = productUpdateDto.getCategory();
        this.stockQuantity = productUpdateDto.getStockQuantity();
    }

    public void minusStock(int productCount) {
        this.stockQuantity-=productCount;
    }

    public void plusStock(int productCount){
        this.stockQuantity+=productCount;
    }
}
