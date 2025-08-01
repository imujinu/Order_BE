package com.order.ordersystem.ordering.repository;

import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.ordering.domain.Ordering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderingRepository extends JpaRepository<Ordering, Long> {
    List<Ordering> findAllByMember(Member member);

    Optional<Ordering> findByIdAndMember(Long id, Member member);
}
