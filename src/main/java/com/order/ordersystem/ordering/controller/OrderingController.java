package com.order.ordersystem.ordering.controller;

import com.order.ordersystem.common.dto.CommonDto;
import com.order.ordersystem.ordering.domain.Ordering;
import com.order.ordersystem.ordering.dto.OrderCreateDto;
import com.order.ordersystem.ordering.dto.TestDto;
import com.order.ordersystem.ordering.service.OrderingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ordering")
@RequiredArgsConstructor
public class OrderingController {
    private final OrderingService orderingService;

    @PostMapping("/create")
    ResponseEntity<?> create(@RequestBody List<OrderCreateDto> orderCreateDto){
        Ordering order = orderingService.create(orderCreateDto);
        return new ResponseEntity<>(CommonDto.builder()
                .result(order)
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문완료")
                .build(), HttpStatus.CREATED);
    }


    @PostMapping("/test")
    ResponseEntity<?> create(@RequestBody TestDto testDto){
        Ordering order = orderingService.test(testDto);
        return new ResponseEntity<>(CommonDto.builder()
                .result(order)
                .statusCode(HttpStatus.CREATED.value())
                .statusMessage("주문완료")
                .build(), HttpStatus.CREATED);
    }
}
