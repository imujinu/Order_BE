package com.order.ordersystem.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ordersystem.common.dto.StockRabbitMqDto;
import com.order.ordersystem.product.domain.Product;
import com.order.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;



@Component
@RequiredArgsConstructor
public class StockRabbitMqService {
    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;

    // rabbitmq에 메시지 발행
    public void publish(Long productId, int productCount){
        StockRabbitMqDto dto = StockRabbitMqDto.builder()
                .productId(productId)
                .productCount(productCount)
                .build();

        rabbitTemplate.convertAndSend("stockDecreaseQueue", dto);
    }

    public void publishIncrease(Long productId, int productCount){
        StockRabbitMqDto dto = StockRabbitMqDto.builder()
                .productId(productId)
                .productCount(productCount)
                .build();

        rabbitTemplate.convertAndSend("stockIncreaseQueue", dto);
    }
    // rabbitmq에 발행된 메시지를 수신
    // listener는 단일 스레드로 메시지를 처리하므로, 동시성 이슈 발생 X
//    @RabbitListener(queues = "stockDecreaseQueue")
//    @Transactional
//    public void subscribe(Message message) throws JsonProcessingException {
//        String messageBody = new String (message.getBody());
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        StockRabbitMqDto dto = objectMapper.readValue(messageBody, StockRabbitMqDto.class);
//        Product product = productRepository.findById(dto.getProductId()).orElseThrow(()->new EntityNotFoundException("존재하지 않는 물건입니다."));
//        product.minusStock(dto.getProductCount());
//        System.out.println(messageBody);
//    }
        @RabbitListener(queues = "stockDecreaseQueue")
    public void subscribe(Message message) throws JsonProcessingException {
        String messageBody = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        StockRabbitMqDto stockRabbitMqDto = objectMapper.readValue(messageBody, StockRabbitMqDto.class);
        Product product = productRepository.findById(stockRabbitMqDto.getProductId()).orElseThrow(()->new EntityNotFoundException());
        product.minusStock(stockRabbitMqDto.getProductCount());
            System.out.println(messageBody);
        }

        @RabbitListener(queues = "stockIncreaseQueue")
    public void subscribeIncrease(Message message) throws JsonProcessingException {
        String messageBody = new String(message.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        StockRabbitMqDto stockRabbitMqDto = objectMapper.readValue(messageBody, StockRabbitMqDto.class);
        Product product = productRepository.findById(stockRabbitMqDto.getProductId()).orElseThrow(()->new EntityNotFoundException());
        product.plusStock(stockRabbitMqDto.getProductCount());
            System.out.println(messageBody);
        }
}
