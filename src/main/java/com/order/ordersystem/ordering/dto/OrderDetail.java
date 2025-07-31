package com.order.ordersystem.ordering.dto;

import com.order.ordersystem.ordering.domain.OrderingDetail;
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

        public OrderDetail fromEntity(OrderingDetail orderingDetail){
                return OrderDetail.builder()
                        .orderDetailId(orderingDetail.getId())
                        .productName(orderingDetail.getProduct().getName())
                        .productCount(orderingDetail.getQuantity())
                        .build();
        }
}
