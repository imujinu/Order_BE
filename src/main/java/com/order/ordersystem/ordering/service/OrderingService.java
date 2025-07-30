package com.order.ordersystem.ordering.service;

import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.member.repository.MemberRepository;
import com.order.ordersystem.ordering.domain.OrderStatus;
import com.order.ordersystem.ordering.domain.Ordering;
import com.order.ordersystem.ordering.domain.OrderingDetail;
import com.order.ordersystem.ordering.dto.OrderCreateDto;
import com.order.ordersystem.ordering.dto.TestDto;
import com.order.ordersystem.ordering.repository.OrderDetailRepository;
import com.order.ordersystem.ordering.repository.OrderingRepository;
import com.order.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public Ordering create(List<OrderCreateDto> orderCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));
        Ordering ordering = orderingRepository.save(Ordering.builder()
                        .member(member)
                        .build());
        List<OrderingDetail> orderings = orderCreateDto.stream().map(a-> OrderingDetail.builder()
                .product(productRepository.findById(a.getProductId()).orElseThrow(()-> new EntityNotFoundException("상품이 존재하지 않습니다.")))
                .quantity(a.getProductCount())
                .ordering(ordering).build()).collect(Collectors.toList());

        for(OrderingDetail o : orderings){
            ordering.getOrderingDetails().add(o);
        }
        return ordering;
    }

    public Ordering test(TestDto testDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));
        Ordering ordering = orderingRepository.save(Ordering.builder()
                .member(member)
                .build());
        List<OrderingDetail> orderingDetails = testDto.getDetail().stream().map((a)->OrderingDetail.builder()
                .product(productRepository.findById(a.getProductId()).orElseThrow(()-> new EntityNotFoundException("상품이 존재하지 않습니다.")))
                .quantity(a.getProductCount())
                .storeId(testDto.getStoreId())
                .payment(testDto.getPayment())
                .ordering(ordering)
                .build()).collect(Collectors.toList());

        for(OrderingDetail o : orderingDetails){
            ordering.getOrderingDetails().add(o);
        }

        return ordering;
    }
}
