package com.order.ordersystem.member.service;

import com.order.ordersystem.common.auth.JwtTokenProvider;
import com.order.ordersystem.member.domain.Member;
import com.order.ordersystem.member.dto.LoginReqDto;
import com.order.ordersystem.member.dto.LoginResDto;
import com.order.ordersystem.member.dto.MemberCreateDto;
import com.order.ordersystem.member.dto.MemberResDto;
import com.order.ordersystem.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    public Long create(MemberCreateDto memberCreateDto){
        memberRepository.findByEmail(memberCreateDto.getEmail()).ifPresent(a-> {throw new IllegalArgumentException("이미 존재하는 이메일입니다.");});
        String password = passwordEncoder.encode(memberCreateDto.getPassword());
        Member member = memberRepository.save(memberCreateDto.memberToEntity(password));

        return member.getId();
    }

    public Member doLogin(LoginReqDto loginReqDto) {
        Optional<Member> member = memberRepository.findByEmail(loginReqDto.getEmail());
        boolean check = true;

        if(!member.isPresent()){
            check=false;
        }else{
            if(!passwordEncoder.matches(loginReqDto.getPassword(), member.get().getPassword())){
                check=false;
            }
        }

        if(!check){
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
        Long id = member.get().getId();
        String token = jwtTokenProvider.createToken(member.get());

        return member.get();
    }

    public List<MemberResDto> list() {
        List<Member> memberList = memberRepository.findAll();
        List<MemberResDto> memberResDtos = new ArrayList<>();

      return memberRepository.findAll().stream().map(a->MemberResDto.fromEntity(a)).collect(Collectors.toList());
    }

    public MemberResDto myInfo() {
         String email = SecurityContextHolder.getContext().getAuthentication().getName();
         Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));

         return MemberResDto.fromEntity(member);
    }

    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));
        member.deleteMember("Y");
    }

    public Member detail(Long memberId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(memberId).orElseThrow(()->new EntityNotFoundException("존재하지 않는 유저입니다."));
        return member;
    }
}
