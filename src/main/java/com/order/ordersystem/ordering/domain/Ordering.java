package com.order.ordersystem.ordering.domain;

import com.order.ordersystem.common.domain.BaseTimeEntity;
import com.order.ordersystem.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ordering extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;

    @OneToMany(mappedBy = "ordering", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @Builder.Default
    private List<OrderingDetail> orderingDetails = new ArrayList<>();

    public void cancel(){
        this.orderStatus=OrderStatus.CANCELD;
    }
}
