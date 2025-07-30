package com.order.ordersystem.ordering.repository;

import com.order.ordersystem.ordering.domain.OrderingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderingDetail, Long> {
}
