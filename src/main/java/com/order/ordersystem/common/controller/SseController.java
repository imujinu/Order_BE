package com.order.ordersystem.common.controller;

import com.order.ordersystem.common.service.SseAlarmService;
import com.order.ordersystem.common.service.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {

    private final SseAlarmService sseAlarmService;
    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping("/connect")
    public SseEmitter subscribe(){
        //sseEmitter 객체 안에 클라이언트의 정보가 담겨져 있다.

        SseEmitter sseEmitter = new SseEmitter( 14400 * 60 * 1000L); // 10일 정도  emitter 유효기간 설정
        System.out.println(sseEmitter);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        sseEmitterRegistry.addSseEmitter(email,sseEmitter);


        //연결확인
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("연결완료"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return sseEmitter;
    }

    @GetMapping("/disconnect")
    public void unSubscribe(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        sseEmitterRegistry.removeEmitter(email);
    }
}
