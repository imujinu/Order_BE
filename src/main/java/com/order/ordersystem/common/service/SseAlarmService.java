package com.order.ordersystem.common.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.order.ordersystem.common.dto.SseMessageDto;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class SseAlarmService {
    //SseEmitter는 연결된 사용자 정보(ip, MacAddress 정보 등...) 를 의미
    // map에는 연결된 클라이언트 정보 들이 담겨져 있다.
    private Map<String, SseEmitter> emitterMap = new HashMap();
    // 특정 사용자에게 message 발송
    //productId는 내가 보내줄 알림 메시지
    public void publishMessage(String receiver, String sender, Long orderingId){
        SseMessageDto dto = SseMessageDto.builder()
                .sender(sender)
                .receiver(receiver)
                .orderingId(orderingId)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();
        String data = null;
        try {
            data = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    // emitter 객체를 통해 메시지 전송
    SseEmitter sseEmitter = emitterMap.get(receiver);
        try {
            sseEmitter.send(SseEmitter.event().name("ordered").data(data));
        } catch (IOException e) {
            e.printStackTrace();
        }

    // 사용자가 로그아웃(새로고침) 후에 다시 화면에 들어왔을 때 알림메시지가 남아있으려면 DB에 추가적으로 저장 필요

    }

    public void addSseEmitter(String email, SseEmitter sseEmitter){
        emitterMap.put(email,sseEmitter);
        System.out.println(emitterMap);
    }

}
