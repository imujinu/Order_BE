package com.order.ordersystem.common.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {
    //SseEmitter는 연결된 사용자 정보(ip, MacAddress 정보 등...) 를 의미
    // map에는 연결된 클라이언트 정보 들이 담겨져 있다.
    // ConcurrentHashMap은 Thread-Safe한 Map(동시성 이슈 발생 X)
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public void addSseEmitter(String email, SseEmitter sseEmitter){
        emitterMap.put(email,sseEmitter);
        System.out.println(emitterMap);
    }

    public void removeEmitter(String email){
        emitterMap.remove(email);
    }

    public SseEmitter getEmitterMap(String email) {
        return emitterMap.get(email);
    }


}
