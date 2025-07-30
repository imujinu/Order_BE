package com.order.ordersystem.ordering.service;

import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.member.repository.MemberRepository;
import com.order.ordersystem.ordering.domain.OrderStatus;
import com.order.ordersystem.ordering.domain.Ordering;
import com.order.ordersystem.ordering.domain.OrderingDetail;
import com.order.ordersystem.ordering.dto.OrderCreateDto;
import com.order.ordersystem.ordering.dto.OrderDetail;
import com.order.ordersystem.ordering.dto.OrderListResDto;
import com.order.ordersystem.ordering.dto.TestDto;
import com.order.ordersystem.ordering.repository.OrderDetailRepository;
import com.order.ordersystem.ordering.repository.OrderingRepository;
import com.order.ordersystem.product.domain.Product;
import com.order.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;


    public Object create(List<OrderCreateDto> orderCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));
        Ordering ordering = orderingRepository.save(Ordering.builder()
                        .member(member)
                        .build());
        StringBuffer sb = new StringBuffer();

        List<OrderingDetail> orderingDetails = new ArrayList<>();

        orderCreateDto.forEach(a -> {
            Product product = productRepository.findById(a.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));

            if (a.getProductCount() > product.getStockQuantity()) {
               sb.append(product.getName() + "의 재고가 부족합니다.\n");
            }else{
                product.minusStock(a.getProductCount());
                orderingDetails.add(OrderingDetail.builder()
                        .product(product)
                        .quantity(a.getProductCount())
                        .ordering(ordering)
                        .build());
            }
        });

        if(!sb.isEmpty()){
            throw new IllegalArgumentException(sb.toString());
        }else{
            ordering.getOrderingDetails().addAll(orderingDetails);
            return ordering;
        }
    }


    public List<OrderListResDto> findAll() {
        List<Ordering> orderings = orderingRepository.findAll();
        List<OrderListResDto> orderListResDtos = new ArrayList<>();
        orderings.forEach(a -> {
            String email = a.getMember().getEmail();
            OrderStatus orderStatus = a.getOrderStatus();
            List<OrderingDetail> orderingDetails = orderDetailRepository.findAllByOrderingId(a.getId());
            orderListResDtos.add(OrderListResDto.builder()
                    .id(a.getId())
                    .memberEmail(email)
                    .orderStatus(orderStatus)
                    .orderingDetailList(orderingDetails.stream().map(b->{
                        return OrderDetail.builder()
                                .orderDetailId(a.getId())
                                .productName(productRepository.findById(b.getProduct().getId()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 상품입니다.")).getName())
                                .productCount(b.getQuantity())
                                .build();


                    }).collect(Collectors.toList()))
                    .build());
        });
        return orderListResDtos;
    }

}
