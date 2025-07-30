package com.order.ordersystem.ordering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {
        private Long orderDetailId;
        private String productName;
        private int productCount;

}
