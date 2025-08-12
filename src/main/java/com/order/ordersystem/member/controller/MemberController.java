package com.order.ordersystem.member.controller;

import com.order.ordersystem.common.auth.JwtTokenProvider;
import com.order.ordersystem.common.dto.CommonDto;
import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.member.dto.*;
import com.order.ordersystem.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody @Valid MemberCreateDto memberCreateDto){
        Long id = memberService.create(memberCreateDto);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(id)
                        .statusCode(HttpStatus.CREATED.value())
                        .statusMessage("회원가입 성공")
                        .build(),
                HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> doLogin(@RequestBody  LoginReqDto loginReqDto){
        Member member = memberService.doLogin(loginReqDto);
        // at 토큰 생성
        String accessToken = jwtTokenProvider.createToken(member);
        // rt 토큰 생성
        String refreshToken = jwtTokenProvider.createRtToken(member);
        LoginResDto loginResDto = LoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new ResponseEntity<>(CommonDto.builder()
                .result(loginResDto)
                .statusCode(HttpStatus.OK.value())
                .statusMessage("login success")
                .build()
                ,HttpStatus.OK);
    }

    // rt를 통한 at 갱신 요청
    @PostMapping("/refresh-at")
    public ResponseEntity<?> generateNewAt(@RequestBody RefreshTokenDto refreshTokenDto){
        //rt 검증 로직
        Member member = jwtTokenProvider.validateRt(refreshTokenDto.getRefreshToken());
        // at 신규 생성
        String accessToken = jwtTokenProvider.createToken(member);

        LoginResDto loginResDto = LoginResDto.builder()
                .accessToken(accessToken)
                .build();

        return new ResponseEntity<>(CommonDto.builder()
                .result(loginResDto)
                .statusCode(HttpStatus.OK.value())
                .statusMessage("login success")
                .build()
                ,HttpStatus.OK);
    }
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> list(){
        List<MemberResDto> memberList = memberService.list();
        return new ResponseEntity<>(CommonDto.builder()
                .result(memberList)
                .statusCode(HttpStatus.ACCEPTED.value())
                .statusMessage("회원 목록 조회 완료")
                .build(),HttpStatus.OK);

    }

    @GetMapping("/detail/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> detail(@PathVariable Long memberId){
        Member member = memberService.detail(memberId);
        return new ResponseEntity<>(CommonDto.builder()
                .result(member)
                .statusCode(HttpStatus.ACCEPTED.value())
                .statusMessage("회원 조회 완료")
                .build(),HttpStatus.ACCEPTED);
    }

    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo(){
        MemberResDto dto  = memberService.myInfo();
        return new ResponseEntity<>(CommonDto.builder()
                .result(dto)
                .statusCode(HttpStatus.ACCEPTED.value())
                .statusMessage("내 정보 조회 완료")
                .build(),HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(){
        memberService.delete();
        return new ResponseEntity<>(CommonDto.builder().result("OK").statusCode(HttpStatus.OK.value()).statusMessage("삭제 완료").build(), HttpStatus.ACCEPTED);
    }

}
