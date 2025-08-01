package com.order.ordersystem.ordering.controller;

import com.order.ordersystem.common.dto.CommonDto;
import com.order.ordersystem.ordering.domain.Ordering;
import com.order.ordersystem.ordering.dto.OrderCreateDto;
import com.order.ordersystem.ordering.dto.OrderListResDto;
import com.order.ordersystem.ordering.dto.TestDto;
import com.order.ordersystem.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
@RequiredArgsConstructor
public class OrderingController {
    private final OrderingService orderingService;

    @PostMapping("/create")
    ResponseEntity<?> create(@RequestBody List<OrderCreateDto> orderCreateDto){
        Long orderId = orderingService.createConcurrent(orderCreateDto);
        return new ResponseEntity<>(CommonDto.builder()
                .result(orderId)
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문완료")
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/list")
    ResponseEntity<?> findAll(){
        List<OrderListResDto> dtos = orderingService.findAll();
        return new ResponseEntity<>(CommonDto.builder()
                .result(dtos)
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문 목록 조회 완료")
                .build(), HttpStatus.CREATED);
    }

    @GetMapping("/myorders")
    ResponseEntity<?> myorders(){
        List<OrderListResDto> dtos = orderingService.findByMember();
        return new ResponseEntity<>(CommonDto.builder()
                .result(dtos)
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문 목록 조회 완료")
                .build(), HttpStatus.CREATED);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.cancel(id);
        return new ResponseEntity<>(CommonDto.builder()
                .result(ordering.getId())
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문 취소 성공")
                .build(), HttpStatus.CREATED);
    }
}
