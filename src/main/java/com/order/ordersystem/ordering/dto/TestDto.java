package com.order.ordersystem.ordering.dto;

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
public class TestDto {
    private List<OrderCreateDto> detail;
    private long storeId;
    private String payment;
}
