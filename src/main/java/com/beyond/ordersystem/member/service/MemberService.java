package com.beyond.ordersystem.member.service;

import com.beyond.ordersystem.member.domain.Member;
import com.beyond.ordersystem.member.dto.MemberLoginDto;
import com.beyond.ordersystem.member.dto.MemberResDto;
import com.beyond.ordersystem.member.dto.MemberSaveReqDto;
import com.beyond.ordersystem.member.dto.ResetPasswordDto;
import com.beyond.ordersystem.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public MemberService(MemberRepository repository, PasswordEncoder passwordEncoder) {
        this.memberRepository = repository;
        this.passwordEncoder = passwordEncoder;
    }



    public Member memberCreate(MemberSaveReqDto dto){
        if(memberRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new IllegalArgumentException("이미 존재하는 email입니다.");
        }
        Member member = memberRepository.save(dto.toEntity(passwordEncoder.encode(dto.getPassword())));
        return member;
    }


    public Page<MemberResDto> memberList(Pageable pageable){
        Page<Member> members = memberRepository.findAll(pageable);
        return members.map(a->a.fromEntity());
    }


    public MemberResDto myInfo(){
        Member member = memberRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 email입니다."));
        MemberResDto memberResDto = member.fromEntity();
        return memberResDto;
    }


    public Member login(MemberLoginDto dto){
//        email의 존재여부
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 email입니다."));

//        password일치여부
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }


    public void resetPassword(ResetPasswordDto dto){
        //        email의 존재여부
        Member member = memberRepository.findByEmail(dto.getEmail()).orElseThrow(()-> new EntityNotFoundException("존재하지 않는 email입니다."));

//        password일치여부
        if (!passwordEncoder.matches(dto.getAsIsPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        member.updatePassword(passwordEncoder.encode(dto.getToBePassword()));
    }

}
