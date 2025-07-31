package com.order.ordersystem.ordering.dto;

import com.order.ordersystem.ordering.domain.OrderStatus;
import com.order.ordersystem.ordering.domain.Ordering;
import com.order.ordersystem.ordering.domain.OrderingDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus;
    private List<OrderDetail> orderingDetailList;

    public OrderListResDto fromEntity(Ordering ordering, List<OrderDetail> orderDetails){
        return OrderListResDto.builder()
                .id(ordering.getId())
                .memberEmail(ordering.getMember().getEmail())
                .orderStatus(ordering.getOrderStatus())
                .orderingDetailList(orderDetails)
                .build();
    }
}

