package com.order.ordersystem.ordering.repository;

import com.order.ordersystem.ordering.domain.OrderingDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderingDetail, Long> {
    List<OrderingDetail> findAllByOrderingId(Long id);
}
